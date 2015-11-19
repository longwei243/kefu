package com.moor.imkf.tcpservice.tcp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moor.imkf.IMChatManager;
import com.moor.imkf.tcpservice.manager.LoginManager;
import com.moor.imkf.tcpservice.manager.SocketManager;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LineBasedFrameDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.util.HashedWheelTimer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;


/**
 * tcp线程,负责tcp的初始化，连接,发送数据等操作
 * @author LongWei
 *
 */
public class SocketThread extends Thread{
	
	private ClientBootstrap clientBootstrap = null;
	private ChannelFactory channelFactory = null;
	private ChannelFuture channelFuture = null;
	private Channel channel = null;
	private int connTryTimes = 0;
	
	private String ipAddress;
	private int ipPort;

	private boolean connecting = true;
	public void setConnecting(boolean connecting){
		this.connecting = connecting;
	}
	public boolean isConnecting() {
		return connecting;
	}

	public SocketThread(String ipAddress, int ipPort, SimpleChannelHandler handler) {
		this.ipAddress = ipAddress;
		this.ipPort = ipPort;
		init(handler);
	}

	@Override
	public void run() {
		doConnect();
	}

	/**
	 * 初始化连接配置项
	 * @param handler 负责业务数据的处理
	 */
	private void init(final SimpleChannelHandler handler) {
		try {
			channelFactory = new NioClientSocketChannelFactory(
					Executors.newSingleThreadExecutor(),
					Executors.newSingleThreadExecutor());

			clientBootstrap = new ClientBootstrap(channelFactory);
			clientBootstrap.setOption("connectTimeoutMillis", 2000);
			clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {

				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = Channels.pipeline();
					// 接收的数据包解码
					pipeline.addLast("decoder", new LineBasedFrameDecoder(1024));
					// 发送的数据包编码
					pipeline.addLast("encoder", new StringEncoder());
					pipeline.addLast("IdleStateHandler", new IdleStateHandler(new HashedWheelTimer(), 290, 0, 0));
					pipeline.addLast("handler", handler);
					return pipeline;

				}

			});

			clientBootstrap.setOption("tcpNoDelay", true);
			clientBootstrap.setOption("keepAlive", true);
		}catch (Exception e) {

		}

	}
	
	
	/**
	 * 进行连接
	 * @return
	 */
	public void doConnect() {

		while (connecting){
			connTryTimes++;
			try {
				if(channel != null){
					this.close();
					channel = null;
				}
				// Start the connection attempt.
				channelFuture = clientBootstrap.connect(new InetSocketAddress(
						ipAddress, ipPort));
				// Wait until the connection attempt succeeds or fails.
				channel = channelFuture.awaitUninterruptibly().getChannel();
				if (!channelFuture.isSuccess()) {
					ConnectivityManager connectivityManager = (ConnectivityManager) IMChatManager.getInstance().getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info = connectivityManager.getActiveNetworkInfo();
					if(info == null || !info.isConnected()) {
						setConnecting(false);
						break;
					}
					if(connTryTimes <= 5){
						sleep(1000);
					}else if(connTryTimes > 5 && connTryTimes < 20){
						sleep(3000);
					}else{
						if(connTryTimes>150){
							break;
						}
						sleep(10000);
					}
					if(!connecting){
						break;
					}
				}else{
					setConnecting(false);
					SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).setStatus(SocketManagerStatus.CONNECTED);
					LoginManager.getInstance(IMChatManager.getInstance().getAppContext()).login();
					break;
				}
			} catch (Exception e) {

			}
		}
	}
	/**
	 * 向服务器发送数据
	 * @param data
	 * @return
	 */
	public boolean sendData(String data) {
		Channel currentChannel =  channelFuture.getChannel();
        boolean isW = currentChannel.isWritable();
        boolean isC  = currentChannel.isConnected();
        if(!(isW && isC)){
			throw new RuntimeException("#sendData#channel is close!");
        }
        ChannelBuffer buffer = ChannelBuffers.buffer(data.length());
        buffer.writeBytes(data.getBytes());
        channelFuture.getChannel().write(buffer);
        return true;
	}


	public void close() {
		if (null == channelFuture)
			return;
		if (null != channelFuture.getChannel()) {
//			channelFuture.getChannel().close();
			channelFuture.getChannel().getCloseFuture().awaitUninterruptibly();
			channelFactory.releaseExternalResources();
		}
        channelFuture.cancel();
	}
	
	public Channel getChannel() {
		return channel;
	}
	
    public boolean isClose(){
        if(channelFuture != null && channelFuture.getChannel() != null){
            return !channelFuture.getChannel().isConnected();
        }
        return true;
    }


}

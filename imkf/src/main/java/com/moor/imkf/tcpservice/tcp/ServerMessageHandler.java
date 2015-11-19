package com.moor.imkf.tcpservice.tcp;

import android.content.Context;

import com.moor.imkf.IMChatManager;
import com.moor.imkf.db.dao.InfoDao;
import com.moor.imkf.event.LoginEvent;
import com.moor.imkf.event.SocketEvent;
import com.moor.imkf.tcpservice.manager.LoginManager;
import com.moor.imkf.tcpservice.manager.SocketManager;
import com.moor.imkf.utils.LogUtil;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelHandler;
import org.jboss.netty.handler.timeout.IdleStateEvent;

import java.nio.charset.Charset;

import de.greenrobot.event.EventBus;


/**
 * tcp数据的处理器,接收到对应的数据后将对应的事件发送出去
 * @author LongWei
 *
 */
public class ServerMessageHandler extends IdleStateAwareChannelHandler {

	private Context context;
	
	public ServerMessageHandler(Context context) {
		this.context = context;

	}
	
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
        /**
         * 1. 已经与远程主机建立的连接，远程主机主动关闭连接，或者网络异常连接被断开的情况
         2. 已经与远程主机建立的连接，本地客户机主动关闭连接的情况
         3. 本地客户机在试图与远程主机建立连接时，遇到类似与connection refused这样的异常，未能连接成功时
         而只有当本地客户机已经成功的与远程主机建立连接（connected）时，连接断开的时候才会触发channelDisconnected事件，即对应上述的1和2两种情况。
         *
         **/
  		super.channelDisconnected(ctx, e);
//		MobileApplication.logger.debug(TimeUtil.getCurrentTime() + "tcp 链接断开了：");
		LogUtil.d("ServerMessageHandler", "已经与Server断开连接。。。。");
		if(SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).getSocketThread() != null
				&& SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).getSocketThread().getChannel() != null){
			if(ctx.getChannel().getId().equals(SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).getSocketThread().getChannel().getId())){
//				MobileApplication.logger.debug(TimeUtil.getCurrentTime() + "tcp1 开始连：");
				//发送tcp服务器连接断开的事件
				EventBus.getDefault().postSticky(SocketEvent.MSG_SERVER_DISCONNECTED);
				LogUtil.d("ServerMessageHandler", "发送tcp服务器连接断开的事件");
			}else{
				System.out.println("发现了 old tcp channel 断开");
			}
		}else{

//			MobileApplication.logger.debug(TimeUtil.getCurrentTime() + "tcp2 开始连：");
			//发送tcp服务器连接断开的事件
			EventBus.getDefault().postSticky(SocketEvent.MSG_SERVER_DISCONNECTED);
			LogUtil.d("ServerMessageHandler", "发送tcp服务器连接断开的事件");
		}
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		super.messageReceived(ctx, e);
		ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
        String result = buffer.toString(Charset.defaultCharset());
        LogUtil.d("ServerMessageHandler", "服务器返回的数据是：" + result);

        if ("3".equals(result)) {
        	//心跳管理器负责
		} else if ("4".equals(result)) {
			//被踢了
			//发送被踢了的事件
			EventBus.getDefault().postSticky(LoginEvent.LOGIN_KICKED);
			SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).setStatus(SocketManagerStatus.BREAK);
		}else if ("100".equals(result)) {
			//有新消息之后的处理
			EventBus.getDefault().postSticky(LoginEvent.NEW_MSG);
		} else if ("400".equals(result)) {
			//登录失败，用户名或密码错误
			//发送登录失败的事件
			LoginManager.getInstance(IMChatManager.getInstance().getAppContext()).setIsStoreUsernamePasswordRight(false);
			EventBus.getDefault().postSticky(LoginEvent.LOGIN_FAILED);
			SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).setStatus(SocketManagerStatus.CONNECTED);
		} else if(result.startsWith("200")) {
			String connectionid = result.replace("200", "");
			InfoDao.getInstance().saveConnectionId(connectionid);
			//发送登录成功的事件
			EventBus.getDefault().postSticky(LoginEvent.LOGIN_SUCCESS);
			SocketManager.getInstance(IMChatManager.getInstance().getAppContext()).setStatus(SocketManagerStatus.LOGINED);
		} else {
		}
	}

    /**
	 *
     * */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
//        super.exceptionCaught(ctx, e);
        LogUtil.d("ServerMessageHandler", "exceptionCaught被调用了，直接断开连接");
        //有异常时直接断开连接
		EventBus.getDefault().postSticky(SocketEvent.MSG_SERVER_DISCONNECTED);
		//关闭channel
		Channel ch = e.getChannel();
		ch.close();
    }


	@Override
	public void channelIdle(ChannelHandlerContext ctx, IdleStateEvent e) throws Exception {
		super.channelIdle(ctx, e);

		switch (e.getState()) {
			case READER_IDLE:
				LogUtil.d("ServerMessageHandler", "读取通道空闲了");
				EventBus.getDefault().postSticky(SocketEvent.MSG_SERVER_DISCONNECTED);
				Channel ch = e.getChannel();
				ch.close();
				break;
		}
	}
}

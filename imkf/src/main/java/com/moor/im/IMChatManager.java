package com.moor.im;

import android.content.Context;
import android.content.Intent;

import com.loopj.android.http.TextHttpResponseHandler;
import com.moor.im.db.dao.InfoDao;
import com.moor.im.db.dao.InvestigateDao;
import com.moor.im.db.dao.MessageDao;
import com.moor.im.event.LoginEvent;
import com.moor.im.http.HttpManager;
import com.moor.im.model.entity.FromToMessage;
import com.moor.im.model.entity.Info;
import com.moor.im.model.entity.Investigate;
import com.moor.im.model.parser.HttpParser;
import com.moor.im.tcpservice.service.IMService;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * SDK管理类
 */
public class IMChatManager {

    /**
     * 接收新消息的action
     */
    public static final String NEW_MSG_ACTION = "com.moor.im.NEW_MSG";

    private Context appContext;

    private static IMChatManager instance = new IMChatManager();

    private InitListener initListener;

    private IMChatManager() {
        EventBus.getDefault().register(this);
    }

    public static IMChatManager getInstance() {
        return instance;
    }

    /**
     * 获取应用全局context
     * @return
     */
    public Context getAppContext() {
        return appContext;
    }

    /**
     * 初始化sdk方法，必须先调用该方法进行初始化后才能使用IM相关功能
     * @param appContext 上下文
     * @param accessId 接入id,必填项
     * @param userName 用户名，可选项，若没有填空字符串即可
     * @param userId 用户id，可选项，若没有填空字符串即可
     */
    public void init(Context appContext, String accessId, String userName,
                     String userId) {
        this.appContext = appContext.getApplicationContext();

        Info info = new Info();
        if(userName != null && !"".equals(userName)) {
            info.loginName = userName;
        }
        if(userId != null && !"".equals(userId)) {
            info.userId = userId;
        }

        if(accessId != null && !"".equals(accessId)) {
            info.accessId = accessId;
        }
        InfoDao.getInstance().insertInfoToDao(info);

        Intent imserviceIntent = new Intent(appContext, IMService.class);
        appContext.startService(imserviceIntent);

    }

    public void onEventMainThread(LoginEvent loginEvent){
        switch (loginEvent){
            case LOGIN_SUCCESS:
                HttpManager.getInvestigateList(InfoDao.getInstance().getConnectionId(), new GetInvestigateResponseHandler());
                if(initListener != null) {
                    initListener.oninitSuccess();
                }
                break;
            case LOGIN_FAILED:
                if(initListener != null) {
                    initListener.onInitFailed();
                }
            default:
                break;
        }
    }

    /**
     * 设置初始化回调接口
     * @param initListener
     */
    public void setOnInitListener(InitListener initListener) {
        this.initListener = initListener;
    }


    /**
     * 获取评价列表
     * @return
     */
    public List<Investigate> getInvestigate() {
        List<Investigate> list = new ArrayList<Investigate>();
        list = InvestigateDao.getInstance().getInvestigatesFromDao();
        if(list.size() == 0) {
            //去网络上加载
            HttpManager.getInvestigateList(InfoDao.getInstance().getConnectionId(), new GetInvestigateResponseHandler());
        }

        return list;
    }

    private class GetInvestigateResponseHandler extends TextHttpResponseHandler {

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            String succeed = HttpParser.getSucceed(responseString);
            if ("true".equals(succeed)) {
                List<Investigate> list = HttpParser.getInvestigates(responseString);
                InvestigateDao.getInstance().deleteAll();
                InvestigateDao.getInstance().insertInvestigateToDao(list);
            } else {

            }
        }
    }


    /**
     * 提交评价
     * @param investigate
     */
    public void submitInvestigate(Investigate investigate, SubmitInvestigateListener listener) {

        HttpManager.submitInvestigate(InfoDao.getInstance().getConnectionId(), investigate.name, investigate.value, new SubmitResponse(listener));
    }

    private class SubmitResponse extends TextHttpResponseHandler {

        private SubmitInvestigateListener listener;

        public SubmitResponse(SubmitInvestigateListener listener) {
            this.listener = listener;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if(listener != null) {
                listener.onFailed();
            }

        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            String succeed = HttpParser.getSucceed(responseString);
            if ("true".equals(succeed)) {
                if(listener != null) {
                    listener.onSuccess();
                }

            } else {
                if(listener != null) {
                    listener.onFailed();
                }

            }
        }
    }

    /**
     * 开始会话
     */
    public void beginSession(OnSessionBeginListener listener) {
        System.out.println("connectionId是:"+InfoDao.getInstance().getConnectionId());
        HttpManager.beginNewChatSession(InfoDao.getInstance().getConnectionId(), new BeginSessionResponse(listener));
    }

    private class BeginSessionResponse extends TextHttpResponseHandler {

        OnSessionBeginListener listener;
        public BeginSessionResponse(OnSessionBeginListener listener) {
            this.listener = listener;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if(listener != null) {
                listener.onFailed();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            String succeed = HttpParser.getSucceed(responseString);
            System.out.println("IMChatManager, 会话开始返回数据为:"+responseString);
            if ("true".equals(succeed)) {

                String leaveMessage = HttpParser.getLeaveMessage(responseString);
                if("true".equals(leaveMessage)) {
                    //弹出留言界面
                    if (listener != null) {
                        listener.onLeaveMessage();
                    }
                }else if("false".equals(leaveMessage)) {
                    String robot = HttpParser.getRobotEnable(responseString);
                    if("true".equals(robot)) {
                        //目前是机器人，需显示转人工按钮
                        if (listener != null) {
                            listener.onRobot();
                        }
                    }else if("false".equals(robot)) {
                        //已经是人工服务了，不用显示转人工按钮
                        if (listener != null) {
                            listener.onPeople();
                        }
                    }
                }


            } else {
                if (listener != null) {
                    listener.onFailed();
                }
            }
        }
    }

    /**
     * 提交离线留言
     * @param content 留言内容
     * @param phone 电话
     * @param email 邮箱
     * @param listener
     */
    public void submitOfflineMessage(String content, String phone, String email, OnSubmitOfflineMessageListener listener) {
       if(content == null) {
           content = "";
       }
        if(phone == null) {
            phone = "";
        }
        if(email == null) {
            email = "";
        }
        HttpManager.submitOfflineMessage(InfoDao.getInstance().getConnectionId(), content, phone, email, new SubmitOfflineMsgResponse(listener));
    }

    private class SubmitOfflineMsgResponse extends TextHttpResponseHandler {

        OnSubmitOfflineMessageListener listener;
        public SubmitOfflineMsgResponse(OnSubmitOfflineMessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if(listener != null) {
                listener.onFailed();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            String succeed = HttpParser.getSucceed(responseString);
            if ("true".equals(succeed)) {
                if(listener != null) {
                    listener.onSuccess();
                }
            } else {
                if (listener != null) {
                    listener.onFailed();
                }
            }
        }
    }


    /**
     * 从本地数据库中获取消息数据
     * @param i 第几页的数据，默认一页15条，从第一页开始取（i=1）
     * @return 消息数据列表
     */
    public List<FromToMessage> getMessages(int i) {
        List<FromToMessage> messagesList = new ArrayList<FromToMessage>();
        messagesList = MessageDao.getInstance().getMessages(i);
        return messagesList;
    }

    /**
     * 更新一条消息数据到本地数据库中
     * @param message
     */
    public void updateMessageToDB(FromToMessage message) {
        MessageDao.getInstance().updateMsgToDao(message);
    }


    /**
     * 通过传递进来的消息数量判断数据库中消息是否全被取出
     * @param size 消息数量
     * @return true说明消息全部被取出,false说明还有消息未取出
     */
    public boolean isReachEndMessage(int size) {
        return MessageDao.getInstance().isReachEndMessage(size);
    }

    /**
     * 转人工服务
     */
    public void convertManual(OnConvertManualListener listener) {
        HttpManager.convertManual(InfoDao.getInstance().getConnectionId(), new ConvertManualResponse(listener));
    }

    private class ConvertManualResponse extends TextHttpResponseHandler {

        OnConvertManualListener listener;
        public ConvertManualResponse(OnConvertManualListener listener) {
            this.listener = listener;
        }

        @Override
        public void onFailure(int statusCode, Header[] headers,
                              String responseString, Throwable throwable) {
            if(listener != null) {
                listener.offLine();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers,
                              String responseString) {
            String succeed = HttpParser.getSucceed(responseString);
            System.out.println("IMChatManager, 转人工返回数据:"+responseString);
            if ("true".equals(succeed)) {
                if(listener != null) {
                    listener.onLine();
                }
            } else {
                if(listener != null) {
                    listener.offLine();
                }
            }
        }
    }

}

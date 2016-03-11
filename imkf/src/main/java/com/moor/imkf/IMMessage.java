package com.moor.imkf;

import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.model.entity.Investigate;

import java.util.List;

/**
 * 创建不同种类消息实体
 */
public class IMMessage {

    /**
     * 构建文本类型消息
     * @param message
     * @return
     */
    public static FromToMessage createTxtMessage(String message) {
        FromToMessage fromToMessage = new FromToMessage();
        fromToMessage.message = message;
        fromToMessage.msgType = FromToMessage.MSG_TYPE_TEXT;
        fromToMessage.userType = "0";
        fromToMessage.when = System.currentTimeMillis();
        fromToMessage.sessionId = IMChat.getInstance().getSessionId();
        fromToMessage.tonotify  = IMChat.getInstance().get_id();
        fromToMessage.type = "User";
        fromToMessage.from = IMChat.getInstance().get_id();

        return fromToMessage;
    }

    /**
     * 构建语音类型消息
     * @param mTime
     * @param filePath
     * @return
     */
    public static FromToMessage createAudioMessage(float mTime, String filePath) {
        FromToMessage fromToMessage = new FromToMessage();
        fromToMessage.msgType = FromToMessage.MSG_TYPE_AUDIO;
        fromToMessage.userType = "0";
        fromToMessage.when = System.currentTimeMillis();
        fromToMessage.sessionId = IMChat.getInstance().getSessionId();
        fromToMessage.tonotify  = IMChat.getInstance().get_id();
        fromToMessage.type = "User";
        fromToMessage.from = IMChat.getInstance().get_id();
        fromToMessage.recordTime = mTime;
        fromToMessage.voiceSecond = Math.round(mTime) + "";
        fromToMessage.filePath = filePath;

        return fromToMessage;
    }


    /**
     * 构建图片类型消息
     * @param picFileFullName
     * @return
     */
    public static FromToMessage createImageMessage(String picFileFullName) {
        FromToMessage fromToMessage = new FromToMessage();
        fromToMessage.msgType = FromToMessage.MSG_TYPE_IMAGE;
        fromToMessage.userType = "0";
        fromToMessage.when = System.currentTimeMillis();
        fromToMessage.sessionId = IMChat.getInstance().getSessionId();
        fromToMessage.tonotify  = IMChat.getInstance().get_id();
        fromToMessage.type = "User";
        fromToMessage.from = IMChat.getInstance().get_id();
        fromToMessage.filePath = picFileFullName;

        return fromToMessage;
    }

    public static FromToMessage createInvestigateMessage(List<Investigate> investigates) {
        FromToMessage fromToMessage = new FromToMessage();
        fromToMessage.msgType = FromToMessage.MSG_TYPE_INVESTIGATE;
        fromToMessage.userType = "0";
        fromToMessage.when = System.currentTimeMillis();
        fromToMessage.sessionId = IMChat.getInstance().getSessionId();
        fromToMessage.tonotify  = IMChat.getInstance().get_id();
        fromToMessage.type = "User";
        fromToMessage.from = IMChat.getInstance().get_id();
        fromToMessage.investigates = investigates;
        fromToMessage.sendState = "true";

        return fromToMessage;
    }
}

package com.moor.im;

/**
 * 发送的聊天消息回调接口
 *
 */
public interface ChatListener {

    void onSuccess();
    void onFailed();
    void onProcess();
}

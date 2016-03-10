package com.moor.imkf.http;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.moor.imkf.requesturl.RequestUrl;
import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.utils.JSONWriter;
import com.moor.imkf.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 请求服务器方法类
 * 
 */
public class HttpManager {

	public static AsyncHttpClient hc = new AsyncHttpClient();

	/**
	 * 发送新消息到服务器
	 * @param connectionId
	 * @param fromToMessage
	 * @param responseHandler
	 */
	public static void newMsgToServer(String connectionId, FromToMessage fromToMessage, ResponseHandlerInterface responseHandler) {
		
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			if(FromToMessage.MSG_TYPE_TEXT.equals(fromToMessage.msgType)) {
				json.put("ContentType", "text");
			}else if(FromToMessage.MSG_TYPE_IMAGE.equals(fromToMessage.msgType)) {
				json.put("ContentType", "image");
			}else if(FromToMessage.MSG_TYPE_AUDIO.equals(fromToMessage.msgType)) {
				json.put("ContentType", "voice");
			}

			json.put("Message", fromToMessage.message);
			json.put("VoiceSecond", fromToMessage.voiceSecond);
			json.put("Action", "sdkNewMsg");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
		
	}

	/**
	 * 取消息
	 * 
	 * @param connectionId
	 * @param array
	 * @param responseHandler
	 */
	public static void getMsg(String connectionId, ArrayList array,
			ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ConnectionId", Utils.replaceBlank(connectionId));
		map.put("ReceivedMsgIds", array);
		map.put("Action", "sdkGetMsg");
		JSONWriter jw = new JSONWriter();
		jw.write(map);

		RequestParams params = new RequestParams();
		params.add("data", jw.write(map));
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);

	}
	/**
	 * 取大量的消息
	 * 
	 * @param connectionId
	 * @param largeMsgIdarray
	 * @param responseHandler
	 */
	public static void getLargeMsgs(String connectionId, ArrayList largeMsgIdarray,
			ResponseHandlerInterface responseHandler) {
		
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ConnectionId", Utils.replaceBlank(connectionId));
		map.put("LargeMsgId", largeMsgIdarray);
		map.put("Action", "getLargeMsg");
		JSONWriter jw = new JSONWriter();
		jw.write(map);
		
		RequestParams params = new RequestParams();
		params.add("data", jw.write(map));
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
		
	}

	/**
	 * 获取7牛的token
	 * 
	 * @param connectionId
	 * @param fileName
	 * @param responseHandler
	 */
	public static void getQiNiuToken(String connectionId, String fileName,
			ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "qiniu.getUptoken");
			json.put("fileName", fileName);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}


	/**
	 * 获取评价列表数据
	 */
	public static void getInvestigateList(String connectionId,
									 ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkGetInvestigate");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}

	/**
	 * 提交评价数据
	 */
	public static void submitInvestigate(String connectionId, String name, String value,
										  ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkSubmitInvestigate");
			json.put("Name", name);
			json.put("Value", value);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}

	/**
	 * 通知服务器开始新会话
	 */
	public static void beginNewChatSession(String connectionId, boolean isNewVisitor, String peerId, ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkBeginNewChatSession");
			json.put("IsNewVisitor", isNewVisitor);
			if(peerId != null && !"".equals(peerId)) {
				json.put("ToPeer", peerId);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}

	/**
	 * 提交离线消息
	 */
	public static void submitOfflineMessage(String connectionId, String peerId, String content, String phone, String email, ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Message", content);
			json.put("Phone", phone);
			json.put("Email", email);
			json.put("Action", "sdkSubmitLeaveMessage");
			if(peerId != null && !"".equals(peerId)) {
				json.put("ToPeer", peerId);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}

	/**
	 * 转人工客服
	 */
	public static void convertManual(String connectionId,
										  ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkConvertManual");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}

	/**
	 * 获取技能组
	 */
	public static void getPeers(String connectionId,
									 ResponseHandlerInterface responseHandler) {
		AsyncHttpClient httpclient = hc;
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkGetPeers");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.add("data", json + "");
		httpclient.post(RequestUrl.baseHttp1, params, responseHandler);
	}
}

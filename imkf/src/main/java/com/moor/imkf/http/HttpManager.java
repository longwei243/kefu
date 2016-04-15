package com.moor.imkf.http;

import android.os.Handler;
import android.os.Looper;

import com.moor.imkf.model.entity.FromToMessage;
import com.moor.imkf.requesturl.RequestUrl;
import com.moor.imkf.utils.JSONWriter;
import com.moor.imkf.utils.Utils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 请求服务器方法类
 * 
 */
public class HttpManager {
	public static OkHttpClient httpClient = new OkHttpClient();
	private static Handler mDelivery;

	static {
		mDelivery = new Handler(Looper.getMainLooper());
	}
	/**
	 * 发送新消息到服务器
	 * @param connectionId
	 * @param fromToMessage
	 * @param listener
	 */
	public static void newMsgToServer(String connectionId, FromToMessage fromToMessage, final HttpResponseListener listener) {

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

			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * 取消息
	 * 
	 * @param connectionId
	 * @param array
	 * @param listener
	 */
	public static void getMsg(String connectionId, ArrayList array,
							  final HttpResponseListener listener) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ConnectionId", Utils.replaceBlank(connectionId));
		map.put("ReceivedMsgIds", array);
		map.put("Action", "sdkGetMsg");
		JSONWriter jw = new JSONWriter();
		jw.write(map);

		RequestBody formBody = new FormEncodingBuilder()
				.add("data", jw.write(map))
				.build();
		Request request = new Request.Builder()
				.url(RequestUrl.baseHttp1)
				.post(formBody)
				.build();
		Call call = httpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				if(listener != null) {
					mDelivery.post(new Runnable() {
						@Override
						public void run() {
							listener.onFailed();
						}
					});

				}
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				if(listener != null) {
					mDelivery.post(new Runnable() {
						@Override
						public void run() {
							try {
								listener.onSuccess(response.body().string());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});

	}
	/**
	 * 取大量的消息
	 * 
	 * @param connectionId
	 * @param largeMsgIdarray
	 * @param listener
	 */
	public static void getLargeMsgs(String connectionId, ArrayList largeMsgIdarray,
									final HttpResponseListener listener) {
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("ConnectionId", Utils.replaceBlank(connectionId));
		map.put("LargeMsgId", largeMsgIdarray);
		map.put("Action", "getLargeMsg");
		JSONWriter jw = new JSONWriter();
		jw.write(map);
		RequestBody formBody = new FormEncodingBuilder()
				.add("data", jw.write(map))
				.build();
		Request request = new Request.Builder()
				.url(RequestUrl.baseHttp1)
				.post(formBody)
				.build();
		Call call = httpClient.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Request request, IOException e) {
				if(listener != null) {
					mDelivery.post(new Runnable() {
						@Override
						public void run() {
							listener.onFailed();
						}
					});

				}
			}

			@Override
			public void onResponse(final Response response) throws IOException {
				if(listener != null) {
					mDelivery.post(new Runnable() {
						@Override
						public void run() {
							try {
								listener.onSuccess(response.body().string());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}
		});
		
	}

	/**
	 * 获取7牛的token
	 * 
	 * @param connectionId
	 * @param fileName
	 * @param listener
	 */
	public static void getQiNiuToken(String connectionId, String fileName,
									 final HttpResponseListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "qiniu.getUptoken");
			json.put("fileName", fileName);
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 获取评价列表数据
	 */
	public static void getInvestigateList(String connectionId,
										  final HttpResponseListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkGetInvestigate");
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}
				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 提交评价数据
	 */
	public static void submitInvestigate(String connectionId, String name, String value,
										 final HttpResponseListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkSubmitInvestigate");
			json.put("Name", name);
			json.put("Value", value);
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 通知服务器开始新会话
	 */
	public static void beginNewChatSession(String connectionId, boolean isNewVisitor, String peerId, final HttpResponseListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkBeginNewChatSession");
			json.put("IsNewVisitor", isNewVisitor);
			if(peerId != null && !"".equals(peerId)) {
				json.put("ToPeer", peerId);
			}
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}
				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 提交离线消息
	 */
	public static void submitOfflineMessage(String connectionId, String peerId, String content, String phone, String email, final HttpResponseListener listener) {
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
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 转人工客服
	 */
	public static void convertManual(String connectionId,
									 final HttpResponseListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkConvertManual");
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取技能组
	 */
	public static void getPeers(String connectionId,
								final HttpResponseListener listener) {
		JSONObject json = new JSONObject();
		try {
			json.put("ConnectionId", Utils.replaceBlank(connectionId));
			json.put("Action", "sdkGetPeers");
			RequestBody formBody = new FormEncodingBuilder()
					.add("data", json.toString())
					.build();
			Request request = new Request.Builder()
					.url(RequestUrl.baseHttp1)
					.post(formBody)
					.build();
			Call call = httpClient.newCall(request);
			call.enqueue(new Callback() {
				@Override
				public void onFailure(Request request, IOException e) {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								listener.onFailed();
							}
						});

					}
				}

				@Override
				public void onResponse(final Response response) throws IOException {
					if(listener != null) {
						mDelivery.post(new Runnable() {
							@Override
							public void run() {
								try {
									listener.onSuccess(response.body().string());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

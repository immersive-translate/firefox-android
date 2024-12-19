package mozilla.components.jsbridge;


import com.google.gson.JsonObject;

public interface WebViewJavascriptBridge {
	
	void sendToWeb(JsonObject data);

	void sendToWeb(JsonObject data, OnBridgeCallback responseCallback);

	void sendToWeb(String function, Object... values);

	/**
	 * 处理从js返回的数据
	 * @param data 数据
	 * @param callbackId jsCallbackId
	 */
	void responseFromWeb(JsonObject data, String callbackId);

}

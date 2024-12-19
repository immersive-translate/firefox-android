package mozilla.components.jsbridge;

import com.google.gson.JsonObject;

public interface OnBridgeCallback {
	
	void onCallBack(JsonObject data);

}

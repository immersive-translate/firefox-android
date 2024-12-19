package mozilla.components.jsbridge;

import com.google.gson.JsonObject;

public interface BridgeHandler {
    void handler(JsonObject jsonObject, OnBridgeCallback callBackFunction);
}

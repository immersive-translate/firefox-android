package mozilla.components.jsbridge;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public String responseId;
    public JsonObject responseData;
    public String callbackId;
    public JsonObject data;
    public String handlerName;

    public static List<Message> toArrayList(JsonArray data) {
        if (data == null) {
            return new ArrayList<>();
        }
        return new Gson().fromJson(data, new TypeToken<List<Message>>(){}.getType());
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public JsonObject getResponseData() {
        return responseData;
    }

    public void setResponseData(JsonObject responseData) {
        this.responseData = responseData;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public JsonObject getData() {
        return data;
    }

    public void setCallbackId(String callbackStr) {
        this.callbackId = callbackStr;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}

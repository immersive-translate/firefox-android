package mozilla.components.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonObject;

import org.mozilla.geckoview.GeckoSession;

import java.util.HashMap;
import java.util.Map;

import mozilla.components.browser.engine.gecko.prompt.GeckoPromptDelegate;

public class JSBridgeInstance {
    @SuppressLint("StaticFieldLeak")
    private static JSBridgeInstance instance;
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static JSBridgeInstance getInstance() {
        if (instance == null) {
            instance = new JSBridgeInstance();
        }
        return instance;
    }

    private Context context;
    private final Map<String, BridgeHelper> bridgeHelperMap = new HashMap<>();

    private JSBridgeInstance() {
    }

    public void init(Context context) {
        this.context = context;
    }

    /**
     * 释放引用
     */
    public void close(@NonNull GeckoSession session) {
        String key = session.toString();
        BridgeHelper helper = bridgeHelperMap.remove(key);
        if (helper == null) {
            return;
        }
        helper.release();
    }

    /**
     * js 注入
     */
    public void injectJsBridge(@NonNull GeckoSession session) {
        if (context == null) {
            return;
        }
        String key = session.toString();
        BridgeHelper helper = bridgeHelperMap.get(key);

        if (helper == null) {
            helper = new BridgeHelper(new GeckoWebView(context, session));
            bridgeHelperMap.put(key, helper);

            GeckoPromptDelegate delegate = (GeckoPromptDelegate) session.getPromptDelegate();
            if (delegate != null) {
                delegate.setOnAlertInterceptor(new GeckoPromptDelegate.OnAlertInterceptor() {
                    @Override
                    public boolean onIntercept(@NonNull GeckoSession session, @NonNull String alertMessage) {
                        boolean isIntercept = !TextUtils.isEmpty(alertMessage) &&
                                alertMessage.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA);
                        if (isIntercept) {
                            handler.post(() -> intercept(session, alertMessage));
                        }
                        return isIntercept;
                    }
                });
            }

            helper.setDefaultHandler((data, callBackFunction) -> {
                handler.post(() -> {
                    if (onJavaScriptCallback != null) {
                        onJavaScriptCallback.onJsCall(session, data, callBackFunction);
                    }
                });
            });
        }

        helper.onPageFinished();
    }

    /**
     * 拦截并且处理
     */
    private void intercept(@NonNull GeckoSession session, String url) {
        String key = session.toString();
        BridgeHelper helper = bridgeHelperMap.get(key);
        if (helper == null) {
            return;
        }
        helper.shouldOverrideUrlLoading(url);
    }

    /**
     * 调用 javaScript
     */
    public void callHandler(@NonNull GeckoSession session,
                            @Nullable String name,
                            @NonNull JsonObject jsonObject,
                            @Nullable OnBridgeCallback callback) {
        String key = session.toString();
        BridgeHelper helper = bridgeHelperMap.get(key);
        if (helper == null) {
            return;
        }

        if (TextUtils.isEmpty(name)) {
            helper.sendToWeb(jsonObject, callback);
        } else {
            helper.callHandler(name, jsonObject, callback);
        }
    }

    private OnJavaScriptCallback onJavaScriptCallback;

    /**
     * js 回调
     */
    public void setOnJavaScriptCallback(OnJavaScriptCallback onJavaScriptCallback) {
        this.onJavaScriptCallback = onJavaScriptCallback;
    }

    public interface OnJavaScriptCallback {
        void onJsCall(@NonNull GeckoSession geckoSession,
                      @NonNull JsonObject jsonObject,
                      @Nullable OnBridgeCallback callback);
    }
}

package mozilla.components.jsbridge;

import android.content.Context;

import org.mozilla.geckoview.GeckoSession;

public class GeckoWebView implements IWebView {
    private final Context context;
    private final GeckoSession geckoSession;

    GeckoWebView(Context context, GeckoSession geckoSession) {
        this.context = context;
        this.geckoSession = geckoSession;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void loadUrl(String url) {
        if (geckoSession == null) {
            return;
        }
        geckoSession.loadUri(url);
    }
}

/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;

public class OrderBean implements Serializable {
    private String imtSessionId;
    private String redirect;
    private String clientSecret;
    private String prePayId;
    private String jsApiUiPackage;

    public String getImtSessionId() {
        return imtSessionId;
    }

    public void setImtSessionId(String imtSessionId) {
        this.imtSessionId = imtSessionId;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPrePayId() {
        return prePayId;
    }

    public void setPrePayId(String prePayId) {
        this.prePayId = prePayId;
    }

    public String getJsApiUiPackage() {
        return jsApiUiPackage;
    }

    public void setJsApiUiPackage(String jsApiUiPackage) {
        this.jsApiUiPackage = jsApiUiPackage;
    }
}

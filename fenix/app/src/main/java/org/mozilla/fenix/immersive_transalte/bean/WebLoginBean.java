/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import android.text.TextUtils;

import org.mozilla.fenix.immersive_transalte.ImmersiveTracker;

import java.io.Serializable;

public class WebLoginBean implements Serializable {
    private String redirectTo;
    private String userSubject;
    private UserTokenBean loginResult;

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }

    public String getUserSubject() {
        return userSubject;
    }

    public void setUserSubject(String userSubject) {
        this.userSubject = userSubject;
    }

    public UserTokenBean getLoginResult() {
        return loginResult;
    }

    public void setLoginResult(UserTokenBean loginResult) {
        this.loginResult = loginResult;
    }

    public boolean hasToken() {
        return loginResult != null
                && !TextUtils.isEmpty(loginResult.getToken())
                && loginResult.getUser() != null;
    }

    public void updateUserInfo() {
        if (!hasToken()) {
            return;
        }
        loginResult.getUser().setToken(loginResult.getToken());
        /*String deviceId = MD5Util.INSTANCE.getHashSha256(
                ImmersiveTracker.INSTANCE.getAdjustDeviceId());*/
        String deviceId = ImmersiveTracker.INSTANCE.getAdjustDeviceId();
        loginResult.getUser().setDeviceId(deviceId);
    }
}

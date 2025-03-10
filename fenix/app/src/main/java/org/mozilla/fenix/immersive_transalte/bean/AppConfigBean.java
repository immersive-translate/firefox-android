/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;
import java.util.List;

public class AppConfigBean implements Serializable {
    private List<String> appHostWhiteList;

    public List<String> getAppHostWhiteList() {
        return appHostWhiteList;
    }

    public void setAppHostWhiteList(List<String> appHostWhiteList) {
        this.appHostWhiteList = appHostWhiteList;
    }
}

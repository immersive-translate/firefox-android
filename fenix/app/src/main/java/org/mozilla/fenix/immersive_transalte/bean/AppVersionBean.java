/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;
import java.util.Map;

public class AppVersionBean implements Serializable {
    private AppVersion appUpdate;

    public AppVersion getAppUpdate() {
        return appUpdate;
    }

    public void setAppUpdate(AppVersion appUpdate) {
        this.appUpdate = appUpdate;
    }

    public static class AppVersion implements Serializable {
        private long versionCode;
        private String versionName;
        private String downloadUrl;
        private Map<String, String> updateContent;
        private boolean forceUpdate;
        private long minVersionCode;
        private boolean showUpdateNotification;
        private String updateTime;

        public long getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(long versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public String getDownloadUrl() {
            return downloadUrl;
        }

        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public Map<String, String> getUpdateContent() {
            return updateContent;
        }

        public void setUpdateContent(Map<String, String> updateContent) {
            this.updateContent = updateContent;
        }

        public boolean isForceUpdate() {
            return forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        public long isMinVersionCode() {
            return minVersionCode;
        }

        public void setMinVersionCode(long minVersionCode) {
            this.minVersionCode = minVersionCode;
        }

        public boolean isShowUpdateNotification() {
            return showUpdateNotification;
        }

        public void setShowUpdateNotification(boolean showUpdateNotification) {
            this.showUpdateNotification = showUpdateNotification;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }
    }
}

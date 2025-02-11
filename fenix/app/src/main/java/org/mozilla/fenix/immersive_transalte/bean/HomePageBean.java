/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;
import java.util.List;

public class HomePageBean implements Serializable {
    private List<TopLinkBean> topLinks;

    public List<TopLinkBean> getTopLinks() {
        return topLinks;
    }

    public void setTopLinks(List<TopLinkBean> topLinks) {
        this.topLinks = topLinks;
    }

    public static class TopLinkBean implements Serializable {
        private long id;
        private String iconUrl;
        private String linkUrl;
        private String title_zh;
        private String title_tr;
        private String title_en;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getIconUrl() {
            return iconUrl;
        }

        public void setIconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

        public String getTitle_zh() {
            return title_zh;
        }

        public void setTitle_zh(String title_zh) {
            this.title_zh = title_zh;
        }

        public String getTitle_tr() {
            return title_tr;
        }

        public void setTitle_tr(String title_tr) {
            this.title_tr = title_tr;
        }

        public String getTitle_en() {
            return title_en;
        }

        public void setTitle_en(String title_en) {
            this.title_en = title_en;
        }
    }

}

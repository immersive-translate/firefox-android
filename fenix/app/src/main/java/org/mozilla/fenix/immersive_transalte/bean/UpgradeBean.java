/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;

public class UpgradeBean implements Serializable {

    private UserBean.Subscription subscription;

    public UserBean.Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(UserBean.Subscription subscription) {
        this.subscription = subscription;
    }

    /**
     * 是否是年度会员
     */
    public boolean isSubYearVip() {
        return subscription != null
                && "yearly".equals(subscription.getSubscriptionType())
                && "active".equals(subscription.getSubscriptionStatus())
                && !subscription.isTrial();
    }

    /**
     * 是否是年度会员
     */
    public boolean isSubYearVipTry() {
        return subscription != null
                && "yearly".equals(subscription.getSubscriptionType())
                && subscription.isTrial();
    }
}

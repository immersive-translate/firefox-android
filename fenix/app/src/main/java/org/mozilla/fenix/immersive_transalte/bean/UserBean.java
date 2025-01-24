/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    private Subscription subscription;
    private int totalTrialMathPixQuota;
    private int trialMathPixUsedCount;
    private int weChatPackageMathPixQuota;
    private int weChatPackageMathPixQuotaUsedCount;
    private boolean hasPhone;
    // private myPhone;
    private String weChatNickName;
    private String weChatAvatarUrl;
    private String userName;
    private String email;
    private String nickName;
    private String lastLoginTime;
    private String openId;
    private String deviceId;
    private String accountType;
    private long uid;
    private String operator;
    private String updateTime;
    private String createTime;
    private boolean isDeleted;
    private int dataVersion;
    private long id;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public int getTotalTrialMathPixQuota() {
        return totalTrialMathPixQuota;
    }

    public void setTotalTrialMathPixQuota(int totalTrialMathPixQuota) {
        this.totalTrialMathPixQuota = totalTrialMathPixQuota;
    }

    public int getTrialMathPixUsedCount() {
        return trialMathPixUsedCount;
    }

    public void setTrialMathPixUsedCount(int trialMathPixUsedCount) {
        this.trialMathPixUsedCount = trialMathPixUsedCount;
    }

    public int getWeChatPackageMathPixQuota() {
        return weChatPackageMathPixQuota;
    }

    public void setWeChatPackageMathPixQuota(int weChatPackageMathPixQuota) {
        this.weChatPackageMathPixQuota = weChatPackageMathPixQuota;
    }

    public int getWeChatPackageMathPixQuotaUsedCount() {
        return weChatPackageMathPixQuotaUsedCount;
    }

    public void setWeChatPackageMathPixQuotaUsedCount(int weChatPackageMathPixQuotaUsedCount) {
        this.weChatPackageMathPixQuotaUsedCount = weChatPackageMathPixQuotaUsedCount;
    }

    public boolean isHasPhone() {
        return hasPhone;
    }

    public void setHasPhone(boolean hasPhone) {
        this.hasPhone = hasPhone;
    }

    public String getWeChatNickName() {
        return weChatNickName;
    }

    public void setWeChatNickName(String weChatNickName) {
        this.weChatNickName = weChatNickName;
    }

    public String getWeChatAvatarUrl() {
        return weChatAvatarUrl;
    }

    public void setWeChatAvatarUrl(String weChatAvatarUrl) {
        this.weChatAvatarUrl = weChatAvatarUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * 是否是月度会员
     */
    public boolean isSubMonthVip() {
        return subscription != null
                && "monthly".equals(subscription.subscriptionType)
                && "active".equals(subscription.subscriptionStatus);
    }

    /**
     * 是否是年度会员
     */
    public boolean isSubYearVip() {
        return subscription != null
                && "yearly".equals(subscription.subscriptionType)
                && "active".equals(subscription.subscriptionStatus)
                && !subscription.isTrial;
    }

    /**
     * 是否是年度会员
     */
    public boolean isSubYearVipTry() {
        return subscription != null
                && "yearly".equals(subscription.subscriptionType)
                && subscription.isTrial;
    }

    public static class Subscription {
        private String subscriptionType; // monthly
        private String subscriptionStatus; // active
        private String cancelAtPeriodEnd;
        private String subscriptionFrom;
        private String subscriptionTo;
        private String subscriptionId;
        private String checkoutSessionId;
        private boolean isTrial;
        private long id;
        private String amountPaid;
        private String currency;
        private String planAmountPaid;
        private String planCurrency;
        private String priceId;
        private int subscriptionDay;

        public String getSubscriptionType() {
            return subscriptionType;
        }

        public void setSubscriptionType(String subscriptionType) {
            this.subscriptionType = subscriptionType;
        }

        public String getSubscriptionStatus() {
            return subscriptionStatus;
        }

        public void setSubscriptionStatus(String subscriptionStatus) {
            this.subscriptionStatus = subscriptionStatus;
        }

        public String getCancelAtPeriodEnd() {
            return cancelAtPeriodEnd;
        }

        public void setCancelAtPeriodEnd(String cancelAtPeriodEnd) {
            this.cancelAtPeriodEnd = cancelAtPeriodEnd;
        }

        public String getSubscriptionFrom() {
            return subscriptionFrom;
        }

        public void setSubscriptionFrom(String subscriptionFrom) {
            this.subscriptionFrom = subscriptionFrom;
        }

        public String getSubscriptionTo() {
            return subscriptionTo;
        }

        public void setSubscriptionTo(String subscriptionTo) {
            this.subscriptionTo = subscriptionTo;
        }

        public String getSubscriptionId() {
            return subscriptionId;
        }

        public void setSubscriptionId(String subscriptionId) {
            this.subscriptionId = subscriptionId;
        }

        public String getCheckoutSessionId() {
            return checkoutSessionId;
        }

        public void setCheckoutSessionId(String checkoutSessionId) {
            this.checkoutSessionId = checkoutSessionId;
        }

        public boolean isTrial() {
            return isTrial;
        }

        public void setTrial(boolean trial) {
            isTrial = trial;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(String amountPaid) {
            this.amountPaid = amountPaid;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getPlanAmountPaid() {
            return planAmountPaid;
        }

        public void setPlanAmountPaid(String planAmountPaid) {
            this.planAmountPaid = planAmountPaid;
        }

        public String getPlanCurrency() {
            return planCurrency;
        }

        public void setPlanCurrency(String planCurrency) {
            this.planCurrency = planCurrency;
        }

        public String getPriceId() {
            return priceId;
        }

        public void setPriceId(String priceId) {
            this.priceId = priceId;
        }

        public int getSubscriptionDay() {
            return subscriptionDay;
        }

        public void setSubscriptionDay(int subscriptionDay) {
            this.subscriptionDay = subscriptionDay;
        }
    }
}

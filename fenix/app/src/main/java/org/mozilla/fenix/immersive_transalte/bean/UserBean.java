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
    private String token;
    private boolean hasBindWeChat;


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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isHasBindWeChat() {
        return hasBindWeChat;
    }

    public void setHasBindWeChat(boolean hasBindWeChat) {
        this.hasBindWeChat = hasBindWeChat;
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
     * 是否是试用年度会员
     */
    public boolean isSubYearVipTry() {
        return subscription != null
                && "yearly".equals(subscription.subscriptionType)
                && "active".equals(subscription.subscriptionStatus)
                && subscription.isTrial;
    }

    public boolean isSubYearVipTryExpired() {
        return subscription != null
                && "yearly".equals(subscription.subscriptionType)
                && !"active".equals(subscription.subscriptionStatus)
                && subscription.isTrial;
    }

    public static class Subscription {
        private int packageDeepLCharacterQuota;
        private int packageDeepLCharacterUsedCount;
        private int packageOpenAITokenQuota;
        private int packageOpenAITokenUsedCount;
        private int packageMathPixQuota;
        private int packageComicsPageQuota;
        private int packageAsrAudioQuota;
        private int packageMathPixUsedCount;
        private int packageComicsPageUsedCount;
        private int packageAsrAudioUsedCount;
        private int openAIOnlyTokenUsedCount;
        private int geminiOnlyTokenUsedCount;
        private int claudeOnlyTokenUsedCount;
        private int bigModelTokenUsedCount;
        private int deepSeekTokenUsedCount;
        private int packageOpenAIOnlyTokenUsedCount;
        private int packageGeminiOnlyTokenUsedCount;
        private int packageClaudeOnlyTokenUsedCount;
        private int packageBigModelTokenUsedCount;
        private int packageDeepSeekTokenUsedCount;
        private int packageOpenAITokenUnUsed;
        private int packageDeepLUnUsed;
        private int totalAsrAudioUsedCount;
        private int totalAsrAudioQuota;
        private int deepLCharacterUsedCountResetTime;
        private long openAITokenUsedCountResetTime;
        private long mathPixUsedCountResetTime;
        private long comicsPageUsedCountResetTime;
        private long asrAudioUsedCountResetTime;
        private String actName;
        private int unifiedCreditQuota;
        private int unifiedPackageCreditQuota;
        private String paymentChannel;
        private long userId;
        private String userEmail;
        private long subscriptionGoodsOrderId;
        private long subscriptionGoodsId;
        private int openAITokenQuota;
        private int openAITokenUsedCount;
        private int deepLCharacterQuota;
        private int deepLCharacterUsedCount;
        private int mathPixQuota;
        private int mathPixUsedCount;
        private int comicsPageQuota;
        private int comicsPageUsedCount;
        private int asrAudioQuota;
        private int asrAudioUsedCount;
        private String updateTime;
        private String createTime;
        private boolean isDeleted;
        private long dataVersion;

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

        public int getPackageDeepLCharacterQuota() {
            return packageDeepLCharacterQuota;
        }

        public void setPackageDeepLCharacterQuota(int packageDeepLCharacterQuota) {
            this.packageDeepLCharacterQuota = packageDeepLCharacterQuota;
        }

        public int getPackageDeepLCharacterUsedCount() {
            return packageDeepLCharacterUsedCount;
        }

        public void setPackageDeepLCharacterUsedCount(int packageDeepLCharacterUsedCount) {
            this.packageDeepLCharacterUsedCount = packageDeepLCharacterUsedCount;
        }

        public int getPackageOpenAITokenQuota() {
            return packageOpenAITokenQuota;
        }

        public void setPackageOpenAITokenQuota(int packageOpenAITokenQuota) {
            this.packageOpenAITokenQuota = packageOpenAITokenQuota;
        }

        public int getPackageOpenAITokenUsedCount() {
            return packageOpenAITokenUsedCount;
        }

        public void setPackageOpenAITokenUsedCount(int packageOpenAITokenUsedCount) {
            this.packageOpenAITokenUsedCount = packageOpenAITokenUsedCount;
        }

        public int getPackageMathPixQuota() {
            return packageMathPixQuota;
        }

        public void setPackageMathPixQuota(int packageMathPixQuota) {
            this.packageMathPixQuota = packageMathPixQuota;
        }

        public int getPackageComicsPageQuota() {
            return packageComicsPageQuota;
        }

        public void setPackageComicsPageQuota(int packageComicsPageQuota) {
            this.packageComicsPageQuota = packageComicsPageQuota;
        }

        public int getPackageAsrAudioQuota() {
            return packageAsrAudioQuota;
        }

        public void setPackageAsrAudioQuota(int packageAsrAudioQuota) {
            this.packageAsrAudioQuota = packageAsrAudioQuota;
        }

        public int getPackageMathPixUsedCount() {
            return packageMathPixUsedCount;
        }

        public void setPackageMathPixUsedCount(int packageMathPixUsedCount) {
            this.packageMathPixUsedCount = packageMathPixUsedCount;
        }

        public int getPackageComicsPageUsedCount() {
            return packageComicsPageUsedCount;
        }

        public void setPackageComicsPageUsedCount(int packageComicsPageUsedCount) {
            this.packageComicsPageUsedCount = packageComicsPageUsedCount;
        }

        public int getPackageAsrAudioUsedCount() {
            return packageAsrAudioUsedCount;
        }

        public void setPackageAsrAudioUsedCount(int packageAsrAudioUsedCount) {
            this.packageAsrAudioUsedCount = packageAsrAudioUsedCount;
        }

        public int getOpenAIOnlyTokenUsedCount() {
            return openAIOnlyTokenUsedCount;
        }

        public void setOpenAIOnlyTokenUsedCount(int openAIOnlyTokenUsedCount) {
            this.openAIOnlyTokenUsedCount = openAIOnlyTokenUsedCount;
        }

        public int getGeminiOnlyTokenUsedCount() {
            return geminiOnlyTokenUsedCount;
        }

        public void setGeminiOnlyTokenUsedCount(int geminiOnlyTokenUsedCount) {
            this.geminiOnlyTokenUsedCount = geminiOnlyTokenUsedCount;
        }

        public int getClaudeOnlyTokenUsedCount() {
            return claudeOnlyTokenUsedCount;
        }

        public void setClaudeOnlyTokenUsedCount(int claudeOnlyTokenUsedCount) {
            this.claudeOnlyTokenUsedCount = claudeOnlyTokenUsedCount;
        }

        public int getBigModelTokenUsedCount() {
            return bigModelTokenUsedCount;
        }

        public void setBigModelTokenUsedCount(int bigModelTokenUsedCount) {
            this.bigModelTokenUsedCount = bigModelTokenUsedCount;
        }

        public int getDeepSeekTokenUsedCount() {
            return deepSeekTokenUsedCount;
        }

        public void setDeepSeekTokenUsedCount(int deepSeekTokenUsedCount) {
            this.deepSeekTokenUsedCount = deepSeekTokenUsedCount;
        }

        public int getPackageOpenAIOnlyTokenUsedCount() {
            return packageOpenAIOnlyTokenUsedCount;
        }

        public void setPackageOpenAIOnlyTokenUsedCount(int packageOpenAIOnlyTokenUsedCount) {
            this.packageOpenAIOnlyTokenUsedCount = packageOpenAIOnlyTokenUsedCount;
        }

        public int getPackageGeminiOnlyTokenUsedCount() {
            return packageGeminiOnlyTokenUsedCount;
        }

        public void setPackageGeminiOnlyTokenUsedCount(int packageGeminiOnlyTokenUsedCount) {
            this.packageGeminiOnlyTokenUsedCount = packageGeminiOnlyTokenUsedCount;
        }

        public int getPackageClaudeOnlyTokenUsedCount() {
            return packageClaudeOnlyTokenUsedCount;
        }

        public void setPackageClaudeOnlyTokenUsedCount(int packageClaudeOnlyTokenUsedCount) {
            this.packageClaudeOnlyTokenUsedCount = packageClaudeOnlyTokenUsedCount;
        }

        public int getPackageBigModelTokenUsedCount() {
            return packageBigModelTokenUsedCount;
        }

        public void setPackageBigModelTokenUsedCount(int packageBigModelTokenUsedCount) {
            this.packageBigModelTokenUsedCount = packageBigModelTokenUsedCount;
        }

        public int getPackageDeepSeekTokenUsedCount() {
            return packageDeepSeekTokenUsedCount;
        }

        public void setPackageDeepSeekTokenUsedCount(int packageDeepSeekTokenUsedCount) {
            this.packageDeepSeekTokenUsedCount = packageDeepSeekTokenUsedCount;
        }

        public int getPackageOpenAITokenUnUsed() {
            return packageOpenAITokenUnUsed;
        }

        public void setPackageOpenAITokenUnUsed(int packageOpenAITokenUnUsed) {
            this.packageOpenAITokenUnUsed = packageOpenAITokenUnUsed;
        }

        public int getPackageDeepLUnUsed() {
            return packageDeepLUnUsed;
        }

        public void setPackageDeepLUnUsed(int packageDeepLUnUsed) {
            this.packageDeepLUnUsed = packageDeepLUnUsed;
        }

        public int getTotalAsrAudioUsedCount() {
            return totalAsrAudioUsedCount;
        }

        public void setTotalAsrAudioUsedCount(int totalAsrAudioUsedCount) {
            this.totalAsrAudioUsedCount = totalAsrAudioUsedCount;
        }

        public int getTotalAsrAudioQuota() {
            return totalAsrAudioQuota;
        }

        public void setTotalAsrAudioQuota(int totalAsrAudioQuota) {
            this.totalAsrAudioQuota = totalAsrAudioQuota;
        }

        public int getDeepLCharacterUsedCountResetTime() {
            return deepLCharacterUsedCountResetTime;
        }

        public void setDeepLCharacterUsedCountResetTime(int deepLCharacterUsedCountResetTime) {
            this.deepLCharacterUsedCountResetTime = deepLCharacterUsedCountResetTime;
        }

        public long getOpenAITokenUsedCountResetTime() {
            return openAITokenUsedCountResetTime;
        }

        public void setOpenAITokenUsedCountResetTime(long openAITokenUsedCountResetTime) {
            this.openAITokenUsedCountResetTime = openAITokenUsedCountResetTime;
        }

        public long getMathPixUsedCountResetTime() {
            return mathPixUsedCountResetTime;
        }

        public void setMathPixUsedCountResetTime(long mathPixUsedCountResetTime) {
            this.mathPixUsedCountResetTime = mathPixUsedCountResetTime;
        }

        public long getComicsPageUsedCountResetTime() {
            return comicsPageUsedCountResetTime;
        }

        public void setComicsPageUsedCountResetTime(long comicsPageUsedCountResetTime) {
            this.comicsPageUsedCountResetTime = comicsPageUsedCountResetTime;
        }

        public long getAsrAudioUsedCountResetTime() {
            return asrAudioUsedCountResetTime;
        }

        public void setAsrAudioUsedCountResetTime(long asrAudioUsedCountResetTime) {
            this.asrAudioUsedCountResetTime = asrAudioUsedCountResetTime;
        }

        public String getActName() {
            return actName;
        }

        public void setActName(String actName) {
            this.actName = actName;
        }

        public int getUnifiedCreditQuota() {
            return unifiedCreditQuota;
        }

        public void setUnifiedCreditQuota(int unifiedCreditQuota) {
            this.unifiedCreditQuota = unifiedCreditQuota;
        }

        public int getUnifiedPackageCreditQuota() {
            return unifiedPackageCreditQuota;
        }

        public void setUnifiedPackageCreditQuota(int unifiedPackageCreditQuota) {
            this.unifiedPackageCreditQuota = unifiedPackageCreditQuota;
        }

        public String getPaymentChannel() {
            return paymentChannel;
        }

        public void setPaymentChannel(String paymentChannel) {
            this.paymentChannel = paymentChannel;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public long getSubscriptionGoodsOrderId() {
            return subscriptionGoodsOrderId;
        }

        public void setSubscriptionGoodsOrderId(long subscriptionGoodsOrderId) {
            this.subscriptionGoodsOrderId = subscriptionGoodsOrderId;
        }

        public long getSubscriptionGoodsId() {
            return subscriptionGoodsId;
        }

        public void setSubscriptionGoodsId(long subscriptionGoodsId) {
            this.subscriptionGoodsId = subscriptionGoodsId;
        }

        public int getOpenAITokenQuota() {
            return openAITokenQuota;
        }

        public void setOpenAITokenQuota(int openAITokenQuota) {
            this.openAITokenQuota = openAITokenQuota;
        }

        public int getOpenAITokenUsedCount() {
            return openAITokenUsedCount;
        }

        public void setOpenAITokenUsedCount(int openAITokenUsedCount) {
            this.openAITokenUsedCount = openAITokenUsedCount;
        }

        public int getDeepLCharacterQuota() {
            return deepLCharacterQuota;
        }

        public void setDeepLCharacterQuota(int deepLCharacterQuota) {
            this.deepLCharacterQuota = deepLCharacterQuota;
        }

        public int getDeepLCharacterUsedCount() {
            return deepLCharacterUsedCount;
        }

        public void setDeepLCharacterUsedCount(int deepLCharacterUsedCount) {
            this.deepLCharacterUsedCount = deepLCharacterUsedCount;
        }

        public int getMathPixQuota() {
            return mathPixQuota;
        }

        public void setMathPixQuota(int mathPixQuota) {
            this.mathPixQuota = mathPixQuota;
        }

        public int getMathPixUsedCount() {
            return mathPixUsedCount;
        }

        public void setMathPixUsedCount(int mathPixUsedCount) {
            this.mathPixUsedCount = mathPixUsedCount;
        }

        public int getComicsPageQuota() {
            return comicsPageQuota;
        }

        public void setComicsPageQuota(int comicsPageQuota) {
            this.comicsPageQuota = comicsPageQuota;
        }

        public int getComicsPageUsedCount() {
            return comicsPageUsedCount;
        }

        public void setComicsPageUsedCount(int comicsPageUsedCount) {
            this.comicsPageUsedCount = comicsPageUsedCount;
        }

        public int getAsrAudioQuota() {
            return asrAudioQuota;
        }

        public void setAsrAudioQuota(int asrAudioQuota) {
            this.asrAudioQuota = asrAudioQuota;
        }

        public int getAsrAudioUsedCount() {
            return asrAudioUsedCount;
        }

        public void setAsrAudioUsedCount(int asrAudioUsedCount) {
            this.asrAudioUsedCount = asrAudioUsedCount;
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

        public long getDataVersion() {
            return dataVersion;
        }

        public void setDataVersion(long dataVersion) {
            this.dataVersion = dataVersion;
        }
    }
}

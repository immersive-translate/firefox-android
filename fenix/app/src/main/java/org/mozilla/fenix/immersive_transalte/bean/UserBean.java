/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    //private String subscription;
    private int totalTrialMathPixQuota;
    private int trialMathPixUsedCount;
    private int weChatPackageMathPixQuota;
    private int weChatPackageMathPixQuotaUsedCount;
    private boolean hasPhone;
    //private myPhone;
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
}

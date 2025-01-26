/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

public class VipUpgradeBean {
    private String account_country;
    private String account_name;
    private float amount_due;
    private float amount_paid;
    private float amount_remaining;
    private String attempted;
    private String billing_reason;
    private String currency;
    private String subs_from;
    private String subs_to;

    public String getAccount_country() {
        return account_country;
    }

    public void setAccount_country(String account_country) {
        this.account_country = account_country;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public float getAmount_due() {
        return amount_due;
    }

    public void setAmount_due(float amount_due) {
        this.amount_due = amount_due;
    }

    public float getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(float amount_paid) {
        this.amount_paid = amount_paid;
    }

    public float getAmount_remaining() {
        return amount_remaining;
    }

    public void setAmount_remaining(float amount_remaining) {
        this.amount_remaining = amount_remaining;
    }

    public String getAttempted() {
        return attempted;
    }

    public void setAttempted(String attempted) {
        this.attempted = attempted;
    }

    public String getBilling_reason() {
        return billing_reason;
    }

    public void setBilling_reason(String billing_reason) {
        this.billing_reason = billing_reason;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSubs_from() {
        return subs_from;
    }

    public void setSubs_from(String subs_from) {
        this.subs_from = subs_from;
    }

    public String getSubs_to() {
        return subs_to;
    }

    public void setSubs_to(String subs_to) {
        this.subs_to = subs_to;
    }
}

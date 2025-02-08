/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.fenix.immersive_transalte.bean;

import java.io.Serializable;
import java.util.List;

public class VipProductBean implements Serializable {
    private Entity entities;
    private List<Currencies> allSupportedCurrencies;

    public Entity getEntities() {
        return entities;
    }

    public void setEntities(Entity entities) {
        this.entities = entities;
    }

    public List<Currencies> getAllSupportedCurrencies() {
        return allSupportedCurrencies;
    }

    public void setAllSupportedCurrencies(List<Currencies> allSupportedCurrencies) {
        this.allSupportedCurrencies = allSupportedCurrencies;
    }

    public static class Entity {
        private Product month;
        private Product year;

        public Product getMonth() {
            return month;
        }

        public void setMonth(Product month) {
            this.month = month;
        }

        public Product getYear() {
            return year;
        }

        public void setYear(Product year) {
            this.year = year;
        }
    }

    public static class Product {
        private String currencySymbol;
        private String currency;
        private float unitAmount;
        private String priceId;
        private String displayedPrice;
        private boolean enableTrial;
        private int trialPeriodDays;
        private String originalDisplayedPrice;
        private String percentDiscountValue;
        private float originalUnitAmount;

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public float getUnitAmount() {
            return unitAmount;
        }

        public void setUnitAmount(float unitAmount) {
            this.unitAmount = unitAmount;
        }

        public String getPriceId() {
            return priceId;
        }

        public void setPriceId(String priceId) {
            this.priceId = priceId;
        }

        public String getDisplayedPrice() {
            return displayedPrice;
        }

        public void setDisplayedPrice(String displayedPrice) {
            this.displayedPrice = displayedPrice;
        }

        public boolean isEnableTrial() {
            return enableTrial;
        }

        public void setEnableTrial(boolean enableTrial) {
            this.enableTrial = enableTrial;
        }

        public int getTrialPeriodDays() {
            return trialPeriodDays;
        }

        public void setTrialPeriodDays(int trialPeriodDays) {
            this.trialPeriodDays = trialPeriodDays;
        }

        public String getOriginalDisplayedPrice() {
            return originalDisplayedPrice;
        }

        public void setOriginalDisplayedPrice(String originalDisplayedPrice) {
            this.originalDisplayedPrice = originalDisplayedPrice;
        }

        public String getPercentDiscountValue() {
            return percentDiscountValue;
        }

        public void setPercentDiscountValue(String percentDiscountValue) {
            this.percentDiscountValue = percentDiscountValue;
        }

        public float getOriginalUnitAmount() {
            return originalUnitAmount;
        }

        public void setOriginalUnitAmount(float originalUnitAmount) {
            this.originalUnitAmount = originalUnitAmount;
        }
    }

    public static class Currencies {
        private String code;
        private String name;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

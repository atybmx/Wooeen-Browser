package com.wooeen.model.to;

import java.io.Serializable;

public class WalletTO implements Serializable {

    private static final long serialVersionUID = 8291657749462467170L;

    private int conversionsPending;
    private int conversionsRegistered;
    private int conversionsApproved;
    private int conversionsRejected;
    private int conversionsWithdrawn;
    private double amountPending;
    private double amountRegistered;
    private double amountApproved;
    private double amountRejected;
    private double amountWithdrawn;
    private int affConversionsPending;
    private int affConversionsRegistered;
    private int affConversionsApproved;
    private int affConversionsRejected;
    private int affConversionsWithdrawn;
    private double affAmountPending;
    private double affAmountRegistered;
    private double affAmountApproved;
    private double affAmountRejected;
    private double affAmountWithdrawn;
    private int recommendationsRegistered;
    private int recommendationsConverted;
    private int recommendationsConfirmed;
    private double recommendationsRegisteredAmount;
    private double recommendationsConvertedAmount;
    private double recommendationsConfirmedAmount;
    private double balance;

    public int getConversionsRegistered() {
        return conversionsRegistered;
    }
    public void setConversionsRegistered(int conversionsRegistered) {
        this.conversionsRegistered = conversionsRegistered;
    }
    public int getConversionsApproved() {
        return conversionsApproved;
    }
    public void setConversionsApproved(int conversionsApproved) {
        this.conversionsApproved = conversionsApproved;
    }
    public int getConversionsRejected() {
        return conversionsRejected;
    }
    public void setConversionsRejected(int conversionsRejected) {
        this.conversionsRejected = conversionsRejected;
    }
    public int getConversionsWithdrawn() {
        return conversionsWithdrawn;
    }
    public void setConversionsWithdrawn(int conversionsWithdrawn) {
        this.conversionsWithdrawn = conversionsWithdrawn;
    }
    public double getAmountRegistered() {
        return amountRegistered;
    }
    public void setAmountRegistered(double amountRegistered) {
        this.amountRegistered = amountRegistered;
    }
    public double getAmountApproved() {
        return amountApproved;
    }
    public void setAmountApproved(double amountApproved) {
        this.amountApproved = amountApproved;
    }
    public double getAmountRejected() {
        return amountRejected;
    }
    public void setAmountRejected(double amountRejected) {
        this.amountRejected = amountRejected;
    }
    public double getAmountWithdrawn() {
        return amountWithdrawn;
    }
    public void setAmountWithdrawn(double amountWithdrawn) {
        this.amountWithdrawn = amountWithdrawn;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public int getRecommendationsRegistered() {
        return recommendationsRegistered;
    }
    public void setRecommendationsRegistered(int recommendationsRegistered) {
        this.recommendationsRegistered = recommendationsRegistered;
    }
    public int getRecommendationsConfirmed() {
        return recommendationsConfirmed;
    }
    public void setRecommendationsConfirmed(int recommendationsConfirmed) {
        this.recommendationsConfirmed = recommendationsConfirmed;
    }
    public double getRecommendationsConfirmedAmount() {
        return recommendationsConfirmedAmount;
    }
    public void setRecommendationsConfirmedAmount(double recommendationsConfirmedAmount) {
        this.recommendationsConfirmedAmount = recommendationsConfirmedAmount;
    }

    public double getRecommendationsRegisteredAmount() {
        return recommendationsRegisteredAmount;
    }

    public void setRecommendationsRegisteredAmount(double recommendationsRegisteredAmount) {
        this.recommendationsRegisteredAmount = recommendationsRegisteredAmount;
    }

    public int getRecommendationsConverted() {
        return recommendationsConverted;
    }
    public void setRecommendationsConverted(int recommendationsConverted) {
        this.recommendationsConverted = recommendationsConverted;
    }
    public double getRecommendationsConvertedAmount() {
        return recommendationsConvertedAmount;
    }
    public void setRecommendationsConvertedAmount(double recommendationsConvertedAmount) {
        this.recommendationsConvertedAmount = recommendationsConvertedAmount;
    }
    public int getConversionsPending() {
        return conversionsPending;
    }
    public void setConversionsPending(int conversionsPending) {
        this.conversionsPending = conversionsPending;
    }
    public double getAmountPending() {
        return amountPending;
    }
    public void setAmountPending(double amountPending) {
        this.amountPending = amountPending;
    }
    public int getAffConversionsPending() {
        return affConversionsPending;
    }
    public void setAffConversionsPending(int affConversionsPending) {
        this.affConversionsPending = affConversionsPending;
    }
    public int getAffConversionsRegistered() {
        return affConversionsRegistered;
    }
    public void setAffConversionsRegistered(int affConversionsRegistered) {
        this.affConversionsRegistered = affConversionsRegistered;
    }
    public int getAffConversionsApproved() {
        return affConversionsApproved;
    }
    public void setAffConversionsApproved(int affConversionsApproved) {
        this.affConversionsApproved = affConversionsApproved;
    }
    public int getAffConversionsRejected() {
        return affConversionsRejected;
    }
    public void setAffConversionsRejected(int affConversionsRejected) {
        this.affConversionsRejected = affConversionsRejected;
    }
    public int getAffConversionsWithdrawn() {
        return affConversionsWithdrawn;
    }
    public void setAffConversionsWithdrawn(int affConversionsWithdrawn) {
        this.affConversionsWithdrawn = affConversionsWithdrawn;
    }
    public double getAffAmountPending() {
        return affAmountPending;
    }
    public void setAffAmountPending(double affAmountPending) {
        this.affAmountPending = affAmountPending;
    }
    public double getAffAmountRegistered() {
        return affAmountRegistered;
    }
    public void setAffAmountRegistered(double affAmountRegistered) {
        this.affAmountRegistered = affAmountRegistered;
    }
    public double getAffAmountApproved() {
        return affAmountApproved;
    }
    public void setAffAmountApproved(double affAmountApproved) {
        this.affAmountApproved = affAmountApproved;
    }
    public double getAffAmountRejected() {
        return affAmountRejected;
    }
    public void setAffAmountRejected(double affAmountRejected) {
        this.affAmountRejected = affAmountRejected;
    }
    public double getAffAmountWithdrawn() {
        return affAmountWithdrawn;
    }
    public void setAffAmountWithdrawn(double affAmountWithdrawn) {
        this.affAmountWithdrawn = affAmountWithdrawn;
    }
}

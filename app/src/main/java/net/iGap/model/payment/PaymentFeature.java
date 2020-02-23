package net.iGap.model.payment;

import com.google.gson.annotations.SerializedName;

public class PaymentFeature {

    @SerializedName("ceil")
    private int max;
    @SerializedName("floor")
    private int min;
    @SerializedName("unit")
    private int unitStep;
    @SerializedName("spent")
    private int spentScore;
    @SerializedName("userScore")
    private int userScore;
    @SerializedName("title")
    private String title;
    @SerializedName("type")
    private String type;
    @SerializedName("priceWithFeature")
    private int price;
    @SerializedName("discount")
    private int discount;
    private boolean isChecked = false;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getUnitStep() {
        return unitStep;
    }

    public void setUnitStep(int unitStep) {
        this.unitStep = unitStep;
    }

    public int getSpentScore() {
        return spentScore;
    }

    public void setSpentScore(int spentScore) {
        this.spentScore = spentScore;
    }

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void toggleCheck() {
        isChecked = !isChecked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}

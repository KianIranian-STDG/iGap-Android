
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class ConfigData {

    @SerializedName("face_values")
    private List<Long> mFaceValues;
    @SerializedName("operator")
    private Operator mOperator;
    @SerializedName("package_charge_types")
    private List<PackageChargeType> mPackageChargeTypes;
    @SerializedName("services")
    private List<Service> mServices;
    @SerializedName("topup_charge_types")
    private List<TopupChargeType> mTopupChargeTypes;

    public List<Long> getFaceValues() {
        return mFaceValues;
    }

    public void setFaceValues(List<Long> faceValues) {
        mFaceValues = faceValues;
    }

    public Operator getOperator() {
        return mOperator;
    }

    public void setOperator(Operator operator) {
        mOperator = operator;
    }

    public List<PackageChargeType> getPackageChargeTypes() {
        return mPackageChargeTypes;
    }

    public void setPackageChargeTypes(List<PackageChargeType> packageChargeTypes) {
        mPackageChargeTypes = packageChargeTypes;
    }

    public List<Service> getServices() {
        return mServices;
    }

    public void setServices(List<Service> services) {
        mServices = services;
    }

    public List<TopupChargeType> getTopupChargeTypes() {
        return mTopupChargeTypes;
    }

    public void setTopupChargeTypes(List<TopupChargeType> topupChargeTypes) {
        mTopupChargeTypes = topupChargeTypes;
    }

}

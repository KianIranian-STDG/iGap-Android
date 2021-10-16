
package net.iGap.model.paymentPackage;

import java.util.List;
import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class ConfigData {

    @SerializedName("face_values")
    private List<FaceValue> mFaceValues;
    @SerializedName("operator")
    private Operator mOperator;
    @SerializedName("package_charge_types")
    private List<PackageChargeType> mPackageChargeTypes;
    @SerializedName("pre_numbers")
    private List<String> mPreNumbers;
    @SerializedName("services")
    private List<Service> mServices;
    @SerializedName("topup_charge_types")
    private List<TopupChargeType> mTopupChargeTypes;

    public List<FaceValue> getFaceValues() {
        return mFaceValues;
    }

    public void setFaceValues(List<FaceValue> faceValues) {
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

    public List<String> getPreNumbers() {
        return mPreNumbers;
    }

    public void setPreNumbers(List<String> preNumbers) {
        mPreNumbers = preNumbers;
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

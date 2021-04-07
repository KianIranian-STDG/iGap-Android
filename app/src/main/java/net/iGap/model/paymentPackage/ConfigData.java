
package net.iGap.model.paymentPackage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConfigData {
    @SerializedName("topup_charge_types")
    @Expose
    private List<TopupChargeType> topupChargeTypes = null;
    @SerializedName("package_charge_types")
    @Expose
    private List<PackageChargeType> packageChargeTypes = null;
    @SerializedName("services")
    @Expose
    private List<Service> services = null;
    @SerializedName("face_values")
    @Expose
    private List<FaceValue> faceValues = null;
    @SerializedName("operator")
    @Expose
    private Operator operator;

    public List<TopupChargeType> getTopupChargeTypes() {
        return topupChargeTypes;
    }

    public void setTopupChargeTypes(List<TopupChargeType> topupChargeTypes) {
        this.topupChargeTypes = topupChargeTypes;
    }

    public List<PackageChargeType> getPackageChargeTypes() {
        return packageChargeTypes;
    }

    public void setPackageChargeTypes(List<PackageChargeType> packageChargeTypes) {
        this.packageChargeTypes = packageChargeTypes;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<FaceValue> getFaceValues() {
        return faceValues;
    }

    public void setFaceValues(List<FaceValue> faceValues) {
        this.faceValues = faceValues;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }
}

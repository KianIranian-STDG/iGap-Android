package net.iGap.igasht.locationdetail.buyticket;

import com.google.gson.annotations.SerializedName;

import net.iGap.G;

import java.util.List;

public class IGashtLocationService {

    @SerializedName("service_id")
    private int serviceId;
    @SerializedName("service_name")
    private String serviceName;
    @SerializedName("english_name")
    private String englishName;
    @SerializedName("description")
    private String description;
    @SerializedName("activation")
    private boolean activation;
    @SerializedName("capacity")
    private String capacity;
    @SerializedName("beneficiary_iban")
    private String beneficiaryIban;
    @SerializedName("beneficiary_payment_id")
    private String beneficiaryPaymentId;
    @SerializedName("model_name")
    private String modelName;
    @SerializedName("model_id")
    private String modelId;
    @SerializedName("type_amounts")
    private List<IGashtServiceAmount> amounts;

    public int getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActivation() {
        return activation;
    }

    public String getCapacity() {
        return capacity;
    }

    public String getBeneficiaryIban() {
        return beneficiaryIban;
    }

    public String getBeneficiaryPaymentId() {
        return beneficiaryPaymentId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getModelId() {
        return modelId;
    }

    public List<IGashtServiceAmount> getAmounts() {
        return amounts;
    }

    public String getSeviceNameWithLanguage() {
        switch (G.selectedLanguage) {
            case "en":
                return getEnglishName();
            case "fa":
                return getServiceName();
            default:
                return getServiceName();
        }
    }
}

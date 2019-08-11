package org.stellar.sdk.responses;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.stellar.sdk.Asset;
import org.stellar.sdk.AssetTypeNative;

import java.lang.reflect.Type;

public class AssetDeserializer implements JsonDeserializer<Asset> {

  public AssetDeserializer() {
  }

  @Override
  public Asset deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
    String type = json.getAsJsonObject().get("asset_type").getAsString();
    if (type.equals("native")) {
      return new AssetTypeNative();
    } else {
      String code = json.getAsJsonObject().get("asset_code").getAsString();
      String issuer = json.getAsJsonObject().get("asset_issuer").getAsString();
      return Asset.createNonNativeAsset(code, issuer);
    }
  }
}

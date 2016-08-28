package com.iGap.request;

import android.util.Log;

import com.iGap.AESCrypt;
import com.iGap.Config;
import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperClassNamePreparation;
import com.iGap.helper.HelperNumerical;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class RequestQueue {

    public static synchronized void sendRequest(RequestWrapper... requestWrappers) throws IllegalAccessException {
        Log.i("SOC_REQ", "RequestQueue 1 sendRequest");
        int length = requestWrappers.length;
        String randomId = HelperString.generateKey();
        if (length == 1) {
            prepareRequest(randomId, requestWrappers[0]);
        } else if (length > 1) {
            ArrayList<Object> relationValue = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                relationValue.add(null);
            }

            G.requestQueueRelationMap.put(randomId, relationValue);

            for (int i = 0; i < requestWrappers.length; i++) {
                prepareRequest(randomId + "." + i, requestWrappers[i]);
            }
        } else if (length == 0) {
            Log.e("SOC_REQ", "RequestWrapper length should bigger than zero");
        }
    }


    protected static synchronized void prepareRequest(String randomId, RequestWrapper requestWrapper) {
        Log.i("SOC_REQ", "RequestQueue 2 prepareRequest");
        if (!G.pullRequestQueueRunned.get()) {
            G.pullRequestQueueRunned.getAndSet(true);
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQueuePullFunction();
                }
            }, Config.TIME_OUT_DELAY_MS);
        }

        requestWrapper.time = System.currentTimeMillis();
        ProtoRequest.Request.Builder requestBuilder = ProtoRequest.Request.newBuilder();
        requestBuilder.setId(randomId);

        Log.i("SOC", "ConnectionSecuringResponse 3");
        try {
            Object protoObject = requestWrapper.getProtoObject();
            Object protoInstance = null;
            try {
                Method setRequestMethod = protoObject.getClass().getMethod("setRequest", ProtoRequest.Request.Builder.class);
                protoInstance = setRequestMethod.invoke(protoObject, requestBuilder);
                Method method2 = protoInstance.getClass().getMethod("build");
                protoInstance = method2.invoke(protoInstance);
            } catch (NoSuchMethodException e) {
                Log.i("SOC_REQ", "RequestQueue Error : " + e);
                e.printStackTrace();
            }
            Log.i("SOC", "ConnectionSecuringResponse 4");
            G.requestQueueMap.put(randomId, requestWrapper);

            byte[] actionId = HelperNumerical.intToByteArray(requestWrapper.actionId);
            actionId = HelperNumerical.orderBytesToLittleEndian(actionId);

            Method toByteArrayMethod = protoInstance.getClass().getMethod("toByteArray");
            byte[] payload = (byte[]) toByteArrayMethod.invoke(protoInstance);
            byte[] message = HelperNumerical.appendByteArrays(actionId, payload);

            Log.i("SOC", "RequestWrapper message : " + message);

            if (G.isSecure) {
                message = AESCrypt.encrypt(G.symmetricKey, message);
            }

            Log.i("SOC_REQ", "RequestQueue **************** message : " + message);
            WebSocketClient.getInstance().sendBinary(message);
            Log.i("SOC_REQ", "RequestQueue 3 sendRequest successful");

        } catch (NoSuchMethodException e) {
            Log.i("SOC_REQ", "RequestQueue Error : " + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.i("SOC_REQ", "RequestQueue Error : " + e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.i("SOC_REQ", "RequestQueue Error : " + e);
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void requestQueuePullFunction() {

        for (Iterator<Map.Entry<String, RequestWrapper>> it = G.requestQueueMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, RequestWrapper> entry = it.next();
            String key = entry.getKey();
            RequestWrapper requestWrapper = entry.getValue();
            boolean delete = timeDifference(requestWrapper.getTime());


            if (delete) {
                if (key.contains(".")) {
                    String randomId = key.split("\\.")[0];

                    if (!G.requestQueueRelationMap.containsKey(randomId))
                        continue;

                    ArrayList<Object> array = G.requestQueueRelationMap.get(randomId);

                    G.requestQueueRelationMap.remove(randomId);

                    for (int i = 0; i < array.size(); i++) {
                        requestQueueMapRemover(randomId + "." + i);
                    }

                } else {
                    requestQueueMapRemover(key);
                }
            }
        }


        if (G.requestQueueMap.size() > 0) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQueuePullFunction();
                }
            }, Config.TIME_OUT_DELAY_MS);
        } else {
            G.pullRequestQueueRunned.getAndSet(false);
        }
    }


    private static void requestQueueMapRemover(String key) {

        try {

            RequestWrapper requestWrapper = G.requestQueueMap.get(key);
            G.requestQueueMap.remove(key);

            int actionId = requestWrapper.getActionId();
            String className = G.lookupMap.get(actionId + Config.LOOKUP_MAP_RESPONSE_OFFSET);
            String responseClassName = HelperClassNamePreparation.preparationResponseClassName(className);

            ProtoResponse.Response.Builder responseBuilder = ProtoResponse.Response.newBuilder();
            responseBuilder.setTimestamp((int) System.currentTimeMillis());
            responseBuilder.setId(key);
            responseBuilder.build();

            ProtoError.ErrorResponse.Builder errorBuilder = ProtoError.ErrorResponse.newBuilder();
            errorBuilder.setResponse(responseBuilder);
            errorBuilder.setMajorCode(5);
            errorBuilder.setMinorCode(1);
            errorBuilder.build();

            Class<?> c = Class.forName(responseClassName);
            Object object = c.getConstructor(int.class, Object.class).newInstance(actionId, errorBuilder);
            Method setTimeoutMethod = object.getClass().getMethod("timeOut");
            setTimeoutMethod.invoke(object);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean timeDifference(long beforeTime) {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - beforeTime);

        if (difference >= Config.TIME_OUT_MS) {
            return true;
        }

        return false;
    }

}

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.request;

import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import net.iGap.Config;
import net.iGap.G;
import net.iGap.WebSocketClient;
import net.iGap.helper.FileLog;
import net.iGap.helper.HelperClassNamePreparation;
import net.iGap.helper.HelperNumerical;
import net.iGap.helper.HelperString;
import net.iGap.module.AESCrypt;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoRequest;
import net.iGap.proto.ProtoResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

import static net.iGap.G.forcePriorityActionId;
import static net.iGap.G.requestQueueMap;

public class RequestQueue {

    private static final int QUEUE_LIMIT = 50;
    private static final int DEFAULT_PRIORITY = 100;

    private static ConcurrentHashMap<Integer, ArrayList<RequestWrapper[]>> priorityRequestWrapper = new ConcurrentHashMap<>();
    private static PriorityQueue<Integer> requestPriorityQueue = new PriorityQueue<>(1000, new Comparator<Integer>() {
        @Override
        public int compare(Integer a, Integer b) {
            if (a < b) {
                return 1;
            }
            if (a > b) {
                return -1;
            }
            return 0;
        }
    });

    // ***************************** Sending Request Methods ***************************************

    public static synchronized String sendRequest(RequestWrapper... requestWrappers) throws IllegalAccessException {
        int length = requestWrappers.length;
        String randomId = HelperString.generateKey();

        if (G.requestQueueMap.size() > QUEUE_LIMIT && !forcePriorityActionId.contains(requestWrappers[0].actionId)) {
            Integer priority = G.priorityActionId.get(requestWrappers[0].actionId);
            if (priority == null) {
                priority = DEFAULT_PRIORITY;
            }
            ArrayList<RequestWrapper[]> arrayWrapper;
            if (requestPriorityQueue.contains(priority)) {
                arrayWrapper = priorityRequestWrapper.get(priority);
            } else {
                arrayWrapper = new ArrayList<>();
            }
            arrayWrapper.add(requestWrappers);
            priorityRequestWrapper.put(priority, arrayWrapper);
            requestPriorityQueue.offer(priority);
            return randomId;
        }

        if (length == 1) {
            prepareRequest(randomId, requestWrappers[0]);
        } else if (length > 1) {
            /**
             * never be called until now
             */
            ArrayList<Object> relationValue = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                relationValue.add(null);
            }

            G.requestQueueRelationMap.put(randomId, relationValue);

            for (int i = 0; i < requestWrappers.length; i++) {
                prepareRequest(randomId + "." + i, requestWrappers[i]);
            }
        } else {
            Log.e("SOC_REQ", "RequestWrapper length should bigger than zero");
        }

        return randomId;
    }

    public static synchronized boolean cancelRequest(String id) {
        RequestWrapper requestWrapper = requestQueueMap.get(id);
        if (requestWrapper == null)
            return false;
        requestQueueMap.remove(id);
        return true;
    }

    public static synchronized void sendRequest() throws IllegalAccessException {
        if (requestPriorityQueue.size() <= 0) {
            return;
        }

        Integer p = requestPriorityQueue.poll();
        ArrayList<RequestWrapper[]> arrayWrapper = priorityRequestWrapper.get(p);
        if (arrayWrapper != null && arrayWrapper.size() > 0) {
            RequestWrapper[] requestWrappers = arrayWrapper.get(0);
            arrayWrapper.remove(0);
            priorityRequestWrapper.put(p, arrayWrapper);

            sendRequest(requestWrappers);
        }
    }

    private static synchronized void prepareRequest(String randomId, RequestWrapper requestWrapper) {
        if (!G.pullRequestQueueRunned.get()) {
            G.pullRequestQueueRunned.getAndSet(true);
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestQueuePullFunction();
                }
            }, Config.TIME_OUT_DELAY_MS);
        }

        //requestWrapper.time = System.currentTimeMillis(); // here the client was previously recorded time
        requestWrapper.setRandomId(randomId);
        ProtoRequest.Request.Builder requestBuilder = ProtoRequest.Request.newBuilder();
        requestBuilder.setId(randomId);

        try {
            Object protoObject = requestWrapper.getProtoObject();
            Object protoInstance = null;
            try {
                Method setRequestMethod = protoObject.getClass().getMethod("setRequest", ProtoRequest.Request.Builder.class);
                protoInstance = setRequestMethod.invoke(protoObject, requestBuilder);
                Method method2 = protoInstance.getClass().getMethod("build");
                protoInstance = method2.invoke(protoInstance);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            G.requestQueueMap.put(randomId, requestWrapper);

            byte[] actionId = HelperNumerical.intToByteArray(requestWrapper.actionId);
            actionId = HelperNumerical.orderBytesToLittleEndian(actionId);

            Method toByteArrayMethod = protoInstance.getClass().getMethod("toByteArray");
            byte[] payload = (byte[]) toByteArrayMethod.invoke(protoInstance);
            byte[] message = HelperNumerical.appendByteArrays(actionId, payload);

            if (G.isSecure) {
                if (G.userLogin || G.unLogin.contains(requestWrapper.actionId + "")) {
                    message = AESCrypt.encrypt(G.symmetricKey, message);
                    if (Config.FILE_LOG_ENABLE) {
                        FileLog.i("MSGR " + "prepareRequest: " + G.lookupMap.get(30000 + requestWrapper.actionId));
                    }
                    WebSocketClient.getInstance().sendBinary(message, requestWrapper);
                } else {
                    if (G.waitingActionIds.contains(requestWrapper.actionId + "")) {

                    }
                }
            } else if (G.unSecure.contains(requestWrapper.actionId + "")) {
                WebSocketClient.getInstance().sendBinary(message, requestWrapper);
            } else { //if (G.waitingActionIds.contains(requestWrapper.actionId + "")) {
                timeOutImmediately(randomId, false);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    // ********************************** Helper Methods *******************************************

    public static void clearPriorityQueue() {
        if (requestPriorityQueue != null) {
            requestPriorityQueue.clear();
        }
    }

    // ************************ Timeout and Remove Request Methods *********************************

    /******
     * This function runs for detecting timeout requests.
     * when request exist it calls itself and
     * else it set boolean value for next request to call this function.
     *****/
    private static synchronized void requestQueuePullFunction() {
        for (Map.Entry<String, RequestWrapper> entry : G.requestQueueMap.entrySet()) {
            String key = entry.getKey();
            RequestWrapper requestWrapper = entry.getValue();
            boolean delete;
            if (requestWrapper.actionId == 102) {
                delete = timeDifference(requestWrapper.getTime(), (10 * DateUtils.SECOND_IN_MILLIS));
            } else {
                delete = timeDifference(requestWrapper.getTime(), Config.TIME_OUT_MS);
            }
            if (delete) {
                deleteRequest(key);
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

    /******
     * Remove a request from Queue whit identity
     *****/
    public static void removeRequestQueue(String identity) {
        for (Map.Entry<String, RequestWrapper> entry : G.requestQueueMap.entrySet()) {
            if (entry.getValue().identity != null && entry.getValue().identity.toString().contains(identity)) {
                G.requestQueueMap.remove(entry.getKey());
            }
        }
    }

    /******
     * Remove a request from Queue whit key and calling timeout and additional check
     * some requests are grouped together and when one of them get timeout we should call timeout
     * for all of request for this group
     * note: group of request never used in iGap until now
     *****/
    private synchronized static void deleteRequest(String key) {
        if (key.contains(".")) {
            /**
             * never be called until now
             */
            String randomId = key.split("\\.")[0];

            if (!G.requestQueueRelationMap.containsKey(randomId)) return;

            ArrayList<Object> array = G.requestQueueRelationMap.get(randomId);

            G.requestQueueRelationMap.remove(randomId);

            for (int i = 0; i < array.size(); i++) {
                requestQueueMapRemover(randomId + "." + i);
            }
        } else {
            requestQueueMapRemover(key);
        }
    }

    /******
     * Remove a request from Queue whit key and calling timeout
     *****/
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
            Object object;
            try {
                object = c.getConstructor(int.class, Object.class, Object.class).newInstance(actionId, errorBuilder, requestWrapper.identity);
            } catch (NoSuchMethodException e) {
                object = c.getConstructor(int.class, Object.class, String.class).newInstance(actionId, errorBuilder, requestWrapper.identity);
            }
            Method setTimeoutMethod = object.getClass().getMethod("timeOut");
            setTimeoutMethod.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * timeOut request
     *
     * @param keyRandomId timeOut with this specific key
     * @param allRequest  timeOut all requests
     */
    public static void timeOutImmediately(@Nullable String keyRandomId, boolean allRequest) {
        for (Map.Entry<String, RequestWrapper> entry : G.requestQueueMap.entrySet()) {
            String key = entry.getKey();
            if (allRequest || key.equals(keyRandomId)) {
                deleteRequest(key);
            }
        }
    }

    /**
     * if time not set yet don't set timeout
     */
    private static boolean timeDifference(long beforeTime, long config) {
        if (beforeTime == 0) {
            return false;
        }

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - beforeTime);

        return difference >= config;
    }


    // **************************************** End ************************************************
}
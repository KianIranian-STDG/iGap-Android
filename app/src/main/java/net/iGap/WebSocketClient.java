/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the kianiranian Company - http://www.kianiranian.com/
 * All rights reserved.
 */

package net.iGap;

import android.text.format.DateUtils;
import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketCloseCode;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import net.iGap.helper.FileLog;
import net.iGap.helper.HelperConnectionState;
import net.iGap.module.enums.ConnectionState;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestClientGetRoomList;
import net.iGap.request.RequestQueue;
import net.iGap.request.RequestWrapper;
import net.iGap.response.ClientGetRoomListResponse;
import net.iGap.response.HandleResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static net.iGap.G.latestHearBeatTime;
import static net.iGap.G.latestResponse;

public class WebSocketClient {

    private static WebSocketClient instance;

    private WebSocket webSocketClient;
    private boolean autoConnect;

    private WebSocketClient() {
        autoConnect = true;
        try {
            this.webSocketClient = new WebSocketFactory().setConnectionTimeout((int) (10 * DateUtils.SECOND_IN_MILLIS)).createSocket(Config.URL_WEB_SOCKET);
            this.webSocketClient.setPingInterval(60 * DateUtils.SECOND_IN_MILLIS);
            this.webSocketClient.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.wtf(this.getClass().getName(), "onConnected");
                    latestHearBeatTime = System.currentTimeMillis();
                    latestResponse = System.currentTimeMillis();
                    HelperConnectionState.connectionState(ConnectionState.CONNECTING);
                    checkFirstResponse();
                    EventManager.getInstance().postEvent(
                            EventManager.SOCKET_CONNECT_OK,
                            ""
                    );

                    if (Config.FILE_LOG_ENABLE) {
                        FileLog.i("on Connected");
                    }

                    super.onConnected(websocket, headers);
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, final byte[] binary) throws Exception {
                    new HandleResponse(binary).run();
                    super.onBinaryMessage(websocket, binary);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    /*resetMainInfo();
                    reconnect(true);*/
                    Log.wtf(this.getClass().getName(), "onError" + cause.getMessage());


                    if (Config.FILE_LOG_ENABLE) {
                        FileLog.e("on Error " + cause.getMessage());
                    }

                    super.onError(websocket, cause);
                }

                @Override
                public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onFrameSent(websocket, frame);

                    /**
                     * set time after that actually frame was sent
                     */
                    RequestWrapper requestWrapper = G.requestQueueMap.get(((RequestWrapper) frame.getRequestWrapper()).getRandomId());
                    requestWrapper.setTime(System.currentTimeMillis());
                    G.requestQueueMap.put(requestWrapper.getRandomId(), requestWrapper);
                }

                @Override
                public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                    super.onStateChanged(websocket, newState);
                    Log.wtf(this.getClass().getName(), "onStateChanged" + "newState: "+ newState.name());
                }

                @Override
                public void onDisconnected(WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame serverCloseFrame, com.neovisionaries.ws.client.WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.wtf(this.getClass().getName(), "onDisconnected");
                    RequestQueue.timeOutImmediately(null, true);
                    RequestQueue.clearPriorityQueue();
                    resetMainInfo();

                    if (autoConnect)
                        G.handler.postDelayed(() -> connect(true), DateUtils.SECOND_IN_MILLIS);

                    EventManager.getInstance().postEvent(
                            EventManager.SOCKET_DISCONNECT,
                            ""
                    );

                    if (Config.FILE_LOG_ENABLE) {
                        FileLog.i("on onDisconnected ");
                    }

                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.wtf(this.getClass().getName(), "onConnectError");
                    resetMainInfo();

                    if (autoConnect)
                        G.handler.postDelayed(() -> connect(true), DateUtils.SECOND_IN_MILLIS);
                    EventManager.getInstance().postEvent(
                            EventManager.SOCKET_CONNECT_ERROR,
                            exception.getError().name() + ": " + exception.getMessage()
                    );
                    super.onConnectError(websocket, exception);
                }
            });

            HelperConnectionState.connectionState(ConnectionState.CONNECTING);
            this.webSocketClient.connectAsynchronously();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static WebSocketClient getInstance() {
        if (instance == null) {
            instance = new WebSocketClient();
        }

        return instance;
    }

    public void sendBinary(byte[] message,RequestWrapper requestWrapper){
        webSocketClient.sendBinary(message, requestWrapper);
    }

    /**
     * role back main data for preparation reconnecting to socket
     */
    private void resetWebsocketInfo() {
        ClientGetRoomListResponse.roomListFetched = false;
        RequestClientGetRoomList.isPendingGetRoomList = false;
        G.canRunReceiver = true;
        G.symmetricKey = null;

        /**
         * when secure is false set useMask true otherwise set false
         */
        G.isSecure = false;
        WebSocket.useMask = true;
        G.userLogin = false;
    }

    /**
     * reset some info just for 'RealmRoom' after connection is lost
     */
    private void resetMainInfo() {
        RealmRoom.clearAllActions();
        resetWebsocketInfo();
    }

    /**
     * if not secure yet send fake message to server for securing preparation
     */
    private void checkFirstResponse() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                int count = 0;

                while (G.symmetricKey == null && isConnect()) {

                    if (count < 3) {
                        count++;
                        try {
                            Thread.sleep(Config.FAKE_PM_DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (G.symmetricKey == null && isConnect()) {
                            if (webSocketClient != null) {
                                webSocketClient.sendText("i need 30001");
                            }
                        }
                    } else {
                        if (webSocketClient != null) {
                            webSocketClient.disconnect();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public boolean isConnect() {
        return webSocketClient != null && webSocketClient.isOpen();
    }

    public void connect(boolean autoConnect) {
        this.autoConnect = autoConnect;

        if (webSocketClient.getState() != WebSocketState.CONNECTING && webSocketClient.getState() != WebSocketState.OPEN) {
            resetWebsocketInfo();
            HelperConnectionState.connectionState(ConnectionState.CONNECTING);
            if (webSocketClient.getState() == WebSocketState.CLOSED) {
                try {
                    webSocketClient = webSocketClient.recreate();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            webSocketClient.connectAsynchronously();
        } else {
            EventManager.getInstance().postEvent(EventManager.SOCKET_CONNECT_DENY, "state of socket is : " + webSocketClient.getState().name());
        }
    }

    public void disconnectSocket(boolean autoConnect) {
        this.autoConnect = autoConnect;
        webSocketClient.disconnect(WebSocketCloseCode.NORMAL, null, 0);
    }
}

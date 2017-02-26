package com.iGap;

import android.text.format.DateUtils;
import android.util.Log;
import com.iGap.helper.HelperConnectionState;
import com.iGap.helper.HelperSetAction;
import com.iGap.helper.HelperTimeOut;
import com.iGap.request.RequestWrapper;
import com.iGap.response.HandleResponse;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * for create and manage webSocketConnection
 */
public class WebSocketClient {

    public static boolean allowForReconnecting = true;
    public static boolean waitingForReconnecting = false;
    private static WebSocket webSocketClient;
    private static int count = 0;
    private static long latestConnectionTryTiming;
    private static WebSocketState connectionState;
    private static long latestConnectionOpenTime = 0;

    /**
     * add webSocketConnection listeners and try for connect
     *
     * @return WebSocket
     */

    private static synchronized WebSocket createSocketConnection() {
        Log.e("DDD", "createSocketConnection 1");
        WebSocket websocketFactory = null;
        try {
            WebSocketFactory webSocketFactory = new WebSocketFactory();
            webSocketFactory.setConnectionTimeout((int) (10 * DateUtils.SECOND_IN_MILLIS));
            websocketFactory = webSocketFactory.createSocket(Config.urlWebsocket);
            websocketFactory.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    latestConnectionOpenTime = System.currentTimeMillis();
                    Log.i("SOC_WebSocket", "onConnected");
                    waitingForReconnecting = false;
                    if (G.isSecure) {
                        allowForReconnecting = true;
                        if (webSocketClient != null) {
                            webSocketClient.disconnect();
                        }
                    } else {
                        G.socketConnection = true;
                        HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                        checkFirstResponse();
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
                    Log.i("SOC_WebSocket", "onError");
                    Log.i("reconnect", "1");
                    reconnect(true);
                    super.onError(websocket, cause);
                }

                @Override
                public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onFrameSent(websocket, frame);

                    /**
                     * set time after that actually frame was sent
                     */
                    Log.i("RequestWrapper", "onFrameSent time 0 : " + ((RequestWrapper) frame.getRequestWrapper()).time);
                    ((RequestWrapper) frame.getRequestWrapper()).time = System.currentTimeMillis();
                    Log.i("RequestWrapper", "onFrameSent time 1 : " + ((RequestWrapper) frame.getRequestWrapper()).time);
                    Log.i("RequestWrapper", "onFrameSent : " + ((RequestWrapper) frame.getRequestWrapper()).getActionId());
                }

                @Override
                public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onUnexpectedError(websocket, cause);
                    Log.i("SOC_WebSocket", "onUnexpectedError");
                }

                @Override
                public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onPingFrame(websocket, frame);
                    Log.i("SOC_WebSocket", "onPingFrame");
                }

                @Override
                public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onPongFrame(websocket, frame);
                    Log.i("SOC_WebSocket", "onPongFrame");
                }

                @Override
                public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                    super.onFrameError(websocket, cause, frame);
                    Log.i("SOC_WebSocket", "onFrameError");
                }

                @Override
                public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                    super.onStateChanged(websocket, newState);
                    connectionState = newState;
                    Log.i("SOC_WebSocket_X", "onStateChanged");
                    Log.i("SOC_WebSocket_X", "newState : " + newState);
                }

                @Override
                public void onDisconnected(WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame serverCloseFrame, com.neovisionaries.ws.client.WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.i("SOC_WebSocket", "onDisconnected");
                    Log.i("SOC_WebSocket", "closedByServer : " + closedByServer);
                    allowForReconnecting = true;
                    Log.i("reconnect", "2");
                    reconnect(true);
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.i("SOC_WebSocket_XXX", "onConnectError");
                    Log.i("reconnect", "3");
                    reconnect(true);
                    super.onConnectError(websocket, exception);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        final WebSocket finalWs = websocketFactory;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (G.allowForConnect) {
                    try {
                        if (finalWs != null) {
                            //if (timeDifference(latestConnectionTryTiming)) {
                            Log.i("SOC_WebSocket", "Connecting");
                            /**
                             * in first make connection client should set latestConnectionTryTiming time
                             * because when run reconnect method timeout checking
                             */
                            latestConnectionTryTiming = System.currentTimeMillis();
                            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                            finalWs.connect();
                            //}
                        }
                    } catch (WebSocketException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        return websocketFactory;
    }

    /**
     * create a webSocketConnection if is null now
     *
     * @return webSocketConnection
     */

    public static WebSocket getInstance() {

        Log.e("DDD", "webSocketClient : " + webSocketClient);
        if (webSocketClient != null) {
            Log.e("DDD", "webSocketClient.isOpen() : " + webSocketClient.isOpen());
        }
        Log.e("DDD", "waitingForReconnecting : " + waitingForReconnecting);

        if (!waitingForReconnecting && (webSocketClient == null || !webSocketClient.isOpen())) {
            Log.e("DDD", "getInstance create new");
            waitingForReconnecting = true;
            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
            checkGetInstanceSuccessfully();
            return webSocketClient = createSocketConnection();
        } else {
            Log.e("DDD", "getInstance return old");
            return webSocketClient;
        }
    }

    /**
     * check current state of socket for insuring that
     * connection established and if socket connection
     * wasn't open or is null try for reconnecting
     */

    private static void checkGetInstanceSuccessfully() {

        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (webSocketClient == null || !webSocketClient.isOpen()) {
                    Log.i("reconnect", "4");
                    reconnect(false);
                }
            }
        }, Config.INSTANCE_SUCCESSFULLY_CHECKING);
    }

    /**
     * clear securing state and reconnect to server
     *
     * @param force if set force true try for reconnect even socket is open.
     * client do this action because maybe connection lost but client not
     * detected this actions(android 7.*).
     */

    public static void reconnect(boolean force) {

        if (force || (webSocketClient == null || !webSocketClient.isOpen())) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (timeDifference(latestConnectionTryTiming) && connectionState != WebSocketState.CONNECTING && (connectionState != WebSocketState.OPEN || (HelperTimeOut.timeoutChecking(0, latestConnectionOpenTime, Config.CONNECTION_OPEN_TIME_OUT)))) {
                        HelperSetAction.clearAllActions();
                        Log.e("DDD", "reconnect 1");
                        if (allowForReconnecting) {
                            allowForReconnecting = false;
                            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                            Log.e("DDD", "reconnect 2");
                            if (G.allowForConnect) {
                                Log.e("DDD", "reconnect 3");
                                latestConnectionTryTiming = System.currentTimeMillis();
                                waitingForReconnecting = false;
                                resetWebsocketInfo();
                                WebSocketClient.getInstance();
                                //checkSocketConnection();
                            }
                        }
                    } else {
                        Log.e("DDD", "try for connect");
                        allowForReconnecting = true;
                        waitingForReconnecting = false;
                        Log.i("reconnect", "5");
                        reconnect(false);
                    }
                }
            }, Config.REPEAT_CONNECTION_CHECKING);
        } else {
            Log.i("SOC_WebSocket", "don't need Reconnect");
        }
    }

    /**
     * check if socket connected or from last try connecting over the past ten seconds and finally
     * reconnect or run this method again
     */

    private static void checkSocketConnection() {

        if (webSocketClient == null || !webSocketClient.isOpen()) {
            Log.i("SOC_WebSocket", "Need For reconnect");
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (timeDifference(latestConnectionTryTiming)) {
                        allowForReconnecting = true;
                        waitingForReconnecting = false;
                        Log.i("reconnect", "6");
                        reconnect(false);
                    } else {
                        checkSocketConnection();
                    }
                }
            }, Config.REPEAT_CONNECTION_CHECKING);
        } else {
            /**
             when connecting was successful and user login ,
             in user login response will be change
             allowForReconnecting=true and waitingForReconnecting=false
             for allow reconnecting later if need
             */
            Log.i("SOC_WebSocket", "Don't Need For Reconnecting");
        }
    }

    /**
     * compute time difference between latest try for connecting to socket and current time
     *
     * @param beforeTime when try for connect to socket (currentTimeMillis)
     * @return return true if difference is bigger than 10 second
     */

    private static boolean timeDifference(long beforeTime) {

        long difference;

        long currentTime = System.currentTimeMillis();
        difference = (currentTime - beforeTime);

        if (difference >= Config.ALLOW_RECONNECT_AGAIN) {
            return true;
        }

        return false;
    }

    /**
     * disconnect current websocket if connection created and is open
     *
     * @return current webSocket
     */

    public static WebSocket disconnect() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.disconnect();
        }
        return webSocketClient;
    }

    public static boolean checkConnection() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            return true;
        }
        /**
         * set allowForReconnecting = true; for allow that to reconnecting
         */
        waitingForReconnecting = false;
        allowForReconnecting = true;
        Log.i("reconnect", "7");
        reconnect(false);
        return false;
    }

    /**
     * role back main data for preparation for reconnecting to socket
     */

    private static void resetWebsocketInfo() {
        count = 0;
        G.canRunReceiver = true;
        G.symmetricKey = null;
        webSocketClient = null;
        /**
         * when secure is false set useMask true otherwise set false
         */
        G.isSecure = false;
        WebSocket.useMask = true;
        G.userLogin = false;
    }

    /**
     * if not secure yet send fake message to server for securing preparation
     */

    private static void checkFirstResponse() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (G.symmetricKey == null && G.socketConnection) {

                    if (count < 3) {
                        count++;
                        try {
                            Thread.sleep(Config.FAKE_PM_DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        G.handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (G.symmetricKey == null && G.socketConnection) {
                                    Log.i("SOC", "I need 30001");
                                    WebSocket webSocket = WebSocketClient.getInstance();
                                    if (webSocket != null) {
                                        webSocket.sendText("i need 30001");
                                    }
                                }
                            }
                        });
                    } else {
                        /*G.allowForConnect = false;
                        WebSocket webSocket = WebSocketClient.getInstance();
                        if (webSocket != null) {
                            webSocket.disconnect();
                        }*/
                        //TODO [Saeed Mozaffari] [2016-09-06 12:31 PM] - go to upgrade page
                    }
                }
            }
        });
        thread.start();
    }
}

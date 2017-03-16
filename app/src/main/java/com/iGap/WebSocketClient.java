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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.iGap.Config.ALLOW_RECONNECT_AGAIN_NORMAL;

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
    private static int allowReconnectAgain = Config.ALLOW_RECONNECT_AGAIN_MINIMUM;
    private static int reconnectCount;
    private static int reconnectQueueLimitation; // this value not allowed to call reconnect method so much
    private static ArrayList<WebSocket> webSocketArrayList = new ArrayList<>();
    private static boolean topPermission = true;

    private enum ReconnectState {
        MINIMUM, MAXIMUM, NORMAL
    }

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
                        reconnectCount = 0;
                        reconnectQueueLimitation = 0;
                        G.socketConnection = true;
                        HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                        checkFirstResponse();

                        //HelperUploadFile.addItemFromQueue();
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
                    reconnect(true);
                    super.onError(websocket, cause);
                }

                @Override
                public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onFrameSent(websocket, frame);

                    /**
                     * set time after that actually frame was sent
                     */
                    ((RequestWrapper) frame.getRequestWrapper()).time = System.currentTimeMillis();
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
                    Log.i("SOC_WebSocket_X", "newState : " + newState);
                }

                @Override
                public void onDisconnected(WebSocket websocket, com.neovisionaries.ws.client.WebSocketFrame serverCloseFrame, com.neovisionaries.ws.client.WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.i("SOC_WebSocket", "onDisconnected");
                    Log.i("SOC_WebSocket", "closedByServer : " + closedByServer);
                    allowForReconnecting = true;
                    G.socketConnection = false;
                    reconnect(true);
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.i("SOC_WebSocket_XXX", "onConnectError");
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
                            Log.i("SOC_WebSocket", "Connecting");
                            /**
                             * in first make connection client should set latestConnectionTryTiming time
                             * because when run reconnect method timeout checking
                             */
                            latestConnectionTryTiming = System.currentTimeMillis();
                            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                            finalWs.connect();
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
        if (!waitingForReconnecting && (webSocketClient == null || !webSocketClient.isOpen())) {
            waitingForReconnecting = true;
            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
            checkGetInstanceSuccessfully();
            //clearAllPreviousConnection();
            return webSocketClient = createSocketConnection();
        } else {
            return webSocketClient;
        }
    }

    /**
     * close and clear all before connection. i used this trick for avoid from duplicate connection.
     */
    private static void clearAllPreviousConnection() {
        topPermission = false;
        for (int i = 0; i < webSocketArrayList.size(); i++) {
            if (webSocketArrayList.get(i) != null) {
                webSocketArrayList.get(i).disconnect();
                webSocketArrayList.set(i, null);
            }
        }
        /**
         * after this time(2000) set topPermission = true for allow reconnect.
         * i used from this time because maybe disconnect was async and before
         * call disconnect in this loop i set this value true and in this state
         * my connection will be lost
         */
        G.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                topPermission = true;
            }
        }, 2000);
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

        if ((force || (webSocketClient == null || !webSocketClient.isOpen()))) { // topPermission
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    //new connection start
                    //if (reconnectCount < 10) {
                    //    Log.e("DDD", "MINIMUM");
                    //    allowReconnectAgain = Config.ALLOW_RECONNECT_AGAIN_MINIMUM;
                    //} else if (reconnectCount < 20) {
                    //    Log.e("DDD", "NORMAL");
                    //    allowReconnectAgain = Config.ALLOW_RECONNECT_AGAIN_NORMAL;
                    //} else {
                    //    Log.e("DDD", "MAXIMUM");
                    //    allowReconnectAgain = Config.ALLOW_RECONNECT_AGAIN_MAXIMUM;
                    //}
                    //new connection end

                    if (timeDifference(latestConnectionTryTiming) && connectionState != WebSocketState.CONNECTING && (connectionState != WebSocketState.OPEN || (HelperTimeOut.timeoutChecking(0, latestConnectionOpenTime, Config.CONNECTION_OPEN_TIME_OUT)))) {
                        //new connection start
                        //reconnectCount++;
                        if (reconnectQueueLimitation > 0) {
                            reconnectQueueLimitation--;
                        }
                        //new connection end

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
                                //if (reconnectQueueLimitation <= 1) {
                                checkSocketConnection();
                                //}
                            }
                        }
                    } else {
                        //new connection start
                        if (reconnectQueueLimitation < Config.TRY_CONNECTION_COUNT) {
                            reconnectQueueLimitation++;
                            //new connection end
                            Log.i("DDD", "try for connect");
                            allowForReconnecting = true;
                            waitingForReconnecting = false;
                            reconnect(false);
                        } else {
                            reconnectQueueLimitation--;
                            Log.i("DDD", "queue is complete now! " + reconnectQueueLimitation);
                        }

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

        if (difference >= ALLOW_RECONNECT_AGAIN_NORMAL) {
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
                        G.allowForConnect = false;
                        WebSocket webSocket = WebSocketClient.getInstance();
                        if (webSocket != null) {
                            webSocket.disconnect();
                        }
                        //TODO [Saeed Mozaffari] [2016-09-06 12:31 PM] - go to upgrade page
                    }
                }
            }
        });
        thread.start();
    }
}

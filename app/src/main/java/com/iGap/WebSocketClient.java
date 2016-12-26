package com.iGap;

import android.util.Log;

import com.iGap.helper.HelperConnectionState;
import com.iGap.helper.HelperSetAction;
import com.iGap.response.HandleResponse;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

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

    /**
     * add webSocketConnection listeners and try for connect
     *
     * @return WebSocket
     */

    private static synchronized WebSocket createSocketConnection() {
        WebSocket websocketFactory = null;
        try {
            websocketFactory = new WebSocketFactory().createSocket(Config.urlWebsocket);
            websocketFactory.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.i("SOC_WebSocket", "onConnected");
                    if (G.isSecure) {
                        webSocketClient.disconnect();
                    } else {
                        G.socketConnection = true;
                        HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                        checkFirstResponse();
                    }
                    super.onConnected(websocket, headers);
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, final byte[] binary) throws Exception {
                    Log.i("SOC_WebSocket", "WebSocketClient binary : " + binary);
                    new HandleResponse(binary).run();
                    super.onBinaryMessage(websocket, binary);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    Log.i("SOC_WebSocket", "onError");
                    reconnect();
                    super.onError(websocket, cause);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.i("SOC_WebSocketD", "onDisconnected");
                    reconnect();
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.i("SOC_WebSocket", "onConnectError");
                    reconnect();
                    super.onConnectError(websocket, exception);
                }

                @Override
                public void onMessageError(WebSocket websocket, WebSocketException cause,
                                           List<WebSocketFrame> frames) throws Exception {
                    Log.i("SOC_WebSocket", "onMessageError");
                    super.onMessageError(websocket, cause, frames);
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
                            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                            finalWs.connect();
                        }
                    } catch (WebSocketException e) {
                        //reconnect();
                        Log.i("SOC_WebSocket", "WebSocketException : " + e);
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
            return webSocketClient = createSocketConnection();
        } else {
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
                    reconnect();
                }
            }
        }, Config.INSTANCE_SUCCESSFULLY_CHECKING);
    }

    /**
     * clear securing state and reconnect to server
     */

    private static void reconnect() {
        HelperSetAction.clearAllActions();
        HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
        if (allowForReconnecting) {//&& (webSocketClient == null || !webSocketClient.isOpen())
            allowForReconnecting = false;
            if (G.allowForConnect) {
                latestConnectionTryTiming = System.currentTimeMillis();
                waitingForReconnecting = false;
                resetWebsocketInfo();
                WebSocketClient.getInstance();
                checkSocketConnection();
            }
        }
    }

    /**
     * check if socket connected or from last try connecting over the past ten seconds and finally
     * reconnect or run this method again
     */

    private static void checkSocketConnection() {

        if (webSocketClient == null || !webSocketClient.isOpen()) {
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (timeDifference(latestConnectionTryTiming)) {
                        allowForReconnecting = true;
                        waitingForReconnecting = false;
                        reconnect();
                    } else {
                        checkSocketConnection();
                    }
                }
            }, Config.REPEAT_CONNECTION_CHECKING);
        } else {
            /*
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
        reconnect();
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
        G.isSecure = false;
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
                                    WebSocketClient.getInstance().sendText("i need 30001");
                                }
                            }
                        });
                    } else {
                        G.allowForConnect = false;
                        WebSocketClient.getInstance().disconnect();
                        //TODO [Saeed Mozaffari] [2016-09-06 12:31 PM] - go to upgrade page
                    }
                }
            }
        });
        thread.start();
    }
}

package com.iGap;

import android.util.Log;

import com.iGap.helper.HelperConnectionState;
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
 * for create webSocketConnection
 */
public class WebSocketClient {

    private static WebSocket webSocketClient;
    private static int count = 0;
    public static boolean allowForReconnecting = true;
    public static boolean waitingForReconnecting = false;
    private static long latestConnectionTryTiming;

    private static synchronized WebSocket createSocketConnection() {
        WebSocket websocketFactory = null;
        try {
            websocketFactory = new WebSocketFactory().createSocket(Config.urlWebsocket);
            websocketFactory.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.i("SOC_WebSocket", "onConnected");
                    G.socketConnection = true;
                    HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                    checkFirstResponse();
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
                    Log.i("SOC_WebSocket", "onDisconnected");
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
                public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
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

    public static WebSocket getInstance() { //TODO [Saeed Mozaffari] [2016-10-03 12:12 PM] - checking this code for waitingForReconnecting(boolean) that can this boolean make problem or no
        if (!waitingForReconnecting && (webSocketClient == null || !webSocketClient.isOpen())) {
            waitingForReconnecting = true;
            Log.i("JJJ", "getInstance");
            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
            return webSocketClient = createSocketConnection();
        } else {
            return webSocketClient;
        }
    }


    public static WebSocket disconnect() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.disconnect();
        }
        return webSocketClient;
    }

    /**
     * clear securing state and reconnect to server
     */

    private static void reconnect() {
        HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
        if (allowForReconnecting) {//&& (webSocketClient == null || !webSocketClient.isOpen())
            allowForReconnecting = false;
            if (G.allowForConnect) {
                Log.i("JJJ", "reconnect");
                latestConnectionTryTiming = System.currentTimeMillis();
                waitingForReconnecting = false;
                resetWebsocketInfo();
                WebSocketClient.getInstance();
                checkSocketConnection();
            }
        }
    }

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
            }, 1000);
        } else {
            /*
             when connecting was successful and user login ,
             in user login response will be change
             allowForReconnecting and waitingForReconnecting
             for allow that reconnecting later if need
             */
            Log.i("JJJ", "Don't Need For Reconnecting");
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

        if (difference >= 10000) {
            return true;
        }

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

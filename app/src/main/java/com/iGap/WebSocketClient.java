package com.iGap;

import android.util.Log;

import com.iGap.helper.HelperCheckInternetConnection;
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
    private static WebSocket webSocketFileUpload;
    private static int count = 0;

    private static WebSocket createFileUploadConnection(String fileSocketAddress) {
        WebSocket webSocketFile = null;

        try {
            webSocketFile = new WebSocketFactory().createSocket(fileSocketAddress);
            webSocketFile.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    super.onConnected(websocket, headers);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    super.onConnectError(websocket, exception);
                }

                @Override
                public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {
                    super.onBinaryMessage(websocket, binary);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    super.onError(websocket, cause);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);

                    webSocketFileUpload = null;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static WebSocket createSocketConnection() {
        WebSocket websocketFactory = null;
        try {
            Log.i("SOC_WebSocket", "createSocket Connection : " + Config.urlWebsocket);
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
                    //G.responseCount++;
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
            Log.i("SOC_WebSocket", "iGap IOException iGap : " + e);
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
                        reconnect();
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

    public static WebSocket getInstance() { //TODO [Saeed Mozaffari] [2016-09-05 11:16 AM] - avoid multiple instance , hint : synchronize
        if (webSocketClient == null) {
            HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
            webSocketClient = createSocketConnection();
        } else {
            if (!webSocketClient.isOpen()) {
                HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
                webSocketClient = createSocketConnection();
            } else {
                return webSocketClient;
            }
        }
        return webSocketClient;
    }

    public static WebSocket getFileUploadSocketInstance(String fileSocketAddress) {

        if (webSocketFileUpload == null) {
            webSocketFileUpload = createFileUploadConnection(fileSocketAddress);
        } else {
            if (!webSocketFileUpload.isOpen()) {
                webSocketFileUpload = createFileUploadConnection(fileSocketAddress);
            }
        }
        return webSocketFileUpload;
    }


    /**
     * clear securing state and reconnect to server
     */

    private static void reconnect() {
        HelperConnectionState.connectionState(Config.ConnectionState.CONNECTING);
        if (G.allowForConnect && HelperCheckInternetConnection.hasNetwork()) {
            resetWebsocketInfo();
            G.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    WebSocketClient.getInstance();
                }
            }, 1000);
        }
    }

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

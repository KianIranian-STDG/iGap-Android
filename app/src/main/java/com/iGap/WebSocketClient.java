package com.iGap;

import android.util.Log;

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

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    Log.i("SOC_WebSocket", "onTextMessage : " + text);
                    super.onTextMessage(websocket, text);
                }

                @Override
                public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                    Log.i("SOC_WebSocket", "onTextMessageError");
                    super.onTextMessageError(websocket, cause, data);
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
                        Log.i("SOC_WebSocket", "Connecting");
                        if (finalWs != null) {
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
            webSocketClient = createSocketConnection();
        } else {
            if (!webSocketClient.isOpen()) {
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

        if (G.allowForConnect) {
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
        webSocketClient = null;
        G.isSecure = false;
        G.userLogin = false;
        G.socketConnectingOrConnected = false;
    }

    /**
     * if not secure yet send fake message to server for securing preparation
     */

    private static void checkFirstResponse() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                int count = 0;
                while (G.symmetricKey == null) {

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
                                Log.i("SOC", "I need 30001");
                                if (G.symmetricKey == null) {
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

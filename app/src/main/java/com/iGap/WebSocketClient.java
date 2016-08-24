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

    private static WebSocket createSocketConnection() {
        WebSocket websocketFactory = null;
        try {
            Log.i("SOC", "createSocket Connection");
            websocketFactory = new WebSocketFactory().createSocket(Config.urlWebsocket);

            websocketFactory.addListener(new WebSocketAdapter() {

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.i("SOC", "onConnected");
                    //checkFirstResponse();
                    super.onConnected(websocket, headers);
                }


                @Override
                public void onBinaryMessage(WebSocket websocket, final byte[] binary) throws Exception {
                    Log.i("SOC", "WebSocketClient binary : " + binary);
                    //G.responseCount++;
                    new HandleResponse(binary).run();

                    super.onBinaryMessage(websocket, binary);
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                    Log.i("SOC", "onError");
                    super.onError(websocket, cause);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.i("SOC", "onDisconnected");

                    reconnect();
                    super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.i("SOC", "onConnectError");
                    super.onConnectError(websocket, exception);
                }

                @Override
                public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                    Log.i("SOC", "onMessageError");
                    super.onMessageError(websocket, cause, frames);
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) throws Exception {
                    Log.i("SOC", "onTextMessage : " + text);
                    super.onTextMessage(websocket, text);
                }

                @Override
                public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                    Log.i("SOC", "onTextMessageError");
                    super.onTextMessageError(websocket, cause, data);
                }


            });
        } catch (IOException e) {
            Log.i("SOC", "iGap IOException iGap : " + e);
            e.printStackTrace();
        }

        final WebSocket finalWs = websocketFactory;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (G.allowForConnect) {
                    try {
                        Log.i("SOC", "Connecting");
                        if (finalWs != null) {
                            finalWs.connect();
                        }
                    } catch (WebSocketException e) {
                        Log.i("SOC", "WebSocketException : " + e);
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

        if (webSocketClient == null) {
            Log.i("SOC", "Instance is null");
            webSocketClient = createSocketConnection();
        } else {

            Log.i("SOC", "instance is not null");
            if (!webSocketClient.isOpen()) {
                Log.i("SOC", "instance need createSocketConnection again");
                webSocketClient = createSocketConnection();
            }
        }
        return webSocketClient;
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
        G.isSecure = false;
//        G.symmetricKey = null;
//        G.ivSize = 0;
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
                                if (G.symmetricKey == null) {
                                    Log.i("SOC", "I need 30001");
                                    WebSocketClient.getInstance().sendText("i need 30001");
                                }
                            }
                        });
                    } else {
//                        WebSocketClient.getInstance().disconnect();
//                        G.allowForConnect = false;
                    }
                }
            }
        });
        thread.start();
    }
}

package com.iGap.helper;

/**
 * Helper Class for preparation class name for using dynamic create class
 */
public class HelperClassNamePreparation {

    public static String preparationProtoClassName(String className) {

        String protoClassName;
        String packageName = "com.iGap.proto.";
        className = className.replace(".", "$"); // example : ProtoConnectionSecuring.ConnectionSymmetricKeyResponse =>
        protoClassName = packageName + className; // example : ChatClearMessage => com.iGap.proto.ProtoConnectionSecuring$ConnectionSymmetricKeyResponse

        return protoClassName;
    }

    public static String preparationResponseClassName(String className) {

        String packageName = "com.iGap.response.";
        String responseClass = className.split("\\.")[1]; // example : ProtoConnectionSecuring.ConnectionSymmetricKeyResponse => ConnectionSymmetricKeyResponse
        String responseClassName = packageName + responseClass; // example : com.iGap.response.ConnectionSymmetricKeyResponse

        return responseClassName;
    }

    public static String preparationRequestClassName(String className) {

        String requestClassName;
        String packageName = "com.iGap.request.";
        String firstWordClassName = "Request";

        className = className.replace(".", ""); // example : Chat.Clear.Message => ChatClearMessage
        requestClassName = packageName + firstWordClassName + className; // example : ChatClearMessage => com.iGap.request.RequestChatClearMessage

        return requestClassName;
    }

}

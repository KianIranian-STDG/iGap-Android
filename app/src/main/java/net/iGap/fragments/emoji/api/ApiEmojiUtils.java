package net.iGap.fragments.emoji.api;

/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */
public class ApiEmojiUtils {

    private ApiEmojiUtils() {
    }

    public static final String BASE_URL = "https://sticker.igap.net/";
//    public static final String BASE_URL = "https://postman-echo.com/";

    public static APIEmojiService getAPIService() {
        return RetrofitClientEmoji.getClient(BASE_URL).create(APIEmojiService.class);
    }
}

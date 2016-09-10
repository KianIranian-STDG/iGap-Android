package com.iGap.module;

/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.text.SimpleDateFormat;
import java.util.Locale;

public final class TimeUtils {
    private TimeUtils() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation");
    }

    /**
     * convert unix time to local time
     *
     * @param unixTime unix time is 13 characters (milliseconds), if you passed seconds, remember to
     *                 multiply by 1000L
     * @param format   String format
     * @return String formatted time in local
     */
    public static String toLocal(long unixTime, String format) {
        return new SimpleDateFormat(format, Locale.US).format(unixTime);
    }
}
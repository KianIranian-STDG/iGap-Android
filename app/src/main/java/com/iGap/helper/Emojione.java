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

package com.iGap.helper;

import android.os.Build;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Emojione {
    private static final HashMap<String, String> _shortNameToUnicode = new HashMap<>();
    private static final Pattern SHORTNAME_PATTERN = Pattern.compile(":([-+\\w]+):");

    static {
        _shortNameToUnicode.put("100", new String(new int[]{0x1f4af}, 0, 1));
        _shortNameToUnicode.put("1234", new String(new int[]{0x1f522}, 0, 1));
        _shortNameToUnicode.put("grinning", new String(new int[]{0x1f600}, 0, 1));
        _shortNameToUnicode.put("grimacing", new String(new int[]{0x1f62c}, 0, 1));
        _shortNameToUnicode.put("grin", new String(new int[]{0x1f601}, 0, 1));
        _shortNameToUnicode.put("joy", new String(new int[]{0x1f602}, 0, 1));
        _shortNameToUnicode.put("smiley", new String(new int[]{0x1f603}, 0, 1));
        _shortNameToUnicode.put("smile", new String(new int[]{0x1f604}, 0, 1));
        _shortNameToUnicode.put("sweat_smile", new String(new int[]{0x1f605}, 0, 1));
        _shortNameToUnicode.put("laughing", new String(new int[]{0x1f606}, 0, 1));
        _shortNameToUnicode.put("innocent", new String(new int[]{0x1f607}, 0, 1));
        _shortNameToUnicode.put("wink", new String(new int[]{0x1f609}, 0, 1));
        _shortNameToUnicode.put("blush", new String(new int[]{0x1f60a}, 0, 1));
        _shortNameToUnicode.put("slight_smile", new String(new int[]{0x1f642}, 0, 1));
        _shortNameToUnicode.put("upside_down", new String(new int[]{0x1f643}, 0, 1));
        _shortNameToUnicode.put("relaxed", new String(new int[]{0x263a}, 0, 1));
        _shortNameToUnicode.put("yum", new String(new int[]{0x1f60b}, 0, 1));
        _shortNameToUnicode.put("relieved", new String(new int[]{0x1f60c}, 0, 1));
        _shortNameToUnicode.put("heart_eyes", new String(new int[]{0x1f60d}, 0, 1));
        _shortNameToUnicode.put("kissing_heart", new String(new int[]{0x1f618}, 0, 1));
        _shortNameToUnicode.put("kissing", new String(new int[]{0x1f617}, 0, 1));
        _shortNameToUnicode.put("kissing_smiling_eyes", new String(new int[]{0x1f619}, 0, 1));
        _shortNameToUnicode.put("kissing_closed_eyes", new String(new int[]{0x1f61a}, 0, 1));
        _shortNameToUnicode.put("stuck_out_tongue_winking_eye",
                new String(new int[]{0x1f61c}, 0, 1));
        _shortNameToUnicode.put("stuck_out_tongue_closed_eyes",
                new String(new int[]{0x1f61d}, 0, 1));
        _shortNameToUnicode.put("stuck_out_tongue", new String(new int[]{0x1f61b}, 0, 1));
        _shortNameToUnicode.put("money_mouth", new String(new int[]{0x1f911}, 0, 1));
        _shortNameToUnicode.put("nerd", new String(new int[]{0x1f913}, 0, 1));
        _shortNameToUnicode.put("sunglasses", new String(new int[]{0x1f60e}, 0, 1));
        _shortNameToUnicode.put("hugging", new String(new int[]{0x1f917}, 0, 1));
        _shortNameToUnicode.put("smirk", new String(new int[]{0x1f60f}, 0, 1));
        _shortNameToUnicode.put("no_mouth", new String(new int[]{0x1f636}, 0, 1));
        _shortNameToUnicode.put("neutral_face", new String(new int[]{0x1f610}, 0, 1));
        _shortNameToUnicode.put("expressionless", new String(new int[]{0x1f611}, 0, 1));
        _shortNameToUnicode.put("unamused", new String(new int[]{0x1f612}, 0, 1));
        _shortNameToUnicode.put("rolling_eyes", new String(new int[]{0x1f644}, 0, 1));
        _shortNameToUnicode.put("thinking", new String(new int[]{0x1f914}, 0, 1));
        _shortNameToUnicode.put("flushed", new String(new int[]{0x1f633}, 0, 1));
        _shortNameToUnicode.put("disappointed", new String(new int[]{0x1f61e}, 0, 1));
        _shortNameToUnicode.put("worried", new String(new int[]{0x1f61f}, 0, 1));
        _shortNameToUnicode.put("angry", new String(new int[]{0x1f620}, 0, 1));
        _shortNameToUnicode.put("rage", new String(new int[]{0x1f621}, 0, 1));
        _shortNameToUnicode.put("pensive", new String(new int[]{0x1f614}, 0, 1));
        _shortNameToUnicode.put("confused", new String(new int[]{0x1f615}, 0, 1));
        _shortNameToUnicode.put("slight_frown", new String(new int[]{0x1f641}, 0, 1));
        _shortNameToUnicode.put("frowning2", new String(new int[]{0x2639}, 0, 1));
        _shortNameToUnicode.put("persevere", new String(new int[]{0x1f623}, 0, 1));
        _shortNameToUnicode.put("confounded", new String(new int[]{0x1f616}, 0, 1));
        _shortNameToUnicode.put("tired_face", new String(new int[]{0x1f62b}, 0, 1));
        _shortNameToUnicode.put("weary", new String(new int[]{0x1f629}, 0, 1));
        _shortNameToUnicode.put("triumph", new String(new int[]{0x1f624}, 0, 1));
        _shortNameToUnicode.put("open_mouth", new String(new int[]{0x1f62e}, 0, 1));
        _shortNameToUnicode.put("scream", new String(new int[]{0x1f631}, 0, 1));
        _shortNameToUnicode.put("fearful", new String(new int[]{0x1f628}, 0, 1));
        _shortNameToUnicode.put("cold_sweat", new String(new int[]{0x1f630}, 0, 1));
        _shortNameToUnicode.put("hushed", new String(new int[]{0x1f62f}, 0, 1));
        _shortNameToUnicode.put("frowning", new String(new int[]{0x1f626}, 0, 1));
        _shortNameToUnicode.put("anguished", new String(new int[]{0x1f627}, 0, 1));
        _shortNameToUnicode.put("cry", new String(new int[]{0x1f622}, 0, 1));
        _shortNameToUnicode.put("disappointed_relieved", new String(new int[]{0x1f625}, 0, 1));
        _shortNameToUnicode.put("sleepy", new String(new int[]{0x1f62a}, 0, 1));
        _shortNameToUnicode.put("sweat", new String(new int[]{0x1f613}, 0, 1));
        _shortNameToUnicode.put("sob", new String(new int[]{0x1f62d}, 0, 1));
        _shortNameToUnicode.put("dizzy_face", new String(new int[]{0x1f635}, 0, 1));
        _shortNameToUnicode.put("astonished", new String(new int[]{0x1f632}, 0, 1));
        _shortNameToUnicode.put("zipper_mouth", new String(new int[]{0x1f910}, 0, 1));
        _shortNameToUnicode.put("mask", new String(new int[]{0x1f637}, 0, 1));
        _shortNameToUnicode.put("thermometer_face", new String(new int[]{0x1f912}, 0, 1));
        _shortNameToUnicode.put("head_bandage", new String(new int[]{0x1f915}, 0, 1));
        _shortNameToUnicode.put("sleeping", new String(new int[]{0x1f634}, 0, 1));
        _shortNameToUnicode.put("zzz", new String(new int[]{0x1f4a4}, 0, 1));
        _shortNameToUnicode.put("poop", new String(new int[]{0x1f4a9}, 0, 1));
        _shortNameToUnicode.put("smiling_imp", new String(new int[]{0x1f608}, 0, 1));
        _shortNameToUnicode.put("imp", new String(new int[]{0x1f47f}, 0, 1));
        _shortNameToUnicode.put("japanese_ogre", new String(new int[]{0x1f479}, 0, 1));
        _shortNameToUnicode.put("japanese_goblin", new String(new int[]{0x1f47a}, 0, 1));
        _shortNameToUnicode.put("skull", new String(new int[]{0x1f480}, 0, 1));
        _shortNameToUnicode.put("ghost", new String(new int[]{0x1f47b}, 0, 1));
        _shortNameToUnicode.put("alien", new String(new int[]{0x1f47d}, 0, 1));
        _shortNameToUnicode.put("robot", new String(new int[]{0x1f916}, 0, 1));
        _shortNameToUnicode.put("smiley_cat", new String(new int[]{0x1f63a}, 0, 1));
        _shortNameToUnicode.put("smile_cat", new String(new int[]{0x1f638}, 0, 1));
        _shortNameToUnicode.put("joy_cat", new String(new int[]{0x1f639}, 0, 1));
        _shortNameToUnicode.put("heart_eyes_cat", new String(new int[]{0x1f63b}, 0, 1));
        _shortNameToUnicode.put("smirk_cat", new String(new int[]{0x1f63c}, 0, 1));
        _shortNameToUnicode.put("kissing_cat", new String(new int[]{0x1f63d}, 0, 1));
        _shortNameToUnicode.put("scream_cat", new String(new int[]{0x1f640}, 0, 1));
        _shortNameToUnicode.put("crying_cat_face", new String(new int[]{0x1f63f}, 0, 1));
        _shortNameToUnicode.put("pouting_cat", new String(new int[]{0x1f63e}, 0, 1));
        _shortNameToUnicode.put("raised_hands", new String(new int[]{0x1f64c}, 0, 1));
        _shortNameToUnicode.put("clap", new String(new int[]{0x1f44f}, 0, 1));
        _shortNameToUnicode.put("wave", new String(new int[]{0x1f44b}, 0, 1));
        _shortNameToUnicode.put("thumbsup", new String(new int[]{0x1f44d}, 0, 1));
        _shortNameToUnicode.put("thumbsdown", new String(new int[]{0x1f44e}, 0, 1));
        _shortNameToUnicode.put("punch", new String(new int[]{0x1f44a}, 0, 1));
        _shortNameToUnicode.put("fist", new String(new int[]{0x270a}, 0, 1));
        _shortNameToUnicode.put("v", new String(new int[]{0x270c}, 0, 1));
        _shortNameToUnicode.put("ok_hand", new String(new int[]{0x1f44c}, 0, 1));
        _shortNameToUnicode.put("raised_hand", new String(new int[]{0x270b}, 0, 1));
        _shortNameToUnicode.put("open_hands", new String(new int[]{0x1f450}, 0, 1));
        _shortNameToUnicode.put("muscle", new String(new int[]{0x1f4aa}, 0, 1));
        _shortNameToUnicode.put("pray", new String(new int[]{0x1f64f}, 0, 1));
        _shortNameToUnicode.put("point_up", new String(new int[]{0x261d}, 0, 1));
        _shortNameToUnicode.put("point_up_2", new String(new int[]{0x1f446}, 0, 1));
        _shortNameToUnicode.put("point_down", new String(new int[]{0x1f447}, 0, 1));
        _shortNameToUnicode.put("point_left", new String(new int[]{0x1f448}, 0, 1));
        _shortNameToUnicode.put("point_right", new String(new int[]{0x1f449}, 0, 1));
        _shortNameToUnicode.put("middle_finger", new String(new int[]{0x1f595}, 0, 1));
        _shortNameToUnicode.put("hand_splayed", new String(new int[]{0x1f590}, 0, 1));
        _shortNameToUnicode.put("metal", new String(new int[]{0x1f918}, 0, 1));
        _shortNameToUnicode.put("vulcan", new String(new int[]{0x1f596}, 0, 1));
        _shortNameToUnicode.put("writing_hand", new String(new int[]{0x270d}, 0, 1));
        _shortNameToUnicode.put("nail_care", new String(new int[]{0x1f485}, 0, 1));
        _shortNameToUnicode.put("lips", new String(new int[]{0x1f444}, 0, 1));
        _shortNameToUnicode.put("tongue", new String(new int[]{0x1f445}, 0, 1));
        _shortNameToUnicode.put("ear", new String(new int[]{0x1f442}, 0, 1));
        _shortNameToUnicode.put("nose", new String(new int[]{0x1f443}, 0, 1));
        _shortNameToUnicode.put("eye", new String(new int[]{0x1f441}, 0, 1));
        _shortNameToUnicode.put("eyes", new String(new int[]{0x1f440}, 0, 1));
        _shortNameToUnicode.put("bust_in_silhouette", new String(new int[]{0x1f464}, 0, 1));
        _shortNameToUnicode.put("busts_in_silhouette", new String(new int[]{0x1f465}, 0, 1));
        _shortNameToUnicode.put("speaking_head", new String(new int[]{0x1f5e3}, 0, 1));
        _shortNameToUnicode.put("baby", new String(new int[]{0x1f476}, 0, 1));
        _shortNameToUnicode.put("boy", new String(new int[]{0x1f466}, 0, 1));
        _shortNameToUnicode.put("girl", new String(new int[]{0x1f467}, 0, 1));
        _shortNameToUnicode.put("man", new String(new int[]{0x1f468}, 0, 1));
        _shortNameToUnicode.put("woman", new String(new int[]{0x1f469}, 0, 1));
        _shortNameToUnicode.put("person_with_blond_hair", new String(new int[]{0x1f471}, 0, 1));
        _shortNameToUnicode.put("older_man", new String(new int[]{0x1f474}, 0, 1));
        _shortNameToUnicode.put("older_woman", new String(new int[]{0x1f475}, 0, 1));
        _shortNameToUnicode.put("man_with_gua_pi_mao", new String(new int[]{0x1f472}, 0, 1));
        _shortNameToUnicode.put("man_with_turban", new String(new int[]{0x1f473}, 0, 1));
        _shortNameToUnicode.put("cop", new String(new int[]{0x1f46e}, 0, 1));
        _shortNameToUnicode.put("construction_worker", new String(new int[]{0x1f477}, 0, 1));
        _shortNameToUnicode.put("guardsman", new String(new int[]{0x1f482}, 0, 1));
        _shortNameToUnicode.put("spy", new String(new int[]{0x1f575}, 0, 1));
        _shortNameToUnicode.put("santa", new String(new int[]{0x1f385}, 0, 1));
        _shortNameToUnicode.put("angel", new String(new int[]{0x1f47c}, 0, 1));
        _shortNameToUnicode.put("princess", new String(new int[]{0x1f478}, 0, 1));
        _shortNameToUnicode.put("bride_with_veil", new String(new int[]{0x1f470}, 0, 1));
        _shortNameToUnicode.put("walking", new String(new int[]{0x1f6b6}, 0, 1));
        _shortNameToUnicode.put("runner", new String(new int[]{0x1f3c3}, 0, 1));
        _shortNameToUnicode.put("dancer", new String(new int[]{0x1f483}, 0, 1));
        _shortNameToUnicode.put("dancers", new String(new int[]{0x1f46f}, 0, 1));
        _shortNameToUnicode.put("couple", new String(new int[]{0x1f46b}, 0, 1));
        _shortNameToUnicode.put("two_men_holding_hands", new String(new int[]{0x1f46c}, 0, 1));
        _shortNameToUnicode.put("two_women_holding_hands", new String(new int[]{0x1f46d}, 0, 1));
        _shortNameToUnicode.put("bow", new String(new int[]{0x1f647}, 0, 1));
        _shortNameToUnicode.put("information_desk_person", new String(new int[]{0x1f481}, 0, 1));
        _shortNameToUnicode.put("no_good", new String(new int[]{0x1f645}, 0, 1));
        _shortNameToUnicode.put("ok_woman", new String(new int[]{0x1f646}, 0, 1));
        _shortNameToUnicode.put("raising_hand", new String(new int[]{0x1f64b}, 0, 1));
        _shortNameToUnicode.put("person_with_pouting_face",
                new String(new int[]{0x1f64e}, 0, 1));
        _shortNameToUnicode.put("person_frowning", new String(new int[]{0x1f64d}, 0, 1));
        _shortNameToUnicode.put("haircut", new String(new int[]{0x1f487}, 0, 1));
        _shortNameToUnicode.put("massage", new String(new int[]{0x1f486}, 0, 1));
        _shortNameToUnicode.put("couple_with_heart", new String(new int[]{0x1f491}, 0, 1));
        _shortNameToUnicode.put("couple_ww",
                new String(new int[]{0x1f469, 0x2764, 0x1f469}, 0, 3));
        _shortNameToUnicode.put("couple_mm",
                new String(new int[]{0x1f468, 0x2764, 0x1f468}, 0, 3));
        _shortNameToUnicode.put("couplekiss", new String(new int[]{0x1f48f}, 0, 1));
        _shortNameToUnicode.put("kiss_ww",
                new String(new int[]{0x1f469, 0x2764, 0x1f48b, 0x1f469}, 0, 4));
        _shortNameToUnicode.put("kiss_mm",
                new String(new int[]{0x1f468, 0x2764, 0x1f48b, 0x1f468}, 0, 4));
        _shortNameToUnicode.put("family", new String(new int[]{0x1f46a}, 0, 1));
        _shortNameToUnicode.put("family_mwg",
                new String(new int[]{0x1f468, 0x1f469, 0x1f467}, 0, 3));
        _shortNameToUnicode.put("family_mwgb",
                new String(new int[]{0x1f468, 0x1f469, 0x1f467, 0x1f466}, 0, 4));
        _shortNameToUnicode.put("family_mwbb",
                new String(new int[]{0x1f468, 0x1f469, 0x1f466, 0x1f466}, 0, 4));
        _shortNameToUnicode.put("family_mwgg",
                new String(new int[]{0x1f468, 0x1f469, 0x1f467, 0x1f467}, 0, 4));
        _shortNameToUnicode.put("family_wwb",
                new String(new int[]{0x1f469, 0x1f469, 0x1f466}, 0, 3));
        _shortNameToUnicode.put("family_wwg",
                new String(new int[]{0x1f469, 0x1f469, 0x1f467}, 0, 3));
        _shortNameToUnicode.put("family_wwgb",
                new String(new int[]{0x1f469, 0x1f469, 0x1f467, 0x1f466}, 0, 4));
        _shortNameToUnicode.put("family_wwbb",
                new String(new int[]{0x1f469, 0x1f469, 0x1f466, 0x1f466}, 0, 4));
        _shortNameToUnicode.put("family_wwgg",
                new String(new int[]{0x1f469, 0x1f469, 0x1f467, 0x1f467}, 0, 4));
        _shortNameToUnicode.put("family_mmb",
                new String(new int[]{0x1f468, 0x1f468, 0x1f466}, 0, 3));
        _shortNameToUnicode.put("family_mmg",
                new String(new int[]{0x1f468, 0x1f468, 0x1f467}, 0, 3));
        _shortNameToUnicode.put("family_mmgb",
                new String(new int[]{0x1f468, 0x1f468, 0x1f467, 0x1f466}, 0, 4));
        _shortNameToUnicode.put("family_mmbb",
                new String(new int[]{0x1f468, 0x1f468, 0x1f466, 0x1f466}, 0, 4));
        _shortNameToUnicode.put("family_mmgg",
                new String(new int[]{0x1f468, 0x1f468, 0x1f467, 0x1f467}, 0, 4));
        _shortNameToUnicode.put("womans_clothes", new String(new int[]{0x1f45a}, 0, 1));
        _shortNameToUnicode.put("shirt", new String(new int[]{0x1f455}, 0, 1));
        _shortNameToUnicode.put("jeans", new String(new int[]{0x1f456}, 0, 1));
        _shortNameToUnicode.put("necktie", new String(new int[]{0x1f454}, 0, 1));
        _shortNameToUnicode.put("dress", new String(new int[]{0x1f457}, 0, 1));
        _shortNameToUnicode.put("bikini", new String(new int[]{0x1f459}, 0, 1));
        _shortNameToUnicode.put("kimono", new String(new int[]{0x1f458}, 0, 1));
        _shortNameToUnicode.put("lipstick", new String(new int[]{0x1f484}, 0, 1));
        _shortNameToUnicode.put("kiss", new String(new int[]{0x1f48b}, 0, 1));
        _shortNameToUnicode.put("footprints", new String(new int[]{0x1f463}, 0, 1));
        _shortNameToUnicode.put("high_heel", new String(new int[]{0x1f460}, 0, 1));
        _shortNameToUnicode.put("sandal", new String(new int[]{0x1f461}, 0, 1));
        _shortNameToUnicode.put("boot", new String(new int[]{0x1f462}, 0, 1));
        _shortNameToUnicode.put("mans_shoe", new String(new int[]{0x1f45e}, 0, 1));
        _shortNameToUnicode.put("athletic_shoe", new String(new int[]{0x1f45f}, 0, 1));
        _shortNameToUnicode.put("womans_hat", new String(new int[]{0x1f452}, 0, 1));
        _shortNameToUnicode.put("tophat", new String(new int[]{0x1f3a9}, 0, 1));
        _shortNameToUnicode.put("helmet_with_cross", new String(new int[]{0x26d1}, 0, 1));
        _shortNameToUnicode.put("mortar_board", new String(new int[]{0x1f393}, 0, 1));
        _shortNameToUnicode.put("crown", new String(new int[]{0x1f451}, 0, 1));
        _shortNameToUnicode.put("school_satchel", new String(new int[]{0x1f392}, 0, 1));
        _shortNameToUnicode.put("pouch", new String(new int[]{0x1f45d}, 0, 1));
        _shortNameToUnicode.put("purse", new String(new int[]{0x1f45b}, 0, 1));
        _shortNameToUnicode.put("handbag", new String(new int[]{0x1f45c}, 0, 1));
        _shortNameToUnicode.put("briefcase", new String(new int[]{0x1f4bc}, 0, 1));
        _shortNameToUnicode.put("eyeglasses", new String(new int[]{0x1f453}, 0, 1));
        _shortNameToUnicode.put("dark_sunglasses", new String(new int[]{0x1f576}, 0, 1));
        _shortNameToUnicode.put("ring", new String(new int[]{0x1f48d}, 0, 1));
        _shortNameToUnicode.put("closed_umbrella", new String(new int[]{0x1f302}, 0, 1));
        _shortNameToUnicode.put("dog", new String(new int[]{0x1f436}, 0, 1));
        _shortNameToUnicode.put("cat", new String(new int[]{0x1f431}, 0, 1));
        _shortNameToUnicode.put("mouse", new String(new int[]{0x1f42d}, 0, 1));
        _shortNameToUnicode.put("hamster", new String(new int[]{0x1f439}, 0, 1));
        _shortNameToUnicode.put("rabbit", new String(new int[]{0x1f430}, 0, 1));
        _shortNameToUnicode.put("bear", new String(new int[]{0x1f43b}, 0, 1));
        _shortNameToUnicode.put("panda_face", new String(new int[]{0x1f43c}, 0, 1));
        _shortNameToUnicode.put("koala", new String(new int[]{0x1f428}, 0, 1));
        _shortNameToUnicode.put("tiger", new String(new int[]{0x1f42f}, 0, 1));
        _shortNameToUnicode.put("lion_face", new String(new int[]{0x1f981}, 0, 1));
        _shortNameToUnicode.put("cow", new String(new int[]{0x1f42e}, 0, 1));
        _shortNameToUnicode.put("pig", new String(new int[]{0x1f437}, 0, 1));
        _shortNameToUnicode.put("pig_nose", new String(new int[]{0x1f43d}, 0, 1));
        _shortNameToUnicode.put("frog", new String(new int[]{0x1f438}, 0, 1));
        _shortNameToUnicode.put("octopus", new String(new int[]{0x1f419}, 0, 1));
        _shortNameToUnicode.put("monkey_face", new String(new int[]{0x1f435}, 0, 1));
        _shortNameToUnicode.put("see_no_evil", new String(new int[]{0x1f648}, 0, 1));
        _shortNameToUnicode.put("hear_no_evil", new String(new int[]{0x1f649}, 0, 1));
        _shortNameToUnicode.put("speak_no_evil", new String(new int[]{0x1f64a}, 0, 1));
        _shortNameToUnicode.put("monkey", new String(new int[]{0x1f412}, 0, 1));
        _shortNameToUnicode.put("chicken", new String(new int[]{0x1f414}, 0, 1));
        _shortNameToUnicode.put("penguin", new String(new int[]{0x1f427}, 0, 1));
        _shortNameToUnicode.put("bird", new String(new int[]{0x1f426}, 0, 1));
        _shortNameToUnicode.put("baby_chick", new String(new int[]{0x1f424}, 0, 1));
        _shortNameToUnicode.put("hatching_chick", new String(new int[]{0x1f423}, 0, 1));
        _shortNameToUnicode.put("hatched_chick", new String(new int[]{0x1f425}, 0, 1));
        _shortNameToUnicode.put("wolf", new String(new int[]{0x1f43a}, 0, 1));
        _shortNameToUnicode.put("boar", new String(new int[]{0x1f417}, 0, 1));
        _shortNameToUnicode.put("horse", new String(new int[]{0x1f434}, 0, 1));
        _shortNameToUnicode.put("unicorn", new String(new int[]{0x1f984}, 0, 1));
        _shortNameToUnicode.put("bee", new String(new int[]{0x1f41d}, 0, 1));
        _shortNameToUnicode.put("bug", new String(new int[]{0x1f41b}, 0, 1));
        _shortNameToUnicode.put("snail", new String(new int[]{0x1f40c}, 0, 1));
        _shortNameToUnicode.put("beetle", new String(new int[]{0x1f41e}, 0, 1));
        _shortNameToUnicode.put("ant", new String(new int[]{0x1f41c}, 0, 1));
        _shortNameToUnicode.put("spider", new String(new int[]{0x1f577}, 0, 1));
        _shortNameToUnicode.put("scorpion", new String(new int[]{0x1f982}, 0, 1));
        _shortNameToUnicode.put("crab", new String(new int[]{0x1f980}, 0, 1));
        _shortNameToUnicode.put("snake", new String(new int[]{0x1f40d}, 0, 1));
        _shortNameToUnicode.put("turtle", new String(new int[]{0x1f422}, 0, 1));
        _shortNameToUnicode.put("tropical_fish", new String(new int[]{0x1f420}, 0, 1));
        _shortNameToUnicode.put("fish", new String(new int[]{0x1f41f}, 0, 1));
        _shortNameToUnicode.put("blowfish", new String(new int[]{0x1f421}, 0, 1));
        _shortNameToUnicode.put("dolphin", new String(new int[]{0x1f42c}, 0, 1));
        _shortNameToUnicode.put("whale", new String(new int[]{0x1f433}, 0, 1));
        _shortNameToUnicode.put("whale2", new String(new int[]{0x1f40b}, 0, 1));
        _shortNameToUnicode.put("crocodile", new String(new int[]{0x1f40a}, 0, 1));
        _shortNameToUnicode.put("leopard", new String(new int[]{0x1f406}, 0, 1));
        _shortNameToUnicode.put("tiger2", new String(new int[]{0x1f405}, 0, 1));
        _shortNameToUnicode.put("water_buffalo", new String(new int[]{0x1f403}, 0, 1));
        _shortNameToUnicode.put("ox", new String(new int[]{0x1f402}, 0, 1));
        _shortNameToUnicode.put("cow2", new String(new int[]{0x1f404}, 0, 1));
        _shortNameToUnicode.put("dromedary_camel", new String(new int[]{0x1f42a}, 0, 1));
        _shortNameToUnicode.put("camel", new String(new int[]{0x1f42b}, 0, 1));
        _shortNameToUnicode.put("elephant", new String(new int[]{0x1f418}, 0, 1));
        _shortNameToUnicode.put("goat", new String(new int[]{0x1f410}, 0, 1));
        _shortNameToUnicode.put("ram", new String(new int[]{0x1f40f}, 0, 1));
        _shortNameToUnicode.put("sheep", new String(new int[]{0x1f411}, 0, 1));
        _shortNameToUnicode.put("racehorse", new String(new int[]{0x1f40e}, 0, 1));
        _shortNameToUnicode.put("pig2", new String(new int[]{0x1f416}, 0, 1));
        _shortNameToUnicode.put("rat", new String(new int[]{0x1f400}, 0, 1));
        _shortNameToUnicode.put("mouse2", new String(new int[]{0x1f401}, 0, 1));
        _shortNameToUnicode.put("rooster", new String(new int[]{0x1f413}, 0, 1));
        _shortNameToUnicode.put("turkey", new String(new int[]{0x1f983}, 0, 1));
        _shortNameToUnicode.put("dove", new String(new int[]{0x1f54a}, 0, 1));
        _shortNameToUnicode.put("dog2", new String(new int[]{0x1f415}, 0, 1));
        _shortNameToUnicode.put("poodle", new String(new int[]{0x1f429}, 0, 1));
        _shortNameToUnicode.put("cat2", new String(new int[]{0x1f408}, 0, 1));
        _shortNameToUnicode.put("rabbit2", new String(new int[]{0x1f407}, 0, 1));
        _shortNameToUnicode.put("chipmunk", new String(new int[]{0x1f43f}, 0, 1));
        _shortNameToUnicode.put("feet", new String(new int[]{0x1f43e}, 0, 1));
        _shortNameToUnicode.put("dragon", new String(new int[]{0x1f409}, 0, 1));
        _shortNameToUnicode.put("dragon_face", new String(new int[]{0x1f432}, 0, 1));
        _shortNameToUnicode.put("cactus", new String(new int[]{0x1f335}, 0, 1));
        _shortNameToUnicode.put("christmas_tree", new String(new int[]{0x1f384}, 0, 1));
        _shortNameToUnicode.put("evergreen_tree", new String(new int[]{0x1f332}, 0, 1));
        _shortNameToUnicode.put("deciduous_tree", new String(new int[]{0x1f333}, 0, 1));
        _shortNameToUnicode.put("palm_tree", new String(new int[]{0x1f334}, 0, 1));
        _shortNameToUnicode.put("seedling", new String(new int[]{0x1f331}, 0, 1));
        _shortNameToUnicode.put("herb", new String(new int[]{0x1f33f}, 0, 1));
        _shortNameToUnicode.put("shamrock", new String(new int[]{0x2618}, 0, 1));
        _shortNameToUnicode.put("four_leaf_clover", new String(new int[]{0x1f340}, 0, 1));
        _shortNameToUnicode.put("bamboo", new String(new int[]{0x1f38d}, 0, 1));
        _shortNameToUnicode.put("tanabata_tree", new String(new int[]{0x1f38b}, 0, 1));
        _shortNameToUnicode.put("leaves", new String(new int[]{0x1f343}, 0, 1));
        _shortNameToUnicode.put("fallen_leaf", new String(new int[]{0x1f342}, 0, 1));
        _shortNameToUnicode.put("maple_leaf", new String(new int[]{0x1f341}, 0, 1));
        _shortNameToUnicode.put("ear_of_rice", new String(new int[]{0x1f33e}, 0, 1));
        _shortNameToUnicode.put("hibiscus", new String(new int[]{0x1f33a}, 0, 1));
        _shortNameToUnicode.put("sunflower", new String(new int[]{0x1f33b}, 0, 1));
        _shortNameToUnicode.put("rose", new String(new int[]{0x1f339}, 0, 1));
        _shortNameToUnicode.put("tulip", new String(new int[]{0x1f337}, 0, 1));
        _shortNameToUnicode.put("blossom", new String(new int[]{0x1f33c}, 0, 1));
        _shortNameToUnicode.put("cherry_blossom", new String(new int[]{0x1f338}, 0, 1));
        _shortNameToUnicode.put("bouquet", new String(new int[]{0x1f490}, 0, 1));
        _shortNameToUnicode.put("mushroom", new String(new int[]{0x1f344}, 0, 1));
        _shortNameToUnicode.put("chestnut", new String(new int[]{0x1f330}, 0, 1));
        _shortNameToUnicode.put("jack_o_lantern", new String(new int[]{0x1f383}, 0, 1));
        _shortNameToUnicode.put("shell", new String(new int[]{0x1f41a}, 0, 1));
        _shortNameToUnicode.put("spider_web", new String(new int[]{0x1f578}, 0, 1));
        _shortNameToUnicode.put("earth_americas", new String(new int[]{0x1f30e}, 0, 1));
        _shortNameToUnicode.put("earth_africa", new String(new int[]{0x1f30d}, 0, 1));
        _shortNameToUnicode.put("earth_asia", new String(new int[]{0x1f30f}, 0, 1));
        _shortNameToUnicode.put("full_moon", new String(new int[]{0x1f315}, 0, 1));
        _shortNameToUnicode.put("waning_gibbous_moon", new String(new int[]{0x1f316}, 0, 1));
        _shortNameToUnicode.put("last_quarter_moon", new String(new int[]{0x1f317}, 0, 1));
        _shortNameToUnicode.put("waning_crescent_moon", new String(new int[]{0x1f318}, 0, 1));
        _shortNameToUnicode.put("new_moon", new String(new int[]{0x1f311}, 0, 1));
        _shortNameToUnicode.put("waxing_crescent_moon", new String(new int[]{0x1f312}, 0, 1));
        _shortNameToUnicode.put("first_quarter_moon", new String(new int[]{0x1f313}, 0, 1));
        _shortNameToUnicode.put("waxing_gibbous_moon", new String(new int[]{0x1f314}, 0, 1));
        _shortNameToUnicode.put("new_moon_with_face", new String(new int[]{0x1f31a}, 0, 1));
        _shortNameToUnicode.put("full_moon_with_face", new String(new int[]{0x1f31d}, 0, 1));
        _shortNameToUnicode.put("first_quarter_moon_with_face",
                new String(new int[]{0x1f31b}, 0, 1));
        _shortNameToUnicode.put("last_quarter_moon_with_face",
                new String(new int[]{0x1f31c}, 0, 1));
        _shortNameToUnicode.put("sun_with_face", new String(new int[]{0x1f31e}, 0, 1));
        _shortNameToUnicode.put("crescent_moon", new String(new int[]{0x1f319}, 0, 1));
        _shortNameToUnicode.put("star", new String(new int[]{0x2b50}, 0, 1));
        _shortNameToUnicode.put("star2", new String(new int[]{0x1f31f}, 0, 1));
        _shortNameToUnicode.put("dizzy", new String(new int[]{0x1f4ab}, 0, 1));
        _shortNameToUnicode.put("sparkles", new String(new int[]{0x2728}, 0, 1));
        _shortNameToUnicode.put("comet", new String(new int[]{0x2604}, 0, 1));
        _shortNameToUnicode.put("sunny", new String(new int[]{0x2600}, 0, 1));
        _shortNameToUnicode.put("white_sun_small_cloud", new String(new int[]{0x1f324}, 0, 1));
        _shortNameToUnicode.put("partly_sunny", new String(new int[]{0x26c5}, 0, 1));
        _shortNameToUnicode.put("white_sun_cloud", new String(new int[]{0x1f325}, 0, 1));
        _shortNameToUnicode.put("white_sun_rain_cloud", new String(new int[]{0x1f326}, 0, 1));
        _shortNameToUnicode.put("cloud", new String(new int[]{0x2601}, 0, 1));
        _shortNameToUnicode.put("cloud_rain", new String(new int[]{0x1f327}, 0, 1));
        _shortNameToUnicode.put("thunder_cloud_rain", new String(new int[]{0x26c8}, 0, 1));
        _shortNameToUnicode.put("cloud_lightning", new String(new int[]{0x1f329}, 0, 1));
        _shortNameToUnicode.put("zap", new String(new int[]{0x26a1}, 0, 1));
        _shortNameToUnicode.put("fire", new String(new int[]{0x1f525}, 0, 1));
        _shortNameToUnicode.put("boom", new String(new int[]{0x1f4a5}, 0, 1));
        _shortNameToUnicode.put("snowflake", new String(new int[]{0x2744}, 0, 1));
        _shortNameToUnicode.put("cloud_snow", new String(new int[]{0x1f328}, 0, 1));
        _shortNameToUnicode.put("snowman2", new String(new int[]{0x2603}, 0, 1));
        _shortNameToUnicode.put("snowman", new String(new int[]{0x26c4}, 0, 1));
        _shortNameToUnicode.put("wind_blowing_face", new String(new int[]{0x1f32c}, 0, 1));
        _shortNameToUnicode.put("dash", new String(new int[]{0x1f4a8}, 0, 1));
        _shortNameToUnicode.put("cloud_tornado", new String(new int[]{0x1f32a}, 0, 1));
        _shortNameToUnicode.put("fog", new String(new int[]{0x1f32b}, 0, 1));
        _shortNameToUnicode.put("umbrella2", new String(new int[]{0x2602}, 0, 1));
        _shortNameToUnicode.put("umbrella", new String(new int[]{0x2614}, 0, 1));
        _shortNameToUnicode.put("droplet", new String(new int[]{0x1f4a7}, 0, 1));
        _shortNameToUnicode.put("sweat_drops", new String(new int[]{0x1f4a6}, 0, 1));
        _shortNameToUnicode.put("ocean", new String(new int[]{0x1f30a}, 0, 1));
        _shortNameToUnicode.put("green_apple", new String(new int[]{0x1f34f}, 0, 1));
        _shortNameToUnicode.put("apple", new String(new int[]{0x1f34e}, 0, 1));
        _shortNameToUnicode.put("pear", new String(new int[]{0x1f350}, 0, 1));
        _shortNameToUnicode.put("tangerine", new String(new int[]{0x1f34a}, 0, 1));
        _shortNameToUnicode.put("lemon", new String(new int[]{0x1f34b}, 0, 1));
        _shortNameToUnicode.put("banana", new String(new int[]{0x1f34c}, 0, 1));
        _shortNameToUnicode.put("watermelon", new String(new int[]{0x1f349}, 0, 1));
        _shortNameToUnicode.put("grapes", new String(new int[]{0x1f347}, 0, 1));
        _shortNameToUnicode.put("strawberry", new String(new int[]{0x1f353}, 0, 1));
        _shortNameToUnicode.put("melon", new String(new int[]{0x1f348}, 0, 1));
        _shortNameToUnicode.put("cherries", new String(new int[]{0x1f352}, 0, 1));
        _shortNameToUnicode.put("peach", new String(new int[]{0x1f351}, 0, 1));
        _shortNameToUnicode.put("pineapple", new String(new int[]{0x1f34d}, 0, 1));
        _shortNameToUnicode.put("tomato", new String(new int[]{0x1f345}, 0, 1));
        _shortNameToUnicode.put("eggplant", new String(new int[]{0x1f346}, 0, 1));
        _shortNameToUnicode.put("hot_pepper", new String(new int[]{0x1f336}, 0, 1));
        _shortNameToUnicode.put("corn", new String(new int[]{0x1f33d}, 0, 1));
        _shortNameToUnicode.put("sweet_potato", new String(new int[]{0x1f360}, 0, 1));
        _shortNameToUnicode.put("honey_pot", new String(new int[]{0x1f36f}, 0, 1));
        _shortNameToUnicode.put("bread", new String(new int[]{0x1f35e}, 0, 1));
        _shortNameToUnicode.put("cheese", new String(new int[]{0x1f9c0}, 0, 1));
        _shortNameToUnicode.put("poultry_leg", new String(new int[]{0x1f357}, 0, 1));
        _shortNameToUnicode.put("meat_on_bone", new String(new int[]{0x1f356}, 0, 1));
        _shortNameToUnicode.put("fried_shrimp", new String(new int[]{0x1f364}, 0, 1));
        _shortNameToUnicode.put("cooking", new String(new int[]{0x1f373}, 0, 1));
        _shortNameToUnicode.put("hamburger", new String(new int[]{0x1f354}, 0, 1));
        _shortNameToUnicode.put("fries", new String(new int[]{0x1f35f}, 0, 1));
        _shortNameToUnicode.put("hotdog", new String(new int[]{0x1f32d}, 0, 1));
        _shortNameToUnicode.put("pizza", new String(new int[]{0x1f355}, 0, 1));
        _shortNameToUnicode.put("spaghetti", new String(new int[]{0x1f35d}, 0, 1));
        _shortNameToUnicode.put("taco", new String(new int[]{0x1f32e}, 0, 1));
        _shortNameToUnicode.put("burrito", new String(new int[]{0x1f32f}, 0, 1));
        _shortNameToUnicode.put("ramen", new String(new int[]{0x1f35c}, 0, 1));
        _shortNameToUnicode.put("stew", new String(new int[]{0x1f372}, 0, 1));
        _shortNameToUnicode.put("fish_cake", new String(new int[]{0x1f365}, 0, 1));
        _shortNameToUnicode.put("sushi", new String(new int[]{0x1f363}, 0, 1));
        _shortNameToUnicode.put("bento", new String(new int[]{0x1f371}, 0, 1));
        _shortNameToUnicode.put("curry", new String(new int[]{0x1f35b}, 0, 1));
        _shortNameToUnicode.put("rice_ball", new String(new int[]{0x1f359}, 0, 1));
        _shortNameToUnicode.put("rice", new String(new int[]{0x1f35a}, 0, 1));
        _shortNameToUnicode.put("rice_cracker", new String(new int[]{0x1f358}, 0, 1));
        _shortNameToUnicode.put("oden", new String(new int[]{0x1f362}, 0, 1));
        _shortNameToUnicode.put("dango", new String(new int[]{0x1f361}, 0, 1));
        _shortNameToUnicode.put("shaved_ice", new String(new int[]{0x1f367}, 0, 1));
        _shortNameToUnicode.put("ice_cream", new String(new int[]{0x1f368}, 0, 1));
        _shortNameToUnicode.put("icecream", new String(new int[]{0x1f366}, 0, 1));
        _shortNameToUnicode.put("cake", new String(new int[]{0x1f370}, 0, 1));
        _shortNameToUnicode.put("birthday", new String(new int[]{0x1f382}, 0, 1));
        _shortNameToUnicode.put("custard", new String(new int[]{0x1f36e}, 0, 1));
        _shortNameToUnicode.put("candy", new String(new int[]{0x1f36c}, 0, 1));
        _shortNameToUnicode.put("lollipop", new String(new int[]{0x1f36d}, 0, 1));
        _shortNameToUnicode.put("chocolate_bar", new String(new int[]{0x1f36b}, 0, 1));
        _shortNameToUnicode.put("popcorn", new String(new int[]{0x1f37f}, 0, 1));
        _shortNameToUnicode.put("doughnut", new String(new int[]{0x1f369}, 0, 1));
        _shortNameToUnicode.put("cookie", new String(new int[]{0x1f36a}, 0, 1));
        _shortNameToUnicode.put("beer", new String(new int[]{0x1f37a}, 0, 1));
        _shortNameToUnicode.put("beers", new String(new int[]{0x1f37b}, 0, 1));
        _shortNameToUnicode.put("wine_glass", new String(new int[]{0x1f377}, 0, 1));
        _shortNameToUnicode.put("cocktail", new String(new int[]{0x1f378}, 0, 1));
        _shortNameToUnicode.put("tropical_drink", new String(new int[]{0x1f379}, 0, 1));
        _shortNameToUnicode.put("champagne", new String(new int[]{0x1f37e}, 0, 1));
        _shortNameToUnicode.put("sake", new String(new int[]{0x1f376}, 0, 1));
        _shortNameToUnicode.put("tea", new String(new int[]{0x1f375}, 0, 1));
        _shortNameToUnicode.put("coffee", new String(new int[]{0x2615}, 0, 1));
        _shortNameToUnicode.put("baby_bottle", new String(new int[]{0x1f37c}, 0, 1));
        _shortNameToUnicode.put("fork_and_knife", new String(new int[]{0x1f374}, 0, 1));
        _shortNameToUnicode.put("fork_knife_plate", new String(new int[]{0x1f37d}, 0, 1));
        _shortNameToUnicode.put("soccer", new String(new int[]{0x26bd}, 0, 1));
        _shortNameToUnicode.put("basketball", new String(new int[]{0x1f3c0}, 0, 1));
        _shortNameToUnicode.put("football", new String(new int[]{0x1f3c8}, 0, 1));
        _shortNameToUnicode.put("baseball", new String(new int[]{0x26be}, 0, 1));
        _shortNameToUnicode.put("tennis", new String(new int[]{0x1f3be}, 0, 1));
        _shortNameToUnicode.put("volleyball", new String(new int[]{0x1f3d0}, 0, 1));
        _shortNameToUnicode.put("rugby_football", new String(new int[]{0x1f3c9}, 0, 1));
        _shortNameToUnicode.put("8ball", new String(new int[]{0x1f3b1}, 0, 1));
        _shortNameToUnicode.put("golf", new String(new int[]{0x26f3}, 0, 1));
        _shortNameToUnicode.put("golfer", new String(new int[]{0x1f3cc}, 0, 1));
        _shortNameToUnicode.put("ping_pong", new String(new int[]{0x1f3d3}, 0, 1));
        _shortNameToUnicode.put("badminton", new String(new int[]{0x1f3f8}, 0, 1));
        _shortNameToUnicode.put("hockey", new String(new int[]{0x1f3d2}, 0, 1));
        _shortNameToUnicode.put("field_hockey", new String(new int[]{0x1f3d1}, 0, 1));
        _shortNameToUnicode.put("cricket", new String(new int[]{0x1f3cf}, 0, 1));
        _shortNameToUnicode.put("ski", new String(new int[]{0x1f3bf}, 0, 1));
        _shortNameToUnicode.put("skier", new String(new int[]{0x26f7}, 0, 1));
        _shortNameToUnicode.put("snowboarder", new String(new int[]{0x1f3c2}, 0, 1));
        _shortNameToUnicode.put("ice_skate", new String(new int[]{0x26f8}, 0, 1));
        _shortNameToUnicode.put("bow_and_arrow", new String(new int[]{0x1f3f9}, 0, 1));
        _shortNameToUnicode.put("fishing_pole_and_fish", new String(new int[]{0x1f3a3}, 0, 1));
        _shortNameToUnicode.put("rowboat", new String(new int[]{0x1f6a3}, 0, 1));
        _shortNameToUnicode.put("swimmer", new String(new int[]{0x1f3ca}, 0, 1));
        _shortNameToUnicode.put("surfer", new String(new int[]{0x1f3c4}, 0, 1));
        _shortNameToUnicode.put("bath", new String(new int[]{0x1f6c0}, 0, 1));
        _shortNameToUnicode.put("basketball_player", new String(new int[]{0x26f9}, 0, 1));
        _shortNameToUnicode.put("lifter", new String(new int[]{0x1f3cb}, 0, 1));
        _shortNameToUnicode.put("bicyclist", new String(new int[]{0x1f6b4}, 0, 1));
        _shortNameToUnicode.put("mountain_bicyclist", new String(new int[]{0x1f6b5}, 0, 1));
        _shortNameToUnicode.put("horse_racing", new String(new int[]{0x1f3c7}, 0, 1));
        _shortNameToUnicode.put("levitate", new String(new int[]{0x1f574}, 0, 1));
        _shortNameToUnicode.put("trophy", new String(new int[]{0x1f3c6}, 0, 1));
        _shortNameToUnicode.put("running_shirt_with_sash", new String(new int[]{0x1f3bd}, 0, 1));
        _shortNameToUnicode.put("medal", new String(new int[]{0x1f3c5}, 0, 1));
        _shortNameToUnicode.put("military_medal", new String(new int[]{0x1f396}, 0, 1));
        _shortNameToUnicode.put("reminder_ribbon", new String(new int[]{0x1f397}, 0, 1));
        _shortNameToUnicode.put("rosette", new String(new int[]{0x1f3f5}, 0, 1));
        _shortNameToUnicode.put("ticket", new String(new int[]{0x1f3ab}, 0, 1));
        _shortNameToUnicode.put("tickets", new String(new int[]{0x1f39f}, 0, 1));
        _shortNameToUnicode.put("performing_arts", new String(new int[]{0x1f3ad}, 0, 1));
        _shortNameToUnicode.put("art", new String(new int[]{0x1f3a8}, 0, 1));
        _shortNameToUnicode.put("circus_tent", new String(new int[]{0x1f3aa}, 0, 1));
        _shortNameToUnicode.put("microphone", new String(new int[]{0x1f3a4}, 0, 1));
        _shortNameToUnicode.put("headphones", new String(new int[]{0x1f3a7}, 0, 1));
        _shortNameToUnicode.put("musical_score", new String(new int[]{0x1f3bc}, 0, 1));
        _shortNameToUnicode.put("musical_keyboard", new String(new int[]{0x1f3b9}, 0, 1));
        _shortNameToUnicode.put("saxophone", new String(new int[]{0x1f3b7}, 0, 1));
        _shortNameToUnicode.put("trumpet", new String(new int[]{0x1f3ba}, 0, 1));
        _shortNameToUnicode.put("guitar", new String(new int[]{0x1f3b8}, 0, 1));
        _shortNameToUnicode.put("violin", new String(new int[]{0x1f3bb}, 0, 1));
        _shortNameToUnicode.put("clapper", new String(new int[]{0x1f3ac}, 0, 1));
        _shortNameToUnicode.put("video_game", new String(new int[]{0x1f3ae}, 0, 1));
        _shortNameToUnicode.put("space_invader", new String(new int[]{0x1f47e}, 0, 1));
        _shortNameToUnicode.put("dart", new String(new int[]{0x1f3af}, 0, 1));
        _shortNameToUnicode.put("game_die", new String(new int[]{0x1f3b2}, 0, 1));
        _shortNameToUnicode.put("slot_machine", new String(new int[]{0x1f3b0}, 0, 1));
        _shortNameToUnicode.put("bowling", new String(new int[]{0x1f3b3}, 0, 1));
        _shortNameToUnicode.put("red_car", new String(new int[]{0x1f697}, 0, 1));
        _shortNameToUnicode.put("taxi", new String(new int[]{0x1f695}, 0, 1));
        _shortNameToUnicode.put("blue_car", new String(new int[]{0x1f699}, 0, 1));
        _shortNameToUnicode.put("bus", new String(new int[]{0x1f68c}, 0, 1));
        _shortNameToUnicode.put("trolleybus", new String(new int[]{0x1f68e}, 0, 1));
        _shortNameToUnicode.put("race_car", new String(new int[]{0x1f3ce}, 0, 1));
        _shortNameToUnicode.put("police_car", new String(new int[]{0x1f693}, 0, 1));
        _shortNameToUnicode.put("ambulance", new String(new int[]{0x1f691}, 0, 1));
        _shortNameToUnicode.put("fire_engine", new String(new int[]{0x1f692}, 0, 1));
        _shortNameToUnicode.put("minibus", new String(new int[]{0x1f690}, 0, 1));
        _shortNameToUnicode.put("truck", new String(new int[]{0x1f69a}, 0, 1));
        _shortNameToUnicode.put("articulated_lorry", new String(new int[]{0x1f69b}, 0, 1));
        _shortNameToUnicode.put("tractor", new String(new int[]{0x1f69c}, 0, 1));
        _shortNameToUnicode.put("motorcycle", new String(new int[]{0x1f3cd}, 0, 1));
        _shortNameToUnicode.put("bike", new String(new int[]{0x1f6b2}, 0, 1));
        _shortNameToUnicode.put("rotating_light", new String(new int[]{0x1f6a8}, 0, 1));
        _shortNameToUnicode.put("oncoming_police_car", new String(new int[]{0x1f694}, 0, 1));
        _shortNameToUnicode.put("oncoming_bus", new String(new int[]{0x1f68d}, 0, 1));
        _shortNameToUnicode.put("oncoming_automobile", new String(new int[]{0x1f698}, 0, 1));
        _shortNameToUnicode.put("oncoming_taxi", new String(new int[]{0x1f696}, 0, 1));
        _shortNameToUnicode.put("aerial_tramway", new String(new int[]{0x1f6a1}, 0, 1));
        _shortNameToUnicode.put("mountain_cableway", new String(new int[]{0x1f6a0}, 0, 1));
        _shortNameToUnicode.put("suspension_railway", new String(new int[]{0x1f69f}, 0, 1));
        _shortNameToUnicode.put("railway_car", new String(new int[]{0x1f683}, 0, 1));
        _shortNameToUnicode.put("train", new String(new int[]{0x1f68b}, 0, 1));
        _shortNameToUnicode.put("monorail", new String(new int[]{0x1f69d}, 0, 1));
        _shortNameToUnicode.put("bullettrain_side", new String(new int[]{0x1f684}, 0, 1));
        _shortNameToUnicode.put("bullettrain_front", new String(new int[]{0x1f685}, 0, 1));
        _shortNameToUnicode.put("light_rail", new String(new int[]{0x1f688}, 0, 1));
        _shortNameToUnicode.put("mountain_railway", new String(new int[]{0x1f69e}, 0, 1));
        _shortNameToUnicode.put("steam_locomotive", new String(new int[]{0x1f682}, 0, 1));
        _shortNameToUnicode.put("train2", new String(new int[]{0x1f686}, 0, 1));
        _shortNameToUnicode.put("metro", new String(new int[]{0x1f687}, 0, 1));
        _shortNameToUnicode.put("tram", new String(new int[]{0x1f68a}, 0, 1));
        _shortNameToUnicode.put("station", new String(new int[]{0x1f689}, 0, 1));
        _shortNameToUnicode.put("helicopter", new String(new int[]{0x1f681}, 0, 1));
        _shortNameToUnicode.put("airplane_small", new String(new int[]{0x1f6e9}, 0, 1));
        _shortNameToUnicode.put("airplane", new String(new int[]{0x2708}, 0, 1));
        _shortNameToUnicode.put("airplane_departure", new String(new int[]{0x1f6eb}, 0, 1));
        _shortNameToUnicode.put("airplane_arriving", new String(new int[]{0x1f6ec}, 0, 1));
        _shortNameToUnicode.put("sailboat", new String(new int[]{0x26f5}, 0, 1));
        _shortNameToUnicode.put("motorboat", new String(new int[]{0x1f6e5}, 0, 1));
        _shortNameToUnicode.put("speedboat", new String(new int[]{0x1f6a4}, 0, 1));
        _shortNameToUnicode.put("ferry", new String(new int[]{0x26f4}, 0, 1));
        _shortNameToUnicode.put("cruise_ship", new String(new int[]{0x1f6f3}, 0, 1));
        _shortNameToUnicode.put("rocket", new String(new int[]{0x1f680}, 0, 1));
        _shortNameToUnicode.put("satellite_orbital", new String(new int[]{0x1f6f0}, 0, 1));
        _shortNameToUnicode.put("seat", new String(new int[]{0x1f4ba}, 0, 1));
        _shortNameToUnicode.put("anchor", new String(new int[]{0x2693}, 0, 1));
        _shortNameToUnicode.put("construction", new String(new int[]{0x1f6a7}, 0, 1));
        _shortNameToUnicode.put("fuelpump", new String(new int[]{0x26fd}, 0, 1));
        _shortNameToUnicode.put("busstop", new String(new int[]{0x1f68f}, 0, 1));
        _shortNameToUnicode.put("vertical_traffic_light", new String(new int[]{0x1f6a6}, 0, 1));
        _shortNameToUnicode.put("traffic_light", new String(new int[]{0x1f6a5}, 0, 1));
        _shortNameToUnicode.put("checkered_flag", new String(new int[]{0x1f3c1}, 0, 1));
        _shortNameToUnicode.put("ship", new String(new int[]{0x1f6a2}, 0, 1));
        _shortNameToUnicode.put("ferris_wheel", new String(new int[]{0x1f3a1}, 0, 1));
        _shortNameToUnicode.put("roller_coaster", new String(new int[]{0x1f3a2}, 0, 1));
        _shortNameToUnicode.put("carousel_horse", new String(new int[]{0x1f3a0}, 0, 1));
        _shortNameToUnicode.put("construction_site", new String(new int[]{0x1f3d7}, 0, 1));
        _shortNameToUnicode.put("foggy", new String(new int[]{0x1f301}, 0, 1));
        _shortNameToUnicode.put("tokyo_tower", new String(new int[]{0x1f5fc}, 0, 1));
        _shortNameToUnicode.put("factory", new String(new int[]{0x1f3ed}, 0, 1));
        _shortNameToUnicode.put("fountain", new String(new int[]{0x26f2}, 0, 1));
        _shortNameToUnicode.put("rice_scene", new String(new int[]{0x1f391}, 0, 1));
        _shortNameToUnicode.put("mountain", new String(new int[]{0x26f0}, 0, 1));
        _shortNameToUnicode.put("mountain_snow", new String(new int[]{0x1f3d4}, 0, 1));
        _shortNameToUnicode.put("mount_fuji", new String(new int[]{0x1f5fb}, 0, 1));
        _shortNameToUnicode.put("volcano", new String(new int[]{0x1f30b}, 0, 1));
        _shortNameToUnicode.put("japan", new String(new int[]{0x1f5fe}, 0, 1));
        _shortNameToUnicode.put("camping", new String(new int[]{0x1f3d5}, 0, 1));
        _shortNameToUnicode.put("tent", new String(new int[]{0x26fa}, 0, 1));
        _shortNameToUnicode.put("park", new String(new int[]{0x1f3de}, 0, 1));
        _shortNameToUnicode.put("motorway", new String(new int[]{0x1f6e3}, 0, 1));
        _shortNameToUnicode.put("railway_track", new String(new int[]{0x1f6e4}, 0, 1));
        _shortNameToUnicode.put("sunrise", new String(new int[]{0x1f305}, 0, 1));
        _shortNameToUnicode.put("sunrise_over_mountains", new String(new int[]{0x1f304}, 0, 1));
        _shortNameToUnicode.put("desert", new String(new int[]{0x1f3dc}, 0, 1));
        _shortNameToUnicode.put("beach", new String(new int[]{0x1f3d6}, 0, 1));
        _shortNameToUnicode.put("island", new String(new int[]{0x1f3dd}, 0, 1));
        _shortNameToUnicode.put("city_sunset", new String(new int[]{0x1f307}, 0, 1));
        _shortNameToUnicode.put("city_dusk", new String(new int[]{0x1f306}, 0, 1));
        _shortNameToUnicode.put("cityscape", new String(new int[]{0x1f3d9}, 0, 1));
        _shortNameToUnicode.put("night_with_stars", new String(new int[]{0x1f303}, 0, 1));
        _shortNameToUnicode.put("bridge_at_night", new String(new int[]{0x1f309}, 0, 1));
        _shortNameToUnicode.put("milky_way", new String(new int[]{0x1f30c}, 0, 1));
        _shortNameToUnicode.put("stars", new String(new int[]{0x1f320}, 0, 1));
        _shortNameToUnicode.put("sparkler", new String(new int[]{0x1f387}, 0, 1));
        _shortNameToUnicode.put("fireworks", new String(new int[]{0x1f386}, 0, 1));
        _shortNameToUnicode.put("rainbow", new String(new int[]{0x1f308}, 0, 1));
        _shortNameToUnicode.put("homes", new String(new int[]{0x1f3d8}, 0, 1));
        _shortNameToUnicode.put("european_castle", new String(new int[]{0x1f3f0}, 0, 1));
        _shortNameToUnicode.put("japanese_castle", new String(new int[]{0x1f3ef}, 0, 1));
        _shortNameToUnicode.put("stadium", new String(new int[]{0x1f3df}, 0, 1));
        _shortNameToUnicode.put("statue_of_liberty", new String(new int[]{0x1f5fd}, 0, 1));
        _shortNameToUnicode.put("house", new String(new int[]{0x1f3e0}, 0, 1));
        _shortNameToUnicode.put("house_with_garden", new String(new int[]{0x1f3e1}, 0, 1));
        _shortNameToUnicode.put("house_abandoned", new String(new int[]{0x1f3da}, 0, 1));
        _shortNameToUnicode.put("office", new String(new int[]{0x1f3e2}, 0, 1));
        _shortNameToUnicode.put("department_store", new String(new int[]{0x1f3ec}, 0, 1));
        _shortNameToUnicode.put("post_office", new String(new int[]{0x1f3e3}, 0, 1));
        _shortNameToUnicode.put("european_post_office", new String(new int[]{0x1f3e4}, 0, 1));
        _shortNameToUnicode.put("hospital", new String(new int[]{0x1f3e5}, 0, 1));
        _shortNameToUnicode.put("bank", new String(new int[]{0x1f3e6}, 0, 1));
        _shortNameToUnicode.put("hotel", new String(new int[]{0x1f3e8}, 0, 1));
        _shortNameToUnicode.put("convenience_store", new String(new int[]{0x1f3ea}, 0, 1));
        _shortNameToUnicode.put("school", new String(new int[]{0x1f3eb}, 0, 1));
        _shortNameToUnicode.put("love_hotel", new String(new int[]{0x1f3e9}, 0, 1));
        _shortNameToUnicode.put("wedding", new String(new int[]{0x1f492}, 0, 1));
        _shortNameToUnicode.put("classical_building", new String(new int[]{0x1f3db}, 0, 1));
        _shortNameToUnicode.put("church", new String(new int[]{0x26ea}, 0, 1));
        _shortNameToUnicode.put("mosque", new String(new int[]{0x1f54c}, 0, 1));
        _shortNameToUnicode.put("synagogue", new String(new int[]{0x1f54d}, 0, 1));
        _shortNameToUnicode.put("kaaba", new String(new int[]{0x1f54b}, 0, 1));
        _shortNameToUnicode.put("shinto_shrine", new String(new int[]{0x26e9}, 0, 1));
        _shortNameToUnicode.put("watch", new String(new int[]{0x231a}, 0, 1));
        _shortNameToUnicode.put("iphone", new String(new int[]{0x1f4f1}, 0, 1));
        _shortNameToUnicode.put("calling", new String(new int[]{0x1f4f2}, 0, 1));
        _shortNameToUnicode.put("computer", new String(new int[]{0x1f4bb}, 0, 1));
        _shortNameToUnicode.put("keyboard", new String(new int[]{0x2328}, 0, 1));
        _shortNameToUnicode.put("desktop", new String(new int[]{0x1f5a5}, 0, 1));
        _shortNameToUnicode.put("printer", new String(new int[]{0x1f5a8}, 0, 1));
        _shortNameToUnicode.put("mouse_three_button", new String(new int[]{0x1f5b1}, 0, 1));
        _shortNameToUnicode.put("trackball", new String(new int[]{0x1f5b2}, 0, 1));
        _shortNameToUnicode.put("joystick", new String(new int[]{0x1f579}, 0, 1));
        _shortNameToUnicode.put("compression", new String(new int[]{0x1f5dc}, 0, 1));
        _shortNameToUnicode.put("minidisc", new String(new int[]{0x1f4bd}, 0, 1));
        _shortNameToUnicode.put("floppy_disk", new String(new int[]{0x1f4be}, 0, 1));
        _shortNameToUnicode.put("cd", new String(new int[]{0x1f4bf}, 0, 1));
        _shortNameToUnicode.put("dvd", new String(new int[]{0x1f4c0}, 0, 1));
        _shortNameToUnicode.put("vhs", new String(new int[]{0x1f4fc}, 0, 1));
        _shortNameToUnicode.put("camera", new String(new int[]{0x1f4f7}, 0, 1));
        _shortNameToUnicode.put("camera_with_flash", new String(new int[]{0x1f4f8}, 0, 1));
        _shortNameToUnicode.put("video_camera", new String(new int[]{0x1f4f9}, 0, 1));
        _shortNameToUnicode.put("movie_camera", new String(new int[]{0x1f3a5}, 0, 1));
        _shortNameToUnicode.put("projector", new String(new int[]{0x1f4fd}, 0, 1));
        _shortNameToUnicode.put("film_frames", new String(new int[]{0x1f39e}, 0, 1));
        _shortNameToUnicode.put("telephone_receiver", new String(new int[]{0x1f4de}, 0, 1));
        _shortNameToUnicode.put("telephone", new String(new int[]{0x260e}, 0, 1));
        _shortNameToUnicode.put("pager", new String(new int[]{0x1f4df}, 0, 1));
        _shortNameToUnicode.put("fax", new String(new int[]{0x1f4e0}, 0, 1));
        _shortNameToUnicode.put("tv", new String(new int[]{0x1f4fa}, 0, 1));
        _shortNameToUnicode.put("radio", new String(new int[]{0x1f4fb}, 0, 1));
        _shortNameToUnicode.put("microphone2", new String(new int[]{0x1f399}, 0, 1));
        _shortNameToUnicode.put("level_slider", new String(new int[]{0x1f39a}, 0, 1));
        _shortNameToUnicode.put("control_knobs", new String(new int[]{0x1f39b}, 0, 1));
        _shortNameToUnicode.put("stopwatch", new String(new int[]{0x23f1}, 0, 1));
        _shortNameToUnicode.put("timer", new String(new int[]{0x23f2}, 0, 1));
        _shortNameToUnicode.put("alarm_clock", new String(new int[]{0x23f0}, 0, 1));
        _shortNameToUnicode.put("clock", new String(new int[]{0x1f570}, 0, 1));
        _shortNameToUnicode.put("hourglass_flowing_sand", new String(new int[]{0x23f3}, 0, 1));
        _shortNameToUnicode.put("hourglass", new String(new int[]{0x231b}, 0, 1));
        _shortNameToUnicode.put("satellite", new String(new int[]{0x1f4e1}, 0, 1));
        _shortNameToUnicode.put("battery", new String(new int[]{0x1f50b}, 0, 1));
        _shortNameToUnicode.put("electric_plug", new String(new int[]{0x1f50c}, 0, 1));
        _shortNameToUnicode.put("bulb", new String(new int[]{0x1f4a1}, 0, 1));
        _shortNameToUnicode.put("flashlight", new String(new int[]{0x1f526}, 0, 1));
        _shortNameToUnicode.put("candle", new String(new int[]{0x1f56f}, 0, 1));
        _shortNameToUnicode.put("wastebasket", new String(new int[]{0x1f5d1}, 0, 1));
        _shortNameToUnicode.put("oil", new String(new int[]{0x1f6e2}, 0, 1));
        _shortNameToUnicode.put("money_with_wings", new String(new int[]{0x1f4b8}, 0, 1));
        _shortNameToUnicode.put("dollar", new String(new int[]{0x1f4b5}, 0, 1));
        _shortNameToUnicode.put("yen", new String(new int[]{0x1f4b4}, 0, 1));
        _shortNameToUnicode.put("euro", new String(new int[]{0x1f4b6}, 0, 1));
        _shortNameToUnicode.put("pound", new String(new int[]{0x1f4b7}, 0, 1));
        _shortNameToUnicode.put("moneybag", new String(new int[]{0x1f4b0}, 0, 1));
        _shortNameToUnicode.put("credit_card", new String(new int[]{0x1f4b3}, 0, 1));
        _shortNameToUnicode.put("gem", new String(new int[]{0x1f48e}, 0, 1));
        _shortNameToUnicode.put("scales", new String(new int[]{0x2696}, 0, 1));
        _shortNameToUnicode.put("wrench", new String(new int[]{0x1f527}, 0, 1));
        _shortNameToUnicode.put("hammer", new String(new int[]{0x1f528}, 0, 1));
        _shortNameToUnicode.put("hammer_pick", new String(new int[]{0x2692}, 0, 1));
        _shortNameToUnicode.put("tools", new String(new int[]{0x1f6e0}, 0, 1));
        _shortNameToUnicode.put("pick", new String(new int[]{0x26cf}, 0, 1));
        _shortNameToUnicode.put("nut_and_bolt", new String(new int[]{0x1f529}, 0, 1));
        _shortNameToUnicode.put("gear", new String(new int[]{0x2699}, 0, 1));
        _shortNameToUnicode.put("chains", new String(new int[]{0x26d3}, 0, 1));
        _shortNameToUnicode.put("gun", new String(new int[]{0x1f52b}, 0, 1));
        _shortNameToUnicode.put("bomb", new String(new int[]{0x1f4a3}, 0, 1));
        _shortNameToUnicode.put("knife", new String(new int[]{0x1f52a}, 0, 1));
        _shortNameToUnicode.put("dagger", new String(new int[]{0x1f5e1}, 0, 1));
        _shortNameToUnicode.put("crossed_swords", new String(new int[]{0x2694}, 0, 1));
        _shortNameToUnicode.put("shield", new String(new int[]{0x1f6e1}, 0, 1));
        _shortNameToUnicode.put("smoking", new String(new int[]{0x1f6ac}, 0, 1));
        _shortNameToUnicode.put("skull_crossbones", new String(new int[]{0x2620}, 0, 1));
        _shortNameToUnicode.put("coffin", new String(new int[]{0x26b0}, 0, 1));
        _shortNameToUnicode.put("urn", new String(new int[]{0x26b1}, 0, 1));
        _shortNameToUnicode.put("amphora", new String(new int[]{0x1f3fa}, 0, 1));
        _shortNameToUnicode.put("crystal_ball", new String(new int[]{0x1f52e}, 0, 1));
        _shortNameToUnicode.put("prayer_beads", new String(new int[]{0x1f4ff}, 0, 1));
        _shortNameToUnicode.put("barber", new String(new int[]{0x1f488}, 0, 1));
        _shortNameToUnicode.put("alembic", new String(new int[]{0x2697}, 0, 1));
        _shortNameToUnicode.put("telescope", new String(new int[]{0x1f52d}, 0, 1));
        _shortNameToUnicode.put("microscope", new String(new int[]{0x1f52c}, 0, 1));
        _shortNameToUnicode.put("hole", new String(new int[]{0x1f573}, 0, 1));
        _shortNameToUnicode.put("pill", new String(new int[]{0x1f48a}, 0, 1));
        _shortNameToUnicode.put("syringe", new String(new int[]{0x1f489}, 0, 1));
        _shortNameToUnicode.put("thermometer", new String(new int[]{0x1f321}, 0, 1));
        _shortNameToUnicode.put("label", new String(new int[]{0x1f3f7}, 0, 1));
        _shortNameToUnicode.put("bookmark", new String(new int[]{0x1f516}, 0, 1));
        _shortNameToUnicode.put("toilet", new String(new int[]{0x1f6bd}, 0, 1));
        _shortNameToUnicode.put("shower", new String(new int[]{0x1f6bf}, 0, 1));
        _shortNameToUnicode.put("bathtub", new String(new int[]{0x1f6c1}, 0, 1));
        _shortNameToUnicode.put("key", new String(new int[]{0x1f511}, 0, 1));
        _shortNameToUnicode.put("key2", new String(new int[]{0x1f5dd}, 0, 1));
        _shortNameToUnicode.put("couch", new String(new int[]{0x1f6cb}, 0, 1));
        _shortNameToUnicode.put("sleeping_accommodation", new String(new int[]{0x1f6cc}, 0, 1));
        _shortNameToUnicode.put("bed", new String(new int[]{0x1f6cf}, 0, 1));
        _shortNameToUnicode.put("door", new String(new int[]{0x1f6aa}, 0, 1));
        _shortNameToUnicode.put("bellhop", new String(new int[]{0x1f6ce}, 0, 1));
        _shortNameToUnicode.put("frame_photo", new String(new int[]{0x1f5bc}, 0, 1));
        _shortNameToUnicode.put("map", new String(new int[]{0x1f5fa}, 0, 1));
        _shortNameToUnicode.put("beach_umbrella", new String(new int[]{0x26f1}, 0, 1));
        _shortNameToUnicode.put("moyai", new String(new int[]{0x1f5ff}, 0, 1));
        _shortNameToUnicode.put("shopping_bags", new String(new int[]{0x1f6cd}, 0, 1));
        _shortNameToUnicode.put("balloon", new String(new int[]{0x1f388}, 0, 1));
        _shortNameToUnicode.put("flags", new String(new int[]{0x1f38f}, 0, 1));
        _shortNameToUnicode.put("ribbon", new String(new int[]{0x1f380}, 0, 1));
        _shortNameToUnicode.put("gift", new String(new int[]{0x1f381}, 0, 1));
        _shortNameToUnicode.put("confetti_ball", new String(new int[]{0x1f38a}, 0, 1));
        _shortNameToUnicode.put("tada", new String(new int[]{0x1f389}, 0, 1));
        _shortNameToUnicode.put("dolls", new String(new int[]{0x1f38e}, 0, 1));
        _shortNameToUnicode.put("wind_chime", new String(new int[]{0x1f390}, 0, 1));
        _shortNameToUnicode.put("crossed_flags", new String(new int[]{0x1f38c}, 0, 1));
        _shortNameToUnicode.put("izakaya_lantern", new String(new int[]{0x1f3ee}, 0, 1));
        _shortNameToUnicode.put("envelope", new String(new int[]{0x2709}, 0, 1));
        _shortNameToUnicode.put("envelope_with_arrow", new String(new int[]{0x1f4e9}, 0, 1));
        _shortNameToUnicode.put("incoming_envelope", new String(new int[]{0x1f4e8}, 0, 1));
        _shortNameToUnicode.put("e-mail", new String(new int[]{0x1f4e7}, 0, 1));
        _shortNameToUnicode.put("love_letter", new String(new int[]{0x1f48c}, 0, 1));
        _shortNameToUnicode.put("postbox", new String(new int[]{0x1f4ee}, 0, 1));
        _shortNameToUnicode.put("mailbox_closed", new String(new int[]{0x1f4ea}, 0, 1));
        _shortNameToUnicode.put("mailbox", new String(new int[]{0x1f4eb}, 0, 1));
        _shortNameToUnicode.put("mailbox_with_mail", new String(new int[]{0x1f4ec}, 0, 1));
        _shortNameToUnicode.put("mailbox_with_no_mail", new String(new int[]{0x1f4ed}, 0, 1));
        _shortNameToUnicode.put("package", new String(new int[]{0x1f4e6}, 0, 1));
        _shortNameToUnicode.put("postal_horn", new String(new int[]{0x1f4ef}, 0, 1));
        _shortNameToUnicode.put("inbox_tray", new String(new int[]{0x1f4e5}, 0, 1));
        _shortNameToUnicode.put("outbox_tray", new String(new int[]{0x1f4e4}, 0, 1));
        _shortNameToUnicode.put("scroll", new String(new int[]{0x1f4dc}, 0, 1));
        _shortNameToUnicode.put("page_with_curl", new String(new int[]{0x1f4c3}, 0, 1));
        _shortNameToUnicode.put("bookmark_tabs", new String(new int[]{0x1f4d1}, 0, 1));
        _shortNameToUnicode.put("bar_chart", new String(new int[]{0x1f4ca}, 0, 1));
        _shortNameToUnicode.put("chart_with_upwards_trend",
                new String(new int[]{0x1f4c8}, 0, 1));
        _shortNameToUnicode.put("chart_with_downwards_trend",
                new String(new int[]{0x1f4c9}, 0, 1));
        _shortNameToUnicode.put("page_facing_up", new String(new int[]{0x1f4c4}, 0, 1));
        _shortNameToUnicode.put("date", new String(new int[]{0x1f4c5}, 0, 1));
        _shortNameToUnicode.put("calendar", new String(new int[]{0x1f4c6}, 0, 1));
        _shortNameToUnicode.put("calendar_spiral", new String(new int[]{0x1f5d3}, 0, 1));
        _shortNameToUnicode.put("card_index", new String(new int[]{0x1f4c7}, 0, 1));
        _shortNameToUnicode.put("card_box", new String(new int[]{0x1f5c3}, 0, 1));
        _shortNameToUnicode.put("ballot_box", new String(new int[]{0x1f5f3}, 0, 1));
        _shortNameToUnicode.put("file_cabinet", new String(new int[]{0x1f5c4}, 0, 1));
        _shortNameToUnicode.put("clipboard", new String(new int[]{0x1f4cb}, 0, 1));
        _shortNameToUnicode.put("notepad_spiral", new String(new int[]{0x1f5d2}, 0, 1));
        _shortNameToUnicode.put("file_folder", new String(new int[]{0x1f4c1}, 0, 1));
        _shortNameToUnicode.put("open_file_folder", new String(new int[]{0x1f4c2}, 0, 1));
        _shortNameToUnicode.put("dividers", new String(new int[]{0x1f5c2}, 0, 1));
        _shortNameToUnicode.put("newspaper2", new String(new int[]{0x1f5de}, 0, 1));
        _shortNameToUnicode.put("newspaper", new String(new int[]{0x1f4f0}, 0, 1));
        _shortNameToUnicode.put("notebook", new String(new int[]{0x1f4d3}, 0, 1));
        _shortNameToUnicode.put("closed_book", new String(new int[]{0x1f4d5}, 0, 1));
        _shortNameToUnicode.put("green_book", new String(new int[]{0x1f4d7}, 0, 1));
        _shortNameToUnicode.put("blue_book", new String(new int[]{0x1f4d8}, 0, 1));
        _shortNameToUnicode.put("orange_book", new String(new int[]{0x1f4d9}, 0, 1));
        _shortNameToUnicode.put("notebook_with_decorative_cover",
                new String(new int[]{0x1f4d4}, 0, 1));
        _shortNameToUnicode.put("ledger", new String(new int[]{0x1f4d2}, 0, 1));
        _shortNameToUnicode.put("books", new String(new int[]{0x1f4da}, 0, 1));
        _shortNameToUnicode.put("book", new String(new int[]{0x1f4d6}, 0, 1));
        _shortNameToUnicode.put("link", new String(new int[]{0x1f517}, 0, 1));
        _shortNameToUnicode.put("paperclip", new String(new int[]{0x1f4ce}, 0, 1));
        _shortNameToUnicode.put("paperclips", new String(new int[]{0x1f587}, 0, 1));
        _shortNameToUnicode.put("scissors", new String(new int[]{0x2702}, 0, 1));
        _shortNameToUnicode.put("triangular_ruler", new String(new int[]{0x1f4d0}, 0, 1));
        _shortNameToUnicode.put("straight_ruler", new String(new int[]{0x1f4cf}, 0, 1));
        _shortNameToUnicode.put("pushpin", new String(new int[]{0x1f4cc}, 0, 1));
        _shortNameToUnicode.put("round_pushpin", new String(new int[]{0x1f4cd}, 0, 1));
        _shortNameToUnicode.put("triangular_flag_on_post", new String(new int[]{0x1f6a9}, 0, 1));
        _shortNameToUnicode.put("flag_white", new String(new int[]{0x1f3f3}, 0, 1));
        _shortNameToUnicode.put("flag_black", new String(new int[]{0x1f3f4}, 0, 1));
        _shortNameToUnicode.put("closed_lock_with_key", new String(new int[]{0x1f510}, 0, 1));
        _shortNameToUnicode.put("lock", new String(new int[]{0x1f512}, 0, 1));
        _shortNameToUnicode.put("unlock", new String(new int[]{0x1f513}, 0, 1));
        _shortNameToUnicode.put("lock_with_ink_pen", new String(new int[]{0x1f50f}, 0, 1));
        _shortNameToUnicode.put("pen_ballpoint", new String(new int[]{0x1f58a}, 0, 1));
        _shortNameToUnicode.put("pen_fountain", new String(new int[]{0x1f58b}, 0, 1));
        _shortNameToUnicode.put("black_nib", new String(new int[]{0x2712}, 0, 1));
        _shortNameToUnicode.put("pencil", new String(new int[]{0x1f4dd}, 0, 1));
        _shortNameToUnicode.put("pencil2", new String(new int[]{0x270f}, 0, 1));
        _shortNameToUnicode.put("crayon", new String(new int[]{0x1f58d}, 0, 1));
        _shortNameToUnicode.put("paintbrush", new String(new int[]{0x1f58c}, 0, 1));
        _shortNameToUnicode.put("mag", new String(new int[]{0x1f50d}, 0, 1));
        _shortNameToUnicode.put("mag_right", new String(new int[]{0x1f50e}, 0, 1));
        _shortNameToUnicode.put("heart", new String(new int[]{0x2764}, 0, 1));
        _shortNameToUnicode.put("yellow_heart", new String(new int[]{0x1f49b}, 0, 1));
        _shortNameToUnicode.put("green_heart", new String(new int[]{0x1f49a}, 0, 1));
        _shortNameToUnicode.put("blue_heart", new String(new int[]{0x1f499}, 0, 1));
        _shortNameToUnicode.put("purple_heart", new String(new int[]{0x1f49c}, 0, 1));
        _shortNameToUnicode.put("broken_heart", new String(new int[]{0x1f494}, 0, 1));
        _shortNameToUnicode.put("heart_exclamation", new String(new int[]{0x2763}, 0, 1));
        _shortNameToUnicode.put("two_hearts", new String(new int[]{0x1f495}, 0, 1));
        _shortNameToUnicode.put("revolving_hearts", new String(new int[]{0x1f49e}, 0, 1));
        _shortNameToUnicode.put("heartbeat", new String(new int[]{0x1f493}, 0, 1));
        _shortNameToUnicode.put("heartpulse", new String(new int[]{0x1f497}, 0, 1));
        _shortNameToUnicode.put("sparkling_heart", new String(new int[]{0x1f496}, 0, 1));
        _shortNameToUnicode.put("cupid", new String(new int[]{0x1f498}, 0, 1));
        _shortNameToUnicode.put("gift_heart", new String(new int[]{0x1f49d}, 0, 1));
        _shortNameToUnicode.put("heart_decoration", new String(new int[]{0x1f49f}, 0, 1));
        _shortNameToUnicode.put("peace", new String(new int[]{0x262e}, 0, 1));
        _shortNameToUnicode.put("cross", new String(new int[]{0x271d}, 0, 1));
        _shortNameToUnicode.put("star_and_crescent", new String(new int[]{0x262a}, 0, 1));
        _shortNameToUnicode.put("om_symbol", new String(new int[]{0x1f549}, 0, 1));
        _shortNameToUnicode.put("wheel_of_dharma", new String(new int[]{0x2638}, 0, 1));
        _shortNameToUnicode.put("star_of_david", new String(new int[]{0x2721}, 0, 1));
        _shortNameToUnicode.put("six_pointed_star", new String(new int[]{0x1f52f}, 0, 1));
        _shortNameToUnicode.put("menorah", new String(new int[]{0x1f54e}, 0, 1));
        _shortNameToUnicode.put("yin_yang", new String(new int[]{0x262f}, 0, 1));
        _shortNameToUnicode.put("orthodox_cross", new String(new int[]{0x2626}, 0, 1));
        _shortNameToUnicode.put("place_of_worship", new String(new int[]{0x1f6d0}, 0, 1));
        _shortNameToUnicode.put("ophiuchus", new String(new int[]{0x26ce}, 0, 1));
        _shortNameToUnicode.put("aries", new String(new int[]{0x2648}, 0, 1));
        _shortNameToUnicode.put("taurus", new String(new int[]{0x2649}, 0, 1));
        _shortNameToUnicode.put("gemini", new String(new int[]{0x264a}, 0, 1));
        _shortNameToUnicode.put("cancer", new String(new int[]{0x264b}, 0, 1));
        _shortNameToUnicode.put("leo", new String(new int[]{0x264c}, 0, 1));
        _shortNameToUnicode.put("virgo", new String(new int[]{0x264d}, 0, 1));
        _shortNameToUnicode.put("libra", new String(new int[]{0x264e}, 0, 1));
        _shortNameToUnicode.put("scorpius", new String(new int[]{0x264f}, 0, 1));
        _shortNameToUnicode.put("sagittarius", new String(new int[]{0x2650}, 0, 1));
        _shortNameToUnicode.put("capricorn", new String(new int[]{0x2651}, 0, 1));
        _shortNameToUnicode.put("aquarius", new String(new int[]{0x2652}, 0, 1));
        _shortNameToUnicode.put("pisces", new String(new int[]{0x2653}, 0, 1));
        _shortNameToUnicode.put("id", new String(new int[]{0x1f194}, 0, 1));
        _shortNameToUnicode.put("atom", new String(new int[]{0x269b}, 0, 1));
        _shortNameToUnicode.put("u7a7a", new String(new int[]{0x1f233}, 0, 1));
        _shortNameToUnicode.put("u5272", new String(new int[]{0x1f239}, 0, 1));
        _shortNameToUnicode.put("radioactive", new String(new int[]{0x2622}, 0, 1));
        _shortNameToUnicode.put("biohazard", new String(new int[]{0x2623}, 0, 1));
        _shortNameToUnicode.put("mobile_phone_off", new String(new int[]{0x1f4f4}, 0, 1));
        _shortNameToUnicode.put("vibration_mode", new String(new int[]{0x1f4f3}, 0, 1));
        _shortNameToUnicode.put("u6709", new String(new int[]{0x1f236}, 0, 1));
        _shortNameToUnicode.put("u7121", new String(new int[]{0x1f21a}, 0, 1));
        _shortNameToUnicode.put("u7533", new String(new int[]{0x1f238}, 0, 1));
        _shortNameToUnicode.put("u55b6", new String(new int[]{0x1f23a}, 0, 1));
        _shortNameToUnicode.put("u6708", new String(new int[]{0x1f237}, 0, 1));
        _shortNameToUnicode.put("eight_pointed_black_star", new String(new int[]{0x2734}, 0, 1));
        _shortNameToUnicode.put("vs", new String(new int[]{0x1f19a}, 0, 1));
        _shortNameToUnicode.put("accept", new String(new int[]{0x1f251}, 0, 1));
        _shortNameToUnicode.put("white_flower", new String(new int[]{0x1f4ae}, 0, 1));
        _shortNameToUnicode.put("ideograph_advantage", new String(new int[]{0x1f250}, 0, 1));
        _shortNameToUnicode.put("secret", new String(new int[]{0x3299}, 0, 1));
        _shortNameToUnicode.put("congratulations", new String(new int[]{0x3297}, 0, 1));
        _shortNameToUnicode.put("u5408", new String(new int[]{0x1f234}, 0, 1));
        _shortNameToUnicode.put("u6e80", new String(new int[]{0x1f235}, 0, 1));
        _shortNameToUnicode.put("u7981", new String(new int[]{0x1f232}, 0, 1));
        _shortNameToUnicode.put("a", new String(new int[]{0x1f170}, 0, 1));
        _shortNameToUnicode.put("b", new String(new int[]{0x1f171}, 0, 1));
        _shortNameToUnicode.put("ab", new String(new int[]{0x1f18e}, 0, 1));
        _shortNameToUnicode.put("cl", new String(new int[]{0x1f191}, 0, 1));
        _shortNameToUnicode.put("o2", new String(new int[]{0x1f17e}, 0, 1));
        _shortNameToUnicode.put("sos", new String(new int[]{0x1f198}, 0, 1));
        _shortNameToUnicode.put("no_entry", new String(new int[]{0x26d4}, 0, 1));
        _shortNameToUnicode.put("name_badge", new String(new int[]{0x1f4db}, 0, 1));
        _shortNameToUnicode.put("no_entry_sign", new String(new int[]{0x1f6ab}, 0, 1));
        _shortNameToUnicode.put("x", new String(new int[]{0x274c}, 0, 1));
        _shortNameToUnicode.put("o", new String(new int[]{0x2b55}, 0, 1));
        _shortNameToUnicode.put("anger", new String(new int[]{0x1f4a2}, 0, 1));
        _shortNameToUnicode.put("hotsprings", new String(new int[]{0x2668}, 0, 1));
        _shortNameToUnicode.put("no_pedestrians", new String(new int[]{0x1f6b7}, 0, 1));
        _shortNameToUnicode.put("do_not_litter", new String(new int[]{0x1f6af}, 0, 1));
        _shortNameToUnicode.put("no_bicycles", new String(new int[]{0x1f6b3}, 0, 1));
        _shortNameToUnicode.put("non-potable_water", new String(new int[]{0x1f6b1}, 0, 1));
        _shortNameToUnicode.put("underage", new String(new int[]{0x1f51e}, 0, 1));
        _shortNameToUnicode.put("no_mobile_phones", new String(new int[]{0x1f4f5}, 0, 1));
        _shortNameToUnicode.put("exclamation", new String(new int[]{0x2757}, 0, 1));
        _shortNameToUnicode.put("grey_exclamation", new String(new int[]{0x2755}, 0, 1));
        _shortNameToUnicode.put("question", new String(new int[]{0x2753}, 0, 1));
        _shortNameToUnicode.put("grey_question", new String(new int[]{0x2754}, 0, 1));
        _shortNameToUnicode.put("bangbang", new String(new int[]{0x203c}, 0, 1));
        _shortNameToUnicode.put("interrobang", new String(new int[]{0x2049}, 0, 1));
        _shortNameToUnicode.put("low_brightness", new String(new int[]{0x1f505}, 0, 1));
        _shortNameToUnicode.put("high_brightness", new String(new int[]{0x1f506}, 0, 1));
        _shortNameToUnicode.put("trident", new String(new int[]{0x1f531}, 0, 1));
        _shortNameToUnicode.put("fleur-de-lis", new String(new int[]{0x269c}, 0, 1));
        _shortNameToUnicode.put("part_alternation_mark", new String(new int[]{0x303d}, 0, 1));
        _shortNameToUnicode.put("warning", new String(new int[]{0x26a0}, 0, 1));
        _shortNameToUnicode.put("children_crossing", new String(new int[]{0x1f6b8}, 0, 1));
        _shortNameToUnicode.put("beginner", new String(new int[]{0x1f530}, 0, 1));
        _shortNameToUnicode.put("recycle", new String(new int[]{0x267b}, 0, 1));
        _shortNameToUnicode.put("u6307", new String(new int[]{0x1f22f}, 0, 1));
        _shortNameToUnicode.put("chart", new String(new int[]{0x1f4b9}, 0, 1));
        _shortNameToUnicode.put("sparkle", new String(new int[]{0x2747}, 0, 1));
        _shortNameToUnicode.put("eight_spoked_asterisk", new String(new int[]{0x2733}, 0, 1));
        _shortNameToUnicode.put("negative_squared_cross_mark",
                new String(new int[]{0x274e}, 0, 1));
        _shortNameToUnicode.put("white_check_mark", new String(new int[]{0x2705}, 0, 1));
        _shortNameToUnicode.put("diamond_shape_with_a_dot_inside",
                new String(new int[]{0x1f4a0}, 0, 1));
        _shortNameToUnicode.put("cyclone", new String(new int[]{0x1f300}, 0, 1));
        _shortNameToUnicode.put("loop", new String(new int[]{0x27bf}, 0, 1));
        _shortNameToUnicode.put("globe_with_meridians", new String(new int[]{0x1f310}, 0, 1));
        _shortNameToUnicode.put("m", new String(new int[]{0x24c2}, 0, 1));
        _shortNameToUnicode.put("atm", new String(new int[]{0x1f3e7}, 0, 1));
        _shortNameToUnicode.put("sa", new String(new int[]{0x1f202}, 0, 1));
        _shortNameToUnicode.put("passport_control", new String(new int[]{0x1f6c2}, 0, 1));
        _shortNameToUnicode.put("customs", new String(new int[]{0x1f6c3}, 0, 1));
        _shortNameToUnicode.put("baggage_claim", new String(new int[]{0x1f6c4}, 0, 1));
        _shortNameToUnicode.put("left_luggage", new String(new int[]{0x1f6c5}, 0, 1));
        _shortNameToUnicode.put("wheelchair", new String(new int[]{0x267f}, 0, 1));
        _shortNameToUnicode.put("no_smoking", new String(new int[]{0x1f6ad}, 0, 1));
        _shortNameToUnicode.put("wc", new String(new int[]{0x1f6be}, 0, 1));
        _shortNameToUnicode.put("parking", new String(new int[]{0x1f17f}, 0, 1));
        _shortNameToUnicode.put("potable_water", new String(new int[]{0x1f6b0}, 0, 1));
        _shortNameToUnicode.put("mens", new String(new int[]{0x1f6b9}, 0, 1));
        _shortNameToUnicode.put("womens", new String(new int[]{0x1f6ba}, 0, 1));
        _shortNameToUnicode.put("baby_symbol", new String(new int[]{0x1f6bc}, 0, 1));
        _shortNameToUnicode.put("restroom", new String(new int[]{0x1f6bb}, 0, 1));
        _shortNameToUnicode.put("put_litter_in_its_place", new String(new int[]{0x1f6ae}, 0, 1));
        _shortNameToUnicode.put("cinema", new String(new int[]{0x1f3a6}, 0, 1));
        _shortNameToUnicode.put("signal_strength", new String(new int[]{0x1f4f6}, 0, 1));
        _shortNameToUnicode.put("koko", new String(new int[]{0x1f201}, 0, 1));
        _shortNameToUnicode.put("ng", new String(new int[]{0x1f196}, 0, 1));
        _shortNameToUnicode.put("ok", new String(new int[]{0x1f197}, 0, 1));
        _shortNameToUnicode.put("up", new String(new int[]{0x1f199}, 0, 1));
        _shortNameToUnicode.put("cool", new String(new int[]{0x1f192}, 0, 1));
        _shortNameToUnicode.put("new", new String(new int[]{0x1f195}, 0, 1));
        _shortNameToUnicode.put("free", new String(new int[]{0x1f193}, 0, 1));
        _shortNameToUnicode.put("zero", new String(new int[]{0x0030, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("one", new String(new int[]{0x0031, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("two", new String(new int[]{0x0032, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("three", new String(new int[]{0x0033, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("four", new String(new int[]{0x0034, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("five", new String(new int[]{0x0035, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("six", new String(new int[]{0x0036, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("seven", new String(new int[]{0x0037, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("eight", new String(new int[]{0x0038, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("nine", new String(new int[]{0x0039, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("keycap_ten", new String(new int[]{0x1f51f}, 0, 1));
        _shortNameToUnicode.put("arrow_forward", new String(new int[]{0x25b6}, 0, 1));
        _shortNameToUnicode.put("pause_button", new String(new int[]{0x23f8}, 0, 1));
        _shortNameToUnicode.put("play_pause", new String(new int[]{0x23ef}, 0, 1));
        _shortNameToUnicode.put("stop_button", new String(new int[]{0x23f9}, 0, 1));
        _shortNameToUnicode.put("record_button", new String(new int[]{0x23fa}, 0, 1));
        _shortNameToUnicode.put("track_next", new String(new int[]{0x23ed}, 0, 1));
        _shortNameToUnicode.put("track_previous", new String(new int[]{0x23ee}, 0, 1));
        _shortNameToUnicode.put("fast_forward", new String(new int[]{0x23e9}, 0, 1));
        _shortNameToUnicode.put("rewind", new String(new int[]{0x23ea}, 0, 1));
        _shortNameToUnicode.put("twisted_rightwards_arrows",
                new String(new int[]{0x1f500}, 0, 1));
        _shortNameToUnicode.put("repeat", new String(new int[]{0x1f501}, 0, 1));
        _shortNameToUnicode.put("repeat_one", new String(new int[]{0x1f502}, 0, 1));
        _shortNameToUnicode.put("arrow_backward", new String(new int[]{0x25c0}, 0, 1));
        _shortNameToUnicode.put("arrow_up_small", new String(new int[]{0x1f53c}, 0, 1));
        _shortNameToUnicode.put("arrow_down_small", new String(new int[]{0x1f53d}, 0, 1));
        _shortNameToUnicode.put("arrow_double_up", new String(new int[]{0x23eb}, 0, 1));
        _shortNameToUnicode.put("arrow_double_down", new String(new int[]{0x23ec}, 0, 1));
        _shortNameToUnicode.put("arrow_right", new String(new int[]{0x27a1}, 0, 1));
        _shortNameToUnicode.put("arrow_left", new String(new int[]{0x2b05}, 0, 1));
        _shortNameToUnicode.put("arrow_up", new String(new int[]{0x2b06}, 0, 1));
        _shortNameToUnicode.put("arrow_down", new String(new int[]{0x2b07}, 0, 1));
        _shortNameToUnicode.put("arrow_upper_right", new String(new int[]{0x2197}, 0, 1));
        _shortNameToUnicode.put("arrow_lower_right", new String(new int[]{0x2198}, 0, 1));
        _shortNameToUnicode.put("arrow_lower_left", new String(new int[]{0x2199}, 0, 1));
        _shortNameToUnicode.put("arrow_upper_left", new String(new int[]{0x2196}, 0, 1));
        _shortNameToUnicode.put("arrow_up_down", new String(new int[]{0x2195}, 0, 1));
        _shortNameToUnicode.put("left_right_arrow", new String(new int[]{0x2194}, 0, 1));
        _shortNameToUnicode.put("arrows_counterclockwise", new String(new int[]{0x1f504}, 0, 1));
        _shortNameToUnicode.put("arrow_right_hook", new String(new int[]{0x21aa}, 0, 1));
        _shortNameToUnicode.put("leftwards_arrow_with_hook",
                new String(new int[]{0x21a9}, 0, 1));
        _shortNameToUnicode.put("arrow_heading_up", new String(new int[]{0x2934}, 0, 1));
        _shortNameToUnicode.put("arrow_heading_down", new String(new int[]{0x2935}, 0, 1));
        _shortNameToUnicode.put("hash", new String(new int[]{0x0023, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("asterisk", new String(new int[]{0x002a, 0x20e3}, 0, 2));
        _shortNameToUnicode.put("information_source", new String(new int[]{0x2139}, 0, 1));
        _shortNameToUnicode.put("abc", new String(new int[]{0x1f524}, 0, 1));
        _shortNameToUnicode.put("abcd", new String(new int[]{0x1f521}, 0, 1));
        _shortNameToUnicode.put("capital_abcd", new String(new int[]{0x1f520}, 0, 1));
        _shortNameToUnicode.put("symbols", new String(new int[]{0x1f523}, 0, 1));
        _shortNameToUnicode.put("musical_note", new String(new int[]{0x1f3b5}, 0, 1));
        _shortNameToUnicode.put("notes", new String(new int[]{0x1f3b6}, 0, 1));
        _shortNameToUnicode.put("wavy_dash", new String(new int[]{0x3030}, 0, 1));
        _shortNameToUnicode.put("curly_loop", new String(new int[]{0x27b0}, 0, 1));
        _shortNameToUnicode.put("heavy_check_mark", new String(new int[]{0x2714}, 0, 1));
        _shortNameToUnicode.put("arrows_clockwise", new String(new int[]{0x1f503}, 0, 1));
        _shortNameToUnicode.put("heavy_plus_sign", new String(new int[]{0x2795}, 0, 1));
        _shortNameToUnicode.put("heavy_minus_sign", new String(new int[]{0x2796}, 0, 1));
        _shortNameToUnicode.put("heavy_division_sign", new String(new int[]{0x2797}, 0, 1));
        _shortNameToUnicode.put("heavy_multiplication_x", new String(new int[]{0x2716}, 0, 1));
        _shortNameToUnicode.put("heavy_dollar_sign", new String(new int[]{0x1f4b2}, 0, 1));
        _shortNameToUnicode.put("currency_exchange", new String(new int[]{0x1f4b1}, 0, 1));
        _shortNameToUnicode.put("copyright", new String(new int[]{0x00a9}, 0, 1));
        _shortNameToUnicode.put("registered", new String(new int[]{0x00ae}, 0, 1));
        _shortNameToUnicode.put("tm", new String(new int[]{0x2122}, 0, 1));
        _shortNameToUnicode.put("end", new String(new int[]{0x1f51a}, 0, 1));
        _shortNameToUnicode.put("back", new String(new int[]{0x1f519}, 0, 1));
        _shortNameToUnicode.put("on", new String(new int[]{0x1f51b}, 0, 1));
        _shortNameToUnicode.put("top", new String(new int[]{0x1f51d}, 0, 1));
        _shortNameToUnicode.put("soon", new String(new int[]{0x1f51c}, 0, 1));
        _shortNameToUnicode.put("ballot_box_with_check", new String(new int[]{0x2611}, 0, 1));
        _shortNameToUnicode.put("radio_button", new String(new int[]{0x1f518}, 0, 1));
        _shortNameToUnicode.put("white_circle", new String(new int[]{0x26aa}, 0, 1));
        _shortNameToUnicode.put("black_circle", new String(new int[]{0x26ab}, 0, 1));
        _shortNameToUnicode.put("red_circle", new String(new int[]{0x1f534}, 0, 1));
        _shortNameToUnicode.put("large_blue_circle", new String(new int[]{0x1f535}, 0, 1));
        _shortNameToUnicode.put("small_orange_diamond", new String(new int[]{0x1f538}, 0, 1));
        _shortNameToUnicode.put("small_blue_diamond", new String(new int[]{0x1f539}, 0, 1));
        _shortNameToUnicode.put("large_orange_diamond", new String(new int[]{0x1f536}, 0, 1));
        _shortNameToUnicode.put("large_blue_diamond", new String(new int[]{0x1f537}, 0, 1));
        _shortNameToUnicode.put("small_red_triangle", new String(new int[]{0x1f53a}, 0, 1));
        _shortNameToUnicode.put("black_small_square", new String(new int[]{0x25aa}, 0, 1));
        _shortNameToUnicode.put("white_small_square", new String(new int[]{0x25ab}, 0, 1));
        _shortNameToUnicode.put("black_large_square", new String(new int[]{0x2b1b}, 0, 1));
        _shortNameToUnicode.put("white_large_square", new String(new int[]{0x2b1c}, 0, 1));
        _shortNameToUnicode.put("small_red_triangle_down", new String(new int[]{0x1f53b}, 0, 1));
        _shortNameToUnicode.put("black_medium_square", new String(new int[]{0x25fc}, 0, 1));
        _shortNameToUnicode.put("white_medium_square", new String(new int[]{0x25fb}, 0, 1));
        _shortNameToUnicode.put("black_medium_small_square",
                new String(new int[]{0x25fe}, 0, 1));
        _shortNameToUnicode.put("white_medium_small_square",
                new String(new int[]{0x25fd}, 0, 1));
        _shortNameToUnicode.put("black_square_button", new String(new int[]{0x1f532}, 0, 1));
        _shortNameToUnicode.put("white_square_button", new String(new int[]{0x1f533}, 0, 1));
        _shortNameToUnicode.put("speaker", new String(new int[]{0x1f508}, 0, 1));
        _shortNameToUnicode.put("sound", new String(new int[]{0x1f509}, 0, 1));
        _shortNameToUnicode.put("loud_sound", new String(new int[]{0x1f50a}, 0, 1));
        _shortNameToUnicode.put("mute", new String(new int[]{0x1f507}, 0, 1));
        _shortNameToUnicode.put("mega", new String(new int[]{0x1f4e3}, 0, 1));
        _shortNameToUnicode.put("loudspeaker", new String(new int[]{0x1f4e2}, 0, 1));
        _shortNameToUnicode.put("bell", new String(new int[]{0x1f514}, 0, 1));
        _shortNameToUnicode.put("no_bell", new String(new int[]{0x1f515}, 0, 1));
        _shortNameToUnicode.put("black_joker", new String(new int[]{0x1f0cf}, 0, 1));
        _shortNameToUnicode.put("mahjong", new String(new int[]{0x1f004}, 0, 1));
        _shortNameToUnicode.put("spades", new String(new int[]{0x2660}, 0, 1));
        _shortNameToUnicode.put("clubs", new String(new int[]{0x2663}, 0, 1));
        _shortNameToUnicode.put("hearts", new String(new int[]{0x2665}, 0, 1));
        _shortNameToUnicode.put("diamonds", new String(new int[]{0x2666}, 0, 1));
        _shortNameToUnicode.put("flower_playing_cards", new String(new int[]{0x1f3b4}, 0, 1));
        _shortNameToUnicode.put("thought_balloon", new String(new int[]{0x1f4ad}, 0, 1));
        _shortNameToUnicode.put("anger_right", new String(new int[]{0x1f5ef}, 0, 1));
        _shortNameToUnicode.put("speech_balloon", new String(new int[]{0x1f4ac}, 0, 1));
        _shortNameToUnicode.put("clock1", new String(new int[]{0x1f550}, 0, 1));
        _shortNameToUnicode.put("clock2", new String(new int[]{0x1f551}, 0, 1));
        _shortNameToUnicode.put("clock3", new String(new int[]{0x1f552}, 0, 1));
        _shortNameToUnicode.put("clock4", new String(new int[]{0x1f553}, 0, 1));
        _shortNameToUnicode.put("clock5", new String(new int[]{0x1f554}, 0, 1));
        _shortNameToUnicode.put("clock6", new String(new int[]{0x1f555}, 0, 1));
        _shortNameToUnicode.put("clock7", new String(new int[]{0x1f556}, 0, 1));
        _shortNameToUnicode.put("clock8", new String(new int[]{0x1f557}, 0, 1));
        _shortNameToUnicode.put("clock9", new String(new int[]{0x1f558}, 0, 1));
        _shortNameToUnicode.put("clock10", new String(new int[]{0x1f559}, 0, 1));
        _shortNameToUnicode.put("clock11", new String(new int[]{0x1f55a}, 0, 1));
        _shortNameToUnicode.put("clock12", new String(new int[]{0x1f55b}, 0, 1));
        _shortNameToUnicode.put("clock130", new String(new int[]{0x1f55c}, 0, 1));
        _shortNameToUnicode.put("clock230", new String(new int[]{0x1f55d}, 0, 1));
        _shortNameToUnicode.put("clock330", new String(new int[]{0x1f55e}, 0, 1));
        _shortNameToUnicode.put("clock430", new String(new int[]{0x1f55f}, 0, 1));
        _shortNameToUnicode.put("clock530", new String(new int[]{0x1f560}, 0, 1));
        _shortNameToUnicode.put("clock630", new String(new int[]{0x1f561}, 0, 1));
        _shortNameToUnicode.put("clock730", new String(new int[]{0x1f562}, 0, 1));
        _shortNameToUnicode.put("clock830", new String(new int[]{0x1f563}, 0, 1));
        _shortNameToUnicode.put("clock930", new String(new int[]{0x1f564}, 0, 1));
        _shortNameToUnicode.put("clock1030", new String(new int[]{0x1f565}, 0, 1));
        _shortNameToUnicode.put("clock1130", new String(new int[]{0x1f566}, 0, 1));
        _shortNameToUnicode.put("clock1230", new String(new int[]{0x1f567}, 0, 1));
        _shortNameToUnicode.put("eye_in_speech_bubble",
                new String(new int[]{0x1f441, 0x1f5e8}, 0, 2));
        _shortNameToUnicode.put("flag_ac", new String(new int[]{0x1f1e6, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_af", new String(new int[]{0x1f1e6, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_al", new String(new int[]{0x1f1e6, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_dz", new String(new int[]{0x1f1e9, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_ad", new String(new int[]{0x1f1e6, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_ao", new String(new int[]{0x1f1e6, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_ai", new String(new int[]{0x1f1e6, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_ag", new String(new int[]{0x1f1e6, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_ar", new String(new int[]{0x1f1e6, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_am", new String(new int[]{0x1f1e6, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_aw", new String(new int[]{0x1f1e6, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_au", new String(new int[]{0x1f1e6, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_at", new String(new int[]{0x1f1e6, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_az", new String(new int[]{0x1f1e6, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_bs", new String(new int[]{0x1f1e7, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_bh", new String(new int[]{0x1f1e7, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_bd", new String(new int[]{0x1f1e7, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_bb", new String(new int[]{0x1f1e7, 0x1f1e7}, 0, 2));
        _shortNameToUnicode.put("flag_by", new String(new int[]{0x1f1e7, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_be", new String(new int[]{0x1f1e7, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_bz", new String(new int[]{0x1f1e7, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_bj", new String(new int[]{0x1f1e7, 0x1f1ef}, 0, 2));
        _shortNameToUnicode.put("flag_bm", new String(new int[]{0x1f1e7, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_bt", new String(new int[]{0x1f1e7, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_bo", new String(new int[]{0x1f1e7, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_ba", new String(new int[]{0x1f1e7, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_bw", new String(new int[]{0x1f1e7, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_br", new String(new int[]{0x1f1e7, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_bn", new String(new int[]{0x1f1e7, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_bg", new String(new int[]{0x1f1e7, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_bf", new String(new int[]{0x1f1e7, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_bi", new String(new int[]{0x1f1e7, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_cv", new String(new int[]{0x1f1e8, 0x1f1fb}, 0, 2));
        _shortNameToUnicode.put("flag_kh", new String(new int[]{0x1f1f0, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_cm", new String(new int[]{0x1f1e8, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_ca", new String(new int[]{0x1f1e8, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_ky", new String(new int[]{0x1f1f0, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_cf", new String(new int[]{0x1f1e8, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_td", new String(new int[]{0x1f1f9, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_cl", new String(new int[]{0x1f1e8, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_cn", new String(new int[]{0x1f1e8, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_co", new String(new int[]{0x1f1e8, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_km", new String(new int[]{0x1f1f0, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_cg", new String(new int[]{0x1f1e8, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_cd", new String(new int[]{0x1f1e8, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_cr", new String(new int[]{0x1f1e8, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_hr", new String(new int[]{0x1f1ed, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_cu", new String(new int[]{0x1f1e8, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_cy", new String(new int[]{0x1f1e8, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_cz", new String(new int[]{0x1f1e8, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_dk", new String(new int[]{0x1f1e9, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_dj", new String(new int[]{0x1f1e9, 0x1f1ef}, 0, 2));
        _shortNameToUnicode.put("flag_dm", new String(new int[]{0x1f1e9, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_do", new String(new int[]{0x1f1e9, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_ec", new String(new int[]{0x1f1ea, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_eg", new String(new int[]{0x1f1ea, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_sv", new String(new int[]{0x1f1f8, 0x1f1fb}, 0, 2));
        _shortNameToUnicode.put("flag_gq", new String(new int[]{0x1f1ec, 0x1f1f6}, 0, 2));
        _shortNameToUnicode.put("flag_er", new String(new int[]{0x1f1ea, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_ee", new String(new int[]{0x1f1ea, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_et", new String(new int[]{0x1f1ea, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_fk", new String(new int[]{0x1f1eb, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_fo", new String(new int[]{0x1f1eb, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_fj", new String(new int[]{0x1f1eb, 0x1f1ef}, 0, 2));
        _shortNameToUnicode.put("flag_fi", new String(new int[]{0x1f1eb, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_fr", new String(new int[]{0x1f1eb, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_pf", new String(new int[]{0x1f1f5, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_ga", new String(new int[]{0x1f1ec, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_gm", new String(new int[]{0x1f1ec, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_ge", new String(new int[]{0x1f1ec, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_de", new String(new int[]{0x1f1e9, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_gh", new String(new int[]{0x1f1ec, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_gi", new String(new int[]{0x1f1ec, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_gr", new String(new int[]{0x1f1ec, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_gl", new String(new int[]{0x1f1ec, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_gd", new String(new int[]{0x1f1ec, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_gu", new String(new int[]{0x1f1ec, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_gt", new String(new int[]{0x1f1ec, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_gn", new String(new int[]{0x1f1ec, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_gw", new String(new int[]{0x1f1ec, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_gy", new String(new int[]{0x1f1ec, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_ht", new String(new int[]{0x1f1ed, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_hn", new String(new int[]{0x1f1ed, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_hk", new String(new int[]{0x1f1ed, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_hu", new String(new int[]{0x1f1ed, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_is", new String(new int[]{0x1f1ee, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_in", new String(new int[]{0x1f1ee, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_id", new String(new int[]{0x1f1ee, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_ir", new String(new int[]{0x1f1ee, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_iq", new String(new int[]{0x1f1ee, 0x1f1f6}, 0, 2));
        _shortNameToUnicode.put("flag_ie", new String(new int[]{0x1f1ee, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_il", new String(new int[]{0x1f1ee, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_it", new String(new int[]{0x1f1ee, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_ci", new String(new int[]{0x1f1e8, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_jm", new String(new int[]{0x1f1ef, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_jp", new String(new int[]{0x1f1ef, 0x1f1f5}, 0, 2));
        _shortNameToUnicode.put("flag_je", new String(new int[]{0x1f1ef, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_jo", new String(new int[]{0x1f1ef, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_kz", new String(new int[]{0x1f1f0, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_ke", new String(new int[]{0x1f1f0, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_ki", new String(new int[]{0x1f1f0, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_xk", new String(new int[]{0x1f1fd, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_kw", new String(new int[]{0x1f1f0, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_kg", new String(new int[]{0x1f1f0, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_la", new String(new int[]{0x1f1f1, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_lv", new String(new int[]{0x1f1f1, 0x1f1fb}, 0, 2));
        _shortNameToUnicode.put("flag_lb", new String(new int[]{0x1f1f1, 0x1f1e7}, 0, 2));
        _shortNameToUnicode.put("flag_ls", new String(new int[]{0x1f1f1, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_lr", new String(new int[]{0x1f1f1, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_ly", new String(new int[]{0x1f1f1, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_li", new String(new int[]{0x1f1f1, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_lt", new String(new int[]{0x1f1f1, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_lu", new String(new int[]{0x1f1f1, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_mo", new String(new int[]{0x1f1f2, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_mk", new String(new int[]{0x1f1f2, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_mg", new String(new int[]{0x1f1f2, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_mw", new String(new int[]{0x1f1f2, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_my", new String(new int[]{0x1f1f2, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_mv", new String(new int[]{0x1f1f2, 0x1f1fb}, 0, 2));
        _shortNameToUnicode.put("flag_ml", new String(new int[]{0x1f1f2, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_mt", new String(new int[]{0x1f1f2, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_mh", new String(new int[]{0x1f1f2, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_mr", new String(new int[]{0x1f1f2, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_mu", new String(new int[]{0x1f1f2, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_mx", new String(new int[]{0x1f1f2, 0x1f1fd}, 0, 2));
        _shortNameToUnicode.put("flag_fm", new String(new int[]{0x1f1eb, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_md", new String(new int[]{0x1f1f2, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_mc", new String(new int[]{0x1f1f2, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_mn", new String(new int[]{0x1f1f2, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_me", new String(new int[]{0x1f1f2, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_ms", new String(new int[]{0x1f1f2, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_ma", new String(new int[]{0x1f1f2, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_mz", new String(new int[]{0x1f1f2, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_mm", new String(new int[]{0x1f1f2, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_na", new String(new int[]{0x1f1f3, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_nr", new String(new int[]{0x1f1f3, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_np", new String(new int[]{0x1f1f3, 0x1f1f5}, 0, 2));
        _shortNameToUnicode.put("flag_nl", new String(new int[]{0x1f1f3, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_nc", new String(new int[]{0x1f1f3, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_nz", new String(new int[]{0x1f1f3, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_ni", new String(new int[]{0x1f1f3, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_ne", new String(new int[]{0x1f1f3, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_ng", new String(new int[]{0x1f1f3, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_nu", new String(new int[]{0x1f1f3, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_kp", new String(new int[]{0x1f1f0, 0x1f1f5}, 0, 2));
        _shortNameToUnicode.put("flag_no", new String(new int[]{0x1f1f3, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_om", new String(new int[]{0x1f1f4, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_pk", new String(new int[]{0x1f1f5, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_pw", new String(new int[]{0x1f1f5, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_ps", new String(new int[]{0x1f1f5, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_pa", new String(new int[]{0x1f1f5, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_pg", new String(new int[]{0x1f1f5, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_py", new String(new int[]{0x1f1f5, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_pe", new String(new int[]{0x1f1f5, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_ph", new String(new int[]{0x1f1f5, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_pl", new String(new int[]{0x1f1f5, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_pt", new String(new int[]{0x1f1f5, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_pr", new String(new int[]{0x1f1f5, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_qa", new String(new int[]{0x1f1f6, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_ro", new String(new int[]{0x1f1f7, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_ru", new String(new int[]{0x1f1f7, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_rw", new String(new int[]{0x1f1f7, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_sh", new String(new int[]{0x1f1f8, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_kn", new String(new int[]{0x1f1f0, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_lc", new String(new int[]{0x1f1f1, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_vc", new String(new int[]{0x1f1fb, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_ws", new String(new int[]{0x1f1fc, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_sm", new String(new int[]{0x1f1f8, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_st", new String(new int[]{0x1f1f8, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_sa", new String(new int[]{0x1f1f8, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_sn", new String(new int[]{0x1f1f8, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_rs", new String(new int[]{0x1f1f7, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_sc", new String(new int[]{0x1f1f8, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_sl", new String(new int[]{0x1f1f8, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_sg", new String(new int[]{0x1f1f8, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_sk", new String(new int[]{0x1f1f8, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_si", new String(new int[]{0x1f1f8, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_sb", new String(new int[]{0x1f1f8, 0x1f1e7}, 0, 2));
        _shortNameToUnicode.put("flag_so", new String(new int[]{0x1f1f8, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_za", new String(new int[]{0x1f1ff, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_kr", new String(new int[]{0x1f1f0, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_es", new String(new int[]{0x1f1ea, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_lk", new String(new int[]{0x1f1f1, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_sd", new String(new int[]{0x1f1f8, 0x1f1e9}, 0, 2));
        _shortNameToUnicode.put("flag_sr", new String(new int[]{0x1f1f8, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_sz", new String(new int[]{0x1f1f8, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_se", new String(new int[]{0x1f1f8, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_ch", new String(new int[]{0x1f1e8, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_sy", new String(new int[]{0x1f1f8, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_tw", new String(new int[]{0x1f1f9, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_tj", new String(new int[]{0x1f1f9, 0x1f1ef}, 0, 2));
        _shortNameToUnicode.put("flag_tz", new String(new int[]{0x1f1f9, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_th", new String(new int[]{0x1f1f9, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_tl", new String(new int[]{0x1f1f9, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_tg", new String(new int[]{0x1f1f9, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_to", new String(new int[]{0x1f1f9, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_tt", new String(new int[]{0x1f1f9, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_tn", new String(new int[]{0x1f1f9, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_tr", new String(new int[]{0x1f1f9, 0x1f1f7}, 0, 2));
        _shortNameToUnicode.put("flag_tm", new String(new int[]{0x1f1f9, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_tv", new String(new int[]{0x1f1f9, 0x1f1fb}, 0, 2));
        _shortNameToUnicode.put("flag_ug", new String(new int[]{0x1f1fa, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_ua", new String(new int[]{0x1f1fa, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_ae", new String(new int[]{0x1f1e6, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_gb", new String(new int[]{0x1f1ec, 0x1f1e7}, 0, 2));
        _shortNameToUnicode.put("flag_us", new String(new int[]{0x1f1fa, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_vi", new String(new int[]{0x1f1fb, 0x1f1ee}, 0, 2));
        _shortNameToUnicode.put("flag_uy", new String(new int[]{0x1f1fa, 0x1f1fe}, 0, 2));
        _shortNameToUnicode.put("flag_uz", new String(new int[]{0x1f1fa, 0x1f1ff}, 0, 2));
        _shortNameToUnicode.put("flag_vu", new String(new int[]{0x1f1fb, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_va", new String(new int[]{0x1f1fb, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_ve", new String(new int[]{0x1f1fb, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_vn", new String(new int[]{0x1f1fb, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_wf", new String(new int[]{0x1f1fc, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_eh", new String(new int[]{0x1f1ea, 0x1f1ed}, 0, 2));
        _shortNameToUnicode.put("flag_ye", new String(new int[]{0x1f1fe, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_zm", new String(new int[]{0x1f1ff, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_zw", new String(new int[]{0x1f1ff, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_re", new String(new int[]{0x1f1f7, 0x1f1ea}, 0, 2));
        _shortNameToUnicode.put("flag_ax", new String(new int[]{0x1f1e6, 0x1f1fd}, 0, 2));
        _shortNameToUnicode.put("flag_ta", new String(new int[]{0x1f1f9, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_io", new String(new int[]{0x1f1ee, 0x1f1f4}, 0, 2));
        _shortNameToUnicode.put("flag_bq", new String(new int[]{0x1f1e7, 0x1f1f6}, 0, 2));
        _shortNameToUnicode.put("flag_cx", new String(new int[]{0x1f1e8, 0x1f1fd}, 0, 2));
        _shortNameToUnicode.put("flag_cc", new String(new int[]{0x1f1e8, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_gg", new String(new int[]{0x1f1ec, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_im", new String(new int[]{0x1f1ee, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_yt", new String(new int[]{0x1f1fe, 0x1f1f9}, 0, 2));
        _shortNameToUnicode.put("flag_nf", new String(new int[]{0x1f1f3, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_pn", new String(new int[]{0x1f1f5, 0x1f1f3}, 0, 2));
        _shortNameToUnicode.put("flag_bl", new String(new int[]{0x1f1e7, 0x1f1f1}, 0, 2));
        _shortNameToUnicode.put("flag_pm", new String(new int[]{0x1f1f5, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_gs", new String(new int[]{0x1f1ec, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_tk", new String(new int[]{0x1f1f9, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_bv", new String(new int[]{0x1f1e7, 0x1f1fb}, 0, 2));
        _shortNameToUnicode.put("flag_hm", new String(new int[]{0x1f1ed, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_sj", new String(new int[]{0x1f1f8, 0x1f1ef}, 0, 2));
        _shortNameToUnicode.put("flag_um", new String(new int[]{0x1f1fa, 0x1f1f2}, 0, 2));
        _shortNameToUnicode.put("flag_ic", new String(new int[]{0x1f1ee, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_ea", new String(new int[]{0x1f1ea, 0x1f1e6}, 0, 2));
        _shortNameToUnicode.put("flag_cp", new String(new int[]{0x1f1e8, 0x1f1f5}, 0, 2));
        _shortNameToUnicode.put("flag_dg", new String(new int[]{0x1f1e9, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_as", new String(new int[]{0x1f1e6, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_aq", new String(new int[]{0x1f1e6, 0x1f1f6}, 0, 2));
        _shortNameToUnicode.put("flag_vg", new String(new int[]{0x1f1fb, 0x1f1ec}, 0, 2));
        _shortNameToUnicode.put("flag_ck", new String(new int[]{0x1f1e8, 0x1f1f0}, 0, 2));
        _shortNameToUnicode.put("flag_cw", new String(new int[]{0x1f1e8, 0x1f1fc}, 0, 2));
        _shortNameToUnicode.put("flag_eu", new String(new int[]{0x1f1ea, 0x1f1fa}, 0, 2));
        _shortNameToUnicode.put("flag_gf", new String(new int[]{0x1f1ec, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_tf", new String(new int[]{0x1f1f9, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("flag_gp", new String(new int[]{0x1f1ec, 0x1f1f5}, 0, 2));
        _shortNameToUnicode.put("flag_mq", new String(new int[]{0x1f1f2, 0x1f1f6}, 0, 2));
        _shortNameToUnicode.put("flag_mp", new String(new int[]{0x1f1f2, 0x1f1f5}, 0, 2));
        _shortNameToUnicode.put("flag_sx", new String(new int[]{0x1f1f8, 0x1f1fd}, 0, 2));
        _shortNameToUnicode.put("flag_ss", new String(new int[]{0x1f1f8, 0x1f1f8}, 0, 2));
        _shortNameToUnicode.put("flag_tc", new String(new int[]{0x1f1f9, 0x1f1e8}, 0, 2));
        _shortNameToUnicode.put("flag_mf", new String(new int[]{0x1f1f2, 0x1f1eb}, 0, 2));
        _shortNameToUnicode.put("raised_hands_tone1",
                new String(new int[]{0x1f64c, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("raised_hands_tone2",
                new String(new int[]{0x1f64c, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("raised_hands_tone3",
                new String(new int[]{0x1f64c, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("raised_hands_tone4",
                new String(new int[]{0x1f64c, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("raised_hands_tone5",
                new String(new int[]{0x1f64c, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("clap_tone1", new String(new int[]{0x1f44f, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("clap_tone2", new String(new int[]{0x1f44f, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("clap_tone3", new String(new int[]{0x1f44f, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("clap_tone4", new String(new int[]{0x1f44f, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("clap_tone5", new String(new int[]{0x1f44f, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("wave_tone1", new String(new int[]{0x1f44b, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("wave_tone2", new String(new int[]{0x1f44b, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("wave_tone3", new String(new int[]{0x1f44b, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("wave_tone4", new String(new int[]{0x1f44b, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("wave_tone5", new String(new int[]{0x1f44b, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("thumbsup_tone1", new String(new int[]{0x1f44d, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("thumbsup_tone2", new String(new int[]{0x1f44d, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("thumbsup_tone3", new String(new int[]{0x1f44d, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("thumbsup_tone4", new String(new int[]{0x1f44d, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("thumbsup_tone5", new String(new int[]{0x1f44d, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("thumbsdown_tone1",
                new String(new int[]{0x1f44e, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("thumbsdown_tone2",
                new String(new int[]{0x1f44e, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("thumbsdown_tone3",
                new String(new int[]{0x1f44e, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("thumbsdown_tone4",
                new String(new int[]{0x1f44e, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("thumbsdown_tone5",
                new String(new int[]{0x1f44e, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("punch_tone1", new String(new int[]{0x1f44a, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("punch_tone2", new String(new int[]{0x1f44a, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("punch_tone3", new String(new int[]{0x1f44a, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("punch_tone4", new String(new int[]{0x1f44a, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("punch_tone5", new String(new int[]{0x1f44a, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("fist_tone1", new String(new int[]{0x270a, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("fist_tone2", new String(new int[]{0x270a, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("fist_tone3", new String(new int[]{0x270a, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("fist_tone4", new String(new int[]{0x270a, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("fist_tone5", new String(new int[]{0x270a, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("v_tone1", new String(new int[]{0x270c, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("v_tone2", new String(new int[]{0x270c, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("v_tone3", new String(new int[]{0x270c, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("v_tone4", new String(new int[]{0x270c, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("v_tone5", new String(new int[]{0x270c, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("ok_hand_tone1", new String(new int[]{0x1f44c, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("ok_hand_tone2", new String(new int[]{0x1f44c, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("ok_hand_tone3", new String(new int[]{0x1f44c, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("ok_hand_tone4", new String(new int[]{0x1f44c, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("ok_hand_tone5", new String(new int[]{0x1f44c, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("raised_hand_tone1",
                new String(new int[]{0x270b, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("raised_hand_tone2",
                new String(new int[]{0x270b, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("raised_hand_tone3",
                new String(new int[]{0x270b, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("raised_hand_tone4",
                new String(new int[]{0x270b, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("raised_hand_tone5",
                new String(new int[]{0x270b, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("open_hands_tone1",
                new String(new int[]{0x1f450, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("open_hands_tone2",
                new String(new int[]{0x1f450, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("open_hands_tone3",
                new String(new int[]{0x1f450, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("open_hands_tone4",
                new String(new int[]{0x1f450, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("open_hands_tone5",
                new String(new int[]{0x1f450, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("muscle_tone1", new String(new int[]{0x1f4aa, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("muscle_tone2", new String(new int[]{0x1f4aa, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("muscle_tone3", new String(new int[]{0x1f4aa, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("muscle_tone4", new String(new int[]{0x1f4aa, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("muscle_tone5", new String(new int[]{0x1f4aa, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("pray_tone1", new String(new int[]{0x1f64f, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("pray_tone2", new String(new int[]{0x1f64f, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("pray_tone3", new String(new int[]{0x1f64f, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("pray_tone4", new String(new int[]{0x1f64f, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("pray_tone5", new String(new int[]{0x1f64f, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("point_up_tone1", new String(new int[]{0x261d, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("point_up_tone2", new String(new int[]{0x261d, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("point_up_tone3", new String(new int[]{0x261d, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("point_up_tone4", new String(new int[]{0x261d, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("point_up_tone5", new String(new int[]{0x261d, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("point_up_2_tone1",
                new String(new int[]{0x1f446, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("point_up_2_tone2",
                new String(new int[]{0x1f446, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("point_up_2_tone3",
                new String(new int[]{0x1f446, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("point_up_2_tone4",
                new String(new int[]{0x1f446, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("point_up_2_tone5",
                new String(new int[]{0x1f446, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("point_down_tone1",
                new String(new int[]{0x1f447, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("point_down_tone2",
                new String(new int[]{0x1f447, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("point_down_tone3",
                new String(new int[]{0x1f447, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("point_down_tone4",
                new String(new int[]{0x1f447, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("point_down_tone5",
                new String(new int[]{0x1f447, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("point_left_tone1",
                new String(new int[]{0x1f448, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("point_left_tone2",
                new String(new int[]{0x1f448, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("point_left_tone3",
                new String(new int[]{0x1f448, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("point_left_tone4",
                new String(new int[]{0x1f448, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("point_left_tone5",
                new String(new int[]{0x1f448, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("point_right_tone1",
                new String(new int[]{0x1f449, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("point_right_tone2",
                new String(new int[]{0x1f449, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("point_right_tone3",
                new String(new int[]{0x1f449, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("point_right_tone4",
                new String(new int[]{0x1f449, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("point_right_tone5",
                new String(new int[]{0x1f449, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("middle_finger_tone1",
                new String(new int[]{0x1f595, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("middle_finger_tone2",
                new String(new int[]{0x1f595, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("middle_finger_tone3",
                new String(new int[]{0x1f595, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("middle_finger_tone4",
                new String(new int[]{0x1f595, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("middle_finger_tone5",
                new String(new int[]{0x1f595, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("hand_splayed_tone1",
                new String(new int[]{0x1f590, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("hand_splayed_tone2",
                new String(new int[]{0x1f590, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("hand_splayed_tone3",
                new String(new int[]{0x1f590, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("hand_splayed_tone4",
                new String(new int[]{0x1f590, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("hand_splayed_tone5",
                new String(new int[]{0x1f590, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("metal_tone1", new String(new int[]{0x1f918, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("metal_tone2", new String(new int[]{0x1f918, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("metal_tone3", new String(new int[]{0x1f918, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("metal_tone4", new String(new int[]{0x1f918, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("metal_tone5", new String(new int[]{0x1f918, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("vulcan_tone1", new String(new int[]{0x1f596, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("vulcan_tone2", new String(new int[]{0x1f596, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("vulcan_tone3", new String(new int[]{0x1f596, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("vulcan_tone4", new String(new int[]{0x1f596, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("vulcan_tone5", new String(new int[]{0x1f596, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("writing_hand_tone1",
                new String(new int[]{0x270d, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("writing_hand_tone2",
                new String(new int[]{0x270d, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("writing_hand_tone3",
                new String(new int[]{0x270d, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("writing_hand_tone4",
                new String(new int[]{0x270d, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("writing_hand_tone5",
                new String(new int[]{0x270d, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("nail_care_tone1",
                new String(new int[]{0x1f485, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("nail_care_tone2",
                new String(new int[]{0x1f485, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("nail_care_tone3",
                new String(new int[]{0x1f485, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("nail_care_tone4",
                new String(new int[]{0x1f485, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("nail_care_tone5",
                new String(new int[]{0x1f485, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("ear_tone1", new String(new int[]{0x1f442, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("ear_tone2", new String(new int[]{0x1f442, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("ear_tone3", new String(new int[]{0x1f442, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("ear_tone4", new String(new int[]{0x1f442, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("ear_tone5", new String(new int[]{0x1f442, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("nose_tone1", new String(new int[]{0x1f443, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("nose_tone2", new String(new int[]{0x1f443, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("nose_tone3", new String(new int[]{0x1f443, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("nose_tone4", new String(new int[]{0x1f443, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("nose_tone5", new String(new int[]{0x1f443, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("baby_tone1", new String(new int[]{0x1f476, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("baby_tone2", new String(new int[]{0x1f476, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("baby_tone3", new String(new int[]{0x1f476, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("baby_tone4", new String(new int[]{0x1f476, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("baby_tone5", new String(new int[]{0x1f476, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("boy_tone1", new String(new int[]{0x1f466, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("boy_tone2", new String(new int[]{0x1f466, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("boy_tone3", new String(new int[]{0x1f466, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("boy_tone4", new String(new int[]{0x1f466, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("boy_tone5", new String(new int[]{0x1f466, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("girl_tone1", new String(new int[]{0x1f467, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("girl_tone2", new String(new int[]{0x1f467, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("girl_tone3", new String(new int[]{0x1f467, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("girl_tone4", new String(new int[]{0x1f467, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("girl_tone5", new String(new int[]{0x1f467, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("man_tone1", new String(new int[]{0x1f468, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("man_tone2", new String(new int[]{0x1f468, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("man_tone3", new String(new int[]{0x1f468, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("man_tone4", new String(new int[]{0x1f468, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("man_tone5", new String(new int[]{0x1f468, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("woman_tone1", new String(new int[]{0x1f469, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("woman_tone2", new String(new int[]{0x1f469, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("woman_tone3", new String(new int[]{0x1f469, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("woman_tone4", new String(new int[]{0x1f469, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("woman_tone5", new String(new int[]{0x1f469, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("person_with_blond_hair_tone1",
                new String(new int[]{0x1f471, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("person_with_blond_hair_tone2",
                new String(new int[]{0x1f471, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("person_with_blond_hair_tone3",
                new String(new int[]{0x1f471, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("person_with_blond_hair_tone4",
                new String(new int[]{0x1f471, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("person_with_blond_hair_tone5",
                new String(new int[]{0x1f471, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("older_man_tone1",
                new String(new int[]{0x1f474, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("older_man_tone2",
                new String(new int[]{0x1f474, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("older_man_tone3",
                new String(new int[]{0x1f474, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("older_man_tone4",
                new String(new int[]{0x1f474, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("older_man_tone5",
                new String(new int[]{0x1f474, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("older_woman_tone1",
                new String(new int[]{0x1f475, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("older_woman_tone2",
                new String(new int[]{0x1f475, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("older_woman_tone3",
                new String(new int[]{0x1f475, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("older_woman_tone4",
                new String(new int[]{0x1f475, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("older_woman_tone5",
                new String(new int[]{0x1f475, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("man_with_gua_pi_mao_tone1",
                new String(new int[]{0x1f472, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("man_with_gua_pi_mao_tone2",
                new String(new int[]{0x1f472, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("man_with_gua_pi_mao_tone3",
                new String(new int[]{0x1f472, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("man_with_gua_pi_mao_tone4",
                new String(new int[]{0x1f472, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("man_with_gua_pi_mao_tone5",
                new String(new int[]{0x1f472, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("man_with_turban_tone1",
                new String(new int[]{0x1f473, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("man_with_turban_tone2",
                new String(new int[]{0x1f473, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("man_with_turban_tone3",
                new String(new int[]{0x1f473, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("man_with_turban_tone4",
                new String(new int[]{0x1f473, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("man_with_turban_tone5",
                new String(new int[]{0x1f473, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("cop_tone1", new String(new int[]{0x1f46e, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("cop_tone2", new String(new int[]{0x1f46e, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("cop_tone3", new String(new int[]{0x1f46e, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("cop_tone4", new String(new int[]{0x1f46e, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("cop_tone5", new String(new int[]{0x1f46e, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("construction_worker_tone1",
                new String(new int[]{0x1f477, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("construction_worker_tone2",
                new String(new int[]{0x1f477, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("construction_worker_tone3",
                new String(new int[]{0x1f477, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("construction_worker_tone4",
                new String(new int[]{0x1f477, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("construction_worker_tone5",
                new String(new int[]{0x1f477, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("guardsman_tone1",
                new String(new int[]{0x1f482, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("guardsman_tone2",
                new String(new int[]{0x1f482, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("guardsman_tone3",
                new String(new int[]{0x1f482, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("guardsman_tone4",
                new String(new int[]{0x1f482, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("guardsman_tone5",
                new String(new int[]{0x1f482, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("santa_tone1", new String(new int[]{0x1f385, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("santa_tone2", new String(new int[]{0x1f385, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("santa_tone3", new String(new int[]{0x1f385, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("santa_tone4", new String(new int[]{0x1f385, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("santa_tone5", new String(new int[]{0x1f385, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("angel_tone1", new String(new int[]{0x1f47c, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("angel_tone2", new String(new int[]{0x1f47c, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("angel_tone3", new String(new int[]{0x1f47c, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("angel_tone4", new String(new int[]{0x1f47c, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("angel_tone5", new String(new int[]{0x1f47c, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("princess_tone1", new String(new int[]{0x1f478, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("princess_tone2", new String(new int[]{0x1f478, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("princess_tone3", new String(new int[]{0x1f478, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("princess_tone4", new String(new int[]{0x1f478, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("princess_tone5", new String(new int[]{0x1f478, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("bride_with_veil_tone1",
                new String(new int[]{0x1f470, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("bride_with_veil_tone2",
                new String(new int[]{0x1f470, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("bride_with_veil_tone3",
                new String(new int[]{0x1f470, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("bride_with_veil_tone4",
                new String(new int[]{0x1f470, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("bride_with_veil_tone5",
                new String(new int[]{0x1f470, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("walking_tone1", new String(new int[]{0x1f6b6, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("walking_tone2", new String(new int[]{0x1f6b6, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("walking_tone3", new String(new int[]{0x1f6b6, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("walking_tone4", new String(new int[]{0x1f6b6, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("walking_tone5", new String(new int[]{0x1f6b6, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("runner_tone1", new String(new int[]{0x1f3c3, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("runner_tone2", new String(new int[]{0x1f3c3, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("runner_tone3", new String(new int[]{0x1f3c3, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("runner_tone4", new String(new int[]{0x1f3c3, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("runner_tone5", new String(new int[]{0x1f3c3, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("dancer_tone1", new String(new int[]{0x1f483, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("dancer_tone2", new String(new int[]{0x1f483, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("dancer_tone3", new String(new int[]{0x1f483, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("dancer_tone4", new String(new int[]{0x1f483, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("dancer_tone5", new String(new int[]{0x1f483, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("bow_tone1", new String(new int[]{0x1f647, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("bow_tone2", new String(new int[]{0x1f647, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("bow_tone3", new String(new int[]{0x1f647, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("bow_tone4", new String(new int[]{0x1f647, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("bow_tone5", new String(new int[]{0x1f647, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("information_desk_person_tone1",
                new String(new int[]{0x1f481, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("information_desk_person_tone2",
                new String(new int[]{0x1f481, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("information_desk_person_tone3",
                new String(new int[]{0x1f481, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("information_desk_person_tone4",
                new String(new int[]{0x1f481, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("information_desk_person_tone5",
                new String(new int[]{0x1f481, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("no_good_tone1", new String(new int[]{0x1f645, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("no_good_tone2", new String(new int[]{0x1f645, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("no_good_tone3", new String(new int[]{0x1f645, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("no_good_tone4", new String(new int[]{0x1f645, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("no_good_tone5", new String(new int[]{0x1f645, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("ok_woman_tone1", new String(new int[]{0x1f646, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("ok_woman_tone2", new String(new int[]{0x1f646, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("ok_woman_tone3", new String(new int[]{0x1f646, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("ok_woman_tone4", new String(new int[]{0x1f646, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("ok_woman_tone5", new String(new int[]{0x1f646, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("raising_hand_tone1",
                new String(new int[]{0x1f64b, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("raising_hand_tone2",
                new String(new int[]{0x1f64b, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("raising_hand_tone3",
                new String(new int[]{0x1f64b, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("raising_hand_tone4",
                new String(new int[]{0x1f64b, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("raising_hand_tone5",
                new String(new int[]{0x1f64b, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("person_with_pouting_face_tone1",
                new String(new int[]{0x1f64e, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("person_with_pouting_face_tone2",
                new String(new int[]{0x1f64e, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("person_with_pouting_face_tone3",
                new String(new int[]{0x1f64e, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("person_with_pouting_face_tone4",
                new String(new int[]{0x1f64e, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("person_with_pouting_face_tone5",
                new String(new int[]{0x1f64e, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("person_frowning_tone1",
                new String(new int[]{0x1f64d, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("person_frowning_tone2",
                new String(new int[]{0x1f64d, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("person_frowning_tone3",
                new String(new int[]{0x1f64d, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("person_frowning_tone4",
                new String(new int[]{0x1f64d, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("person_frowning_tone5",
                new String(new int[]{0x1f64d, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("haircut_tone1", new String(new int[]{0x1f487, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("haircut_tone2", new String(new int[]{0x1f487, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("haircut_tone3", new String(new int[]{0x1f487, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("haircut_tone4", new String(new int[]{0x1f487, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("haircut_tone5", new String(new int[]{0x1f487, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("massage_tone1", new String(new int[]{0x1f486, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("massage_tone2", new String(new int[]{0x1f486, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("massage_tone3", new String(new int[]{0x1f486, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("massage_tone4", new String(new int[]{0x1f486, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("massage_tone5", new String(new int[]{0x1f486, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("rowboat_tone1", new String(new int[]{0x1f6a3, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("rowboat_tone2", new String(new int[]{0x1f6a3, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("rowboat_tone3", new String(new int[]{0x1f6a3, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("rowboat_tone4", new String(new int[]{0x1f6a3, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("rowboat_tone5", new String(new int[]{0x1f6a3, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("swimmer_tone1", new String(new int[]{0x1f3ca, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("swimmer_tone2", new String(new int[]{0x1f3ca, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("swimmer_tone3", new String(new int[]{0x1f3ca, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("swimmer_tone4", new String(new int[]{0x1f3ca, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("swimmer_tone5", new String(new int[]{0x1f3ca, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("surfer_tone1", new String(new int[]{0x1f3c4, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("surfer_tone2", new String(new int[]{0x1f3c4, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("surfer_tone3", new String(new int[]{0x1f3c4, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("surfer_tone4", new String(new int[]{0x1f3c4, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("surfer_tone5", new String(new int[]{0x1f3c4, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("bath_tone1", new String(new int[]{0x1f6c0, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("bath_tone2", new String(new int[]{0x1f6c0, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("bath_tone3", new String(new int[]{0x1f6c0, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("bath_tone4", new String(new int[]{0x1f6c0, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("bath_tone5", new String(new int[]{0x1f6c0, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("basketball_player_tone1",
                new String(new int[]{0x26f9, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("basketball_player_tone2",
                new String(new int[]{0x26f9, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("basketball_player_tone3",
                new String(new int[]{0x26f9, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("basketball_player_tone4",
                new String(new int[]{0x26f9, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("basketball_player_tone5",
                new String(new int[]{0x26f9, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("lifter_tone1", new String(new int[]{0x1f3cb, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("lifter_tone2", new String(new int[]{0x1f3cb, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("lifter_tone3", new String(new int[]{0x1f3cb, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("lifter_tone4", new String(new int[]{0x1f3cb, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("lifter_tone5", new String(new int[]{0x1f3cb, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("bicyclist_tone1",
                new String(new int[]{0x1f6b4, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("bicyclist_tone2",
                new String(new int[]{0x1f6b4, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("bicyclist_tone3",
                new String(new int[]{0x1f6b4, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("bicyclist_tone4",
                new String(new int[]{0x1f6b4, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("bicyclist_tone5",
                new String(new int[]{0x1f6b4, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("mountain_bicyclist_tone1",
                new String(new int[]{0x1f6b5, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("mountain_bicyclist_tone2",
                new String(new int[]{0x1f6b5, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("mountain_bicyclist_tone3",
                new String(new int[]{0x1f6b5, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("mountain_bicyclist_tone4",
                new String(new int[]{0x1f6b5, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("mountain_bicyclist_tone5",
                new String(new int[]{0x1f6b5, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("horse_racing_tone1",
                new String(new int[]{0x1f3c7, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("horse_racing_tone2",
                new String(new int[]{0x1f3c7, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("horse_racing_tone3",
                new String(new int[]{0x1f3c7, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("horse_racing_tone4",
                new String(new int[]{0x1f3c7, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("horse_racing_tone5",
                new String(new int[]{0x1f3c7, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("spy_tone1", new String(new int[]{0x1f575, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("spy_tone2", new String(new int[]{0x1f575, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("spy_tone3", new String(new int[]{0x1f575, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("spy_tone4", new String(new int[]{0x1f575, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("spy_tone5", new String(new int[]{0x1f575, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("tone1", new String(new int[]{0x1f3fb}, 0, 1));
        _shortNameToUnicode.put("tone2", new String(new int[]{0x1f3fc}, 0, 1));
        _shortNameToUnicode.put("tone3", new String(new int[]{0x1f3fd}, 0, 1));
        _shortNameToUnicode.put("tone4", new String(new int[]{0x1f3fe}, 0, 1));
        _shortNameToUnicode.put("tone5", new String(new int[]{0x1f3ff}, 0, 1));
        _shortNameToUnicode.put("prince_tone1", new String(new int[]{0x1f934, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("prince_tone2", new String(new int[]{0x1f934, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("prince_tone3", new String(new int[]{0x1f934, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("prince_tone4", new String(new int[]{0x1f934, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("prince_tone5", new String(new int[]{0x1f934, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("mrs_claus_tone1",
                new String(new int[]{0x1f936, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("mrs_claus_tone2",
                new String(new int[]{0x1f936, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("mrs_claus_tone3",
                new String(new int[]{0x1f936, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("mrs_claus_tone4",
                new String(new int[]{0x1f936, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("mrs_claus_tone5",
                new String(new int[]{0x1f936, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("man_in_tuxedo_tone1",
                new String(new int[]{0x1f935, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("man_in_tuxedo_tone2",
                new String(new int[]{0x1f935, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("man_in_tuxedo_tone3",
                new String(new int[]{0x1f935, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("man_in_tuxedo_tone4",
                new String(new int[]{0x1f935, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("man_in_tuxedo_tone5",
                new String(new int[]{0x1f935, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("shrug_tone1", new String(new int[]{0x1f937, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("shrug_tone2", new String(new int[]{0x1f937, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("shrug_tone3", new String(new int[]{0x1f937, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("shrug_tone4", new String(new int[]{0x1f937, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("shrug_tone5", new String(new int[]{0x1f937, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("face_palm_tone1",
                new String(new int[]{0x1f926, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("face_palm_tone2",
                new String(new int[]{0x1f926, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("face_palm_tone3",
                new String(new int[]{0x1f926, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("face_palm_tone4",
                new String(new int[]{0x1f926, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("face_palm_tone5",
                new String(new int[]{0x1f926, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("pregnant_woman_tone1",
                new String(new int[]{0x1f930, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("pregnant_woman_tone2",
                new String(new int[]{0x1f930, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("pregnant_woman_tone3",
                new String(new int[]{0x1f930, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("pregnant_woman_tone4",
                new String(new int[]{0x1f930, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("pregnant_woman_tone5",
                new String(new int[]{0x1f930, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("man_dancing_tone1",
                new String(new int[]{0x1f57a, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("man_dancing_tone2",
                new String(new int[]{0x1f57a, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("man_dancing_tone3",
                new String(new int[]{0x1f57a, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("man_dancing_tone4",
                new String(new int[]{0x1f57a, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("man_dancing_tone5",
                new String(new int[]{0x1f57a, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("selfie_tone1", new String(new int[]{0x1f933, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("selfie_tone2", new String(new int[]{0x1f933, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("selfie_tone3", new String(new int[]{0x1f933, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("selfie_tone4", new String(new int[]{0x1f933, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("selfie_tone5", new String(new int[]{0x1f933, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("fingers_crossed_tone1",
                new String(new int[]{0x1f91e, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("fingers_crossed_tone2",
                new String(new int[]{0x1f91e, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("fingers_crossed_tone3",
                new String(new int[]{0x1f91e, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("fingers_crossed_tone4",
                new String(new int[]{0x1f91e, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("fingers_crossed_tone5",
                new String(new int[]{0x1f91e, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("call_me_tone1", new String(new int[]{0x1f919, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("call_me_tone2", new String(new int[]{0x1f919, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("call_me_tone3", new String(new int[]{0x1f919, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("call_me_tone4", new String(new int[]{0x1f919, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("call_me_tone5", new String(new int[]{0x1f919, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("left_facing_fist_tone1",
                new String(new int[]{0x1f91b, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("left_facing_fist_tone2",
                new String(new int[]{0x1f91b, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("left_facing_fist_tone3",
                new String(new int[]{0x1f91b, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("left_facing_fist_tone4",
                new String(new int[]{0x1f91b, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("left_facing_fist_tone5",
                new String(new int[]{0x1f91b, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("right_facing_fist_tone1",
                new String(new int[]{0x1f91c, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("right_facing_fist_tone2",
                new String(new int[]{0x1f91c, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("right_facing_fist_tone3",
                new String(new int[]{0x1f91c, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("right_facing_fist_tone4",
                new String(new int[]{0x1f91c, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("right_facing_fist_tone5",
                new String(new int[]{0x1f91c, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("raised_back_of_hand_tone1",
                new String(new int[]{0x1f91a, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("raised_back_of_hand_tone2",
                new String(new int[]{0x1f91a, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("raised_back_of_hand_tone3",
                new String(new int[]{0x1f91a, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("raised_back_of_hand_tone4",
                new String(new int[]{0x1f91a, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("raised_back_of_hand_tone5",
                new String(new int[]{0x1f91a, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("handshake_tone1",
                new String(new int[]{0x1f91d, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("handshake_tone2",
                new String(new int[]{0x1f91d, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("handshake_tone3",
                new String(new int[]{0x1f91d, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("handshake_tone4",
                new String(new int[]{0x1f91d, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("handshake_tone5",
                new String(new int[]{0x1f91d, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("cartwheel_tone1",
                new String(new int[]{0x1f938, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("cartwheel_tone2",
                new String(new int[]{0x1f938, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("cartwheel_tone3",
                new String(new int[]{0x1f938, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("cartwheel_tone4",
                new String(new int[]{0x1f938, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("cartwheel_tone5",
                new String(new int[]{0x1f938, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("wrestlers_tone1",
                new String(new int[]{0x1f93c, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("wrestlers_tone2",
                new String(new int[]{0x1f93c, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("wrestlers_tone3",
                new String(new int[]{0x1f93c, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("wrestlers_tone4",
                new String(new int[]{0x1f93c, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("wrestlers_tone5",
                new String(new int[]{0x1f93c, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("water_polo_tone1",
                new String(new int[]{0x1f93d, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("water_polo_tone2",
                new String(new int[]{0x1f93d, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("water_polo_tone3",
                new String(new int[]{0x1f93d, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("water_polo_tone4",
                new String(new int[]{0x1f93d, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("water_polo_tone5",
                new String(new int[]{0x1f93d, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("handball_tone1", new String(new int[]{0x1f93e, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("handball_tone2", new String(new int[]{0x1f93e, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("handball_tone3", new String(new int[]{0x1f93e, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("handball_tone4", new String(new int[]{0x1f93e, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("handball_tone5", new String(new int[]{0x1f93e, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("juggling_tone1", new String(new int[]{0x1f939, 0x1f3fb}, 0, 2));
        _shortNameToUnicode.put("juggling_tone2", new String(new int[]{0x1f939, 0x1f3fc}, 0, 2));
        _shortNameToUnicode.put("juggling_tone3", new String(new int[]{0x1f939, 0x1f3fd}, 0, 2));
        _shortNameToUnicode.put("juggling_tone4", new String(new int[]{0x1f939, 0x1f3fe}, 0, 2));
        _shortNameToUnicode.put("juggling_tone5", new String(new int[]{0x1f939, 0x1f3ff}, 0, 2));
        _shortNameToUnicode.put("speech_left", new String(new int[]{0x1f5e8}, 0, 1));
        _shortNameToUnicode.put("eject", new String(new int[]{0x23cf}, 0, 1));
        _shortNameToUnicode.put("gay_pride_flag", new String(new int[]{0x1f3f3, 0x1f308}, 0, 2));
        _shortNameToUnicode.put("cowboy", new String(new int[]{0x1f920}, 0, 1));
        _shortNameToUnicode.put("clown", new String(new int[]{0x1f921}, 0, 1));
        _shortNameToUnicode.put("nauseated_face", new String(new int[]{0x1f922}, 0, 1));
        _shortNameToUnicode.put("rofl", new String(new int[]{0x1f923}, 0, 1));
        _shortNameToUnicode.put("drooling_face", new String(new int[]{0x1f924}, 0, 1));
        _shortNameToUnicode.put("lying_face", new String(new int[]{0x1f925}, 0, 1));
        _shortNameToUnicode.put("sneezing_face", new String(new int[]{0x1f927}, 0, 1));
        _shortNameToUnicode.put("prince", new String(new int[]{0x1f934}, 0, 1));
        _shortNameToUnicode.put("man_in_tuxedo", new String(new int[]{0x1f935}, 0, 1));
        _shortNameToUnicode.put("mrs_claus", new String(new int[]{0x1f936}, 0, 1));
        _shortNameToUnicode.put("face_palm", new String(new int[]{0x1f926}, 0, 1));
        _shortNameToUnicode.put("shrug", new String(new int[]{0x1f937}, 0, 1));
        _shortNameToUnicode.put("pregnant_woman", new String(new int[]{0x1f930}, 0, 1));
        _shortNameToUnicode.put("selfie", new String(new int[]{0x1f933}, 0, 1));
        _shortNameToUnicode.put("man_dancing", new String(new int[]{0x1f57a}, 0, 1));
        _shortNameToUnicode.put("call_me", new String(new int[]{0x1f919}, 0, 1));
        _shortNameToUnicode.put("raised_back_of_hand", new String(new int[]{0x1f91a}, 0, 1));
        _shortNameToUnicode.put("left_facing_fist", new String(new int[]{0x1f91b}, 0, 1));
        _shortNameToUnicode.put("right_facing_fist", new String(new int[]{0x1f91c}, 0, 1));
        _shortNameToUnicode.put("handshake", new String(new int[]{0x1f91d}, 0, 1));
        _shortNameToUnicode.put("fingers_crossed", new String(new int[]{0x1f91e}, 0, 1));
        _shortNameToUnicode.put("black_heart", new String(new int[]{0x1f5a4}, 0, 1));
        _shortNameToUnicode.put("eagle", new String(new int[]{0x1f985}, 0, 1));
        _shortNameToUnicode.put("duck", new String(new int[]{0x1f986}, 0, 1));
        _shortNameToUnicode.put("bat", new String(new int[]{0x1f987}, 0, 1));
        _shortNameToUnicode.put("shark", new String(new int[]{0x1f988}, 0, 1));
        _shortNameToUnicode.put("owl", new String(new int[]{0x1f989}, 0, 1));
        _shortNameToUnicode.put("fox", new String(new int[]{0x1f98a}, 0, 1));
        _shortNameToUnicode.put("butterfly", new String(new int[]{0x1f98b}, 0, 1));
        _shortNameToUnicode.put("deer", new String(new int[]{0x1f98c}, 0, 1));
        _shortNameToUnicode.put("gorilla", new String(new int[]{0x1f98d}, 0, 1));
        _shortNameToUnicode.put("lizard", new String(new int[]{0x1f98e}, 0, 1));
        _shortNameToUnicode.put("rhino", new String(new int[]{0x1f98f}, 0, 1));
        _shortNameToUnicode.put("wilted_rose", new String(new int[]{0x1f940}, 0, 1));
        _shortNameToUnicode.put("croissant", new String(new int[]{0x1f950}, 0, 1));
        _shortNameToUnicode.put("avocado", new String(new int[]{0x1f951}, 0, 1));
        _shortNameToUnicode.put("cucumber", new String(new int[]{0x1f952}, 0, 1));
        _shortNameToUnicode.put("bacon", new String(new int[]{0x1f953}, 0, 1));
        _shortNameToUnicode.put("potato", new String(new int[]{0x1f954}, 0, 1));
        _shortNameToUnicode.put("carrot", new String(new int[]{0x1f955}, 0, 1));
        _shortNameToUnicode.put("french_bread", new String(new int[]{0x1f956}, 0, 1));
        _shortNameToUnicode.put("salad", new String(new int[]{0x1f957}, 0, 1));
        _shortNameToUnicode.put("shallow_pan_of_food", new String(new int[]{0x1f958}, 0, 1));
        _shortNameToUnicode.put("stuffed_flatbread", new String(new int[]{0x1f959}, 0, 1));
        _shortNameToUnicode.put("champagne_glass", new String(new int[]{0x1f942}, 0, 1));
        _shortNameToUnicode.put("tumbler_glass", new String(new int[]{0x1f943}, 0, 1));
        _shortNameToUnicode.put("spoon", new String(new int[]{0x1f944}, 0, 1));
        _shortNameToUnicode.put("octagonal_sign", new String(new int[]{0x1f6d1}, 0, 1));
        _shortNameToUnicode.put("shopping_cart", new String(new int[]{0x1f6d2}, 0, 1));
        _shortNameToUnicode.put("scooter", new String(new int[]{0x1f6f4}, 0, 1));
        _shortNameToUnicode.put("motor_scooter", new String(new int[]{0x1f6f5}, 0, 1));
        _shortNameToUnicode.put("canoe", new String(new int[]{0x1f6f6}, 0, 1));
        _shortNameToUnicode.put("cartwheel", new String(new int[]{0x1f938}, 0, 1));
        _shortNameToUnicode.put("juggling", new String(new int[]{0x1f939}, 0, 1));
        _shortNameToUnicode.put("wrestlers", new String(new int[]{0x1f93c}, 0, 1));
        _shortNameToUnicode.put("boxing_glove", new String(new int[]{0x1f94a}, 0, 1));
        _shortNameToUnicode.put("martial_arts_uniform", new String(new int[]{0x1f94b}, 0, 1));
        _shortNameToUnicode.put("water_polo", new String(new int[]{0x1f93d}, 0, 1));
        _shortNameToUnicode.put("handball", new String(new int[]{0x1f93e}, 0, 1));
        _shortNameToUnicode.put("goal", new String(new int[]{0x1f945}, 0, 1));
        _shortNameToUnicode.put("fencer", new String(new int[]{0x1f93a}, 0, 1));
        _shortNameToUnicode.put("first_place", new String(new int[]{0x1f947}, 0, 1));
        _shortNameToUnicode.put("second_place", new String(new int[]{0x1f948}, 0, 1));
        _shortNameToUnicode.put("third_place", new String(new int[]{0x1f949}, 0, 1));
        _shortNameToUnicode.put("drum", new String(new int[]{0x1f941}, 0, 1));
        _shortNameToUnicode.put("shrimp", new String(new int[]{0x1f990}, 0, 1));
        _shortNameToUnicode.put("squid", new String(new int[]{0x1f991}, 0, 1));
        _shortNameToUnicode.put("egg", new String(new int[]{0x1f95a}, 0, 1));
        _shortNameToUnicode.put("milk", new String(new int[]{0x1f95b}, 0, 1));
        _shortNameToUnicode.put("peanuts", new String(new int[]{0x1f95c}, 0, 1));
        _shortNameToUnicode.put("kiwi", new String(new int[]{0x1f95d}, 0, 1));
        _shortNameToUnicode.put("pancakes", new String(new int[]{0x1f95e}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_z", new String(new int[]{0x1f1ff}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_y", new String(new int[]{0x1f1fe}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_x", new String(new int[]{0x1f1fd}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_w", new String(new int[]{0x1f1fc}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_v", new String(new int[]{0x1f1fb}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_u", new String(new int[]{0x1f1fa}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_t", new String(new int[]{0x1f1f9}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_s", new String(new int[]{0x1f1f8}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_r", new String(new int[]{0x1f1f7}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_q", new String(new int[]{0x1f1f6}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_p", new String(new int[]{0x1f1f5}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_o", new String(new int[]{0x1f1f4}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_n", new String(new int[]{0x1f1f3}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_m", new String(new int[]{0x1f1f2}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_l", new String(new int[]{0x1f1f1}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_k", new String(new int[]{0x1f1f0}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_j", new String(new int[]{0x1f1ef}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_i", new String(new int[]{0x1f1ee}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_h", new String(new int[]{0x1f1ed}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_g", new String(new int[]{0x1f1ec}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_f", new String(new int[]{0x1f1eb}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_e", new String(new int[]{0x1f1ea}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_d", new String(new int[]{0x1f1e9}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_c", new String(new int[]{0x1f1e8}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_b", new String(new int[]{0x1f1e7}, 0, 1));
        _shortNameToUnicode.put("regional_indicator_a", new String(new int[]{0x1f1e6}, 0, 1));
    }

    /**
     * Replace shortnames to unicode characters.
     */
    public static String shortnameToUnicode(String input, boolean removeIfUnsupported) {
        Matcher matcher = SHORTNAME_PATTERN.matcher(input);
        boolean supported = Build.VERSION.SDK_INT >= 16;

        while (matcher.find()) {
            String unicode = _shortNameToUnicode.get(matcher.group(1));
            if (unicode == null) {
                continue;
            }

            if (supported) {
                input = input.replace(":" + matcher.group(1) + ":", unicode);
            } else if (!supported && removeIfUnsupported) {
                input = input.replace(":" + matcher.group(1) + ":", "");
            }
        }

        return input;
    }
}
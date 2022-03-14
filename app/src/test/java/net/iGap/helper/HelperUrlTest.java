package net.iGap.helper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shadow.org.apache.commons.io.FileUtils;

public class HelperUrlTest {

    private static final String KEY_IGAP_LINK = "igap_link";
    private static final String KEY_WEB_LINK = "web_link";
    private static final String KEY_IGAP_DEEP_LINK = "igap_deep_link";
    private static final String KEY_BOT_LINK = "bot_command";
    private static final String KEY_IGAP_RESOLVE_LINK = "igap_resolve_link";
    private static final String KEY_AT_SIGN_LINK = "at_sign_link";
    private static final String KEY_HASH_TAG_LINK = "hash_tag";
    private static final String KEY_ALL_LINK_TYPES = "all_link_types";
    private static final String KEY_LINK = "link";
    private static final String KEY_LINK_INFO = "link_info";
    private static final String FILE_PATH = "src/test/java/net/iGap/helper/link.json";
    private static File file = null;
    private static String data = "";
    private static JSONObject jsonObjectLinks = null;

    @Before
    public void loadFile() {
        file = new File(FILE_PATH);
        try {
            data = FileUtils.readFileToString(file, "UTF-8");
            jsonObjectLinks = new JSONObject(data.trim());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testIGapLinkGetLinkInfo() {
        try {
            HashMap<String, String> texts = getItemsByType(KEY_IGAP_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testIGapLinkGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWebLinkGetLinkInfo() {

        try {
            HashMap<String, String> texts = getItemsByType(KEY_WEB_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testWebLinkGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIGapDeepLinkGetLinkInfo() {
        try {
            HashMap<String, String> texts = getItemsByType(KEY_IGAP_DEEP_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testIGapDeepLinkGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testBotGetLinkInfo() {
        try {

            HashMap<String, String> texts = getItemsByType(KEY_BOT_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testBotGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testResolveGetLinkInfo() {
        try {

            HashMap<String, String> texts = getItemsByType(KEY_IGAP_RESOLVE_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testResolveGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAtSignGetLinkInfo() {
        try {

            HashMap<String, String> texts = getItemsByType(KEY_AT_SIGN_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testAtSignGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHashTagGetLinkInfo() {
        try {

            HashMap<String, String> texts = getItemsByType(KEY_HASH_TAG_LINK);
            for (Map.Entry linkText : texts.entrySet()) {
                String helperLinkInfo = HelperUrl.getLinkInfo(linkText.getKey().toString());
                Assert.assertEquals("testHashTagGetLinkInfo :" + linkText.getKey().toString(), linkText.getValue().toString(), helperLinkInfo);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFullTestRegexGetLinkInfo() {
        try {

            JSONArray jsonArrayIgapLinks = jsonObjectLinks.getJSONArray(KEY_ALL_LINK_TYPES);
            for (int i = 0; i < jsonArrayIgapLinks.length(); i++) {
                JSONObject jsonObjectLinkModel = jsonArrayIgapLinks.getJSONObject(i);
                List texts = new ArrayList();
                StringBuilder a = new StringBuilder(jsonObjectLinkModel.getString(KEY_LINK));
                String[] links = a.toString().split(" ");
                int lastIndex = 0;
                for (String link : links) {
                    link += " ";
                    String linkInfo = HelperUrl.getLinkInfo(link);
                    if (linkInfo.length() > 1) {
                        String[] linksIndex = linkInfo.split("@");
                        for (String index : linksIndex) {
                            String[] regexEditor = index.split("_");
                            linkInfo = (Integer.parseInt(regexEditor[0]) + lastIndex) + "_" + (Integer.parseInt(regexEditor[1]) + lastIndex) + "_" + regexEditor[2] + "@";
                            texts.add(linkInfo);
                        }

                    }
                    lastIndex += link.length();

                }

                String fullLinkInfo = HelperUrl.getLinkInfo(a.toString());
                StringBuilder trueLinkInfo = new StringBuilder();
                for (int j = 0; j < texts.size(); j++) {
                    trueLinkInfo.append(texts.get(j));
                }
                Assert.assertEquals("fullTestRegex :" + a.toString(), fullLinkInfo, trueLinkInfo.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private HashMap<String, String> getItemsByType(String type) {
        HashMap<String, String> texts = new HashMap<>();
        try {
            JSONArray jsonArrayIgapLinks = jsonObjectLinks.getJSONArray(type);

            for (int i = 0; i < jsonArrayIgapLinks.length(); i++) {
                JSONObject jsonObjectLinkModel = jsonArrayIgapLinks.getJSONObject(i);
                texts.put(jsonObjectLinkModel.getString(KEY_LINK), jsonObjectLinkModel.getString(KEY_LINK_INFO));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return texts;
    }


}
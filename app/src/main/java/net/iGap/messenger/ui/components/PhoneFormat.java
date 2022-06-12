package net.iGap.messenger.ui.components;

import net.iGap.G;
import net.iGap.helper.FileLog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneFormat {
    private static volatile PhoneFormat Instance = null;
    public byte[] data;
    public ByteBuffer buffer;
    public String defaultCountry;
    public String defaultCallingCode;
    public HashMap<String, Integer> callingCodeOffsets;
    public HashMap<String, ArrayList<String>> callingCodeCountries;
    public HashMap<String, CallingCodeInfo> callingCodeData;
    public HashMap<String, String> countryCallingCode;
    private boolean initialzed = false;

    public PhoneFormat() {
        init(null);
    }

    public PhoneFormat(String countryCode) {
        init(countryCode);
    }

    public static PhoneFormat getInstance() {
        PhoneFormat localInstance = Instance;
        if (localInstance == null) {
            synchronized (PhoneFormat.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new PhoneFormat();
                }
            }
        }
        return localInstance;
    }

    public static String strip(String str) {
        StringBuilder res = new StringBuilder(str);
        String phoneChars = "0123456789+*#";
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!phoneChars.contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static String stripExceptNumbers(String str, boolean includePlus) {
        if (str == null) {
            return null;
        }
        StringBuilder res = new StringBuilder(str);
        String phoneChars = "0123456789";
        if (includePlus) {
            phoneChars += "+";
        }
        for (int i = res.length() - 1; i >= 0; i--) {
            if (!phoneChars.contains(res.substring(i, i + 1))) {
                res.deleteCharAt(i);
            }
        }
        return res.toString();
    }

    public static String stripExceptNumbers(String str) {
        return stripExceptNumbers(str, false);
    }

    public void init(String countryCode) {
        InputStream stream = null;
        ByteArrayOutputStream bos = null;
        try {
            stream = G.context.getAssets().open("PhoneFormats.dat");
            bos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len;
            while ((len = stream.read(buf, 0, 1024)) != -1) {
                bos.write(buf, 0, len);
            }
            data = bos.toByteArray();
            buffer = ByteBuffer.wrap(data);
            buffer.order(ByteOrder.LITTLE_ENDIAN);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (countryCode != null && countryCode.length() != 0) {
            defaultCountry = countryCode;
        } else {
            Locale loc = Locale.getDefault();
            defaultCountry = loc.getCountry().toLowerCase();
        }
        callingCodeOffsets = new HashMap<>(255);
        callingCodeCountries = new HashMap<>(255);
        callingCodeData = new HashMap<>(10);
        countryCallingCode = new HashMap<>(255);

        parseDataHeader();
        initialzed = true;
    }

    public String defaultCallingCode() {
        return callingCodeForCountryCode(defaultCountry);
    }

    public String callingCodeForCountryCode(String countryCode) {
        return countryCallingCode.get(countryCode.toLowerCase());
    }

    public ArrayList countriesForCallingCode(String callingCode) {
        if (callingCode.startsWith("+")) {
            callingCode = callingCode.substring(1);
        }
        return callingCodeCountries.get(callingCode);
    }

    public CallingCodeInfo findCallingCodeInfo(String str) {
        CallingCodeInfo res = null;
        for (int i = 0; i < 3; i++) {
            if (i < str.length()) {
                res = callingCodeInfo(str.substring(0, i + 1));
                if (res != null) {
                    break;
                }
            } else {
                break;
            }
        }

        return res;
    }

    public String format(String orig) {
        if (!initialzed) {
            return orig;
        }
        try {
            String str = strip(orig);

            if (str.startsWith("+")) {
                String rest = str.substring(1);
                CallingCodeInfo info = findCallingCodeInfo(rest);
                if (info != null) {
                    String phone = info.format(rest);
                    return "+" + phone;
                } else {
                    return orig;
                }
            } else {
                CallingCodeInfo info = callingCodeInfo(defaultCallingCode);
                if (info == null) {
                    return orig;
                }
                String accessCode = info.matchingAccessCode(str);
                if (accessCode != null) {
                    String rest = str.substring(accessCode.length());
                    String phone = rest;
                    CallingCodeInfo info2 = findCallingCodeInfo(rest);
                    if (info2 != null) {
                        phone = info2.format(rest);
                    }

                    if (phone.length() == 0) {
                        return accessCode;
                    } else {
                        return String.format("%s %s", accessCode, phone);
                    }
                } else {
                    return info.format(str);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return orig;
        }
    }

    public boolean isPhoneNumberValid(String phoneNumber) {
        if (!initialzed) {
            return true;
        }
        String str = strip(phoneNumber);

        if (str.startsWith("+")) {
            String rest = str.substring(1);
            CallingCodeInfo info = findCallingCodeInfo(rest);
            return info != null && info.isValidPhoneNumber(rest);
        } else {
            CallingCodeInfo info = callingCodeInfo(defaultCallingCode);
            if (info == null) {
                return false;
            }

            String accessCode = info.matchingAccessCode(str);
            if (accessCode != null) {
                String rest = str.substring(accessCode.length());
                if (rest.length() != 0) {
                    CallingCodeInfo info2 = findCallingCodeInfo(rest);
                    return info2 != null && info2.isValidPhoneNumber(rest);
                } else {
                    return false;
                }
            } else {
                return info.isValidPhoneNumber(str);
            }
        }
    }

    int value32(int offset) {
        if (offset + 4 <= data.length) {
            buffer.position(offset);
            return buffer.getInt();
        } else {
            return 0;
        }
    }

    short value16(int offset) {
        if (offset + 2 <= data.length) {
            buffer.position(offset);
            return buffer.getShort();
        } else {
            return 0;
        }
    }

    public String valueString(int offset) {
        try {
            for (int a = offset; a < data.length; a++) {
                if (data[a] == '\0') {
                    if (offset == a - offset) {
                        return "";
                    }
                    return new String(data, offset, a - offset);
                }
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public CallingCodeInfo callingCodeInfo(String callingCode) {
        CallingCodeInfo res = callingCodeData.get(callingCode);
        if (res == null) {
            Integer num = callingCodeOffsets.get(callingCode);
            if (num != null) {
                final byte[] bytes = data;
                int start = num;
                int offset = start;
                res = new CallingCodeInfo();
                res.callingCode = callingCode;
                res.countries = callingCodeCountries.get(callingCode);
                callingCodeData.put(callingCode, res);
                int block1Len = value16(offset);
                offset += 2;
                offset += 2;
                int block2Len = value16(offset);
                offset += 2;
                offset += 2;
                int setCnt = value16(offset);
                offset += 2;
                offset += 2;
                ArrayList<String> strs = new ArrayList<>(5);
                String str;
                while ((str = valueString(offset)).length() != 0) {
                    strs.add(str);
                    offset += str.length() + 1;
                }
                res.trunkPrefixes = strs;
                offset++;
                strs = new ArrayList<>(5);
                while ((str = valueString(offset)).length() != 0) {
                    strs.add(str);
                    offset += str.length() + 1;
                }
                res.intlPrefixes = strs;
                ArrayList<RuleSet> ruleSets = new ArrayList<>(setCnt);
                offset = start + block1Len;
                for (int s = 0; s < setCnt; s++) {
                    RuleSet ruleSet = new RuleSet();
                    ruleSet.matchLen = value16(offset);
                    offset += 2;
                    int ruleCnt = value16(offset);
                    offset += 2;
                    ArrayList<PhoneRule> rules = new ArrayList<>(ruleCnt);
                    for (int r = 0; r < ruleCnt; r++) {
                        PhoneRule rule = new PhoneRule();
                        rule.minVal = value32(offset);
                        offset += 4;
                        rule.maxVal = value32(offset);
                        offset += 4;
                        rule.byte8 = (int) bytes[offset++];
                        rule.maxLen = (int) bytes[offset++];
                        rule.otherFlag = (int) bytes[offset++];
                        rule.prefixLen = (int) bytes[offset++];
                        rule.flag12 = (int) bytes[offset++];
                        rule.flag13 = (int) bytes[offset++];
                        int strOffset = value16(offset);
                        offset += 2;
                        rule.format = valueString(start + block1Len + block2Len + strOffset);
                        int openPos = rule.format.indexOf("[[");
                        if (openPos != -1) {
                            int closePos = rule.format.indexOf("]]");
                            rule.format = String.format("%s%s", rule.format.substring(0, openPos), rule.format.substring(closePos + 2));
                        }
                        rules.add(rule);
                        if (rule.hasIntlPrefix) {
                            ruleSet.hasRuleWithIntlPrefix = true;
                        }
                        if (rule.hasTrunkPrefix) {
                            ruleSet.hasRuleWithTrunkPrefix = true;
                        }
                    }
                    ruleSet.rules = rules;
                    ruleSets.add(ruleSet);
                }
                res.ruleSets = ruleSets;
            }
        }
        return res;
    }

    public void parseDataHeader() {
        int count = value32(0);
        int base = count * 12 + 4;
        int spot = 4;
        for (int i = 0; i < count; i++) {
            String callingCode = valueString(spot);
            spot += 4;
            String country = valueString(spot);
            spot += 4;
            int offset = value32(spot) + base;
            spot += 4;
            if (country.equals(defaultCountry)) {
                defaultCallingCode = callingCode;
            }
            countryCallingCode.put(country, callingCode);
            callingCodeOffsets.put(callingCode, offset);
            ArrayList<String> countries = callingCodeCountries.get(callingCode);
            if (countries == null) {
                countries = new ArrayList<>();
                callingCodeCountries.put(callingCode, countries);
            }
            countries.add(country);
        }
        if (defaultCallingCode != null) {
            callingCodeInfo(defaultCallingCode);
        }
    }

    public class CallingCodeInfo {
        public ArrayList<String> countries = new ArrayList<>();
        public String callingCode = "";
        public ArrayList<String> trunkPrefixes = new ArrayList<>();
        public ArrayList<String> intlPrefixes = new ArrayList<>();
        public ArrayList<RuleSet> ruleSets = new ArrayList<>();

        String matchingAccessCode(String str) {
            for (String code : intlPrefixes) {
                if (str.startsWith(code)) {
                    return code;
                }
            }
            return null;
        }

        String matchingTrunkCode(String str) {
            for (String code : trunkPrefixes) {
                if (str.startsWith(code)) {
                    return code;
                }
            }

            return null;
        }

        String format(String orig) {
            String str = orig;
            String trunkPrefix = null;
            String intlPrefix = null;
            if (str.startsWith(callingCode)) {
                intlPrefix = callingCode;
                str = str.substring(intlPrefix.length());
            } else {
                String trunk = matchingTrunkCode(str);
                if (trunk != null) {
                    trunkPrefix = trunk;
                    str = str.substring(trunkPrefix.length());
                }
            }
            for (RuleSet set : ruleSets) {
                String phone = set.format(str, intlPrefix, trunkPrefix, true);
                if (phone != null) {
                    return phone;
                }
            }
            for (RuleSet set : ruleSets) {
                String phone = set.format(str, intlPrefix, trunkPrefix, false);
                if (phone != null) {
                    return phone;
                }
            }
            if (intlPrefix != null && str.length() != 0) {
                return String.format("%s %s", intlPrefix, str);
            }

            return orig;
        }

        boolean isValidPhoneNumber(String orig) {
            String str = orig;
            String trunkPrefix = null;
            String intlPrefix = null;
            if (str.startsWith(callingCode)) {
                intlPrefix = callingCode;
                str = str.substring(intlPrefix.length());
            } else {
                String trunk = matchingTrunkCode(str);
                if (trunk != null) {
                    trunkPrefix = trunk;
                    str = str.substring(trunkPrefix.length());
                }
            }
            for (RuleSet set : ruleSets) {
                boolean valid = set.isValid(str, intlPrefix, trunkPrefix, true);
                if (valid) {
                    return true;
                }
            }
            for (RuleSet set : ruleSets) {
                boolean valid = set.isValid(str, intlPrefix, trunkPrefix, false);
                if (valid) {
                    return true;
                }
            }
            return false;
        }
    }

    public class RuleSet {
        public int matchLen;
        public ArrayList<PhoneRule> rules = new ArrayList<PhoneRule>();
        public boolean hasRuleWithIntlPrefix;
        public boolean hasRuleWithTrunkPrefix;
        public Pattern pattern = Pattern.compile("[0-9]+");

        String format(String str, String intlPrefix, String trunkPrefix, boolean prefixRequired) {
            if (str.length() >= matchLen) {
                String begin = str.substring(0, matchLen);
                int val = 0;
                Matcher matcher = pattern.matcher(begin);
                if (matcher.find()) {
                    String num = matcher.group(0);
                    val = Integer.parseInt(num);
                }
                for (PhoneRule rule : rules) {
                    if (val >= rule.minVal && val <= rule.maxVal && str.length() <= rule.maxLen) {
                        if (prefixRequired) {
                            if (((rule.flag12 & 0x03) == 0 && trunkPrefix == null && intlPrefix == null) || (trunkPrefix != null && (rule.flag12 & 0x01) != 0) || (intlPrefix != null && (rule.flag12 & 0x02) != 0)) {
                                return rule.format(str, intlPrefix, trunkPrefix);
                            }
                        } else {
                            if ((trunkPrefix == null && intlPrefix == null) || (trunkPrefix != null && (rule.flag12 & 0x01) != 0) || (intlPrefix != null && (rule.flag12 & 0x02) != 0)) {
                                return rule.format(str, intlPrefix, trunkPrefix);
                            }
                        }
                    }
                }
                if (!prefixRequired) {
                    if (intlPrefix != null) {
                        for (PhoneRule rule : rules) {
                            if (val >= rule.minVal && val <= rule.maxVal && str.length() <= rule.maxLen) {
                                if (trunkPrefix == null || (rule.flag12 & 0x01) != 0) {
                                    return rule.format(str, intlPrefix, trunkPrefix);
                                }
                            }
                        }
                    } else if (trunkPrefix != null) {
                        for (PhoneRule rule : rules) {
                            if (val >= rule.minVal && val <= rule.maxVal && str.length() <= rule.maxLen) {
                                if (intlPrefix == null || (rule.flag12 & 0x02) != 0) {
                                    return rule.format(str, intlPrefix, trunkPrefix);
                                }
                            }
                        }
                    }
                }
                return null;
            } else {
                return null;
            }
        }

        boolean isValid(String str, String intlPrefix, String trunkPrefix, boolean prefixRequired) {
            if (str.length() >= matchLen) {
                String begin = str.substring(0, matchLen);
                int val = 0;
                Matcher matcher = pattern.matcher(begin);
                if (matcher.find()) {
                    String num = matcher.group(0);
                    val = Integer.parseInt(num);
                }
                for (PhoneRule rule : rules) {
                    if (val >= rule.minVal && val <= rule.maxVal && str.length() == rule.maxLen) {
                        if (prefixRequired) {
                            if (((rule.flag12 & 0x03) == 0 && trunkPrefix == null && intlPrefix == null) || (trunkPrefix != null && (rule.flag12 & 0x01) != 0) || (intlPrefix != null && (rule.flag12 & 0x02) != 0)) {
                                return true;
                            }
                        } else {
                            if ((trunkPrefix == null && intlPrefix == null) || (trunkPrefix != null && (rule.flag12 & 0x01) != 0) || (intlPrefix != null && (rule.flag12 & 0x02) != 0)) {
                                return true;
                            }
                        }
                    }
                }
                if (!prefixRequired) {
                    if (intlPrefix != null && !hasRuleWithIntlPrefix) {
                        for (PhoneRule rule : rules) {
                            if (val >= rule.minVal && val <= rule.maxVal && str.length() == rule.maxLen) {
                                if (trunkPrefix == null || (rule.flag12 & 0x01) != 0) {
                                    return true;
                                }
                            }
                        }
                    } else if (trunkPrefix != null && !hasRuleWithTrunkPrefix) {
                        for (PhoneRule rule : rules) {
                            if (val >= rule.minVal && val <= rule.maxVal && str.length() == rule.maxLen) {
                                if (intlPrefix == null || (rule.flag12 & 0x02) != 0) {
                                    return true;
                                }
                            }
                        }
                    }
                }
                return false;
            } else {
                return false;
            }
        }
    }

    public class PhoneRule {
        public int minVal;
        public int maxVal;
        public int byte8;
        public int maxLen;
        public int otherFlag;
        public int prefixLen;
        public int flag12;
        public int flag13;
        public String format;
        public boolean hasIntlPrefix;
        public boolean hasTrunkPrefix;

        String format(String str, String intlPrefix, String trunkPrefix) {
            boolean hadC = false;
            boolean hadN = false;
            boolean hasOpen = false;
            int spot = 0;
            StringBuilder res = new StringBuilder(20);
            for (int i = 0; i < format.length(); i++) {
                char ch = format.charAt(i);
                switch (ch) {
                    case 'c':
                        hadC = true;
                        if (intlPrefix != null) {
                            res.append(intlPrefix);
                        }
                        break;
                    case 'n':
                        hadN = true;
                        if (trunkPrefix != null) {
                            res.append(trunkPrefix);
                        }
                        break;
                    case '#':
                        if (spot < str.length()) {
                            res.append(str.substring(spot, spot + 1));
                            spot++;
                        } else if (hasOpen) {
                            res.append(" ");
                        }
                        break;
                    case '(':
                        if (spot < str.length()) {
                            hasOpen = true;
                        }
                    default:
                        if (!(ch == ' ' && i > 0 && ((format.charAt(i - 1) == 'n' && trunkPrefix == null) || (format.charAt(i - 1) == 'c' && intlPrefix == null)))) {
                            if (spot < str.length() || (hasOpen && ch == ')')) {
                                res.append(format.substring(i, i + 1));
                                if (ch == ')') {
                                    hasOpen = false;
                                }
                            }
                        }
                        break;
                }
            }
            if (intlPrefix != null && !hadC) {
                res.insert(0, String.format("%s ", intlPrefix));
            } else if (trunkPrefix != null && !hadN) {
                res.insert(0, trunkPrefix);
            }
            return res.toString();
        }

        boolean hasIntlPrefix() {
            return (flag12 & 0x02) != 0;
        }

        boolean hasTrunkPrefix() {
            return (flag12 & 0x01) != 0;
        }
    }
}

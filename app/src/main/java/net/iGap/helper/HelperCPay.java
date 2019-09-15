package net.iGap.helper;

public class HelperCPay {

    public static String PLAQUE = "plaque";
    private static String[] PLAQUE_ALPHABETS = {
            "الف", "ب", "پ", "ت", "ث", "ج", "چ", "ح", "خ", "د", "ذ",
            "ر", "ز", "ژ", "س", "ش", "ص", "ض", "ط", "ظ", "ع", "غ",
            "ف", "ق", "ک", "گ", "ل", "م", "ن", "و", "ه", "ی",
    };

    public static String getPlaqueAlphabet(int index) {
        return PLAQUE_ALPHABETS[index - 1];
    }

    public static String getPlaqueCode(String s) {
        int index = getPlaqueIndexByValue(PLAQUE_ALPHABETS, s) + 1;
        return String.valueOf(index).length() == 1 ? "0" + index : String.valueOf(index);
    }

    public static String[] getPlaque(String plaque) {
        String[] result = {"", "", "", ""};

        result[0] = plaque.substring(0, 2);
        result[1] = plaque.substring(2, 4);
        result[2] = plaque.substring(4, 7);
        result[3] = plaque.substring(7, 9);

        return result;
    }

    public static int getPlaqueIndexByValue(String[] arr, String s) {

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(s)) {
                return i;
            }
        }

        return 0;

    }

    public static String[] getPlaqueAlphabets() {
        return PLAQUE_ALPHABETS;
    }
}

package net.iGap.helper;

public class HelperCPay {


    public static String getPlaqueAlphabet(String s) {
        return "غ";
    }

    public static String[] getPlaque(String plaque) {
        String[] result = {"", "", "", ""};

        result[0] = plaque.substring(0, 2);
        result[1] = plaque.substring(2, 4);
        result[2] = plaque.substring(4, 7);
        result[3] = plaque.substring(7, 9);

        return result;
    }
}

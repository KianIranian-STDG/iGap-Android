package net.iGap.helper;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.iGap.R;

public class HelperMobileBank {

    public static int bankName(String cardNumber) {
        if (cardNumber == null)
            return R.string.empty_error_message;
        else if (cardNumber.startsWith("603799"))
            return R.string.bank_melli;
        else if (cardNumber.startsWith("589210"))
            return R.string.bank_sepah;
        else if (cardNumber.startsWith("627648"))
            return R.string.bank_tosee_saderat;
        else if (cardNumber.startsWith("627961"))
            return R.string.bank_sanato_madan;
        else if (cardNumber.startsWith("603770"))
            return R.string.bank_keshavarzi;
        else if (cardNumber.startsWith("628023"))
            return R.string.bank_maskan;
        else if (cardNumber.startsWith("627760"))
            return R.string.bank_post_bank;
        else if (cardNumber.startsWith("502908"))
            return R.string.bank_tosee_taavon;
        else if (cardNumber.startsWith("627412"))
            return R.string.bank_eghtesad_novin;
        else if (cardNumber.startsWith("622106"))
            return R.string.bank_parsian;
        else if (cardNumber.startsWith("502229"))
            return R.string.bank_pasargad;
        else if (cardNumber.startsWith("627488"))
            return R.string.bank_karafarin;
        else if (cardNumber.startsWith("621986"))
            return R.string.bank_saman;
        else if (cardNumber.startsWith("639346"))
            return R.string.bank_sina;
        else if (cardNumber.startsWith("639607"))
            return R.string.bank_sarmayeh;
        else if (cardNumber.startsWith("502806"))
            return R.string.bank_shahr;
        else if (cardNumber.startsWith("502938"))
            return R.string.bank_dey;
        else if (cardNumber.startsWith("603769"))
            return R.string.bank_saderat;
        else if (cardNumber.startsWith("610433"))
            return R.string.bank_mellat;
        else if (cardNumber.startsWith("627353"))
            return R.string.bank_tejarat;
        else if (cardNumber.startsWith("585983"))
            return R.string.bank_tejarat;
        else if (cardNumber.startsWith("627381"))
            return R.string.bank_ansar;
        else if (cardNumber.startsWith("639370"))
            return R.string.bank_mehr_eghtesad;
        else
            return R.string.empty_error_message;
    }

    public static int bankLogo(String cardNumber) {
        if (cardNumber == null)
            return R.drawable.bank_logo_default;
        else if (cardNumber.startsWith("603799"))
            return R.drawable.bank_logo_melli;
        else if (cardNumber.startsWith("589210"))
            return R.drawable.bank_logo_sepah;
        else if (cardNumber.startsWith("627648"))
            return R.drawable.bank_logo_saderat;
        else if (cardNumber.startsWith("627961"))
            return R.drawable.bank_logo_sanato_madan;
        else if (cardNumber.startsWith("603770"))
            return R.drawable.bank_logo_keshavarzi;
        else if (cardNumber.startsWith("628023"))
            return R.drawable.bank_logo_maskan;
        else if (cardNumber.startsWith("627760"))
            return R.drawable.bank_logo_post;
        else if (cardNumber.startsWith("502908"))
            return R.drawable.bank_logo_tosee_taavon;
        else if (cardNumber.startsWith("627412"))
            return R.drawable.bank_logo_eghtesad_novin;
        else if (cardNumber.startsWith("622106"))
            return R.drawable.ic_logo_parsian_without_theme;
        else if (cardNumber.startsWith("502229"))
            return R.drawable.bank_logo_pasargad;
        else if (cardNumber.startsWith("627488"))
            return R.drawable.bank_logo_karafarin;
        else if (cardNumber.startsWith("621986"))
            return R.drawable.bank_logo_saman;
        else if (cardNumber.startsWith("639346"))
            return R.drawable.bank_logo_sina;
        else if (cardNumber.startsWith("639607"))
            return R.drawable.bank_logo_sarmayeh;
        else if (cardNumber.startsWith("502806"))
            return R.drawable.bank_logo_shahr;
        else if (cardNumber.startsWith("502938"))
            return R.drawable.bank_logo_dey;
        else if (cardNumber.startsWith("603769"))
            return R.drawable.bank_logo_saderat;
        else if (cardNumber.startsWith("610433"))
            return R.drawable.bank_logo_mellat;
        else if (cardNumber.startsWith("627353"))
            return R.drawable.bank_logo_tejarat;
        else if (cardNumber.startsWith("585983"))
            return R.drawable.bank_logo_tejarat;
        else if (cardNumber.startsWith("627381"))
            return R.drawable.bank_logo_ansar;
        else if (cardNumber.startsWith("639370"))
            return R.drawable.bank_logo_mehr_eghtesad;
        else if (cardNumber.startsWith("636214"))
            return R.drawable.bank_logo_ayandeh;
        else if (cardNumber.startsWith("505416"))
            return R.drawable.bank_logo_gardeshgari;
        else if (cardNumber.startsWith("639599"))
            return R.drawable.bank_logo_ghavamin;
        else if (cardNumber.startsWith("636949"))
            return R.drawable.bank_logo_hekmat_iranian;
        else if (cardNumber.startsWith("636949"))
            return R.drawable.bank_logo_hekmat_iranian;
        else if (cardNumber.startsWith("505785"))
            return R.drawable.bank_logo_iran_zamin;
        else if (cardNumber.startsWith("636795"))
            return R.drawable.bank_logo_markazi;
        else if (cardNumber.startsWith("606373"))
            return R.drawable.bank_logo_mehr_iran;
        else if (cardNumber.startsWith("589463"))
            return R.drawable.bank_logo_refah;
        else if (cardNumber.startsWith("589463"))
            return R.drawable.bank_logo_refah;
        else
            return R.drawable.bank_logo_default;
    }

    public static String getCardNumberPattern(String cardNumber) {
        try {
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(cardNumber), String.class);
            return checkNumbersInMultiLangs(tempArray[0] + " - " + tempArray[1] + " - " + tempArray[2] + " - " + tempArray[3]);
        } catch (Exception e) {
            return checkNumbersInMultiLangs(cardNumber);
        }
    }

    public static String checkNumbersInMultiLangs(String number) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(number) : number;
    }
}

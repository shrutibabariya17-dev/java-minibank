import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern IFSC_PATTERN = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[1-9][0-9]*$");

    public static boolean isValidMobile(String mobile) {
        if (mobile == null) {
            return false;
        }
        Matcher matcher = MOBILE_PATTERN.matcher(mobile.trim());
        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email.trim());
        return matcher.matches();
    }

    public static boolean isValidPan(String pan) {
        if (pan == null) {
            return false;
        }
        Matcher matcher = PAN_PATTERN.matcher(pan.trim());
        return matcher.matches();
    }

    public static boolean isValidIfsc(String ifsc) {
        if (ifsc == null) {
            return false;
        }
        Matcher matcher = IFSC_PATTERN.matcher(ifsc.trim());
        return matcher.matches();
    }

    public static boolean isValidAmount(String amount) {
        if (amount == null) {
            return false;
        }
        Matcher matcher = AMOUNT_PATTERN.matcher(amount.trim());
        return matcher.matches();
    }
}

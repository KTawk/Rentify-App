package helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationHelper {
    private static final String USERNAME_REGEX = "[a-zA-Z0-9-_]+";
    private static final String NAME_REGEX = "[a-zA-Z0-9-_ ]+";

    public static boolean isValidUsername(String username) {
        Pattern pattern = Pattern.compile(USERNAME_REGEX);
        Matcher matcher = pattern.matcher(username);
        return !username.isEmpty() && matcher.matches();
    }

    public static boolean isValidName(String name) {
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return !name.isEmpty() && matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        return !password.isEmpty() && password.length() >= 3;
    }
}


package resources.Security;

import jakarta.ws.rs.NotAuthorizedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Security related stuff not related to Argon2.
 */
public class SecurityChecks {
    /// Includes very ASCII considered special character (from the Internet).
    private static final List<Character> SPECIAL_CHARACTERS = new ArrayList<>(Arrays.asList('!', '"', '#', '$', '%', '&',
            '\'', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '<', '=', '>', '?', '@', '[', '\\', ']', '^', '_',
            '`', '{', '|', '}', '~'));
    /// Includes every number.
    private static final List<Character> NUMBER_CHARACTERS = new ArrayList<>(Arrays.asList('1','2','3','4','5','6','7',
            '8','9','0'));
    /// Includes every Mayus letter. (Accepts ñ because I wanna - Firmed Daniel)
    private static final List<Character> MAYUS_CHARACTERS = new ArrayList<>(Arrays.asList('A','B','C','D','E','F','G',
            'H','I','J','K','L','M','N','Ñ','O','P','Q','R','S','T','U','V','W','X','Y','Z'));

    /**
     * Returns if password has a special character (considered by ASCII). Decide to make it different methods for better
     * error handling on FrontEnd.
     * @param password password being checked
     * @return if password contains special character
     */
    public static boolean hasSpecialCharacter(String password) {
        char[] charactedPassword = password.toCharArray();
        for (char special_character : charactedPassword) {
            if (SPECIAL_CHARACTERS.contains(special_character)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns if password has a number.
     * @param password password being checked
     * @return if password contains number
     */
    public static boolean hasNumberCharacter(String password) {
        char[] charactedPassword = password.toCharArray();
        for (char special_character : charactedPassword) {
            if (NUMBER_CHARACTERS.contains(special_character)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Returns if password has a mayus character.
     * @param password password being checked
     * @return if password contains mayus character
     */
    public static boolean hasMayusCharacter(String password) {
        char[] charactedPassword = password.toCharArray();
        for (char special_character : charactedPassword) {
            if (MAYUS_CHARACTERS.contains(special_character)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks every minimum required for password to be accepted, if wrong throw errors.
     * @param password password being checked
     */
    public static void followsAllMinimumRules(String password) {
        if (!SecurityChecks.hasSpecialCharacter(password)) {
            throw new NotAuthorizedException("Password does not have an special character");
        }
        if (!SecurityChecks.hasMayusCharacter(password)) {
            throw new NotAuthorizedException("Password does not have an mayus character");
        }
        if (!SecurityChecks.hasNumberCharacter(password)) {
            throw new NotAuthorizedException("Password does not have an number character");
        }
        if (password.length()<12) {
            throw new NotAuthorizedException("Password has less than 12 characters");
        }
    }
}

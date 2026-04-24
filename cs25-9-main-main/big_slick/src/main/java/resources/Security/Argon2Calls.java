package resources.Security;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Everything related to Argon2.
 */
public class Argon2Calls {
    private static final Argon2 argon2 = Argon2Factory.create();

    /**
     * Hashed an unhashed password.
     * @param unHashedPassword password to be hashed
     * @return hashed password
     */
    public static String hashPassword(String unHashedPassword) {
        char[] passwordChars = unHashedPassword.toCharArray();
        String hash = argon2.hash(10, 262144, 1, passwordChars);
        argon2.wipeArray(passwordChars);
        return hash;
    }

    /**
     * Verifies if an unhashedPassword and a hashedPassword are the same.
     *
     * @param hashedPassword   password already hashed
     * @param unhashedPassword password un-hashed
     * @return true=equal
     */
    public static boolean verifyPassword(String hashedPassword, String unhashedPassword) {
        char[] passwordChars = unhashedPassword.toCharArray();
//        return true;
        boolean match = argon2.verify(hashedPassword, passwordChars);
        argon2.wipeArray(passwordChars);
        System.out.println(match);
        return match; //Right click and then inspect a
    }
}

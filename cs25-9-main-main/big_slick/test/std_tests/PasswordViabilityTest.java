package std_tests;

import jakarta.ws.rs.NotAuthorizedException;
import org.junit.jupiter.api.Test;
import resources.Security.SecurityChecks;

import static org.junit.jupiter.api.Assertions.*;


public class PasswordViabilityTest {
    String notEnoughCharacters = "zT(^<20?8?=";
    String noSpecialCharacters = "KGc4S9r247DtFX";
    String noNumbers = "cu!EaN.+n+ZWOG";
    String noCapitals = "\\ls46{m2ah£0\"]2lv&!£";
    String validPassword = "5\\47iM`H(Qpr7ud7$~EBX=@f(";

    @Test
    public void shorty(){
        assertThrows(NotAuthorizedException.class,() -> SecurityChecks.followsAllMinimumRules(notEnoughCharacters));
    }
    @Test
    public void notSpecial() {
        assertThrows(NotAuthorizedException.class,() -> SecurityChecks.followsAllMinimumRules(noSpecialCharacters));

    }
    @Test
    public void numberless(){
        assertThrows(NotAuthorizedException.class,() -> SecurityChecks.followsAllMinimumRules(noNumbers));
        }
    @Test
    public void capitalLess(){
        assertThrows(NotAuthorizedException.class,() -> SecurityChecks.followsAllMinimumRules(noCapitals));
    }
    @Test
    public void passwordRules(){
    SecurityChecks.followsAllMinimumRules(validPassword);
    }
}

package std_tests;
import com.sun.source.tree.UsesTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resources.Security.Argon2Calls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class HashingTest {
    String password = "|ThisIsAPassword123|";
    String hashedPassword = Argon2Calls.hashPassword(password);

    @Test
    public void verifyMethodTest(){
        assertTrue(Argon2Calls.verifyPassword(hashedPassword, password));
    }
}

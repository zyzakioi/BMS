package utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {
    @Test
    void testToHash() {
        char[] password = "Login123".toCharArray();
        for (int i = 0; i < 3; i++) {
            String hashed = SecurityUtils.toHash(password);
            System.out.println(hashed);
        }
    }

    @Test
    void testUniqueHash() {
        char[] password = "AdminPassword123".toCharArray();
        HashSet<String> used = new HashSet<>();
        for (int i = 0; i < 50; i++) {
            String hashed = SecurityUtils.toHash(password);
            assertFalse(used.contains(hashed));
            assertEquals(69, hashed.length());
            used.add(hashed);
        }
    }

    @Test
    void testDecode() {
        char[] badPasswd = "goodbye_world".toCharArray();
        char[] passwd = "hello_world".toCharArray();
        for (int i = 0; i < 10; i++) {
            String hashed = SecurityUtils.toHash(passwd);
            assertTrue(SecurityUtils.checkPasswd(passwd, hashed));
            assertFalse(SecurityUtils.checkPasswd(badPasswd, hashed));
        }
    }

    @Test
    void testSpecial() {
        String hash = "nPb8zO4cL6v/Ps9qTSRzrQ==:z3qJAeL9hGVeuj5rmmOwDmcUVuDC4t+mjlUANPgJq3s=";
        char[] passwd = "Login123".toCharArray();
        assertTrue(SecurityUtils.checkPasswd(passwd, hash));

    }
}
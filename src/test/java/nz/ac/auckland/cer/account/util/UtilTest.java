package nz.ac.auckland.cer.account.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class UtilTest {

    @Test
    public void testCreateAccountName() {

        Util util = new Util();
        assertEquals("N/A", util.createAccountName(null, null));
        assertEquals("N/A", util.createAccountName("test@test.org", null));
        assertEquals("N/A", util.createAccountName("test.org", null));
        assertEquals("N/A", util.createAccountName("test@test.org", ""));
        assertEquals("N/A", util.createAccountName("test.org", ""));
        assertEquals("abcd123", util.createAccountName("abcd123@auckland.ac.nz", "Test User"));
        assertEquals("test.user", util.createAccountName("abcd123@test.org", "Test User"));
        assertEquals("test.user", util.createAccountName("abcd123@test.org", "Test   User"));
        assertEquals("test.user.user", util.createAccountName("abcd123@test.org", "Test User User"));
    }

}

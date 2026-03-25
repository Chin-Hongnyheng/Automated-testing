package lab01;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class Usertest {
    @Test
    public void testChangeEmail(){
        User user = new User();
        assertNull(user.getEmail());
        user.setEmail("Johny@gmail.com");
        assertEquals("Johny@gmail.com", user.getEmail());
    }
}

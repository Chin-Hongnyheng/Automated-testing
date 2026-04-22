package test.java.ip;

import org.junit.jupiter.api.Test;
import test.BankAccount;
import test.IBankAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for BankAccount.
 */
public class BankAccountTest {

    private static final double TEN = 10.0;
    private static final double FIVE = 5.0;
    private static final double TWENTY = 20.0;

    private IBankAccount createAccount() {
        return new BankAccount();
    }

    @Test
    public void testDeposit() {
        IBankAccount acc = createAccount();
        acc.deposit(TEN);
        assertEquals(TEN, acc.getBalance());
    }

    @Test
    public void testWithdraw() {
        IBankAccount acc = createAccount();
        acc.deposit(TEN);
        acc.withdraw(FIVE);
        assertEquals(FIVE, acc.getBalance());
    }

    @Test
    public void testWithdrawInsufficientBalance() {
        IBankAccount acc = createAccount();
        acc.deposit(FIVE);

        Exception e = assertThrows(IllegalStateException.class, () -> {
            acc.withdraw(TEN);
        });

        assertEquals("Insufficient balance", e.getMessage());
    }

    @Test
    public void testTransfer() {
        IBankAccount acc1 = createAccount();
        IBankAccount acc2 = new BankAccount();

        acc1.deposit(TEN);
        acc1.transfer(acc2, FIVE);

        assertEquals(FIVE, acc1.getBalance());
        assertEquals(FIVE, acc2.getBalance());
    }

    @Test
    public void testHasSufficientBalance() {
        IBankAccount acc = createAccount();
        acc.deposit(TEN);

        assertTrue(acc.hasSufficientBalance(FIVE));
        assertFalse(acc.hasSufficientBalance(TWENTY));
    }
}

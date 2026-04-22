package test;

/**
 * Interface representing basic bank account operations.
 */
public interface IBankAccount {

    /**
     * Deposits money into the account.
     *
     * @param amount amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is negative or zero
     */
    void deposit(double amount);

    /**
     * Withdraws money from the account.
     *
     * @param amount amount to withdraw (must be positive and <= balance)
     * @throws IllegalArgumentException if amount is invalid
     * @throws IllegalStateException if insufficient balance
     */
    void withdraw(double amount);

    /**
     * Returns current account balance.
     *
     * @return current balance
     */
    double getBalance();

    /**
     * Transfers money from this account to another account.
     *
     * @param toAccount target account
     * @param amount amount to transfer
     * @throws IllegalArgumentException if amount is invalid
     * @throws IllegalStateException if insufficient balance
     */
    void transfer(IBankAccount toAccount, double amount);

    /**
     * Checks if account has sufficient balance.
     *
     * @param amount amount to check
     * @return true if balance >= amount, false otherwise
     */
    boolean hasSufficientBalance(double amount);
}

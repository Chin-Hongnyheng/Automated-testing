package test;

/**
 * Simple implementation of IBankAccount.
 */
public class BankAccount implements IBankAccount {

    private double balance;

    public BankAccount() {
        this.balance = 0.0;
    }

    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
        balance += amount;
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid withdraw amount");
        }
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance");
        }
        balance -= amount;
    }

    @Override
    public double getBalance() {
        return balance;
    }

    @Override
    public void transfer(IBankAccount toAccount, double amount) {
        if (toAccount == null) {
            throw new IllegalArgumentException("Target account is null");
        }
        this.withdraw(amount);
        toAccount.deposit(amount);
    }

    @Override
    public boolean hasSufficientBalance(double amount) {
        return balance >= amount;
    }
}

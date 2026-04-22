package test;

/**
 * Interface for the ICalculator.
 */
public interface ICalculator {

    /**
     * Adds two numbers.
     *
     * @param a first number
     * @param b second number
     * @return sum
     */
    int add(int a, int b);

    /**
     * Subtracts b from a.
     *
     * @param a first number
     * @param b second number
     * @return difference
     */
    int subtract(int a, int b);

    /**
     * Multiplies two numbers.
     *
     * @param a first number
     * @param b second number
     * @return product
     */
    double multiply(double a, double b);

    /**
     * Divides a by b.
     *
     * @param a first number
     * @param b second number
     * @return quotient
     * @throws ArithmeticException if b is zero
     */
    double divide(double a, double b) throws ArithmeticException;

    /**
     * Returns remainder of a divided by b.
     *
     * @param a first number
     * @param b second number
     * @return remainder
     * @throws ArithmeticException if b is zero
     */
    double modulo(double a, double b) throws ArithmeticException;
}

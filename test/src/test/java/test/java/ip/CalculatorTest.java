package test.java.ip;

import org.junit.jupiter.api.Test;
import test.Calculator;
import test.ICalculator;

/**
 * Unit tests for ICalculator.
 */
public class CalculatorTest {

    /** Constant value 1. */
    private static final double ONE = 1.0;

    /** Constant value 2. */
    private static final double TWO = 2.0;

    /** Constant value 3. */
    private static final double THREE = 3.0;

    /** Constant value 4. */
    private static final int FOUR = 4;

    /** Constant value 5. */
    private static final int FIVE = 5;

    /** Constant value 9. */
    private static final int NINE = 9;

    public ICalculator getCalculator() {
        return new Calculator();
    }

    @Test
    public void testAdd() {
        ICalculator calculator = getCalculator();
        int a = FOUR;
        int b = FIVE;
        int expected = NINE;
        assert calculator.add(a, b) == expected
                : "Addition test failed (Expected: " + expected + ", Actual: " + calculator.add(a, b) + ")";
    }

    @Test
    public void testSubtract() {
        ICalculator calculator = getCalculator();
        int a = FIVE;
        int b = FOUR;
        int expected = (int) ONE;
        assert calculator.subtract(a, b) == expected
                : "Subtraction test failed (Expected: " + expected + ", Actual: " + calculator.subtract(a, b) + ")";
    }

    @Test
    public void testMultiply() {
        ICalculator calculator = getCalculator();
        double a = ONE;
        double b = THREE;
        double expected = THREE;
        assert calculator.multiply(a, b) == expected
                : "Multiplication test failed (Expected: " + expected + ", Actual: " + calculator.multiply(a, b) + ")";
    }

    @Test
    public void testDivide() {
        ICalculator calculator = getCalculator();
        double a = TWO;
        double b = ONE;
        double expected = TWO;
        assert calculator.divide(a, b) == expected
                : "Division test failed (Expected: " + expected + ", Actual: " + calculator.divide(a, b) + ")";
    }

    @Test
    public void testModulo() {
        ICalculator calculator = getCalculator();
        double a = THREE;
        double b = TWO;
        double expected = ONE;
        assert calculator.modulo(a, b) == expected
                : "Modulo test failed (Expected: " + expected + ", Actual: " + calculator.modulo(a, b) + ")";
    }

    @Test
    public void testDivideByZero() {
        ICalculator calculator = getCalculator();
        try {
            calculator.divide(ONE, 0);
            assert false : "Division by zero test failed (Expected: ArithmeticException)";
        } catch (ArithmeticException e) {
            assert e.getMessage().equals("Division by zero")
                    : "Division by zero test failed (Actual: '" + e.getMessage() + "')";
        }
    }

    @Test
    public void testModuloByZero() {
        ICalculator calculator = getCalculator();
        try {
            calculator.modulo(THREE, 0);
            assert false : "Modulo by zero test failed (Expected: ArithmeticException)";
        } catch (ArithmeticException e) {
            assert e.getMessage().equals("Division by zero")
                    : "Modulo by zero test failed (Actual: '" + e.getMessage() + "')";
        }
    }
}

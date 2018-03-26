package com.example.tapiwa.collegebuddy.Main.Calculator;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by tapiwa on 3/24/18.
 */
public class CalculatorEngineTest {

    /* SIMPLE INPUT TESTS */
    @Test
    public void additionTest() throws Exception {
        double delta = 0.1;
        //simple addition test
        String input1 = "1 + 1";
        double output1;
        double expected1 = 2.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input1);
        output1 = calculatorEngine.getResult();

        assertEquals("Addition test failed", expected1, output1, delta);
    }

    @Test
    public void subtractionTest() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "13 - 1";
        double output;
        double expected = 12.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("Simple subtraction test failed", expected, output, delta);

    }

    @Test
    public void multiplicationTest() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "13 x 2";
        double output;
        double expected = 26.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("Simple multiplication test failed", expected, output, delta);
    }

    @Test
    public void divisionTest() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "13 / 12";
        double output;
        double expected = 1.0833333333;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("Simple division test failed", expected, output, delta);
    }

    /* MULTIPLE OPERATORS TESTS */
    @Test
    public void multipleOperators1() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "7 x 8 - 234 + 48 / 3 - 4 + 2";
        double output;
        double expected = -164.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("MultipleOperators test1 failed", expected, output, delta);
    }


    @Test
    public void multipleOperators2() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "67 / 4 / 5 / 3 + 3 - 3 x 1";
        double output;
        double expected = 1.1166666666;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("MultipleOperators test2 failed", expected, output, delta);
    }


    @Test
    public void multipleOperators3() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "10 x 3 x 4 x 3 - 10002";
        double output;
        double expected = -9642;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("MultipleOperators test3 failed", expected, output, delta);
    }

    /* POSITIVE AND NEGATIVE NUMBERS TESTS */
    @Test
    public void posNegTest1() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "-1 + -1";
        double output;
        double expected = -2;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("MultipleOperators test3 failed", expected, output, delta);
    }


    @Test
    public void posNegTest2() throws Exception {
        double delta = 0.1;
        //simple subtraction test
        String input = "+1 - -4 + -7 x -3 / 4";
        double output;
        double expected = 10.25;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("posNegTest2  failed", expected, output, delta);
    }


    @Test
    public void posNegTest3() throws Exception {
        double delta = 0.1;

        String input = "1 - -1 - -1 - -1";
        double output;
        double expected = 4;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("postNegTest3 failed", expected, output, delta);
    }


    @Test
    public void posNegTest4() throws Exception {
        double delta = 0.1;

        String input = "-1 / 3 x -4";
        double output;
        double expected = 1.3333333333;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("postNegTest4 failed", expected, output, delta);
    }

    /* BRACKETS TESTS */
    @Test
    public void bracketsTest1() throws Exception {
        double delta = 0.1;
        //brackets at end of expression
        String input = "2 x (5 + 2)";
        double output;
        double expected = 14;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("bracketsTest1 failed", expected, output, delta);
    }



    @Test
    public void bracketsTest2() throws Exception {
        double delta = 0.1;
        //brackets in the middle of expression
        String input = "14 / (3 + 4) + 4";
        double output;
        double expected = 6.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("bracketsTest2 failed", expected, output, delta);
    }

    @Test
    public void bracketsTest3() throws Exception {
        double delta = 0.1;

        String input = "5 x 2 + (3 x (5 x 2))";
        double output;
        double expected = 40.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("bracketsTest3 failed", expected, output, delta);
    }

    @Test
    public void bracketsTest4() throws Exception {
        double delta = 0.1;
        //brackets at beginning of expression
        String input = "(3 - 1) / 8 x 9";
        double output;
        double expected = 2.25;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("bracketsTest4 failed", expected, output, delta);
    }

    @Test
    public void bracketsTest5() throws Exception {
        double delta = 0.1;
        //brackets in the middle test
        String input = "5 + (3 + 4 / 2 - 4) + 9";
        double output;
        double expected = 9.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("bracketsTest5 failed", expected, output, delta);
    }



    /* EXTREMES TESTS */
    @Test
    public void extremeTests1() throws Exception {
        double delta = 0.1;
        //large numbers
        String input = "1000 x 1233 x 23242 / 1234";
        double output;
        double expected = 23223165.316;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("extremeTests1 failed", expected, output, delta);
    }

    @Test
    public void extremeTests() throws Exception {
        double delta = 0.1;
        //large numbers
        String input = "-1000 x 1233 x 23242 / 1234";
        double output;
        double expected = -23223165.316;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("extremeTests failed", expected, output, delta);
    }



    @Test
    public void extremeTests2() throws Exception {
        double delta = 0.1;
        //division by zero
        String input = "1 / 0";
        double output;
        double expected = 0.0;

        CalculatorEngine calculatorEngine = new CalculatorEngine(input);
        output = calculatorEngine.getResult();
        assertEquals("extremeTests2 failed", expected, output, delta);
    }








}
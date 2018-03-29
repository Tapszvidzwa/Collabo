package com.example.tapiwa.collegebuddy.Main.Calculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.LinkedList;

/**
 * Created by tapiwa on 3/23/18.
 */

public class CalculatorEngine {

    private LinkedList<Datum> lst;
    public String expression;
    public boolean error = false;

    public CalculatorEngine() {
    }

    public CalculatorEngine(String expression) {
        this.lst = new LinkedList<>();
        this.expression = expression;
    }

    public double getResult() {

        try {
            if (expression.length() == 0) {
                return 0;
            }

            //populate linked list with expression arguments
            populatelst();

            checkCorrectFormat();

            //run calculation for division and multiplication first (if any)
            char operators[] = {'/', 'x'};
            for (char c : operators) {
                int index;
                while ((index = containsOperator(lst, c)) != -1) {
                    double args[] = getArgs(lst, index);
                    double result;
                    switch (c) {
                        case '/':
                            result = args[0] / args[1];
                            replaceOldArgs(lst, result, index);
                            break;
                        case 'x':
                            result = args[0] * args[1];
                            replaceOldArgs(lst, result, index);
                            break;
                    }
                }
            }
            //run calculation for addition or subtraction(if any)
            int indx;
            while ((indx = containsAddSubOperator(lst)) != -1) {
                double args2[] = getArgs(lst, indx);
                double result2;
                char k = lst.get(indx).operator;

                switch (k) {
                    case '+':
                        result2 = args2[0] + args2[1];
                        replaceOldArgs(lst, result2, indx);
                        break;
                    case '-':
                        result2 = args2[0] - args2[1];
                        replaceOldArgs(lst, result2, indx);
                        break;
                }
            }

            //return the result
            return roundedResult();

        } catch (Exception e) {
            error = true;
            return 0;
        }
    }

    private double roundedResult() {
        double value = lst.get(0).data;
        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);
        BigDecimal bd = new BigDecimal(value, mc);
        return bd.doubleValue();
    }

    private int findInnerMostBracket(String expression) {

        for(int i = 0; i < expression.length(); i++) {
            if(expression.charAt(i) == '(') {
                if(containsOpeningBracket(expression.substring(i + 1))){
                    continue;
                } else {
                    return i;
                }
            }
        }

        return -1;
    }


    private int findNextClosingBracket(String expression) {

        for(int i = 0; i < expression.length(); i++) {
            if(expression.charAt(i) == ')') {
                if(containsOpeningBracket(expression.substring(i + 1))){
                    continue;
                } else {
                    return i;
                }
            }
        }

        return -1;
    }


    private boolean containsOpeningBracket(String expression) {
        for(int i = 0; i < expression.length(); i++) {
            if(expression.charAt(i) == '(') {
                return true;
            }
        }
        return false;
    }

    private void evaluateBrackets() {
        int startIndex, endIndex;
        double result;

        while((startIndex = findInnerMostBracket(expression)) != -1) {
            endIndex = findNextClosingBracket(expression);

            CalculatorEngine calculatorEngine = new CalculatorEngine(expression.substring(startIndex + 1, endIndex));
            result = calculatorEngine.getResult();

            StringBuilder newExpression = new StringBuilder();
            newExpression.append(expression);
            newExpression.replace(startIndex, endIndex + 1, Double.toString(result));

            expression = newExpression.toString();
        }

    }

    //populates the linked list with the data
    private void populatelst() {

        boolean digits = false;
        StringBuilder value = new StringBuilder();

        //evaluates all the expressions in the brackets first and updates the expression
        evaluateBrackets();

        for (int i = 0; i < expression.length(); i++) {

            char c = expression.charAt(i);

            //if we find a digit, just append it to value
            if (Character.isDigit(c) || c == '.') {
                value.append(expression.charAt(i));
                digits = true;

                if(i == expression.length() - 1) {
                    char sign = '+';
                    double data;
                    if ((data = Double.parseDouble(value.toString())) < 0) {
                        sign = '-';
                    }
                    //add the digits to the lst
                    lst.add(new CalculatorEngine.Datum(false, data, sign));
                    digits = false;
                    value.setLength(0);
                }
                continue;
            }

            //if you reach a space character
            if (Character.isSpaceChar(c)) {
                //if the previous values before the space were digits
                if (digits) {
                    //determine the sign of the digits
                    char sign = '+';
                    double data;
                    if ((data = Double.parseDouble(value.toString())) < 0) {
                        sign = '-';
                    }
                    //add the digits to the lst
                    lst.add(new CalculatorEngine.Datum(false, data, sign));
                    digits = false;
                    value.setLength(0);
                } else {
                    //otherwise it must have been an operator, add the operator to lst
                    lst.add(new CalculatorEngine.Datum(true, 0.0, expression.charAt(i - 1)));
                    value.setLength(0);
                }

                continue;
            }

            if(c == '-' || c == '+') {
               if(Character.isDigit(expression.charAt(i + 1))) {
                   value.append(expression.charAt(i));
                   digits = true;
                   continue;
               }
            }
        }
    }

    /* containsAddSubOperator
    @purpose: checks whether the given list contains a '+' or '-' operator
    @param lst: Linked list of type Datum
    @return: returns the index of the '+' or '-' operator if found, otherwise -1
     */
    private int containsAddSubOperator(LinkedList<CalculatorEngine.Datum> lst) {
        int i = 0;
        //look for the given operator c
        while(i < lst.size()) {
            CalculatorEngine.Datum datum = lst.get(i);
            //if you find the operator, return its index in the linked list
            if(datum.is_operator && ((datum.operator == '+') || (datum.operator == '-'))) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void replaceOldArgs(LinkedList<CalculatorEngine.Datum> lst, double result, int operator_index) {
        //determine sign of result
        char sign = '+';
        if(result < 0) {
            sign = '-';
        }
        //create new datum for the result
        CalculatorEngine.Datum new_arg = new CalculatorEngine.Datum(false, result, sign);
        //add datum to list and remove old arguments
        lst.set(operator_index, new_arg);
        lst.remove(operator_index + 1);
        lst.remove(operator_index - 1);
    }

    private double[] getArgs(LinkedList<CalculatorEngine.Datum> lst, int operator_index) {
        double args[] = new double[2];
        args[0] = lst.get(operator_index - 1).data;
        args[1] = lst.get(operator_index + 1).data;
        return args;
    }

    private int containsOperator(LinkedList<CalculatorEngine.Datum> result, char c) {
        int i = 0;
        //look for the given operator c
        while(i < result.size()) {
            CalculatorEngine.Datum datum = result.get(i);
            //if you find the operator, return its index in the linked list
            if(datum.is_operator && datum.operator == c) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void checkCorrectFormat() {
        //matching brackets
        //no sign symbol left at the end
        if(lst.get(lst.size() - 1).is_operator) {
            lst.add(new Datum(false, 0.0, '+'));
        }
        //
    }

    private class Datum{
        private boolean is_operator;
        private double data;
        private char operator;

        public Datum(boolean is_operator, double data, char operator) {
            CalculatorEngine.Datum.this.is_operator = is_operator;
            CalculatorEngine.Datum.this.data = data;
            CalculatorEngine.Datum.this.operator = operator;
        }
    }
}

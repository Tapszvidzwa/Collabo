package com.example.tapiwa.collegebuddy.Main.Calculator;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tapiwa.collegebuddy.R;
import com.itextpdf.text.pdf.hyphenation.TernaryTree;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by tapiwa on 10/5/17.
 */

public class Calculator extends Fragment {

    private Button oneBtn, twoBtn, threeBtn, fourBtn, fiveBtn,
            sixBtn, sevenBtn, eightBtn, nineBtn, equalsBtn,
            divBtn, addBtn, subBtn, zeroBtn, dotBtn, multBtn,
            delBtn, opnBtn, clsBtn, percentBtn;

    private View CalculatorView;
    private EditText calculatorScreen;
    private TextView miniCalculatorScreen;
    private boolean previous_char_operator = true;
    private boolean previous_char_sign = false;
    private boolean pre_sign_div_mult = false;

    public Calculator() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        CalculatorView = inflater.inflate(R.layout.activity_calculator, container, false);
        initializeViews();
        setButtonListeners();

        return CalculatorView;
    }

    private void initializeViews() {
        zeroBtn = CalculatorView.findViewById(R.id.num_kepad_0);
        oneBtn = CalculatorView.findViewById(R.id.num_kepad_1);
        twoBtn = CalculatorView.findViewById(R.id.num_kepad_2);
        threeBtn = CalculatorView.findViewById(R.id.num_keypad_3);
        fourBtn = CalculatorView.findViewById(R.id.num_kepad_4);
        fiveBtn = CalculatorView.findViewById(R.id.num_kepad_5);
        sixBtn = CalculatorView.findViewById(R.id.num_keypad_6);
        sevenBtn = CalculatorView.findViewById(R.id.num_kepad_7);
        eightBtn = CalculatorView.findViewById(R.id.num_keypad_8);
        nineBtn = CalculatorView.findViewById(R.id.num_keypad_9);
        equalsBtn = CalculatorView.findViewById(R.id.num_keypad_equals);
        divBtn = CalculatorView.findViewById(R.id.num_keypad_division);
        addBtn = CalculatorView.findViewById(R.id.num_keypad_addition);
        dotBtn = CalculatorView.findViewById(R.id.num_kepad_dot);
        subBtn = CalculatorView.findViewById(R.id.num_keypad_subtraction);
        multBtn = CalculatorView.findViewById(R.id.num_keypad_multiplication);
        percentBtn = CalculatorView.findViewById(R.id.num_keypad_percentage);
        delBtn = CalculatorView.findViewById(R.id.num_keypad_delete);
        clsBtn = CalculatorView.findViewById(R.id.num_kepad_closing_bracket);
        opnBtn = CalculatorView.findViewById(R.id.num_kepad_opening_bracket);
        calculatorScreen = CalculatorView.findViewById(R.id.calculator_screen);
        miniCalculatorScreen = CalculatorView.findViewById(R.id.mini_calculator_screen);
    }

    private void setButtonListeners() {
        zeroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.zero));
                resetSignChecks();
            }
        });

        oneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.one));
                resetSignChecks();
            }
        });

        twoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.two));
                resetSignChecks();
            }
        });

        threeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.three));
                resetSignChecks();
            }
        });

        fourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.four));
                resetSignChecks();
            }
        });

        fiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.five));
                resetSignChecks();
            }
        });

        sixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.six));
                resetSignChecks();
            }
        });

        sevenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.seven));
                resetSignChecks();
            }
        });

        eightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.eight));
                resetSignChecks();
            }
        });

        nineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.nine));
                resetSignChecks();
            }
        });

        equalsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String expression = calculatorScreen.getText().toString();
                int len = expression.length();

                if(!Character.isDigit(expression.charAt(len - 1))) {
                    return;
                }

                copy_display_to_mini_screen();
                calculate_result();
                //resetSignChecks();
                previous_char_operator = true;
            }
        });

        divBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pre_sign_div_mult) {
                    return;
                } else {
                    pre_sign_div_mult = true;
                    calculatorScreen.append(getText(R.string.division));
                }
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(previous_char_sign) {
                    return;
                }

                if(lastCharDigit()) {
                    calculatorScreen.append(getText(R.string.addition));
                    previous_char_operator = true;
                    previous_char_sign = false;
                    return;
                }

                if(previous_char_operator) {
                    calculatorScreen.append(getText(R.string.positive));
                    previous_char_sign = true;
                    previous_char_operator = false;
                } else {
                    calculatorScreen.append(getText(R.string.addition));
                    previous_char_operator = true;
                    previous_char_sign = false;
                }
            }
        });

        multBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pre_sign_div_mult) {
                    return;
                } else {
                    pre_sign_div_mult = true;
                    calculatorScreen.append(getText(R.string.multiplication));
                }
            }
        });

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(previous_char_sign) {
                    return;
                }

                if(lastCharDigit()) {
                    calculatorScreen.append(getText(R.string.subtraction));
                    previous_char_operator = true;
                    previous_char_sign = false;
                    return;
                }

                if(previous_char_operator) {
                    calculatorScreen.append(getText(R.string.negative));
                    previous_char_sign = true;
                    previous_char_operator = false;
                } else {
                    calculatorScreen.append(getText(R.string.subtraction));
                    previous_char_operator = true;
                    previous_char_sign = false;
                }

            }
        });

        dotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.dot));
            }
        });

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.setText("");
                miniCalculatorScreen.setText("");
                previous_char_sign = false;
                previous_char_operator = true;
            }
        });

        clsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.closing_bracket));
            }
        });

        opnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.opening_bracket));
            }
        });

        percentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculatorScreen.append(getText(R.string.percentage));
            }
        });

    }

    private void resetSignChecks() {
        previous_char_sign = false;
        previous_char_operator = false;
        pre_sign_div_mult = false;
    }

    private void copy_display_to_mini_screen() {
        miniCalculatorScreen.setText(calculatorScreen.getText().toString());
        miniCalculatorScreen.append(getText(R.string.equals));
    }

    private boolean lastCharDigit() {

        if(calculatorScreen.getText().length() > 0) {
            int len = calculatorScreen.length();
            if(Character.isDigit(calculatorScreen.getText().toString().charAt(len - 1))) {
               return true;
            }
        }
        return false;
    }

    private void calculate_result() {
        //Get the expression on the calculator screen
        String expression = calculatorScreen.getText().toString();
        //Get the result of the calculation
        CalculatorEngine calculatorEngine = new CalculatorEngine(expression);

        double result = calculatorEngine.getResult();
        //if the returned value is an integer, display it as an integer
        if(result % 1 == 0) {
            calculatorScreen.setText(Integer.toString((int) result));
        } else {
            //otherwise display it as a double
            calculatorScreen.setText(Double.toString(result));
        }
    }

}


package net.i4ideas.mustakim.calculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    // IDs of all the numeric buttons
    private int[] numericButtons = {R.id.button_zero, R.id.button_one, R.id.button_two, R.id.button_three, R.id.button_four, R.id.button_five, R.id.button_six, R.id.button_seven, R.id.button_eight, R.id.button_nine};
    // IDs of all the operator buttons
    private int[] operatorButtons = {R.id.button_add, R.id.button_substract, R.id.button_multiply, R.id.button_divide};
    // ID of equal sign
    private int equalButton;
    // Object of input Display
    private TextView inputTextView;
    
    // Object of input Display
    private TextView displayTextView;
    // Flag for first input
    private boolean isFirstInput;
    // Flag for single operator
    private boolean isSingleOperator;
    // Flag for numeric Input
    private boolean isNumericInput;
    // Flag for single equal
    private boolean isEqualButtonNotUsed = true;
    // Flag for single decimal point
    private boolean isSingleDecimal;
    // Flag for first decimal
    private boolean isFirstDecimal;
    // First Value
    private double firstValue;
    // Last Value
    private double lastValue;
    // Operator
    private String operator;
    // Result
    private double result;
    //current Input
    private String currentInput = "";
    // Shared Preferences
    private SharedPreferences sharedPref;
    //Editor
    private SharedPreferences.Editor editor;
    // Total Result
    String totalResult;
    // Rounding Format
    DecimalFormat decimalFormat = new DecimalFormat("#.####");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);


        isFirstInput = true;
        isSingleOperator = true;
        isSingleDecimal = true;
        isFirstDecimal = true;

        isNumericInput = false;


        inputTextView = (TextView) findViewById(R.id.input_text);
        displayTextView = (TextView) findViewById(R.id.display_text);


        // Shared Preferences
        sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.sharedPref_file_Name), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        totalResult = sharedPref.getString(getString(R.string.sharedPref_Name), "");


        onClickNumericalButton ();
        onClickOperatorButton ();

        equalButton = R.id.button_equal;

        onClickEqualButton ();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onClickNumericalButton () {
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                isNumericInput = true;
                isEqualButtonNotUsed = true;
                TextView currentButton = (TextView)v;
                if(isFirstInput && isSingleDecimal) {
                    inputTextView.setText(currentButton.getText().toString());
                    isFirstInput = false;
                } else {
                    inputTextView.setText(inputTextView.getText().toString() + currentButton.getText().toString());
                }
            }
        };
        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void onClickOperatorButton () {
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                isEqualButtonNotUsed = true;
                isNumericInput = false;
                TextView currentButton = (TextView)v;
                if(!isFirstInput && isSingleOperator) {
                    operator = currentButton.getText().toString();
                    inputTextView.setText(inputTextView.getText().toString() + currentButton.getText().toString());
                    isSingleOperator = false;
                    isNumericInput = false;
                    isSingleDecimal = true;
                    isFirstDecimal = true;
                }
            }
        };
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    private void onClickEqualButton () {
        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                currentInput = inputTextView.getText().toString();
                if(!isSingleOperator && isEqualButtonNotUsed == true) {
                    if (isNumericInput) {
                        //Calculation
                        if(operator.equals("+")) {
                            getValueFromStringTokenizer("+");
                            result = firstValue + lastValue;
                            //Print in display Text View
                            displayTextView.setText(String.valueOf(decimalFormat.format(result)));
                        } else if (operator.equals("-")) {
                            getValueFromStringTokenizer("-");
                            result = firstValue - lastValue;
                            //Print in display Text View
                            displayTextView.setText(String.valueOf(decimalFormat.format(result)));
                        } else if (operator.equals("*")) {
                            getValueFromStringTokenizer("*");
                            result = firstValue * lastValue;
                            //Print in display Text View
                            displayTextView.setText(String.valueOf(decimalFormat.format(result)));
                        } else if (operator.equals("/")) {
                            getValueFromStringTokenizer("/");
                            if (lastValue != 0) {
                                result = firstValue / lastValue;
                                //Print in display Text View
                                displayTextView.setText(String.valueOf(decimalFormat.format(result)));
                            } else {
                                displayTextView.setText("Infinite");
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                } else if (isEqualButtonNotUsed == true){
                    displayTextView.setText(currentInput);
                }

                String historyString = currentInput + " = " + displayTextView.getText();
                if(!isSingleOperator && isNumericInput) {
                    totalResult = totalResult +"\n"+ historyString;
                    editor.putString(getString(R.string.sharedPref_Name), totalResult);
                    editor.commit();
                }

                //Again set everything to normal
                isFirstInput = true;
                isSingleOperator = true;
                isFirstDecimal = true;
                isSingleDecimal = true;
                isEqualButtonNotUsed = false;
            }
        };
        findViewById(equalButton).setOnClickListener(listener);
    }


    //if history is clicked
    public void onClickHistoryButton (View v) {
        setContentView(R.layout.history_layout);
        TextView historyTextView = (TextView)findViewById(R.id.history_content);
        historyTextView.setText(sharedPref.getString("History", "No History Found"));
    }

    public void onClickHistoryBack(View v) {
        setContentView(R.layout.activity_main);
        isFirstInput = true;
        isSingleOperator = true;
        isSingleDecimal = true;
        isFirstDecimal = true;
        inputTextView = (TextView) findViewById(R.id.input_text);
        displayTextView = (TextView) findViewById(R.id.display_text);



        onClickNumericalButton ();
        onClickOperatorButton ();

        equalButton = R.id.button_equal;

        onClickEqualButton ();
    }

    public void onClickClear(View v) {
        inputTextView.setText(R.string.zero);
        displayTextView.setText(R.string.zero);
        isFirstInput = true;
        isSingleOperator = true;
        isSingleDecimal = true;
        isFirstDecimal = true;
        isEqualButtonNotUsed = true;
    }

    public void onClickDecimal (View v) {
        if(isSingleDecimal && isFirstDecimal && !isNumericInput) {
            if(isSingleOperator == false) {
                inputTextView.setText(inputTextView.getText().toString() + "0.");
            } else {
                inputTextView.setText("0" + ".");
            }
            isFirstDecimal = false;
            isFirstInput = false;
            isSingleDecimal = false;
        } else if(isSingleDecimal && isNumericInput && isEqualButtonNotUsed) {
            inputTextView.setText(inputTextView.getText().toString() + ".");
            isSingleDecimal = false;
            isFirstInput = false;
        }
    }

    public void getValueFromStringTokenizer(String operator) {
        StringTokenizer removeOperatorPlus = new StringTokenizer(currentInput, operator);
        firstValue = Double.valueOf(removeOperatorPlus.nextToken());
        lastValue = Double.valueOf(removeOperatorPlus.nextToken());
    }


}
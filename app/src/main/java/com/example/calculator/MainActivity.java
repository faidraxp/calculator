package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.DownloadManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity  {

    EditText editText;
    TextView textView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    Button button7;
    Button button8;
    Button button9;
    Button buttonC;
    Button buttonDel;
    Button buttonAdd;
    Button buttonSub;
    Button buttonDiv;
    Button buttonMul;
    Button buttonEqual;
    Button buttonSign;
    Button buttonDot;
    private char CURRENT_ACTION;
    private static final char ADDITION = '+';
    private static final char SUBTRACTION = '-';
    private static final char MULTIPLICATION = '*';
    private static final char DIVISION = '/';

    private Double valueOne = Double.NaN;
    private Double valueTwo;
    private DecimalFormat decimalFormat;

    ConverterFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        decimalFormat = new DecimalFormat("#.##########");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragment = (ConverterFragment) getSupportFragmentManager()
                .findFragmentById(R.id.converter);
        setUpCalculationButtons();

    }
    public void onClickDelete(View view){

        String str=editText.getText().toString();
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
            editText.setText(str);
        }

    }
    public void onClickClear(View view){

        if(valueOne!=Double.NaN){
            textView.setText(null);
            editText.setText(null);
            Bundle bundle = new Bundle();
            bundle.putString("value","1");
            fragment.sendData(bundle);
            valueOne = Double.NaN;
            valueTwo=null;
        }

    }
    public void onClickDivision(View view){
        String value=editText.getText().toString();
        if(!value.isEmpty()){
            computeCalculation();
            if(!Double.isNaN(valueOne)){
                CURRENT_ACTION = DIVISION;
                if((int) Math.ceil(valueOne)<0){
                    textView.setText("("+decimalFormat.format(valueOne)+")" + "/");
                }
                else{
                    textView.setText(decimalFormat.format(valueOne) + "/");
                }
                editText.setText(null);
            }
            else{
                editText.setText(null);
                Toast.makeText(this,"An error has occured",Toast.LENGTH_LONG).show();
            }
        }

    }
    public void onClickMultiply(View view){
        String value=editText.getText().toString();
        if(!value.isEmpty()){
            computeCalculation();
            if(!Double.isNaN(valueOne)){
                CURRENT_ACTION = MULTIPLICATION;
                if((int) Math.ceil(valueOne)<0){
                    textView.setText("("+decimalFormat.format(valueOne)+")" + "*");
                }
                else{
                    textView.setText(decimalFormat.format(valueOne) + "*");
                }
                editText.setText(null);
            }
            else{
                editText.setText(null);
                Toast.makeText(this,"An error has occured",Toast.LENGTH_LONG).show();
            }

        }


    }
    public void onClickSubtract(View view){
        String value=editText.getText().toString();
        if(!value.isEmpty()){
            computeCalculation();
            if(!Double.isNaN(valueOne)){
                CURRENT_ACTION = SUBTRACTION;
                textView.setText(decimalFormat.format(valueOne) + "-");
                editText.setText(null);
            }
            else{
                editText.setText(null);
                Toast.makeText(this,"An error has occured",Toast.LENGTH_LONG).show();
            }
        }

    }
    public void onClickAdd(View view){
        String value=editText.getText().toString();
        if(!value.isEmpty()){
            computeCalculation();
            if(!Double.isNaN(valueOne)){
                CURRENT_ACTION = ADDITION;
                textView.setText(decimalFormat.format(valueOne) + "+");
                editText.setText(null);
            }
            else{
                editText.setText(null);
                Toast.makeText(this,"An error has occured",Toast.LENGTH_LONG).show();
            }

        }

    }
    public void onClickEqual(View view){
        if(!editText.getText().toString().isEmpty()&&!Double.isNaN(valueOne)){
            Double value1= Double.parseDouble(editText.getText().toString());
            Integer value =value1.intValue();
            if(CURRENT_ACTION==DIVISION && value.equals(0) && Double.compare(Math.abs(value1),0)==0){
                Log.d("exception","value: "+value +"Current action"+ CURRENT_ACTION);
                Toast.makeText(this,"Division by zero is not permitted",Toast.LENGTH_LONG).show();
            }
            else {
                computeCalculation();
                if((int) Math.ceil(valueTwo)<0){
                    textView.setText(textView.getText().toString() + "("+
                            decimalFormat.format(valueTwo)+")" + " = " + decimalFormat.format(valueOne));
                }
                else{
                    textView.setText(textView.getText().toString() +
                            decimalFormat.format(valueTwo) + " = " + decimalFormat.format(valueOne));

                }
                Bundle bundle = new Bundle();
                bundle.putString("value",String.valueOf(Math.abs(valueOne)));
                fragment.sendData(bundle);
                valueOne = Double.NaN;
                valueTwo = null;
                CURRENT_ACTION = '0';

            }

        }
        else if(!editText.getText().toString().isEmpty() && Double.isNaN(valueOne) ){
            textView.setText(decimalFormat.format(Double.parseDouble(editText.getText().toString())));
            Bundle bundle = new Bundle();
            bundle.putString("value",String.valueOf(Math.abs(Double.parseDouble(editText.getText().toString()))));
            fragment.sendData(bundle);
            CURRENT_ACTION = '0';
        }
    }
    public void onClickNumber(View view){
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        editText.setText(editText.getText()+buttonText);
    }
    public void onClickSign(View view){
        if(editText.getText()!=null &&editText.getText().toString()!="0"&&!editText.getText().toString().isEmpty()){
            try {
                Double buttonValue = Double.parseDouble(editText.getText().toString())*(-1);
                editText.setText(decimalFormat.format(buttonValue));
            }catch (Exception e){
                Log.e("exception","an exception has occured");
            }

        }

    }
    public void onClickDot(View view){

        String text=editText.getText().toString();
        if(text.length()>0){

            char lastChar=text.charAt(text.length()-1);
            if(lastChar!='.'|| text.indexOf('.')==-1){
                Boolean flag=computeLastChar(editText.getText().toString());
                if(flag){
                    editText.setText(editText.getText()+".");
                }
                else{
                    editText.setText(editText.getText()+"0.");
                }

            }
        }
        else {
            editText.setText(editText.getText()+"0.");
        }
    }
    private void computeCalculation() {
        if(!Double.isNaN(valueOne)) {
            valueTwo = Double.parseDouble(editText.getText().toString());
            editText.setText(null);

            if(CURRENT_ACTION == ADDITION)
                valueOne = this.valueOne + valueTwo;
            else if(CURRENT_ACTION == SUBTRACTION)
                valueOne = this.valueOne - valueTwo;
            else if(CURRENT_ACTION == MULTIPLICATION)
                valueOne = this.valueOne * valueTwo;
            else if(CURRENT_ACTION == DIVISION ){
                valueOne = this.valueOne / valueTwo;
            }

        }
        else {
            try {
                valueOne = Double.parseDouble(editText.getText().toString());
            }
            catch (Exception e){
                Log.d("exception",e.getLocalizedMessage());
            }
        }
    }
    private Boolean computeLastChar(String text){
        Boolean flag;
        Log.d("test","int"+text.length());

        if(text.length()>0){
            flag = Character.isDigit(text.charAt(text.length()-1));
        }
        else{
            flag=false;
        }

        return flag;
    }
    private void setUpCalculationButtons(){
        button0=findViewById(R.id.zero);
        button1=findViewById(R.id.one);
        button2=findViewById(R.id.two);
        button3=findViewById(R.id.three);
        button4=findViewById(R.id.four);
        button5=findViewById(R.id.five);
        button6=findViewById(R.id.six);
        button7=findViewById(R.id.seven);
        button8=findViewById(R.id.eight);
        button9=findViewById(R.id.nine);
        buttonC=findViewById(R.id.delete);
        buttonDel=findViewById(R.id.Char);
        buttonAdd=findViewById(R.id.add);
        buttonSub=findViewById(R.id.subtract);
        buttonDiv=findViewById(R.id.division);
        buttonMul=findViewById(R.id.multiply);
        buttonEqual=findViewById(R.id.equal);
        buttonSign=findViewById(R.id.sign);
        buttonDot=findViewById(R.id.dot);
        textView=findViewById(R.id.results);
        editText=findViewById(R.id.calculation);


    }


}
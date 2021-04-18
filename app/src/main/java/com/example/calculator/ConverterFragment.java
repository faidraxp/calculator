package com.example.calculator;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConverterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConverterFragment extends Fragment implements OnItemSelectedListener {
    EditText editTextCur1;
    EditText editTextCur2;
    Spinner spinner1;
    Spinner spinner2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String VALUE_ONE = "valueOne";
    private static final String VALUE_TWO = "valueTwo";
    private static final String BASE_CURRENCY = "baseCurrency";
    private static final String CONVERTED_TO_CURRENCY = "convertedToCurrency";
    private static final String DECIMAL_FORM = "param2";
    // TODO: Rename and change types of parameters

    private String baseCurrency = "GBP";
    private String convertedToCurrency = "USD";
    private DecimalFormat decimalFormat;
    private View view;
    private Double value;

    public ConverterFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static ConverterFragment newInstance(Double value) {
        ConverterFragment fragment = new ConverterFragment();
        Bundle args = new Bundle();
        args.putDouble(VALUE_ONE, value);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            value= Double.parseDouble(getArguments().getString(VALUE_ONE));

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_converter, container, false);
        editTextCur1=view.findViewById(R.id.firstCurrencyValue);
        editTextCur1.setText("1");
        editTextCur2=view.findViewById(R.id.secondCurrencyValue);
        spinner1=view.findViewById(R.id.firstCurrency);
        spinner1.setOnItemSelectedListener(this);
        spinner2=view.findViewById(R.id.secondCurrency);
        spinner2.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currencies, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.currencies2, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        return view;
    }
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        if (parent.getId() == R.id.firstCurrency) {
            baseCurrency=parent.getItemAtPosition(pos).toString();
            getApiResult();
        }
        else if(parent.getId() == R.id.secondCurrency){
            convertedToCurrency=parent.getItemAtPosition(pos).toString();
            getApiResult();
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
    private void getApiResult() {

        if (baseCurrency == convertedToCurrency) {
            Toast.makeText(getActivity(), "Please pick a currency to convert", Toast.LENGTH_SHORT).show();
        } else {
            getCurrencyResult();
        }
    }

    private void getCurrencyResult() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://api.ratesapi.io/api/latest?base=" + baseCurrency + "&symbols=" + convertedToCurrency;
        Log.d("",url);
        JsonObjectRequest stringRequest = new JsonObjectRequest (Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("json response",response.toString());
                        try {

                            Float rates=Float.parseFloat(response.getJSONObject("rates").getString(convertedToCurrency));
                            Float value =(Float.parseFloat(editTextCur1.getText().toString())*rates);
                            editTextCur2.setText(value.toString());

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response + "\"");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("json response",error.getLocalizedMessage());
                    }
                });
        Log.d("",""+stringRequest);
        queue.add(stringRequest);

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("","test");
    }
    public void sendData(Bundle bundle){
        if(bundle!=null){
            String value = bundle.getString("value");
            editTextCur1.setText(value.toString());
            getApiResult();
        }

    }
}
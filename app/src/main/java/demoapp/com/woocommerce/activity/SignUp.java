package demoapp.com.woocommerce.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.SignatureType;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import demoapp.com.woocommerce.R;
import demoapp.com.woocommerce.WooCommerceApi;

public class SignUp extends AppCompatActivity {

    private static final String TAG = SignUp.class.getSimpleName();
    TextInputLayout inputLayoutFirstName, inputLayoutLastName, inputLayoutEmail, inputLayoutAddress, inputLayoutCity, inputLayoutDistrict, inputLayoutPostCode, inputLayoutCountry, inputLayoutPhone, inputLayoutPassword, inputLayoutRePassword;
    EditText _firstNameText, _lastNameText, _emailText, _addressText, _cityText, _districtText, _postCodeText, _countryText, _phoneText, _passwordText, _reEnterPasswordText;
    Button _signupButton;
    String firstName, lastName, email, address, city, district, post, country, phone, password, reEnterPassword;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.signup_layout);

        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setTintColor(Color.parseColor("#de4256"));

        // Set up the toolbar.
        Toolbar signup_toolbar = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(signup_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar cptb = getSupportActionBar();
        cptb.setDisplayHomeAsUpEnabled(true);
        cptb.setDisplayShowHomeEnabled(true);

        TextView mSignUpToolbarTitle = (TextView) findViewById(R.id.signup_toolbar_title);
        mSignUpToolbarTitle.setText("Reverie Shop SignUp");

        inputLayoutFirstName = (TextInputLayout) findViewById(R.id.inputLayoutFirstName);
        inputLayoutLastName = (TextInputLayout) findViewById(R.id.inputLayoutLastName);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.inputLayoutEmail);
        inputLayoutAddress = (TextInputLayout) findViewById(R.id.inputLayoutAddress);
        inputLayoutCity = (TextInputLayout) findViewById(R.id.inputLayoutCity);
        inputLayoutDistrict = (TextInputLayout) findViewById(R.id.inputLayoutDistrict);
        inputLayoutPostCode = (TextInputLayout) findViewById(R.id.inputLayoutPostCode);
        inputLayoutCountry = (TextInputLayout) findViewById(R.id.inputLayoutCountry);
        inputLayoutPhone = (TextInputLayout) findViewById(R.id.inputLayoutPhone);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.inputLayoutPassword);
        inputLayoutRePassword = (TextInputLayout) findViewById(R.id.inputLayoutRePassword);

        _firstNameText = (EditText) findViewById(R.id.input_first_name);
        _lastNameText = (EditText) findViewById(R.id.input_last_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _addressText = (EditText) findViewById(R.id.input_address);
        _cityText = (EditText) findViewById(R.id.input_city);
        _districtText = (EditText) findViewById(R.id.input_district);
        _postCodeText = (EditText) findViewById(R.id.input_post_code);
        _countryText = (EditText) findViewById(R.id.input_country);
        _phoneText = (EditText) findViewById(R.id.input_phone);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _reEnterPasswordText = (EditText) findViewById(R.id.input_reEnterPassword);

        _signupButton = (Button) findViewById(R.id.btn_signup);
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");
        if (!validate()) {
            onSignupFailed();
            return;
        }
        _signupButton.setEnabled(false);
        new UserSignUpAsyncTask().execute();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "SignUp failed for Wrong Data", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        firstName = _firstNameText.getText().toString();
        lastName = _lastNameText.getText().toString();
        email = _emailText.getText().toString();
        address = _addressText.getText().toString();
        city = _cityText.getText().toString();
        district = _districtText.getText().toString();
        post = _postCodeText.getText().toString();
        country = _countryText.getText().toString();
        phone = _phoneText.getText().toString();
        password = _passwordText.getText().toString();
        reEnterPassword = _reEnterPasswordText.getText().toString();

        if (firstName.isEmpty() || firstName.length() < 3 || firstName.length() > 20) {
            _firstNameText.setError("should between 3 and 20 characters");
            valid = false;
        } else {
            _firstNameText.setError(null);
        }

        if (lastName.isEmpty() || lastName.length() < 3 || lastName.length() > 20) {
            _lastNameText.setError("should between 3 and 20 characters");
            valid = false;
        } else {
            _lastNameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (address.isEmpty() || address.length() < 5 || address.length() > 500) {
            _addressText.setError("should between 5 and 500 characters");
            valid = false;
        } else {
            _addressText.setError(null);
        }

        if (city.isEmpty() || city.length() < 5 || city.length() > 20) {
            _cityText.setError("should between 5 and 50 characters");
            valid = false;
        } else {
            _cityText.setError(null);
        }

        if (district.isEmpty() || district.length() < 5 || district.length() > 20) {
            _districtText.setError("should between 5 and 20 characters");
            valid = false;
        } else {
            _firstNameText.setError(null);
        }

        if (post.isEmpty() || post.length() < 4 || post.length() > 5) {
            _postCodeText.setError("should between 4 and 5 characters");
            valid = false;
        } else {
            _postCodeText.setError(null);
        }

        if (country.isEmpty() || country.length() < 5 || country.length() > 20) {
            _countryText.setError("should between 5 and 20 characters");
            valid = false;
        } else {
            _countryText.setError(null);
        }

        if (phone.isEmpty() || phone.length() != 11) {
            _phoneText.setError("Enter Valid Phone Number");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 16) {
            _passwordText.setError("between 8 and 16 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 8 || reEnterPassword.length() > 16 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }
        return valid;
    }

    private class UserSignUpAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SignUp.this);
            progressDialog.setMessage("Registering...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject requestObject = new JSONObject();
            JSONObject subRequestObject = new JSONObject();
            try {
                requestObject.put("username", email);
                requestObject.put("email", email);
                requestObject.put("password", password);
                subRequestObject.put("city", city);
                requestObject.put("billing", subRequestObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String restURL = "http://www.reveriegroup.com/demo/wp-json/wc/v1/customers";
            OAuthService service = new ServiceBuilder()
                    .provider(WooCommerceApi.class)
                    .apiKey("ck_8e344e1ef42a00f4d4c3bb1ede1557d48790c322") //Your Consumer key
                    .apiSecret("cs_09de5a54244811bc1291ebb4a4d4b7b6725c4cec") //Your Consumer secret
                    .scope("API.Public") //fixed
                    .signatureType(SignatureType.QueryString)
                    .build();
            String payload = requestObject.toString();
            OAuthRequest request = new OAuthRequest(Verb.POST, restURL);
            request.addHeader("Content-Type", "application/json");
            request.addPayload(payload);
            Token accessToken = new Token("", ""); //not required for context.io
            service.signRequest(accessToken, request);
            Response response = request.send();
            String data = response.getBody();
            Log.d("OAuthTask", response.getBody());
            try {
                parseJSON(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (response.getCode() !=201){
                onSignupFailed();
            }
            int responseCode = response.getCode();
            Log.d("ResponseCode:: ", String.valueOf(responseCode));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            progressDialog = null;
            Intent intent = new Intent(SignUp.this, MainActivity.class);
            SignUp.this.startActivity(intent);
        }
    }

    public void parseJSON(String data) throws JSONException {
        JSONArray jsonArr = new JSONArray(data);
        for (int i = 0; i < jsonArr.length(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            String id = jsonObj.getString("id").toString();
            String username = jsonObj.getString("username");
            String email = jsonObj.getString("email");
            String subdata = jsonObj.getString("billing");
            JSONArray json_data1 = new JSONArray(subdata);
            for (int j = 0; j < json_data1.length(); j++) {
                jsonObj = json_data1.getJSONObject(j);
                city = jsonObj.getString("city");
            }
            Log.d("User Details: ", "ID:: " + id + " Name:: " + username+" Email:: "+email+" City:: "+city);
        }
    }

    // back arrow action
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // back button press method
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

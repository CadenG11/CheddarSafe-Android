package com.cheddarsecurity.cheddarsafe.setup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.login.LoginActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is for setting up the email after setting up an account.
 */
public class SetupEmailActivity extends AppCompatActivity {
    /** Variables **/
    EditText email;
    Button save;
    JSONObject jo = new JSONObject();
    Intent receivedIntent;

    String recaptchaToken = "";

    JSONObject userJson = new JSONObject();

    /**
     * Prevents the user from pressing back to leave the activity. Sends
     * alert to tell the user that they cannot leave.
     */
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"You must give an email.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets the variables to their initial values when this activity is created.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_email);

        email = (EditText)findViewById(R.id.setupEmail);
        save = (Button)findViewById(R.id.setupEmailBtn);
        receivedIntent = getIntent();

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Setup security");
    }

    /**
     * This method saves the email that was created to the database.
     * They have specifications to meet and will alert the user if it does not.
     *
     * @param v The button that calls this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void saveEmail(View v) {
        String emailText = email.getText().toString();
        boolean filled;
        if(emailText.length() == 0) {
            filled = false;
        } else {
            filled = true;
        }
        if(filled) {
            if (emailText.length() <= 100) {
                try {
                    jo.put("mname",receivedIntent.getStringExtra("mname"));
                    jo.put("mpass",receivedIntent.getStringExtra("mpass"));
                    jo.put("email", emailText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TokenHandler.sendVolley(this, Request.Method.POST, TokenHandler.api_url + "/validateemail", jo, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        userJson = jo;
                        recaptchaExec();
                    }

                    @Override
                    public void onFailure(JSONObject result) {

                    }
                });
            } else {
                Toast.makeText(this, "Email can't have over 100 characters. Currently " + emailText.length(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please make sure all blanks are filled in.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Executes the reCAPTCHA and sends the username to the server.
     */
    private void recaptchaExec() {
        SafetyNet.getClient(this).verifyWithRecaptcha(TokenHandler.recaptchaKey)
                .addOnSuccessListener(this,
                        response -> {
                            // Indicates communication with reCAPTCHA service was
                            // successful.
                            String userResponseToken = response.getTokenResult();
                            assert userResponseToken != null;
                            if (!userResponseToken.isEmpty()) {
                                // Validate the user response token using the
                                // reCAPTCHA siteverify API.
                                recaptchaToken = userResponseToken;
                                sendUser();
                            }
                        })
                .addOnFailureListener(this, e -> {
                    if (e instanceof ApiException) {
                        // An error occurred when communicating with the
                        // reCAPTCHA service. Refer to the status code to
                        // handle the error appropriately.
                        ApiException apiException = (ApiException) e;
                        int statusCode = apiException.getStatusCode();
                        Log.d("reCAPTCHA Error", "Error: " + CommonStatusCodes
                                .getStatusCodeString(statusCode));
                    } else {
                        // A different, unknown type of error occurred.
                        Log.d("reCAPTCHA Error", "Error: " + e.getMessage());
                    }
                });
    }

    private void sendUser() {
        try {
            userJson.put("recaptchaToken", recaptchaToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TokenHandler.sendVolley(this, Request.Method.POST, TokenHandler.api_url + "/confirmation", userJson, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(SetupEmailActivity.this, "Confirmation email sent.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SetupEmailActivity.this, LoginActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(JSONObject result) {
                Toast.makeText(SetupEmailActivity.this, "Error sending confirmation email.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
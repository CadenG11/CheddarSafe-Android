package com.cheddarsecurity.cheddarsafe.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.start.ForkActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for helping the user if they forgot their account information.
 */
public class ForgotAccActivity extends AppCompatActivity {

    /** Variables **/
    Button enter;
    TextView question;
    EditText answer;
    String answerText;

    String recaptchaToken = "";


    /**
     * When the back button is pressed, it will send the user to the fork activity.
     */
    @Override
    public void onBackPressed() {
        Intent fork = new Intent(this, ForkActivity.class);
        startActivity(fork);
    }

    /**
     * Called when this activity is created. Sets initial values of the variables.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_acc);

        enter = (Button)findViewById(R.id.forgotEnterButton);
        answer = (EditText)findViewById(R.id.forgotAnswerText);
        question = (TextView)findViewById(R.id.forgotAccText);
        question.setText("Please enter in your username.");
        answer.setHint("Username");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Forgot Account Info");
    }

    /**
     * Checks to see if the answer given by the user is a valid username.
     *
     * @param v The button that is pressed.
     */
    public void checkUsernameAnswer(View v) {
        answerText = answer.getText().toString();
        if (answerText.length() != 0) {
            recaptchaExec();
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
                                sendName();
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

    /**
     * Sends the username to the server to send a recovery email.
     */
    private void sendName() {
        answerText = answer.getText().toString();
        JSONObject json = new JSONObject();
        try {
            json.put("mname", answerText);
            json.put("recaptchaToken", recaptchaToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TokenHandler.sendVolley(this, Request.Method.POST, TokenHandler.api_url + "/recover", json, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(ForgotAccActivity.this, "Recovery email sent.", Toast.LENGTH_SHORT).show();
                Intent fork = new Intent(ForgotAccActivity.this, ForkActivity.class);
                startActivity(fork);
            }
            @Override
            public void onFailure(JSONObject result) {

            }
        });
    }
}
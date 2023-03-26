package com.cheddarsecurity.cheddarsafe.login;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.CSSingleton;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;
import com.cheddarsecurity.cheddarsafe.setup.SetupActivity;
import com.cheddarsecurity.cheddarsafe.start.ForkActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the screen for the user to input the username and password
 * they have set for the master account.
 */
@RequiresApi(api = Build.VERSION_CODES.P)
public class LoginActivity extends AppCompatActivity {

    /**
     * Variables
     **/
    AccountDatabaseHelper accDB = new AccountDatabaseHelper(this);
    EditText loginUsername, loginPassword;
    Switch passSwitch;
    int def;

    /**
     * When the back button is pressed, the user is sent to the fork activity.
     */
    @Override
    public void onBackPressed() {
        Intent fork = new Intent(this, ForkActivity.class);
        startActivity(fork);
    }

    /**
     * This method is called when this activity is created.
     * Sets the initial values of the variables used.
     *
     * @param savedInstanceState The current status of the view.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUsername = (EditText) findViewById(R.id.loginUsername);
        loginPassword = (EditText) findViewById(R.id.loginPassword);
        def = loginPassword.getInputType();
        passSwitch = (Switch) findViewById(R.id.switch1);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Login");
    }

    /**
     * This method checks to see if the user has the correct information and
     * then logs the user in.
     *
     * @param v The button that is pressed when this method is called.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void login(View v) {
        String username = ((EditText) findViewById(R.id.loginUsername)).getText().toString();
        String password = ((EditText) findViewById(R.id.loginPassword)).getText().toString();
        String JSON_URL = TokenHandler.api_url + "/validate";
        JSONObject jo = new JSONObject();
        try {
            jo.put("mname", username);
            jo.put("mpass", password);
        } catch (JSONException e) {
            Log.d("JSONObj Error", e.toString());
        }
        TokenHandler.sendVolley(this, Request.Method.POST, JSON_URL, jo,new ServerCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();
                TokenHandler.loggedUsername = username;
                Intent folder = new Intent(LoginActivity.this, FolderActivity.class);
                startActivity(folder);
            }

            @Override
            public void onFailure(JSONObject result) {

            }
        });
    }

    /**
     * This method allows the password typed in to be viewed as plain text
     * or as password dots instead.
     *
     * @param v The button that is pressed when this method is called.
     */
    public void showHideLoginPass(View v) {
        if (passSwitch.isChecked()) {
            loginPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            loginPassword.setInputType(def);
        }
        loginPassword.setSelection(loginPassword.length());
    }

    /**
     * Sends user to create account screen.
     *
     * @param v The view that activates this method.
     */
    public void toCreate(View v) {
        Intent create = new Intent(this, SetupActivity.class);
        startActivity(create);
    }
}



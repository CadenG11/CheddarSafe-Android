package com.cheddarsecurity.cheddarsafe.setup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.google.common.base.CharMatcher;

import org.json.JSONObject;

/**
 * This class is for setting up an account for the app. This is done by
 * asking for a username and password. The user and pass have certain restrictions.
 */
public class SetupActivity extends AppCompatActivity {

    /** Variables **/
    AccountDatabaseHelper myDB;
    Button view, create;
    EditText setupUsername, setupPassword, setupRepassword;
    Switch passSwitch;
    int def;

    /**
     * This method is called when this activity is first created.
     * Sets the initial values of the variables.
     *
     * @param savedInstanceState is the saved state of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Create account");

        create = (Button) findViewById(R.id.createAccount);
        setupUsername = (EditText) findViewById(R.id.setupUsername);
        setupPassword = (EditText) findViewById(R.id.setupPassword);
        setupRepassword = (EditText) findViewById(R.id.setupRePassword);
        def = setupPassword.getInputType();
        passSwitch = (Switch)findViewById(R.id.switch3);
        myDB = new AccountDatabaseHelper(SetupActivity.this);
    }

    /**
     * Creates the account using the username and password provided. Checks to see if the
     * username and password are valid.
     *
     * @param v The view that calls this method.
     */
    public void createAccount(View v) {
        String username = ((EditText)findViewById(R.id.setupUsername)).getText().toString();
        String password = ((EditText)findViewById(R.id.setupPassword)).getText().toString();
        String repassword = ((EditText)findViewById(R.id.setupRePassword)).getText().toString();
        boolean valid = true, match = true, filled = true;
        if(CharMatcher.whitespace().matchesAnyOf(username) || CharMatcher.whitespace().matchesAnyOf(password)
            || username.length() <= 4 || password.length() <= 4) {
            valid = false;
        }
        if(!password.equals(repassword)) {
            match = false;
        }
        if(username.length() == 0 || password.length() == 0 || repassword.length() == 0) {
            filled = false;
        }
        if(filled) {
            if(valid) {
                if(match) {
                    JSONObject json = new JSONObject();
                    String url = TokenHandler.api_url + "/validatecreate";
                    try {
                        json.put("mname", username);
                        json.put("mpass", password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    TokenHandler.sendVolley(this, Request.Method.POST, url, json, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            AddData(username, password);
                        }

                        @Override
                        public void onFailure(JSONObject result) {

                        }
                    });
                } else {
                    Toast.makeText(this,"Passwords do not match. Make sure the passwords entered match.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,"Invalid username or password.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"Please make sure all blanks are filled in.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adds data to the database for the account created. Will alert the user if successful or not.
     *
     * @param mname is the username to add to the database.
     * @param mpass is the password to add to the database.
     */
    public void AddData(String mname, String mpass) {
        Intent email = new Intent(SetupActivity.this, SetupEmailActivity.class);
        email.putExtra("mname", mname);
        email.putExtra("mpass", mpass);
        startActivity(email);
    }

    /**
     * Changes the password text between plain text and dots.
     *
     * @param v The button that calls this method.
     */
    public void showHideSetupPass(View v) {
        if(passSwitch.isChecked()) {
            setupPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            setupRepassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            setupPassword.setInputType(def);
            setupRepassword.setInputType(def);
        }
        setupPassword.setSelection(setupPassword.length());
        setupRepassword.setSelection(setupRepassword.length());
    }
}

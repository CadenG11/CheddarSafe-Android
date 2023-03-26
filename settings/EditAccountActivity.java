package com.cheddarsecurity.cheddarsafe.settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.login.ForgotAccActivity;
import com.cheddarsecurity.cheddarsafe.login.LoginActivity;
import com.google.common.base.CharMatcher;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class allows the user to edit their account information.
 */
public class EditAccountActivity extends AppCompatActivity {

    /** Variables **/
    AccountDatabaseHelper accDB = new AccountDatabaseHelper(this);
    Button saveAccEditBtn;
    TextView infoText;
    EditText user, oldpass, newpass, renewpass;
    String typedUser, typedOldPass, typedNewPass, typedReNewPass, passwordfill;
    Switch oldPassSwitch, newPassSwitch;
    int def;

    /**
     * This method is called on creation of this class. Sets the initial
     * values of the variables.
     *
     * @param savedInstanceState is the saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_account);
        saveAccEditBtn = (Button)findViewById(R.id.saveAccEditBtn);
        user = (EditText)findViewById(R.id.accEditUser);
        oldpass = (EditText)findViewById(R.id.accEditOldPass);
        newpass = (EditText)findViewById(R.id.accEditNewPass);
        renewpass = (EditText)findViewById(R.id.accEditReNewPass);
        infoText = (TextView)findViewById(R.id.infoText);
        String content = TokenHandler.loggedUsername.equals("") ? getIntent().getStringExtra("oldusername") : TokenHandler.loggedUsername;
        SpannableString str = new SpannableString(new StringBuilder().append(getString(R.string.displayInfo)).append(" ").append(content));
        str.setSpan(new UnderlineSpan(), 24, str.length(), 0);
        infoText.setText(str);
        Intent from = getIntent();
        passwordfill = from.getStringExtra("forgotPassword");
        oldpass.setText(passwordfill);
        def = oldpass.getInputType();
        oldPassSwitch = (Switch)findViewById(R.id.switch2);
        newPassSwitch = (Switch)findViewById(R.id.switch4);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Change account information");
    }

    /**
     * Checks to see if the new information given is valid and is typed correctly.
     *
     * @param v The button that calls this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void checkForUpdate(View v) {
        boolean match, valid, filled;
        typedUser = user.getText().toString();
        typedOldPass = oldpass.getText().toString();
        typedNewPass = newpass.getText().toString();
        typedReNewPass = renewpass.getText().toString();
        match = typedNewPass.equals(typedReNewPass);
        valid = !CharMatcher.whitespace().matchesAnyOf(typedNewPass) && typedNewPass.length() > 4 && !CharMatcher.whitespace().matchesAnyOf(typedUser) && typedUser.length() > 4;
        filled = typedUser.length() != 0 && typedOldPass.length() != 0 && typedNewPass.length() != 0 && typedReNewPass.length() != 0;
        if(filled) {
            if(valid) {
                if(match) {
                    JSONObject jo = new JSONObject(), total = new JSONObject();
                    try {
                        jo.put("mname",getIntent().getStringExtra("oldusername"));
                        jo.put("mpass",typedOldPass);
                        total.accumulate("old_user", jo);
                        JSONObject joTwo = new JSONObject();
                        joTwo.put("mname",typedUser);
                        joTwo.put("mpass",typedNewPass);
                        total.accumulate("new_user", joTwo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TokenHandler.sendVolley(this, Request.Method.POST, TokenHandler.api_url + "/updateuser", total, new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            Toast.makeText(EditAccountActivity.this, "Account updated successfully.", Toast.LENGTH_SHORT).show();
                            Intent login = new Intent(EditAccountActivity.this, LoginActivity.class);
                            startActivity(login);
                        }

                        @Override
                        public void onFailure(JSONObject result) {

                        }
                    });
                } else {
                    Toast.makeText(this,"Passwords do not match. Make sure the passwords entered match.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,"Invalid new username or password.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"Please make sure all blanks are filled in.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Toggles between hidden and plain text in the new password field.
     *
     * @param v The button that calls this method.
     */
    public void showHideEditNewPass(View v) {
        if(newPassSwitch.isChecked()) {
            newpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            renewpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            newpass.setInputType(def);
            renewpass.setInputType(def);
        }
        newpass.setSelection(newpass.length());
        renewpass.setSelection(renewpass.length());
    }

    /**
     * Toggles between hidden and plain text in the old password field.
     *
     * @param v The button that calls this method.
     */
    public void showHideEditOldPass(View v) {
        if(oldPassSwitch.isChecked()) {
            oldpass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            oldpass.setInputType(def);
        }
        oldpass.setSelection(oldpass.length());
    }

    /**
     * Makes it so when the user presses the back button,
     * it will send the user back to the activity they were
     * at before.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void onBackPressed() {
        String prevActivity = getIntent().getStringExtra("prevActivity");
        if (prevActivity.equals("ForgotAcc")) {
            Intent folders = new Intent(this, ForgotAccActivity.class);
            startActivity(folders);
        }
        if (prevActivity.equals("Settings")) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        }
    }
}
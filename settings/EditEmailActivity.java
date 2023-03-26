package com.cheddarsecurity.cheddarsafe.settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This method edits the email address of the user.
 */
public class EditEmailActivity extends AppCompatActivity {

    /** Variables **/
    Button save;
    EditText email;
    TextView currentEmail;

    /**
     * This method is called when this activity is created. Sets the variables
     * to their initial values.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        save = (Button)findViewById(R.id.saveEditEmailBtn);
        email = (EditText)findViewById(R.id.editEmail);
        currentEmail = (TextView)findViewById(R.id.currentEmailText);
        Intent receivedIntent = getIntent();
        currentEmail.setText(receivedIntent.getStringExtra("oldemail"));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Change security information");
    }

    /**
     * Updates the security email address to the given value.
     *
     * @param v The button that calls this method.
     */
    public void updateEmail(View v) {
        String emailText = email.getText().toString();
        boolean filled;
        filled = emailText.length() != 0;
        if(filled) {
            if(emailText.length() <= 100) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("mname", TokenHandler.loggedUsername);
                    jo.put("mpass", "");
                    jo.put("email", emailText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TokenHandler.sendVolley(this, Request.Method.POST, TokenHandler.api_url + "/updateemail", jo, new ServerCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(EditEmailActivity.this, "Email address updated successfully.", Toast.LENGTH_SHORT).show();
                        Intent settings = new Intent(EditEmailActivity.this, SettingsActivity.class);
                        startActivity(settings);
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
}
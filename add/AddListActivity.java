package com.cheddarsecurity.cheddarsafe.add;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;
import com.cheddarsecurity.cheddarsafe.items.ListActivity;
import com.cheddarsecurity.cheddarsafe.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class adds entries to the folders.
 */
public class AddListActivity extends AppCompatActivity {

    /** Variables **/
    EditText username, password, accountId;
    AccountDatabaseHelper accDB;
    Button saveButton;
    String folder;

    /**
     * This is called when the activity is created. Initializes the variables values.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        setTitle("Add Entry");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        username = (EditText)findViewById(R.id.addUsername);
        password = (EditText)findViewById(R.id.addPassword);
        accountId = (EditText)findViewById(R.id.addName);
        accDB = new AccountDatabaseHelper(this);
        saveButton = (Button)findViewById(R.id.editButton);
        Intent receivedIntent = getIntent();
        folder = receivedIntent.getStringExtra("folder");
    }

    /**
     * Check to see if the account already exists and is valid. If not valid
     * or already exists, an error message will be displayed.
     *
     * @param v The button that calls this method.
     */
    public void infoAddChecker(View v) {
        String item1 = username.getText().toString();
        String item2 = password.getText().toString();
        String item3 = accountId.getText().toString();
        String item4 = folder;
        if(!item1.equals("") && !item2.equals("") && !item3.equals("")) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("username", item1);
                jo.put("password", item2);
                jo.put("accountid", item3);
                jo.put("folder", item4);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int type = Request.Method.POST;
            String url = TokenHandler.api_url + "/addaccount";
            TokenHandler.sendVolley(this, type, url, jo, new ServerCallback() {
                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onSuccess(JSONObject result) {
                    if (accDB.addInfoData(item1, item2, item3, item4)) {
                        Toast.makeText(AddListActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                    }
                    Intent list = new Intent(AddListActivity.this, ListActivity.class);
                    list.putExtra("folder", folder);
                    startActivity(list);
                }

                @Override
                public void onFailure(JSONObject result) {

                }
            });
        } else {
            Toast.makeText(this, "Please make sure all blanks are filled.", Toast.LENGTH_SHORT).show();
        }
    }
}
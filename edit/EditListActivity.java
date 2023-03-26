package com.cheddarsecurity.cheddarsafe.edit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.add.AddListActivity;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;
import com.cheddarsecurity.cheddarsafe.items.ListActivity;
import com.cheddarsecurity.cheddarsafe.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class adds entries to the folders.
 */
public class EditListActivity extends AppCompatActivity {

    /** Variables **/
    EditText username, password, accountId;
    AccountDatabaseHelper accDB;
    Button saveButton;
    String selectedName, selectedUser, selectedPass, selectedFolder;

    /**
     * This method is called when this class is created. Sets initial
     * values to the variables.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);
        username = (EditText)findViewById(R.id.editUsername);
        password = (EditText)findViewById(R.id.editPassword);
        accountId = (EditText)findViewById(R.id.editName);
        accDB = new AccountDatabaseHelper(this);
        saveButton = (Button)findViewById(R.id.editButton);

        Intent receivedIntent = getIntent();

        selectedName = receivedIntent.getStringExtra("name");
        setTitle("Edit " + selectedName);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        Cursor userdata = accDB.getUserInfo(selectedName);
        userdata.moveToFirst();
        selectedUser = userdata.getString(0);
        Cursor passdata = accDB.getPassInfo(selectedName);
        passdata.moveToFirst();
        selectedPass = passdata.getString(0);
        selectedFolder = receivedIntent.getStringExtra("folder");
        accountId.setText(selectedName);
        username.setText(selectedUser);
        password.setText(selectedPass);
    }

    /**
     * Checks to see if the username, password, and account id are all valid.
     *
     * @param v The button that calls this method.
     */
    public void infoChecker(View v) {
        String item1 = username.getText().toString();
        String item2 = password.getText().toString();
        String item3 = accountId.getText().toString();
        if(!item1.equals("") && !item2.equals("") && !item3.equals("")) {
            JSONObject jo = new JSONObject();
            JSONObject old = new JSONObject();
            JSONObject notold = new JSONObject();
            try {
                old.put("username", selectedUser);
                old.put("password", selectedPass);
                old.put("accountid", selectedName);
                old.put("folder", selectedFolder);
                jo.accumulate("old_account", old);
                notold.put("username", item1);
                notold.put("password", item2);
                notold.put("accountid", item3);
                notold.put("folder", selectedFolder);
                jo.accumulate("new_account", notold);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int type = Request.Method.POST;
            String url = TokenHandler.api_url + "/updateaccount";
            TokenHandler.sendVolley(this, type, url, jo, new ServerCallback() {
                @RequiresApi(api = Build.VERSION_CODES.R)
                @Override
                public void onSuccess(JSONObject result) {
                    if (accDB.addInfoData(item1, item2, item3, selectedFolder)) {
                        Toast.makeText(EditListActivity.this, "Account updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                    accDB.deleteEntry(selectedUser, selectedPass, selectedName, selectedFolder);
                    Intent list = new Intent(EditListActivity.this, ListActivity.class);
                    list.putExtra("folder", selectedFolder);
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

    /**
     * Deletes the selected entry from the folder.
     *
     * @param v The button that calls this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void deleteEntry(View v) {

        JSONObject old = new JSONObject();
        try {
            old.put("username", selectedUser);
            old.put("password", selectedPass);
            old.put("accountid", selectedName);
            old.put("folder", selectedFolder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int type = Request.Method.POST;
        String url = TokenHandler.api_url + "/delaccount";
        TokenHandler.sendVolley(this, type, url, old, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                accDB.deleteEntry(selectedUser, selectedPass, selectedName, selectedFolder);
                Toast.makeText(EditListActivity.this, "Account successfully deleted!", Toast.LENGTH_SHORT).show();
                Intent list = new Intent(EditListActivity.this, ListActivity.class);
                list.putExtra("folder", selectedFolder);
                startActivity(list);
            }

            @Override
            public void onFailure(JSONObject result) {

            }
        });
    }


}

package com.cheddarsecurity.cheddarsafe.add;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class adds a folder to the list.
 */
public class AddFolderActivity extends AppCompatActivity {

    /** Variables **/
    EditText folder;
    AccountDatabaseHelper accDB;
    Button addButton;

    /**
     * This method is called when this activity is created. Sets the variables to their
     * initial values.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_folder);
        setTitle("Add Folder");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        folder = (EditText)findViewById(R.id.addFolderName);
        accDB = new AccountDatabaseHelper(this);
        addButton = (Button)findViewById(R.id.addFolderButton);
    }

    /**
     * Checks to see if the folder name is valid. If it is not valid or if the folder already
     * exists, then an error message will be sent.
     *
     * @param v THe button that calls this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void infoFolderAddChecker(View v) {
        String item1 = folder.getText().toString();
        JSONObject jo = new JSONObject();
        try {
            jo.put("old_name", item1);
            jo.put("new_name", item1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!item1.equals("")) {
            int type = Request.Method.POST;
            String url = TokenHandler.api_url + "/addfolder";
            TokenHandler.sendVolley(this, type, url, jo, new ServerCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    Toast.makeText(AddFolderActivity.this, "Folder was successfully added!", Toast.LENGTH_SHORT).show();
                    accDB.addFolderData(item1);
                    Intent folder = new Intent(AddFolderActivity.this, FolderActivity.class);
                    startActivity(folder);
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
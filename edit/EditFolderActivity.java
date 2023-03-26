package com.cheddarsecurity.cheddarsafe.edit;

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
import com.cheddarsecurity.cheddarsafe.add.AddFolderActivity;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;
import com.cheddarsecurity.cheddarsafe.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class edits the folders in the app. Can be used to
 * delete folders or rename them.
 */
public class EditFolderActivity extends AppCompatActivity {

    /** Variables **/
    String folder;
    Button deleteFolderBtn;
    EditText folderEditText;
    AccountDatabaseHelper accDB;

    /**
     * This method is called when this class is created. Sets initial values
     * of the variables.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_folder);

        Intent receivedIntent = getIntent();
        folder = receivedIntent.getStringExtra("folder");
        setTitle("Edit " + folder);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));

        deleteFolderBtn = (Button)findViewById(R.id.deleteFolderBtn);
        folderEditText = (EditText)findViewById(R.id.folderEditText);
        accDB = new AccountDatabaseHelper(this);

        folderEditText.setText(folder);
    }

    /**
     * Edits the folder name to the given name unless it is invalid or already existing.
     *
     * @param v The button that calls this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void editFolderName(View v) {
        String item1 = folderEditText.getText().toString();

        if(!item1.equals("")) {
            if (!item1.equals(folder)) {
                int type = Request.Method.POST;
                String url = TokenHandler.api_url + "/updatefolder";
                JSONObject jo = new JSONObject();
                try {
                    jo.put("old_name", folder);
                    jo.put("new_name", item1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                TokenHandler.sendVolley(this, type, url, jo, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        Toast.makeText(EditFolderActivity.this, "Folder was successfully updated!", Toast.LENGTH_SHORT).show();
                        accDB.updateFolder(item1, folder);
                        Intent folder = new Intent(EditFolderActivity.this, FolderActivity.class);
                        startActivity(folder);
                    }

                    @Override
                    public void onFailure(JSONObject result) {

                    }
                });
            }
            else {
                Toast.makeText(this, "Name can't be the same as it was.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please make sure all blanks are filled.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Deletes the folder and all of its contents.
     *
     * @param v The button that calls this method.
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void deleteFolder(View v) {
        int type = Request.Method.POST;
        String url = TokenHandler.api_url + "/delfolder";
        JSONObject jo = new JSONObject();
        try {
            jo.put("old_name", folder);
            jo.put("new_name", folder);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TokenHandler.sendVolley(this, type, url, jo, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Toast.makeText(EditFolderActivity.this, "Folder was successfully deleted!", Toast.LENGTH_SHORT).show();
                accDB.deleteFolder(folder);
                Intent folder = new Intent(EditFolderActivity.this, FolderActivity.class);
                startActivity(folder);
            }

            @Override
            public void onFailure(JSONObject result) {

            }
        });
    }
}
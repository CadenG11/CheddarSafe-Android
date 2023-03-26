package com.cheddarsecurity.cheddarsafe.items;

import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.add.AddFolderActivity;
import com.cheddarsecurity.cheddarsafe.edit.EditFolderActivity;
import com.cheddarsecurity.cheddarsafe.settings.SettingsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles updates and creation of folders for items to be put into.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class FolderActivity extends AppCompatActivity {

    /** Variables **/
    AccountDatabaseHelper myDB;
    TextView emptyText;
    ListView folder;
    ArrayList<String> theFolders;
    Button sortbtn;
    ImageView folderImage;

    private static boolean initializedDB = false;

    /**
     * This method is left blank so when the back button is pressed,
     * nothing happens and the user stays in this activity.
     */
    @Override
    public void onBackPressed() {}

    /**
     * This method is called when this activity is created. Sets initial variables.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_folder);
        folder = (ListView) findViewById(R.id.folder);
        myDB = new AccountDatabaseHelper(this);
        setTitle("Folders");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));

        sortbtn = (Button)findViewById(R.id.sortFolderBtn);
        sortbtn.setText("Sort");
        emptyText = (TextView)findViewById(R.id.emptyFolderText);
        emptyText.setText("");
        folderImage = (ImageView)findViewById(R.id.folderImage);
        if (!initializedDB) {
            pullAPIDatabaseFolders();
        }
        else {
            pullLocalDatabaseFolders();
        }

        folder.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * When a folder is long clicked, then the folder can then be edited.
             *
             * @param parent This is the list of folders.
             * @param view The button that is pressed.
             * @param position The position in the list the folder is.
             * @param id The id of the folder selected.
             * @return Returns false
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String url = TokenHandler.api_url + "/active";
                TokenHandler.sendVolley(FolderActivity.this, Request.Method.POST, url, null, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        String folder = parent.getItemAtPosition(position).toString();
                        Intent deleteFolder = new Intent(FolderActivity.this, EditFolderActivity.class);
                        deleteFolder.putExtra("folder",folder);
                        startActivity(deleteFolder);
                    }

                    @Override
                    public void onFailure(JSONObject result) {

                    }
                });
                return false;
            }
        });
        folder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * When a folder is single clicked, go into the folder and view contents.
             *
             * @param parent The list of the folders.
             * @param view The button that is clicked.
             * @param position The position in the list of the folder clicked.
             * @param id The id of the selected folder.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = TokenHandler.api_url + "/active";
                TokenHandler.sendVolley(FolderActivity.this, Request.Method.POST, url, null, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        String folder = parent.getItemAtPosition(position).toString();
                        Intent editScreenIntent = new Intent(FolderActivity.this, ListActivity.class);
                        editScreenIntent.putExtra("folder",folder);
                        startActivity(editScreenIntent);
                    }

                    @Override
                    public void onFailure(JSONObject result) {

                    }
                });
            }
        });
    }

    /**
     * Sends the user to add a new folder.
     *
     * @param v The button that is pressed.
     */
    public void addFolder(View v) {
        Intent add = new Intent(this, AddFolderActivity.class);
        startActivity(add);
    }

    /**
     * Sends the user to the settings.
     *
     * @param v The button that is pressed.
     */
    public void openSettings(View v) {
        String url = TokenHandler.api_url + "/active";
        TokenHandler.sendVolley(FolderActivity.this, Request.Method.POST, url, null, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                Intent settings = new Intent(FolderActivity.this, SettingsActivity.class);
                startActivity(settings);
            }

            @Override
            public void onFailure(JSONObject result) {

            }
        });
    }

    boolean asc = false;

    /**
     * Sorts the folders based on whether they are ascending
     * or descending already.
     *
     * @param v The button that is pressed.
     */
    public void sortFolders(View v) {
        sortbtn.setText("");
        if(!asc) {
            sortFoldersAsc(v);
            sortbtn.setForeground(getDrawable(android.R.drawable.arrow_up_float));
        } else {
            sortFoldersDesc(v);
            sortbtn.setForeground(getDrawable(android.R.drawable.arrow_down_float));
        }

    }

    /**
     * Sorts the folders into ascending order.
     *
     * @param v The button that is pressed.
     */
    public void sortFoldersAsc(View v) {
        asc = true;
        Cursor data = myDB.getFoldersOrdered(asc);
        theFolders.clear();
        while(data.moveToNext()) {
            theFolders.add(data.getString(0));
            ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theFolders);
            folder.setAdapter(listAdapter);
        }
    }

    /**
     * Sorts the folders into descending order.
     *
     * @param v The button that is pressed.
     */
    public void sortFoldersDesc(View v) {
        asc = false;
        Cursor data = myDB.getFoldersOrdered(asc);
        theFolders.clear();
        while(data.moveToNext()) {
            theFolders.add(data.getString(0));
            ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theFolders);
            folder.setAdapter(listAdapter);
        }
    }

    /**
     * After the initial log in, pulls folders from the local copy of the database instead.
     */
    private void pullLocalDatabaseFolders() {
        theFolders = new ArrayList<>();
        Cursor data = myDB.getInfoFolderContents();

        if(data.getCount() == 0) {
            emptyText.setText("There were no folders found...");
            emptyText.setTextSize(20);
            emptyText.setGravity(Gravity.CENTER);
            folderImage.setVisibility(View.VISIBLE);
        } else {
            emptyText.setText("");
            folderImage.setVisibility(View.INVISIBLE);
            while(data.moveToNext()) {
                theFolders.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(FolderActivity.this, android.R.layout.simple_list_item_1, theFolders);
                folder.setAdapter(listAdapter);
            }
        }
    }

    /**
     * Pulls the folders from the API after initial login.
     */
    private void pullAPIDatabaseFolders() {
        String url = TokenHandler.api_url + "/pull";
        TokenHandler.sendVolley(FolderActivity.this, Request.Method.GET, url, null, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    initializedDB = true;
                    JSONArray arr = result.getJSONArray("folders");
                    for(int i=0; i< arr.length(); i++) {
                        myDB.addFolderData(arr.getString(i));
                    }
                    JSONArray accArray = result.getJSONArray("accounts");
                    for(int j=0; j< accArray.length(); j++) {
                        JSONArray account = accArray.getJSONArray(j);
                        myDB.addInfoData(account.getString(0),
                                account.getString(1),
                                account.getString(2),
                                account.getString(3));
                    }
                    pullLocalDatabaseFolders();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(JSONObject result) {

            }
        });
    }
}

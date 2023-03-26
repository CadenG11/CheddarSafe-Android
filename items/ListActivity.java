package com.cheddarsecurity.cheddarsafe.items;

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
import com.cheddarsecurity.cheddarsafe.add.AddListActivity;
import com.cheddarsecurity.cheddarsafe.edit.EditListActivity;
import com.cheddarsecurity.cheddarsafe.settings.SettingsActivity;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This is the activity that lists all of the items within a folder.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class ListActivity extends AppCompatActivity {

    /** Variables **/
    AccountDatabaseHelper myDB;
    String folder;
    TextView emptyText;
    ListView list;
    ArrayList<String> theList;
    Button sortbtn;

    /**
     * This method makes it so when the back button is pressed, the user
     * is sent back to the folder activity.
     */
    @Override
    public void onBackPressed() {
        Intent folder = new Intent(this, FolderActivity.class);
        startActivity(folder);
    }

    /**
     * This method is called when this activity is first created.
     * Sets the initial values of the variables.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        list = findViewById(R.id.list);
        myDB = new AccountDatabaseHelper(this);
        folder = getIntent().getStringExtra("folder");
        setTitle(folder);
        sortbtn = (Button)findViewById(R.id.sortEntryBtn);
        sortbtn.setText("Sort");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));

        theList = new ArrayList<>();
        Cursor data = myDB.getInfoAccountIdContents(folder);

        emptyText = (TextView)findViewById(R.id.emptyEntryText);
        emptyText.setText("");

        if(data.getCount() == 0) {
            emptyText.setText("There were no entries found...");
            emptyText.setTextSize(20);
            emptyText.setGravity(Gravity.CENTER);
        } else {
            emptyText.setText("");
            while(data.moveToNext()) {
                theList.add(data.getString(0));
                ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
                list.setAdapter(listAdapter);
            }
        }
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * This is the onClick listener for each individual item.
             *
             * @param parent The parent list of the items.
             * @param view The button that is pressed.
             * @param position The position in the list the item is.
             * @param id The id of the selected item.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = TokenHandler.api_url + "/active";
                TokenHandler.sendVolley(ListActivity.this, Request.Method.POST, url, null, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        String accountId = parent.getItemAtPosition(position).toString();
                        Intent viewScreenIntent = new Intent(ListActivity.this, ViewListActivity.class);
                        viewScreenIntent.putExtra("name",accountId);
                        viewScreenIntent.putExtra("folder",folder);
                        startActivity(viewScreenIntent);
                    }

                    @Override
                    public void onFailure(JSONObject result) {

                    }
                });
            }
        });

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            /**
             * Sets the items action for when the user long clicks.
             *
             * @param parent The parent list.
             * @param view The view the list is in.
             * @param position The position in the list.
             * @param id The id of the item.
             * @return Returns true if the action was successful.
             */
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String url = TokenHandler.api_url + "/active";
                TokenHandler.sendVolley(ListActivity.this, Request.Method.POST, url, null, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        String accountId = parent.getItemAtPosition(position).toString();
                        Intent editScreenIntent = new Intent(ListActivity.this, EditListActivity.class);
                        editScreenIntent.putExtra("name",accountId);
                        editScreenIntent.putExtra("folder",folder);
                        startActivity(editScreenIntent);
                    }

                    @Override
                    public void onFailure(JSONObject result) {

                    }
                });
                return true;
            }
        });
    }

    /**
     * Sends the user to the activity to add an item to the list.
     *
     * @param v The button that is pressed.
     */
    public void addEntry(View v) {
        Intent add = new Intent(this, AddListActivity.class);
        add.putExtra("folder",folder);
        startActivity(add);
    }

    boolean asc = false;

    /**
     * Sorts the entries in either descending or ascending order.
     *
     * @param v The button that is pressed.
     */
    public void sortEntries(View v) {
        sortbtn.setText("");
        if(!asc) {
            sortEntriesAsc(v);
            sortbtn.setForeground(getDrawable(android.R.drawable.arrow_up_float));
        } else {
            sortEntriesDesc(v);
            sortbtn.setForeground(getDrawable(android.R.drawable.arrow_down_float));
        }

    }

    /**
     * Sorts the items into ascending order.
     *
     * @param v The button that is pressed.
     */
    public void sortEntriesAsc(View v) {
        asc = true;
        Cursor data = myDB.getInfoAccountIdContentsOrdered(folder, asc);
        theList.clear();
        while(data.moveToNext()) {
            theList.add(data.getString(0));
            ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
            list.setAdapter(listAdapter);
        }
    }

    /**
     * Sorts the items into descending order.
     *
     * @param v The button that is pressed.
     */
    public void sortEntriesDesc(View v) {
        asc = false;
        Cursor data = myDB.getInfoAccountIdContentsOrdered(folder, asc);
        theList.clear();
        while(data.moveToNext()) {
            theList.add(data.getString(0));
            ListAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theList);
            list.setAdapter(listAdapter);
        }
    }
}

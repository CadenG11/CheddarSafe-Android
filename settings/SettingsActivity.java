package com.cheddarsecurity.cheddarsafe.settings;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.ServerCallback;
import com.cheddarsecurity.cheddarsafe.TokenHandler;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;
import com.cheddarsecurity.cheddarsafe.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class provides the functionality for the settings activity.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class SettingsActivity extends AppCompatActivity {

    /** Variables **/
    AccountDatabaseHelper myDB;
    String[] settingsTextArray;

    /**
     * This is the constructor. Sets initial values for the variables and sets
     * initial texts in the activity.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ListView settingsList = (ListView) findViewById(R.id.settings);
        myDB = new AccountDatabaseHelper(this);
        setTitle("Settings");

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        settingsTextArray = myDB.getSettingsArray();
        ArrayAdapter<String> theSettings = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, settingsTextArray);
        settingsList.setAdapter(theSettings);
        settingsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * This sends the user to different sections of the settings depending on
             * which setting the user clicks.
             *
             * @param parent The parent activity that holds all the buttons.
             * @param view The button that is pressed.
             * @param position The position of the button that is clicked.
             * @param id The id of the setting that is pressed.
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedSetting = parent.getItemAtPosition(position).toString();
                switch (selectedSetting) {
                    case "Change account information":
                        Intent editacc = new Intent(SettingsActivity.this, EditAccountActivity.class);
                        editacc.putExtra("prevActivity", "Settings");
                        editacc.putExtra("oldusername", TokenHandler.loggedUsername);
                        startActivity(editacc);
                        break;
                    case "Change email address":
                        String url = TokenHandler.api_url + "/getemail";
                        TokenHandler.sendVolley(SettingsActivity.this, Request.Method.GET, url, null, new ServerCallback() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try {
                                    String email = result.getString("email");
                                    Intent editemail = new Intent(SettingsActivity.this, EditEmailActivity.class);
                                    editemail.putExtra("oldemail", email);
                                    startActivity(editemail);
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(JSONObject result) {

                            }
                        });
                        break;
                    default:
                        Toast.makeText(SettingsActivity.this, "Failed to open.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Makes it so when the user presses the back button in the settings,
     * it will send the user back to the folder activity.
     */
    @Override
    public void onBackPressed() {
        Intent folders = new Intent(this, FolderActivity.class);
        startActivity(folders);
    }
}


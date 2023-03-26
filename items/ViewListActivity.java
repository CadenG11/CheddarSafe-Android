package com.cheddarsecurity.cheddarsafe.items;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.edit.EditListActivity;
import com.cheddarsecurity.cheddarsafe.items.FolderActivity;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.start.ForkActivity;

/**
 * This class provides the screen for the user to view
 * the information for the item they clicked.
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class ViewListActivity extends AppCompatActivity {

    /** Variables **/
    String folderName, selectedName;
    TextView accountId, username, password;
    AccountDatabaseHelper myDB;

    /**
     * This method is called when this activity is created.
     * Sets the initial values of the variables used.
     *
     * @param savedInstanceState The current status of the view.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        myDB = new AccountDatabaseHelper(this);
        Intent receivedIntent = getIntent();
        selectedName = receivedIntent.getStringExtra("name");
        folderName = receivedIntent.getStringExtra("folder");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle(selectedName);

        accountId = (TextView)findViewById(R.id.accountId);
        username = (TextView)findViewById(R.id.username);
        password = (TextView)findViewById(R.id.password);

        Cursor userData = myDB.getUserInfo(selectedName);
        userData.moveToFirst();
        Cursor passData = myDB.getPassInfo(selectedName);
        passData.moveToFirst();

        accountId.setText(selectedName);
        username.setText(userData.getString(0));
        password.setText(passData.getString(0));

    }

    /**
     * When the back button is pressed, the user is sent to the list activity.
     */
    @Override
    public void onBackPressed() {
        goBack(null);
    }

    /**
     * Goes back to the list activity.
     *
     * @param v The button that activates this method.
     */
    public void goBack(View v) {
        Intent list = new Intent(this, ListActivity.class);
        list.putExtra("folder", folderName);
        startActivity(list);
    }

    /**
     * Sends the user to edit the shown entry.
     *
     * @param v The button that calls this method.
     */
    public void toEdit(View v) {
        Intent editScreenIntent = new Intent(this, EditListActivity.class);
        editScreenIntent.putExtra("name",selectedName);
        editScreenIntent.putExtra("folder",folderName);
        startActivity(editScreenIntent);
    }
}



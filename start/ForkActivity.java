package com.cheddarsecurity.cheddarsafe.start;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.login.FingerprintScanActivity;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.setup.SetupActivity;

import java.util.Objects;
/**
 * This class allows the user to login, create an account if one does not exist, or
 * recover an account if login information is forgotten.
 */
public class ForkActivity extends AppCompatActivity {

    /**
     * This is an override method so when the back button is pressed, the user
     * is sent back to the logo activity.
     */
    @Override
    public void onBackPressed() {
        Intent logo = new Intent(this, LogoActivity.class);
        startActivity(logo);
    }

    /**
     * This is an override method that gets the layout and sets it as the current view. It also
     * hides the action bar from being seen in this view.
     *
     * @param savedInstanceState The current status of the view.
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fork);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }


    /**
     * This sends user to screen to recover account.
     *
     * @param v The view that activates this method.
     */
    public void checkForAccount(View v) {
        Intent forgot = new Intent(this, FingerprintScanActivity.class);
        startActivity(forgot);
    }

    /**
     * Sends user to login screen.
     *
     * @param v The view that activates this method.
     */
    public void toLogin(View v) {
        Intent login = new Intent(this, FingerprintScanActivity.class);
        login.putExtra("quick", true);
        startActivity(login);
    }
}

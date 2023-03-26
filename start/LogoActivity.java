package com.cheddarsecurity.cheddarsafe.start;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.cheddarsecurity.cheddarsafe.AccountDatabaseHelper;
import com.cheddarsecurity.cheddarsafe.ClearDBService;
import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.setup.SetupEmailActivity;
import java.util.Objects;

/**
 * This class displays the logo on the opening screen when the app is first opened.
 */
public class LogoActivity extends AppCompatActivity {

    /** Variables **/
    private final AccountDatabaseHelper myDB = new AccountDatabaseHelper(this);

    /**
     * This is an override method that makes the back button do nothing when pressed.
     */
    @Override
    public void onBackPressed() {}

    /**
     * This is an override method that gets the layout and sets it as the current view. It also
     * hides the action bar from being seen in this view.
     *
     * @param savedInstanceState The current status of the view.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        Objects.requireNonNull(getSupportActionBar()).hide();
        startService(new Intent(getBaseContext(), ClearDBService.class));
    }

    /**
     * This method starts the fork activity.
     *
     * @param v The button that calls this method.
     */
    public void checkAccount(View v) {
        Intent fork = new Intent(this, ForkActivity.class);
        startActivity(fork);
    }
}

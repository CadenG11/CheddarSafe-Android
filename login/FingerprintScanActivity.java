package com.cheddarsecurity.cheddarsafe.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Toast;

import com.cheddarsecurity.cheddarsafe.R;
import com.cheddarsecurity.cheddarsafe.start.ForkActivity;

/**
 * This class handles the fingerprint scanning. If a fingerprint does not exist
 * or if it is not possible on older phones, then this will be bypassed.
 */
public class FingerprintScanActivity extends AppCompatActivity {

    /** Variables **/
    private CancellationSignal cancellationSignal;

    /**
     * This method is called when this class is created and sets individual
     * variables to their actual values.
     *
     * @param savedInstanceState The saved state of the activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_scan);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.primary)));
        setTitle("Fingerprint authentication");
        checkBiometricSupport();
        authenticateUser(findViewById(R.id.button6));
    }

    /**
     * This checks if the fingerprint permission is available and if it is, then see
     * if their exists fingerprints on the phone.
     *
     * @return Returns true if the system has fingerprint capabilities. False otherwise.
     */
    private boolean checkBiometricSupport() {

        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        PackageManager packageManager = this.getPackageManager();

        if (!keyguardManager.isKeyguardSecure()) {
            notifyUser("Lock screen security not enabled in Settings");
            Intent login = new Intent(FingerprintScanActivity.this, LoginActivity.class);
            startActivity(login);
            return false;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
            notifyUser("Fingerprint authentication permission not enabled");
            return false;
        }
        packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT);
        return true;
    }

    /**
     * This method is called when the fingerprint is scanned.
     *
     * @return Returns an AuthenticationCallback after the fingerprint is accepted or denied.
     */
    private BiometricPrompt.AuthenticationCallback getAuthenticationCallback() {

        return new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                notifyUser("" + errString);
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                notifyUser("Authentication helped.");
                super.onAuthenticationHelp(helpCode, helpString);
            }

            @Override
            public void onAuthenticationFailed() {
                notifyUser("Authentication failed.");
                super.onAuthenticationFailed();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                notifyUser("Authentication succeeded!");
                if(getIntent().getBooleanExtra("quick", false)) {
                    Intent folder = new Intent(FingerprintScanActivity.this, LoginActivity.class);
                    startActivity(folder);
                } else {
                    Intent recover = new Intent(FingerprintScanActivity.this, ForgotAccActivity.class);
                    startActivity(recover);
                }
                super.onAuthenticationSucceeded(result);
            }
        };
    }

    /**
     * This method is called when the cancel button is pressed on the authentication screen.
     *
     * @return Returns a CancellationSignal when cancel is pressed.
     */
    private CancellationSignal getCancellationSignal() {
        cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(new CancellationSignal.OnCancelListener() {
            @Override
            public void onCancel() {
                //when cancel is pressed
            }
        });
        return cancellationSignal;
    }

    /**
     * This method is called when the button is pressed to authenticate the user.
     * Sets the dialogue box for the scanning and presents it to the user.
     *
     * @param v The button that is pressed to call this method.
     */
    public void authenticateUser(View v) {
        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("Biometric Demo")
                .setSubtitle("Authentication is required to continue")
                .setDescription("This app uses biometric authentication to protect your data.")
                .setNegativeButton("Cancel", this.getMainExecutor(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent fork = new Intent(FingerprintScanActivity.this, ForkActivity.class);
                                startActivity(fork);
                            }
                        })
                .build();
        biometricPrompt.authenticate(getCancellationSignal(), getMainExecutor(), getAuthenticationCallback());
    }

    /**
     * This method is called to print a alert to the phone for the user.
     *
     * @param message is the message the method will put in the alert to the user.
     */
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is called when back button is pressed and will
     * send the user to the fingerprint scanning activity.
     */
    @Override
    public void onBackPressed() {
        Intent folder = new Intent(FingerprintScanActivity.this, ForkActivity.class);
        startActivity(folder);
        }
    }
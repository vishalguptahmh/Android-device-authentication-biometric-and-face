package com.vishalguptahmh.faceauthentication;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //biometric
        findViewById(R.id.touchButton).setOnClickListener((view) ->
            openWithBioMetric()
        );
        //keyguard
        findViewById(R.id.touchButton2).setOnClickListener(v -> {
            openWithKeyGuard();
        });
    }

    private void openWithKeyGuard() {
        Log.d(TAG, "openWithKeyGuard: ");
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (km.isDeviceLocked()) {
                Log.d(TAG, "openPhone: phone is locked");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    km.requestDismissKeyguard(MainActivity.this, new KeyguardManager.KeyguardDismissCallback() {
                        @Override
                        public void onDismissError() {
                            super.onDismissError();
                            Log.d(TAG, "onDismissError: ");
                        }

                        @Override
                        public void onDismissSucceeded() {
                            super.onDismissSucceeded();
                            Log.d(TAG, "onDismissSucceeded: ");
                            onsuccessAuthenticaion();

                        }

                        @Override
                        public void onDismissCancelled() {
                            super.onDismissCancelled();
                            Log.d(TAG, "onDismissCancelled: ");
                        }
                    });
                }
            } else {
                Log.d(TAG, "openPhone: phone is unlocked");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
                    if (km.isKeyguardSecure()) {
                        Intent authIntent = km.createConfirmDeviceCredentialIntent("title", "description");
                        startActivityForResult(authIntent, 110);
                    }
                }
            }
        }
    }

    private void openWithBioMetric() {
        Log.d(TAG, "openWithBioMetric: ");
        askBIOmetric().authenticate(getBiometricPrompt());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " + requestCode + "  " + resultCode);
        if (requestCode == 110 && resultCode == RESULT_OK) {
            onsuccessAuthenticaion();
        }
    }

    public BiometricPrompt askBIOmetric() {
        Log.d(TAG, "askBIOmetric: ");
        return new BiometricPrompt(this, ContextCompat.getMainExecutor(this), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d(TAG, "onAuthenticationError: " + errorCode);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    // user clicked negative/cancel button
                    Log.d(TAG, "onAuthenticationError: negative button clicked");

                }
                if (errorCode == BiometricPrompt.ERROR_CANCELED) {
                    Log.d(TAG, "onAuthenticationError: cancelled");
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //open activity
                        Log.d(TAG, "run: opening another activiyt");
                        onsuccessAuthenticaion();
                    }
                });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Authentication failed! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BiometricPrompt.PromptInfo getBiometricPrompt() {
        return new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric title")
                .setSubtitle("Biometric subtitle")
                .setDescription("Biometric Description")
                .setDeviceCredentialAllowed(true)
                .setConfirmationRequired(false)
                .build();
    }

    private void onsuccessAuthenticaion() {
        startActivity(new Intent(MainActivity.this, MainActivity2.class));
    }
}
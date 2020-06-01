package com.example.blackcoffertask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private static final String TAG = "log_tag";
    EditText et_phone;
    Button btn_otp;
    FirebaseAuth mAuth;
    String codeSent = "";
    TextView textView2;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        dialog = new ProgressDialog(OtpActivity.this);

        mAuth = FirebaseAuth.getInstance();

        et_phone = findViewById(R.id.et_phone);

        textView2 = findViewById(R.id.textView2);

        btn_otp = findViewById(R.id.btn_otp);
        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(codeSent.isEmpty()) {
                    //for phone number
                    String phoneNumber = "+91"+et_phone.getText().toString();
                    if(!phoneNumber.isEmpty()) {
                        if ((phoneNumber.length() == 13)) {
                            getCode(phoneNumber);
                            textView2.setText("Please enter the verification code we sent you.");
                            btn_otp.setText("Verify OTP");
                            et_phone.setText("");
                            et_phone.setHint("Enter OTP");
                            dialog.setMessage("Please wait...");
                            dialog.setCancelable(false);
                            dialog.show();
                        }
                        else {
                            et_phone.setError("Enter Valid Phone Number");
                            et_phone.requestFocus();
                        }
                    }
                    else {
                        et_phone.setError("Phone Number Required");
                        et_phone.requestFocus();
                    }
                }
                else {
                    // for otp
                    if(!(et_phone.getText().toString().isEmpty()))
                    {
                        dialog.setMessage("Please wait...");
                        dialog.setCancelable(false);
                        dialog.show();
                        verifyLoginCode();
                    }
                    else
                    {
                        et_phone.setError("Please enter OTP");
                        Toast.makeText(getApplicationContext(), "Please enter OTP", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void verifyLoginCode() {
        if(!codeSent.isEmpty())
        {
            String code = et_phone.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                            // Write a message to the database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("message");
                            myRef.setValue("Hello, World!");
                            startActivity(new Intent(OtpActivity.this, TagsActivity.class));
                            finishAffinity();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Incorrect OTP", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void getCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {

        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            dialog.dismiss();
        }
    };
}

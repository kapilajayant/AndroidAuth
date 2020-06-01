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
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private EditText et_email, et_password;
    private Button btn_logIn;
    private RadioButton radioButton;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progress;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private ProgressDialog dialog;

    private ImageView imageView4, imageView5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        dialog = new ProgressDialog(LoginActivity.this);

        et_email = findViewById(R.id.editText);
        et_password = findViewById(R.id.editText2);
        btn_logIn = findViewById(R.id.btn_logIn);
        radioButton = findViewById(R.id.radioButton);

        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleAuthSignUp();
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, FacebookLoginActivity.class));
                finish();
            }
        });

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPoints();
            }
        });
    }
    private void checkPoints() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if(!(email.isEmpty()))
        {
            if(!(password.isEmpty()))
            {
                if (password.length() > 6)
                {
                    if(radioButton.isChecked())
                    {
                        emailSignIn(email, password);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Please agree to privacy policy to continue", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    et_password.setError("At least 6 characters required");
                }
            }
            else {
                et_password.setError("Password is required");
            }
        }
        else
        {
            et_email.setError("Email is required");
        }
    }

    private void emailSignIn(String email, String password) {
        progress = new ProgressDialog(LoginActivity.this);
        progress.setTitle("Logging In...");
        progress.setMessage("Please Wait");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful())
                {
                    progress.dismiss();
                    startActivity(new Intent(LoginActivity.this, OtpActivity.class));
                    finish();
                }

                else {
                    progress.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(EmailActivity.this, "Some Error Occurred, Try Again", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    private void googleAuthSignUp() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signIn();
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                dialog.setMessage("Please wait...");
                dialog.setCancelable(false);
                dialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("log_google", "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("log_google", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("log_google", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, OtpActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                            Log.w("log_google", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void signUpIntentFromLogIn(View view) {
        startActivity(new Intent(LoginActivity.this, EmailActivity.class));
        finish();
    }
}

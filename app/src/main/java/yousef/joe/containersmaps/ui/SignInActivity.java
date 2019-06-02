package yousef.joe.containersmaps.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import yousef.joe.containersmaps.InternetConnectivity;
import yousef.joe.containersmaps.R;
import yousef.joe.containersmaps.ui.MapsActivity;

public class SignInActivity extends AppCompatActivity {
    EditText emailEditText;
    EditText passwordEditText;
    Button signInButton;
    TextView failedTextView;
    ProgressBar progressBar;
    TextView noInternetTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Get views
        emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        signInButton = findViewById(R.id.btn_signIn);
        progressBar = findViewById(R.id.progress_bar);
        failedTextView = findViewById(R.id.tv_incorrect_credentials);
        noInternetTextView = findViewById(R.id.tv_check_connection);
        noInternetTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        if(!InternetConnectivity.isOnline(this)){
            noInternetTextView.setVisibility(View.VISIBLE);
            return;
        }

        // Check if the user has already logged in before
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null){
            // User is already logged in
            openMapsActivity();
        }


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get email and phone numbers from input fields
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                signIn(email, password);
            }
        });


    }

    private void signIn(String email, String password) {
        // Sign in using Firebase
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // if logged in open Maps Screen
                            progressBar.setVisibility(View.GONE);
                            openMapsActivity();
                        } else {
                            // If signing in has failed, show user that their Email or Password is wrong
                            progressBar.setVisibility(View.GONE);
                            failedTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void openMapsActivity() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}

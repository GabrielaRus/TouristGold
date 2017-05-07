package com.example.gabriella.touristgold;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SigupActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private TextInputLayout emailWrapper, passwordWrapper;
    private Button singUpBtn;
    private ProgressBar progressBar;

    private FirebaseAuth auth;

    //Constants
    private static final String PASSWORD_PATTERN = "^((?=.*\\d)(?=.*[a-zA-Z])(?=.*[@#$%])(?=\\S+$).{6,20})$";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigup);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        inputEmail = (EditText) findViewById(R.id.email_input);
        inputPassword = (EditText) findViewById(R.id.password_input);
        emailWrapper = (TextInputLayout) findViewById(R.id.email_wrapper);
        passwordWrapper = (TextInputLayout) findViewById(R.id.password_wrapper);
        singUpBtn = (Button) findViewById(R.id.register_button);
        singUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                if ((validateEmail() && validatePassword()) || (validatePassword() && validateEmail())) {
                    progressBar.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(email,password)
                            .addOnCompleteListener(SigupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Toast.makeText(SigupActivity.this,"createUserWithEmail:onCompleate:"+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    if(!task.isSuccessful()){
                                        Toast.makeText(SigupActivity.this,"Authentication failed. "+task.getException(),Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(SigupActivity.this,"Authentication succeed",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
        //region EditorActionListener
        inputEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT){
                    validateEmail();
                }
                return false;
            }
        });
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    singUpBtn.performClick();
                    return true;
                }
                return false;
            }
        });
        //endregion
        //region TextChangeListener
        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                emailWrapper.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordWrapper.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //endregion
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString();
        if (email.isEmpty()) {
            emailWrapper.setError(getString(R.string.error_empty_email));
            requestFocus(inputEmail);
            return false;
        } else if (!isValidEmail(email)) {
            emailWrapper.setError(getString(R.string.error_invalid_email));
            requestFocus(inputEmail);
            return false;
        } else {
            emailWrapper.setErrorEnabled(false);
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        String password = inputPassword.getText().toString();
        if (password.isEmpty()) {
            passwordWrapper.setError(getString(R.string.error_empty_password));
            requestFocus(inputPassword);
            return false;
        } else if (!isValidatePassword(password)) {
            passwordWrapper.setError(getString(R.string.error_invalid_password));
            requestFocus(inputPassword);
            return false;
        } else {
            passwordWrapper.setErrorEnabled(false);
        }
        return true;
    }

    /**
     * Method for password validation
     * A valid must contains 6-20 characters: at least one special character(@,#,$,%), at least one digit and any alphabets
     * from A-Z,a-z and not space
     *
     * @param password - the password to be validate
     * @return true for valid password and false otherwise
     */
    public boolean isValidatePassword(String password) {
        Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = passwordPattern.matcher(password);
        return matcher.matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

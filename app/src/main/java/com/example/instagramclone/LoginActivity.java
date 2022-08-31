package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener{

    static final String TAG = "LoginActivity";
    //Lu, abc123

    boolean Login = true;
    EditText username;
    EditText password;
    Button loginButton;
    TextView loginSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initial all the view
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        loginSwitch = findViewById(R.id.loginSwitch);

        if(ParseUser.getCurrentUser() != null){
            goToMainActivity();
        }

        password.setOnKeyListener(this);

    }



    @Override
    public void onClick(View view) {
        if(view == loginSwitch){
            LoginSignUpSwitch();
        }
        else if(view == loginButton){
            LoginSignUp(username.getText().toString(), password.getText().toString());
        }
        else{
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //Switch between login and sign up
    private void LoginSignUpSwitch(){
        //Set loginSwitch Listener
        if(Login){
            Login = false;
            loginButton.setText("Sign Up");
            loginSwitch.setText("Or Login");
        }
        else{
            Login = true;
            loginButton.setText("Login");
            loginSwitch.setText("Or Sign Up");
        }
    }

    //Login or Signup user
    private void LoginSignUp(String username, String password){
        Log.i(TAG, username + " " + password);
        //Login
        if(Login){
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(e != null){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    else{
                        Log.i(TAG, "Successfully Login");
                        goToMainActivity();
                    }
                }
            });
        }
        //Sign Up
        else{
            ParseUser user = new ParseUser();
            user.setUsername(username);
            user.setPassword(password);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Log.i(TAG, "Successfully Sign Up");
                        goToMainActivity();
                    }
                }
            });
        }
    }

    //JumpTo MainActivity
    private void goToMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    //OnKey listener
    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        if(i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
            LoginSignUp(username.getText().toString(), password.getText().toString());
        }
        return false;
    }
}
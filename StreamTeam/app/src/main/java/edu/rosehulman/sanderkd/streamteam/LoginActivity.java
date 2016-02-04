package edu.rosehulman.sanderkd.streamteam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginActivity extends AppCompatActivity {

    private EditText mPassword;
    private EditText mUsername;
    private View mProgressSpinner;
    private View mLoginForm;
    Button mLoginButton;
    private Button mCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mProgressSpinner = findViewById(R.id.login_progress);
        mProgressSpinner = findViewById(R.id.login_form);
        mLoginButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        mCreateUser = (Button) findViewById(R.id.create_user);
        mCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateUser.class);
                startActivity(intent);
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void login() {
        String userid = mUsername.getText().toString();
        String pw = mPassword.getText().toString();
        String query = "Exec Login '" +userid + "','" + pw + "'";
        new LoginQuery().execute(query);

    }
    private class LoginQuery extends AsyncTask<String, ResultSet, ResultSet> {
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            try{

                r.next();
                if(r.getString("Status").equals("0")) {
                    Log.e("error", "Invalid Credintials");
                    Log.d("db", r.getWarnings().getMessage());
                    Toast.makeText(LoginActivity.this, r.getWarnings().getMessage(), Toast.LENGTH_SHORT).show();
                }
                else {
                    String username = r.getString("Username");
                    Intent intent = new Intent();
                    intent.putExtra(MainActivity.EXTRA_USERNAME, username);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }


            }
            catch (SQLException e){
                e.printStackTrace();
            }

        }

        @Override
        protected ResultSet doInBackground(String...params){
            String mQuery = params[0];
            ResultSet res = null;
            try {
                Connection con = MainActivity.con.CONN();
                if (con == null) {
                    Log.e("error", "no connection");
                } else {
                    Log.d("LoginActivity", "Performing query");
                    CallableStatement stmt = con.prepareCall(mQuery);
                    Log.d("LoginActivity", "Created Statment");
//                    boolean test = stmt.execute(mQuery);
                    stmt.execute();
                    res = stmt.getResultSet();
                }
            } catch (Exception ex) {
                Log.d("db", ex.toString());
            }
            return res;
        }
    }

}

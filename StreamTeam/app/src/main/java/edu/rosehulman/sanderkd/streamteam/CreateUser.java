package edu.rosehulman.sanderkd.streamteam;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateUser extends AppCompatActivity {
    EditText mUsername;
    EditText mPassword;
    Button mCreateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        mUsername = (EditText) findViewById(R.id.create_user_username);
        mPassword = (EditText) findViewById(R.id.create_user_password);
        mCreateUser = (Button) findViewById(R.id.create_user_button);

        mCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    private void createUser() {
        String userid = mUsername.getText().toString();
        String pw = mPassword.getText().toString();
        String query = "insert into Users Values ('" + userid + "','" + pw + "')";
        new CreateUserQuery().execute(query, userid);
    }

    private class CreateUserQuery extends AsyncTask<String, ResultSet, ResultSet> {
        String username;
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
                Intent intent = new Intent();
                intent.putExtra(MainActivity.EXTRA_USERNAME, username);
                setResult(Activity.RESULT_OK, intent);
                finish();

        }

        @Override
        protected ResultSet doInBackground(String...params){
            String mQuery = params[0];
            username = params[1];
            ResultSet res = null;
            try {
                Connection con = MainActivity.con.CONN();
                if (con == null) {
                } else {
                    Statement stmt = con.createStatement();
                    res = stmt.executeQuery(mQuery);
                }
            } catch (Exception ex) {
                Log.d("db", ex.toString());
            }
            return res;
        }
    }


}

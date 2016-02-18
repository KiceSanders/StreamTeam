package edu.rosehulman.sanderkd.streamteam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.rosehulman.sanderkd.streamteam.Adapters.MessageAdapter;
import edu.rosehulman.sanderkd.streamteam.Fragments.MessageFragment;

public class MessageActivity extends AppCompatActivity {

    public String User2;
    private EditText mEditMessage;
    private Button mSendMessage;
    private RecyclerView mRecyclerView;
    private MessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Bundle b = getIntent().getExtras();
        User2 = b.getString("friend");
        Log.d("messageActivity", User2);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.message_toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle("Message with " + User2);

        mEditMessage = (EditText) findViewById(R.id.send_message_text);
        mSendMessage = (Button) findViewById(R.id.send_message_button);
        mSendMessage.setEnabled(false);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(lm);


        mRecyclerView.setAdapter(new MessageAdapter(User2, lm));

        mEditMessage.addTextChangedListener(tw);

        mSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "Exec new_message '" + MainActivity.USER + "', '" + User2 + "', '" + mEditMessage.getText().toString()+"'";
                new MessageQuery().execute(query);
                mEditMessage.setText("");

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            Log.d("db", "up");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(s.equals("") || s.length() > 140){
                mSendMessage.setEnabled(false);
            }
            else{
                mSendMessage.setEnabled(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private class MessageQuery extends AsyncTask<String, ResultSet, ResultSet> {
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            Log.d("messageAct", "executed");

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
                    Log.d("FriendAdapter", "Performing query");
                    CallableStatement stmt = con.prepareCall(mQuery);
                    Log.d("FriendAdapter", "Created Statment");
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

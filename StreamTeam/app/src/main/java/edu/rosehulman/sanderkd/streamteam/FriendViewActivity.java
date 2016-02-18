package edu.rosehulman.sanderkd.streamteam;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendViewActivity extends AppCompatActivity {
    public TextView mUsername;
    public TextView mFriends;
    public ImageView mPicture;
    public TextView mType;
    public TextView mName;
    public User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle b = getIntent().getExtras();
        String User2 = b.getString("friend");
        Log.d("messageActivity", User2);

        String query = "Exec get_profile_info " + User2;
        new GetUserQuery().execute(query);

        mUsername = (TextView) findViewById(R.id.friend_username);
        mFriends = (TextView) findViewById(R.id.friend_friends);
        mPicture = (ImageView) findViewById(R.id.friend_image);
        mType = (TextView) findViewById(R.id.friend_type);
        mName = (TextView) findViewById(R.id.friend_name);



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

    private class GetUserQuery extends AsyncTask<String, ResultSet, ResultSet> {

        int i;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(ResultSet r) {
            try {
                while (r.next()) {
                    if (i == 0) {
                        mUser = new User(r.getString("Username"), r.getInt("Num of Friends"));
                        Log.d("db", r.getString("Username") + " " + r.getInt("Num of Friends"));
                        mUsername.setText(r.getString("Username"));
                        mFriends.setText("This user has " + r.getInt("Num of Friends") + " friends");
                        i++;
                    }
                    Log.d("db", r.getString("Type") + " " + r.getString("Name") + " " + r.getString("Picture") + " " + r.getBoolean("On\\Off"));
                    Account acc = new Account(r.getString("Type"), r.getString("Name"), r.getString("Picture"), r.getBoolean("On\\Off"));
                    mUser.addAccount(acc);
                    if (acc.isOn()) {
                        Log.d("db", "getImageTask.execute " + acc.getmPicture());
                        mType.setText(r.getString("Type"));
                        mName.setText(r.getString("Name"));
                        new GetImageTask(FriendViewActivity.this).execute(acc.getmPicture());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected ResultSet doInBackground(String... params) {
            String mQuery = params[0];
            ResultSet res = null;
            i = 0;
            try {
                Connection con = MainActivity.con.CONN();
                if (con == null) {
                    Log.e("error", "no connection");
                } else {
                    CallableStatement stmt = con.prepareCall(mQuery);
//                    boolean test = stmt.execute(mQuery);
                    stmt.execute();
                    res = stmt.getResultSet();
                }
            } catch (Exception ex) {
                Log.d("db", ex.toString());
            }
            return res;
        }

        class GetImageTask extends AsyncTask<String, Void, Bitmap> {
            FriendViewActivity act;

            public GetImageTask(FriendViewActivity activity) {
                act = activity;
            }

            @Override
            protected Bitmap doInBackground(String... urlStrings) {
                String urlString = urlStrings[0];
                Bitmap bitmap = null;
                try {
                    InputStream in = new java.net.URL(urlString).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                } catch (IOException e) {
                    Log.d("db", "ERROR: " + e.toString());
                }
                return bitmap;
            }

            protected void onPostExecute(Bitmap image) {
                act.onImageLoaded(image);
            }
        }

    }
    private void onImageLoaded(Bitmap image) {
        mPicture.setImageBitmap(image);
    }
}

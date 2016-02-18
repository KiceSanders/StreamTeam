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
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;

public class UserViewActivity extends AppCompatActivity {

    private TextView mUsername;
    private TextView mFriends;
    private ImageView mFirstImage;
    private CheckBox mFirstCheck;
    private TextView mFirstName;
    private ImageView mSecondImage;
    private CheckBox mSecondCheck;
    private TextView mSecondName;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mUser = MainActivity.mUser;

        mUsername = (TextView) findViewById(R.id.user_view_username_view);
        mFriends = (TextView) findViewById(R.id.user_view_num_friends);
        mFirstImage = (ImageView) findViewById(R.id.user_picture_1);
        mFirstCheck = (CheckBox) findViewById(R.id.user_check_1);
        mFirstName = (TextView) findViewById(R.id.user_name_1);
        mSecondImage = (ImageView) findViewById(R.id.user_picture_2);
        mSecondCheck = (CheckBox) findViewById(R.id.user_check_2);
        mSecondName = (TextView) findViewById(R.id.user_name_2);

        mUsername.setText(mUser.getUsername());
        mFriends.setText(mUser.getFriends() + " Friends");
        new GetImageTask(this, 1).execute(mUser.getAccounts().get(0).getmPicture());
        mFirstName.setText(mUser.getAccounts().get(0).getmName());
        mFirstCheck.setText(mUser.getAccounts().get(0).getmType());
        mFirstCheck.setChecked(mUser.getAccounts().get(0).isOn());
        mFirstCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSecondCheck.setChecked(!isChecked);

            }
        });
        Log.d("db", mUser.getAccounts().toString());

        if(mUser.getAccounts().size() == 2){
            mUsername.setText(mUser.getUsername());
            mFriends.setText(mUser.getFriends()+ " Friends");
            new GetImageTask(this, 2).execute(mUser.getAccounts().get(1).getmPicture());
            mSecondName.setText(mUser.getAccounts().get(1).getmName());
            mSecondCheck.setText(mUser.getAccounts().get(1).getmType());
            mSecondCheck.setChecked(mUser.getAccounts().get(1).isOn());
            mSecondCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mFirstCheck.setChecked(!isChecked);
                }
            });
        }
        else{
            mSecondCheck.setVisibility(View.INVISIBLE);
        }


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

    private void onImageLoaded(Bitmap image, int num) {
        if(num ==1){
            mFirstImage.setImageBitmap(image);
        }
        else{
            mSecondImage.setImageBitmap(image);
        }

    }

    class GetImageTask extends AsyncTask<String, Void, Bitmap> {
        UserViewActivity act;
        int currNum;

        public GetImageTask(UserViewActivity activity, int num) {
            act = activity;
            currNum = num;
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
            Log.d("db", "post execute");
            act.onImageLoaded(image, currNum);
        }
    }
}

package edu.rosehulman.sanderkd.streamteam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.rosehulman.sanderkd.streamteam.Fragments.AddFriendFragment;
import edu.rosehulman.sanderkd.streamteam.Fragments.FragmentFacebookLoginButton;
import edu.rosehulman.sanderkd.streamteam.Fragments.FriendListFragment;
import edu.rosehulman.sanderkd.streamteam.Fragments.FriendTopFragment;
import edu.rosehulman.sanderkd.streamteam.Fragments.IgnoreFragment;
import edu.rosehulman.sanderkd.streamteam.Fragments.MessageFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FriendTopFragment.Callback,
        FriendListFragment.Callback,
        MessageFragment.Callback {

    public static ConnectionClass con;
    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static final int REQUEST_LOGIN = 1;
    public static String USER;
    public static User mUser;
    public ImageView mProfilePic;
    public Bitmap profileImage;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        con = new ConnectionClass();

        if (savedInstanceState == null && MainActivity.USER == null) {
            logout();
        } else{
            String query = "Exec get_profile_info " + USER;
            new GetUserQuery().execute(query);
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK) {
            USER = data.getStringExtra(EXTRA_USERNAME);
            String query = "Exec get_profile_info " + USER;
            new GetUserQuery().execute(query);
        }
    }

    private void onImageLoaded(Bitmap image) {
        Log.d("db", "allegedly adding to db");
        mProfilePic = (ImageView) findViewById(R.id.nav_image_pic);
        mProfilePic.setImageBitmap(image);
        profileImage = image;
        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserViewActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment switchTo = null;

        if (id == R.id.nav_social_media) {
            switchTo = new FragmentFacebookLoginButton();
        } else if (id == R.id.nav_friends) {
            friendFragment(getString(R.string.frag_int_friend));
        } else if (id == R.id.nav_messages) {
            switchTo = new MessageFragment();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        if (switchTo != null) {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, switchTo);
            if (mFragmentManager.findFragmentById(R.id.fragment_container_lower) != null) {
                ft.remove(mFragmentManager.findFragmentById(R.id.fragment_container_lower));
            }
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, REQUEST_LOGIN);
    }

    //friend : true if on friends tab, false if on friend request tab
    private void friendFragment(String frag) {
        Log.d("db", frag);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        //doesn't replace if already on the friends tab
        if (!(mFragmentManager.findFragmentById(R.id.fragment_container) instanceof FriendTopFragment)) {
            ft.replace(R.id.fragment_container, new FriendTopFragment());
        }
        if (frag.equals(getString(R.string.frag_int_friend)) && !(mFragmentManager.findFragmentById(R.id.fragment_container_lower) instanceof FriendListFragment)) {
            ft.replace(R.id.fragment_container_lower, new FriendListFragment());
        } else if (frag.equals(getString(R.string.frag_int_friend_req)) && !(mFragmentManager.findFragmentById(R.id.fragment_container_lower) instanceof AddFriendFragment)) {
            ft.replace(R.id.fragment_container_lower, new AddFriendFragment());
        } else if (frag.equals(getString(R.string.frag_int_ignore)) && !(mFragmentManager.findFragmentById(R.id.fragment_container_lower) instanceof IgnoreFragment)) {
            ft.replace(R.id.fragment_container_lower, new IgnoreFragment());
        }
        ft.commit();

    }


    // added by derrowap:

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onFragmentInteraction(String frag) {
        friendFragment(frag);
    }

    @Override
    public void onFriendSelect(String user) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("friend", user);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFriendView(String user) {
        Intent intent = new Intent(this, FriendViewActivity.class);
        intent.putExtra("friend", user);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFriendChoose(String user) {
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra("friend", user);
        startActivity(intent);
        finish();
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
                        i++;
                    }
                    Log.d("db", r.getString("Type") + " " + r.getString("Name") + " " + r.getString("Picture") + " " + r.getBoolean("On\\Off"));
                    Account acc = new Account(r.getString("Type"), r.getString("Name"), r.getString("Picture"), r.getBoolean("On\\Off"));
                    mUser.addAccount(acc);
                    if (acc.isOn()) {
                        Log.d("db", "getImageTask.execute " + acc.getmPicture());
                        new GetImageTask(MainActivity.this).execute(acc.getmPicture());
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
            MainActivity act;

            public GetImageTask(MainActivity activity) {
                act = activity;
            }

            @Override
            protected Bitmap doInBackground(String... urlStrings) {
                String urlString = urlStrings[0];
                Bitmap bitmap = null;
                try {
                    InputStream in = new java.net.URL(urlString).openStream();
                    bitmap = BitmapFactory.decodeStream(in);
                    Log.d("db", "got bitmap");
                } catch (IOException e) {
                    Log.d("db", "ERROR: " + e.toString());
                }
                return bitmap;
            }

            protected void onPostExecute(Bitmap image) {
                Log.d("db", "post execute");
                act.onImageLoaded(image);
            }
        }
    }
}


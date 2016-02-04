package edu.rosehulman.sanderkd.streamteam;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.TextView;

import edu.rosehulman.sanderkd.streamteam.Fragments.AddFriendFragment;
import edu.rosehulman.sanderkd.streamteam.Fragments.FragmentFacebookLoginButton;
import edu.rosehulman.sanderkd.streamteam.Fragments.FriendListFragment;
import edu.rosehulman.sanderkd.streamteam.Fragments.FriendTopFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FriendTopFragment.Callback{

    public static ConnectionClass con;
    public static final String EXTRA_USERNAME = "EXTRA_USERNAME";
    public static final int REQUEST_LOGIN = 1;
    public TextView mText;
    public static String USER;

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragmentManager = getSupportFragmentManager();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        con = new ConnectionClass();

        if (savedInstanceState == null) {
            logout();
        }

        Log.d("db", "message to be displayed");
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
        if(requestCode == REQUEST_LOGIN && resultCode == Activity.RESULT_OK){
            USER = data.getStringExtra(EXTRA_USERNAME);
        }
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
            friendFragment(true);

        } else if (id == R.id.nav_logout) {
            logout();
        }

        if(switchTo!=null){
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.replace(R.id.fragment_container, switchTo);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout(){
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivityForResult(loginIntent, REQUEST_LOGIN);
    }

    //friend : true if on friends tab, false if on friend request tab
    private void friendFragment(Boolean friend) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        //doesn't replace if already on the friends tab
        if(!(mFragmentManager.findFragmentById(R.id.fragment_container) instanceof FriendTopFragment)) {
            ft.replace(R.id.fragment_container, new FriendTopFragment());
        }
        if(friend && !(mFragmentManager.findFragmentById(R.id.fragment_container_lower) instanceof FriendListFragment)){
            ft.replace(R.id.fragment_container_lower, new FriendListFragment());
        }
        else if(!friend && !(mFragmentManager.findFragmentById(R.id.fragment_container_lower) instanceof AddFriendFragment)){
            ft.replace(R.id.fragment_container_lower, new AddFriendFragment());
        }
        int nEntries = getSupportFragmentManager().getBackStackEntryCount();
        for(int i = 0 ; i < nEntries ; i ++){
            getSupportFragmentManager().popBackStackImmediate();
        }
        ft.commit();
    }



    // added by derrowap:

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onFragmentInteraction(Boolean friend) {
        friendFragment(friend);
    }
}

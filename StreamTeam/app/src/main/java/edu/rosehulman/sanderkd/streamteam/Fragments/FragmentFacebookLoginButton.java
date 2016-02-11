package edu.rosehulman.sanderkd.streamteam.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * Created by derrowap on 2/3/2016.
 */
public class FragmentFacebookLoginButton extends Fragment {

    private TextView mTextDetails;
    private CallbackManager mCallbackManager;
    private LoginButton mFacebookLogin;

    private AccessTokenTracker mTokenTracker;
    private ProfileTracker mProfileTracker;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

        mCallbackManager=CallbackManager.Factory.create();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook_login, container, false);

        setupLoginButton(view);
        setupTokenTracker();
        setupProfileTracker();

        mTokenTracker.startTracking();
        mProfileTracker.startTracking();


        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupTextDetails(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        mTextDetails.setText(constructWelcomeMessage(profile));
    }

    @Override
    public void onStop() {
        super.onStop();
        mTokenTracker.stopTracking();
        mProfileTracker.stopTracking();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("db", "got activity result");
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void setupTextDetails(View view) {
        mTextDetails = (TextView) view.findViewById(R.id.fb_greet);
        Log.d("FacbookLogin", mTextDetails.toString());
    }

    private void setupTokenTracker() {
        mTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                Log.d("db", "currentAccessToken: " + currentAccessToken);
            }
        };
    }

    private void setupProfileTracker() {
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.d("db", "currentProfile: " + currentProfile);
                mTextDetails.setText(constructWelcomeMessage(currentProfile));
            }
        };
    }

    private void setupLoginButton(final View view) {
        mFacebookLogin = (LoginButton) view.findViewById(R.id.login_button);
        mFacebookLogin.setFragment(this);
        mFacebookLogin.setReadPermissions("user_friends", "user_posts");
        mFacebookLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("db", "facebook login success");
                AccessToken accessToken = loginResult.getAccessToken();
                Log.d("access", accessToken.toString());
                Profile profile = Profile.getCurrentProfile();
                mTextDetails.setText(constructWelcomeMessage(profile));
            }

            @Override
            public void onCancel() {

                Log.d("db", "facebook login cancelled");
            }

            @Override
            public void onError(FacebookException error) {

                Log.d("db", "facebook login error: " + error);
            }
        });
    }



    private String constructWelcomeMessage(Profile profile) {
        StringBuffer stringBuffer = new StringBuffer();
        if(profile != null) {
            stringBuffer.append("Welcome " + profile.getName());
            Log.d("FacebookLogin", stringBuffer.toString());
        }
        return stringBuffer.toString();
    }
}

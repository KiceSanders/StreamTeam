package edu.rosehulman.sanderkd.streamteam.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.rosehulman.sanderkd.streamteam.Adapters.FacebookAdapter;
import edu.rosehulman.sanderkd.streamteam.FacebookPost;
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
    private RecyclerView mListView;
    private FacebookAdapter mAdapter;


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
        mListView = (RecyclerView) view.findViewById(R.id.feed_list_view);

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
        if(profile != null)
            setupPosts(profile);
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
                setupPosts(profile);
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

    private void setupPosts(Profile profile) {
        Log.d("db - fb", "setupPosts entered");
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/feed",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                        Log.d("db - fb", response.toString());
                        JSONObject j = response.getJSONObject();
                        ArrayList<FacebookPost> fb_posts = new ArrayList<FacebookPost>();
                        JSONArray a = null;
                        try {
                            a = j.getJSONArray("data");
                        } catch (JSONException e) {
                            Log.d("db - fb", e.getMessage());
                        }
                        for (int i = 0; i < a.length(); i++) {
                            try {
                                Log.d("feed", a.getJSONObject(i).getString("message"));
                                Log.d("feed", a.getJSONObject(i).getString("story"));
                                Log.d("feed", a.getJSONObject(i).getString("created_time"));
                                FacebookPost p = new FacebookPost(a.getJSONObject(i).getString("message"), a.getJSONObject(i).getString("story"), a.getJSONObject(i).getString("created_time"));
                                fb_posts.add(p);
                            } catch (JSONException e) {
                                Log.d("db - fb", e.getMessage());
                            }
                        }

                        // TODO: use fb_posts to add to android view
//                        mAdapter = new ArrayAdapter<FacebookPost>(getContext(), android.R.layout.simple_expandable_list_item_1, list);
//                        mListView.setAdapter(mAdapter);
                        mAdapter = new FacebookAdapter(fb_posts);
                        mListView.setLayoutManager(new LinearLayoutManager(getContext()));
                        mListView.setAdapter(mAdapter);
                        Log.d("db", fb_posts.toString());
                    }
                }
        ).executeAsync();

//        new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/{user-id}",
//                null,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                        /* handle the result */
//                        Log.d("db - fb", response.toString());
//                        JSONObject j = response.getJSONObject();
//                        Iterator<String> keys = j.keys();
//
//                        while(keys.hasNext()) {
//                            String key = (String) keys.next();
//                            try {
//                                if (j.get(key) instanceof JSONObject) {
////                                    mTextDetails.append(((JSONObject) j.get(key)).);
//                                }
//                            } catch(JSONException e) {
//                                Log.d("db - fb", e.toString());
//                            }
//                        }
//                    }
//                }
//        ).executeAsync();
    }

}

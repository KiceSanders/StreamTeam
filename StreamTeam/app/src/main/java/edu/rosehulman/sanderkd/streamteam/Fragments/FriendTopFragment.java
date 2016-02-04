package edu.rosehulman.sanderkd.streamteam.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.rosehulman.sanderkd.streamteam.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FriendTopFragment.Callback} interface
 * to handle interaction events.
 */
public class FriendTopFragment extends Fragment {

    private Callback mListener;
    private Button mFriendsButton;
    private Button mFriendRequestButton;

    public FriendTopFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_top, container, false);
        mFriendsButton = (Button) view.findViewById(R.id.friend_view_button);
        mFriendsButton.setBackgroundResource(R.color.friend_button_background);
        mFriendRequestButton = (Button) view.findViewById(R.id.friend_request_view_button);
        mFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show friend tab
                mFriendRequestButton.setBackgroundResource(android.R.drawable.btn_default);
                mFriendsButton.setBackgroundResource(R.color.friend_button_background);
                mListener.onFragmentInteraction(true);
            }
        });
        mFriendRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show friend-request tab
                mFriendsButton.setBackgroundResource(android.R.drawable.btn_default);
                mFriendRequestButton.setBackgroundResource(R.color.friend_button_background);
                mListener.onFragmentInteraction(false);
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mListener = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface Callback{
        // TODO: Update argument type and name
        void onFragmentInteraction(Boolean friend);
    }
}

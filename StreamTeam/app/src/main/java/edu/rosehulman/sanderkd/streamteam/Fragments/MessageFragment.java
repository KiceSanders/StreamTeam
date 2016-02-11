package edu.rosehulman.sanderkd.streamteam.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.MessageActivity;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private ListView mListView;
    private ArrayAdapter<String> mMessageAdapter;
    private ArrayList<String> mMessages;
    public Callback mListener;

    public MessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        mListView = (ListView) view.findViewById(R.id.message_list_view);
        mMessages = new ArrayList<>();

        mMessageAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, mMessages);
        mListView.setAdapter(mMessageAdapter);

        String query = "Exec users_who_you_message '" + MainActivity.USER + "'";
        new MessageQuery().execute(query);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onFriendChoose(mMessages.get(position));
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

    private class MessageQuery extends AsyncTask<String, ResultSet, ResultSet> {
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            try{
                r.next();
                while(r.next()){
                    mMessages.add(r.getString("Users"));
                }

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            mMessageAdapter.notifyDataSetChanged();

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

    public interface Callback {
        // TODO: Update argument type and name
        void onFriendChoose(String user);
    }

}

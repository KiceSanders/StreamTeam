package edu.rosehulman.sanderkd.streamteam.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sanderkd.streamteam.Adapters.FriendRequestAdapter;
import edu.rosehulman.sanderkd.streamteam.Adapters.IgnoreAdapter;
import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class IgnoreFragment extends Fragment {
    private IgnoreAdapter mAdapter;
    private ArrayList<String> mSearch;
    private SearchView addFriendSearch;
    private ListView mListView;
    private boolean searchFocus;
    private ArrayAdapter<String> mSearchAdapter;

    public IgnoreFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ignore, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.ignore_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.hasFixedSize();
        mAdapter = new IgnoreAdapter(getContext());
        rv.setAdapter(mAdapter);

        mSearch = new ArrayList<>();

        addFriendSearch = (SearchView) view.findViewById(R.id.ignore_search);
        addFriendSearch.setQueryHint("Add to ignore");

        mListView = (ListView) view.findViewById(R.id.ignore_list);

        addFriendSearch.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeFocus(hasFocus);
            }
        });

        addFriendSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    changeFocus(false);
                    return true;
                }
                else{
                    if(!searchFocus){
                        changeFocus(true);
                    }
                }
                mSearch.clear();
                Log.d("AddFriendFrag", "mSearch: " + mSearch.toString());
                mSearchAdapter.notifyDataSetChanged();
                String query = "Exec search_for_friends '" + newText + "', '" + MainActivity.USER + "'" ;
                new Query().execute(query, "search");
                return true;
            }
        });

        return view;
    }

    private void changeFocus(boolean hasFocus) {
        searchFocus = hasFocus;
        if (hasFocus) { //set Listview
            mSearchAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mSearch);
            mListView.setAdapter(mSearchAdapter);
            mListView.setClickable(true);
            mListView.bringToFront();
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String query = "Exec add_to_ignore '" + MainActivity.USER + "', '" + mSearch.get(position) + "'";
                    new Query().execute(query, "addFriend", mSearch.get(position));
                }
            });
        } else {
            mListView.setAdapter(null);
            mListView.setClickable(false);
        }
    }

    private class Query extends AsyncTask<String, ResultSet, ResultSet> {
        String mHandler;
        String mUser;
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            if(mHandler.equals("addFriend")){
                Toast.makeText(IgnoreFragment.this.getContext(), "Added to Ignore", Toast.LENGTH_SHORT).show();
                mAdapter.addToIgnore(mUser);
                mAdapter.notifyDataSetChanged();
            }
            else{
                try {
                    if (r != null) {
                        while (r.next()) {
                            Log.d("AddFriendFrag", r.getString("Username"));
                            mSearch.add(r.getString("Username"));
                            mSearchAdapter.notifyDataSetChanged();
                        }
                    }
                }catch(SQLException e){
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected ResultSet doInBackground(String...params){
            String mQuery = params[0];
            mHandler = params[1];
            if(params.length == 3) {
                mUser = params[2];
            }
            ResultSet res = null;
            try {
                Connection con = MainActivity.con.CONN();
                if (con == null) {
                    Log.e("error", "no connection");
                } else {
                    CallableStatement stmt = con.prepareCall(mQuery);
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

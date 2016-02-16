package edu.rosehulman.sanderkd.streamteam.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sanderkd.streamteam.Adapters.FriendRequestAdapter;
import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
// * {@link  interface
 * to handle interaction events.
 * Use the {@link AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FriendRequestAdapter mAdapter;
    private ArrayList<String> mSearch;
    private SearchView addFriendSearch;
    private ListView mListView;
    private boolean searchFocus;
    private ArrayAdapter<String> mSearchAdapter;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance(String param1, String param2) {
        AddFriendFragment fragment = new AddFriendFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.friend_request_recycler_view);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.hasFixedSize();
        mAdapter = new FriendRequestAdapter(getContext());
        rv.setAdapter(mAdapter);

        mSearch = new ArrayList<>();

        addFriendSearch = (SearchView) view.findViewById(R.id.add_friend_search);
        addFriendSearch.setQueryHint("Add a Friend!");

        mListView = (ListView) view.findViewById(R.id.friend_search_list);

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
                Log.d("AddFriendFrag","mSearch: "+mSearch.toString());
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
                    String query = "Exec new_friend_request '" + MainActivity.USER + "', '" + mSearch.get(position) + "'";
                    new Query().execute(query, "addFriend");
                }
            });
        } else {
            mListView.setAdapter(null);
            mListView.setClickable(false);
        }
    }

    private class Query extends AsyncTask<String, ResultSet, ResultSet> {
        String mHandler;

        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            if(mHandler.equals("addFriend")){
                Toast.makeText(AddFriendFragment.this.getContext(), "", Toast.LENGTH_SHORT).show();
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

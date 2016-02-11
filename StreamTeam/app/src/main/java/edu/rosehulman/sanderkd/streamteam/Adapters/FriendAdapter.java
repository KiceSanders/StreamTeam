package edu.rosehulman.sanderkd.streamteam.Adapters;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sanderkd.streamteam.Fragments.FriendListFragment;
import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.MessageActivity;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * Created by sanderkd on 2/3/2016.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    ArrayList<String> mFriendArray;
    FriendListFragment mFragment;

    public FriendAdapter(FriendListFragment frag){
        mFriendArray = new ArrayList<>();
        mFragment = frag;
        String query = "Exec get_Friends '" + MainActivity.USER + "'";
        new FriendQuery().execute(query);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mText.setText(mFriendArray.get(position));

    }

    @Override
    public int getItemCount() {
        return mFriendArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mText;
        private Button mButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.friend_row_image);
            mText = (TextView) itemView.findViewById(R.id.friend_row_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mFragment.mListener.onFriendSelect(mText.toString());
                }
            });
        }
    }

    private class FriendQuery extends AsyncTask<String, ResultSet, ResultSet> {
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            try{
                Log.d("FriendAdapter", ""+r.getMetaData().getColumnCount());
                while(r.next()){
                    if(r.getString("User1").equals(MainActivity.USER)){
                        mFriendArray.add(r.getString("User2"));
                    } else{
                        mFriendArray.add(r.getString("User1"));
                    }
                }

            }
            catch (SQLException e){
                e.printStackTrace();
            }
            notifyDataSetChanged();

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
}

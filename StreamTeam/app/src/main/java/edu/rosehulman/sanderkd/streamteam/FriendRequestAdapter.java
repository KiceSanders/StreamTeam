package edu.rosehulman.sanderkd.streamteam;

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

/**
 * Created by sanderkd on 2/4/2016.
 */
public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>{
    ArrayList<String> mFriendRequestArray;

    public FriendRequestAdapter(){
        mFriendRequestArray = new ArrayList<>();
        String query = "Exec get_friend_requests '" + MainActivity.USER + "'";
        new FriendRequestQuery().execute(query);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mText.setText(mFriendRequestArray.get(position));
        holder.mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "Exec handle_friend_request 1, '" + mFriendRequestArray.get(position) + "', '" + MainActivity.USER + "'";
                new FriendRequestQuery().execute(query);
            }
        });
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = "Exec handle_friend_request 0, '" +mFriendRequestArray.get(position)+ "', '" +MainActivity.USER + "'";
                new FriendRequestQuery().execute(query);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFriendRequestArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private TextView mText;
        private Button mAddButton;
        private Button mDeleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.friend_row_image);
            mText = (TextView) itemView.findViewById(R.id.friend_row_text);
            mAddButton = (Button) itemView.findViewById(R.id.friend_request_add);
            mDeleteButton = (Button) itemView.findViewById(R.id.friend_request_delete);
        }
    }

    private class FriendRequestQuery extends AsyncTask<String, ResultSet, ResultSet> {
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            if (r != null) {
                try {
                    while (r.next()) {
                        if (r.getString("User1").equals(MainActivity.USER)) {
                            mFriendRequestArray.add(r.getString("User2"));
                        } else {
                            mFriendRequestArray.add(r.getString("User1"));
                        }
                    }

                }
                catch(SQLException e){
                    e.printStackTrace();
                }
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



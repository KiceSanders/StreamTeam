package edu.rosehulman.sanderkd.streamteam.Adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * Created by sanderkd on 2/15/2016.
 */
public class IgnoreAdapter extends RecyclerView.Adapter<IgnoreAdapter.ViewHolder>{
    ArrayList<String> mFriendRequestArray;
    Context mContext;



    public IgnoreAdapter(Context context){
        mContext = context;
        mFriendRequestArray = new ArrayList<>();
        String query = "Exec get_ignore '" + MainActivity.USER + "'";
        new FriendRequestQuery().execute(query);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_row, parent, false);
        return new ViewHolder(view);
    }

    public void addToIgnore(String user){
        mFriendRequestArray.add(user);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mText.setText(mFriendRequestArray.get(position));
        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFriendRequestArray.remove(position);
//                String query = "Exec handle_friend_request 0, '" +mFriendRequestArray.get(position)+ "', '" + MainActivity.USER+ "'";
//                new FriendRequestQuery().execute(query);
//                Toast.makeText(mContext, mFriendRequestArray.get(position)+" deleted", Toast.LENGTH_SHORT).show();
//                mFriendRequestArray.remove(position);
                notifyDataSetChanged();
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

            mText = (TextView) itemView.findViewById(R.id.friend_row_text);
            mAddButton = (Button) itemView.findViewById(R.id.friend_request_add);
            mAddButton.setVisibility(View.INVISIBLE);
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
                        mFriendRequestArray.add(r.getString("IgnoreName"));

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
    }


}

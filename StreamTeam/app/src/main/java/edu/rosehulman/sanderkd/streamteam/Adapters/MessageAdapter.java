package edu.rosehulman.sanderkd.streamteam.Adapters;

import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import edu.rosehulman.sanderkd.streamteam.MainActivity;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * Created by sanderkd on 2/10/2016.
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<String> mMessages;
    private String User2;
    private LinearLayoutManager mLayoutManager;

    public MessageAdapter(String u, LinearLayoutManager layout){
        User2 = u;
        mMessages = new ArrayList<>();
        mLayoutManager = layout;

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String query = "Exec get_messages '" + MainActivity.USER + "', '" + User2 + "'";
                new MessageQuery().execute(query, "get_messages");
            }
        }, 0, 1500);


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String[] arr = mMessages.get(position).split("/n");
        int length = MainActivity.USER.length();
        if (mMessages.get(position).substring(0, length).equals(MainActivity.USER)) {
            holder.u1Text.setText(arr[1].trim());
            holder.u2Text.setText("");
        } else {
            holder.u2Text.setText(arr[1].trim());
            holder.u1Text.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView u1Text;
        private TextView u2Text;
        public ViewHolder(View itemView) {
            super(itemView);
            u1Text = (TextView) itemView.findViewById(R.id.user1_text);
            u2Text = (TextView) itemView.findViewById(R.id.user2_text);
        }
    }

    private class MessageQuery extends AsyncTask<String, ResultSet, ResultSet> {
        String mHandler;
        @Override
        protected void onPreExecute () {

        }

        @Override
        protected void onPostExecute(ResultSet r){
            try{
                for(int i=0; i < mMessages.size(); i++){
                    r.next();
                }
                while (r.next()) {
                    mMessages.add(r.getString("message"));
                    notifyDataSetChanged();
                    mLayoutManager.scrollToPosition(mMessages.size() -1);
                }




            }
            catch (SQLException e){
                e.printStackTrace();
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

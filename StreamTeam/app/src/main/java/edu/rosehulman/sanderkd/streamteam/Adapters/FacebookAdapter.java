package edu.rosehulman.sanderkd.streamteam.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.sanderkd.streamteam.FacebookPost;
import edu.rosehulman.sanderkd.streamteam.R;

/**
 * Created by sanderkd on 2/16/2016.
 */
public class FacebookAdapter extends RecyclerView.Adapter<FacebookAdapter.ViewHolder>{

    private ArrayList<FacebookPost> mFacebookPosts;

    public FacebookAdapter(ArrayList<FacebookPost> fb_post){
        mFacebookPosts = fb_post;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.feedText.setText(mFacebookPosts.get(position).getMessage());
        holder.nameText.setText(mFacebookPosts.get(position).getStory());
        holder.timeText.setText(mFacebookPosts.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mFacebookPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView feedText;
        private TextView nameText;
        private TextView timeText;
        public ViewHolder(View itemView) {
            super(itemView);
            feedText = (TextView) itemView.findViewById(R.id.feed_text);
            nameText = (TextView) itemView.findViewById(R.id.name_text);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
        }
    }
}

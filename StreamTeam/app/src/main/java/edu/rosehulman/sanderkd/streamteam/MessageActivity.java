package edu.rosehulman.sanderkd.streamteam;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import edu.rosehulman.sanderkd.streamteam.Adapters.MessageAdapter;

public class MessageActivity extends AppCompatActivity {

    public String User2;
    private EditText mEditMessage;
    private Button mSendMessage;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

//        Bundle b = getIntent().getExtras();
//        User2 = b.getString("friend");

        mEditMessage = (EditText) findViewById(R.id.send_message_text);
        mSendMessage = (Button) findViewById(R.id.send_message_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setReverseLayout(true);
        mRecyclerView.setLayoutManager(lm);

        mRecyclerView.setAdapter(new MessageAdapter());

    }
}

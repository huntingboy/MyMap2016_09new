package com.nomad.friend;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nomad.mymap2016_09.R;

public class FriendsActivity extends AppCompatActivity {

    private Button btAdd;
    private Button btSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        btAdd = (Button) findViewById(R.id.bt_add_friends);
        btSelect = (Button) findViewById(R.id.bt_select_friends);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendsActivity.this, AddFriendActivity.class));
                //finish();
            }
        });
        btSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FriendsActivity.this, SelectFriendActivity.class));
                //finish();
            }
        });
    }
}

package com.nomad.friend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.nomad.location.SelectTrace;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.mymap2016_09.R;
import com.nomad.sharedres.SharedRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FriendFenceInfoActivity extends AppCompatActivity {
    private String friName;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fence_delete);
        //得到好友名信息
        friName = this.getIntent().getStringExtra("FriUsername");
        if (MainActivity.isFlagSignIn()) {
            listView = (ListView) findViewById(R.id.lv_fence_del);

            ExecutorService executorService = Executors.newCachedThreadPool();
            Future<ArrayList<HashMap<String, Object>>> future = executorService.submit(new SelectFriendFence(friName));
            Log.d("Tag", "好友名：" + friName);
            ArrayList<HashMap<String, Object>> fences = new ArrayList<>();
            try {
                fences = future.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.d("Tag", "查询好友围栏Info表信息出错！！！");
                e.printStackTrace();
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(this,
                    fences,
                    R.layout.fence_item,
                    new String[]{"Date", "Latitude", "Longitude", "Radius", "Address"},
                    new int[]{R.id.itemDat, R.id.itemLat, R.id.itemLon, R.id.itemRad, R.id.itemAddr});
            listView.setAdapter(simpleAdapter);

            //单击跳转在地图上显示围栏
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) listView.getItemAtPosition(i);
                    if (!hashMap.equals(null)) {
                        double latitude = (double) hashMap.get("Latitude");
                        double longitude = (double) hashMap.get("Longitude");
                        String title = (String) hashMap.get("Address");

                        //地图显示marker，个人地址
                        SharedRes.aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f).position(new LatLng(latitude, longitude))
                                .title(title).draggable(false)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))).showInfoWindow();
                        Log.d("Tag", "跳转到地图上面显示地址！");
//                        Intent intent = new Intent(FenceDeleteActivity.this, MainActivity.class);
//                        //intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
                        Toast.makeText(FriendFenceInfoActivity.this, "围栏已经显示到了地图上面", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });

        } else {
            Toast.makeText(this, "请先登录!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
}

package com.nomad.mymap2016_09;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Vibrator;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.nomad.friend.FriendsActivity;
import com.nomad.geocode.GeoCoding;
import com.nomad.geofence.FenceSetActivity;
import com.nomad.geofence.Insert;
import com.nomad.geofence.MyGeoFence;
import com.nomad.location.Location;
import com.nomad.location.TraceActivity;
import com.nomad.login.LoginActivity;
import com.nomad.customview.ClearableEditText;
import com.nomad.poisearch.Poisearch;
import com.nomad.sharedres.SharedRes;
import com.nomad.unity.Info;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener,
        AMap.OnMarkerClickListener,
        AMap.InfoWindowAdapter,
        AMap.OnMapClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static TextView textViewName;
    private static TextView textViewEmail;
    private static String userName = null;
    private Toolbar toolbar;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;

    private MapView mMapview;
    private RadioGroup mRadioGroup;

    private RadioButton mRadio1, mRadio2, mRadio3;

    private UiSettings mUiSettings;
    private ImageButton button1;
    private ImageButton button2;

    private String keyword;
    private ClearableEditText editText1;
    // 内容清除图标
//    private Drawable mClearDrawable;
    private Spinner spinner;
    private int juli = 1000;
    private static ListView listView;

    private static Context context;
    //    private FloatingActionButton fab;
    private NavigationView navigationView;
    private static MenuItem menuItem;
    private LinearLayout lineSearch;
    //围栏相关变量
    public static LinearLayout lineFenceSetting;
    private Button btAck;
    private Button btCan;
    private EditText editLatitude;
    private EditText editLongitude;
    private EditText editRadius;
    private String address;
    private GeoCoding geoCoding;
    //创建广播监听
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MyGeoFence.GEOFENCE_BROADCAST_ACTION.equals(intent.getAction())) {
                //解析广播内容
                //广播内容是通过 Bundle 进行传递的
                //获取Bundle
                Bundle bundle = intent.getExtras();
                //获取围栏行为：
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                if (status == GeoFenceClient.GEOFENCE_IN) {
                    Toast.makeText(MainActivity.getContext(), "进入地理围栏", Toast.LENGTH_LONG).show();
                } else if (status == GeoFenceClient.GEOFENCE_OUT) {
                    Toast.makeText(MainActivity.getContext(), "离开地理围栏", Toast.LENGTH_LONG).show();
                } else {
                    //bug
                    //TODO 此处震动不知为什么没有别调用
                    Toast.makeText(MainActivity.getContext(), "在地理围栏10分钟", Toast.LENGTH_LONG).show();
                    Vibrator vt = (Vibrator) MainActivity.getContext().getSystemService(Service.VIBRATOR_SERVICE);
                    vt.vibrate(2000);
                }
                //获取自定义的围栏标识：
                String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                Log.d("tag", "===========>" + customId + "<=============");
                //获取围栏ID:
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                Log.d("tag", "===========>" + fenceId + "<=============");
                //获取当前有触发的围栏对象：
                GeoFence fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE);
                Log.d("tag", "===========>" + fence.toString() + "<=============");
            }
        }
    };

    private static ImageView imageView;
    private static boolean flagSignIn = false;

    public static boolean isFlagSignIn() {
        return flagSignIn;
    }

    public static Context getContext() {
        return context;
    }

    public static String getUserName() {
        return userName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

//        getSupportActionBar().hide();
        //        给主界面添加工具栏
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //        得到发送email的控件,并为其设置监听器
//        fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(this);

        //        得到抽屉布局控件
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        toggle实现了DrawerLayout.DrawerListener监听器,完成对抽屉布局界面切换的回调
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        给抽屉布局控件设置监听器
        drawer.setDrawerListener(toggle);
//        同步状态
        toggle.syncState();

        //        得到抽屉(侧滑)视图中的导航视图控件,并为其设置点击item后的监听器
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //注意imageView的获取方式，他不是R.layout.activity_main.xml中的控件，而是NavigationView的header属性
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        //或者View headerView = navigationView.getHeaderView(R.layout.nav_header_main);

        imageView = (ImageView) headerView.findViewById(R.id.imageView);
        imageView.setOnClickListener(this);
        textViewName = (TextView) headerView.findViewById(R.id.tv_Name);
        textViewEmail = (TextView) headerView.findViewById(R.id.tv_Email);

        //从导航栏中找到菜单，菜单项，动态改变里面的title
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        menuItem = navigationView.getMenu().findItem(R.id.item_community).getSubMenu().findItem(R.id.nav_send);


        mMapview = (MapView) findViewById(R.id.map);
        mMapview.onCreate(savedInstanceState);
        init();


    }

    /**
     * 自定义函数
     * 初始化工作
     */
    private void init() {
        if (SharedRes.aMap == null) {
            SharedRes.aMap = mMapview.getMap();
        }
        setUp();
        //注册广播
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(MyGeoFence.GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, filter);
    }

    /**
     * 自定义函数
     * 进行一些地图设置初始化工作
     */
    private void setUp() {
//        设置定位资源。如果不设置此定位资源则定位按钮不可点击。
        SharedRes.aMap.setLocationSource(new Location(getApplicationContext()));
//        设置定位层是否显示。
//        如果显示定位层，则界面上将出现定位按钮，如果未设置Location Source 则定位按钮不可点击。
        SharedRes.aMap.setMyLocationEnabled(true);
//        总共有三种模式，定位，跟随和旋转
        SharedRes.aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
        SharedRes.aMap.setTrafficEnabled(true);

        button1 = (ImageButton) findViewById(R.id.search_button);
        button1.setOnClickListener(this);
        button2 = (ImageButton) findViewById(R.id.next);
        button2.setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.juli);
        String[] ints = {"1000", "2000", "3000", "4000", "5000"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, ints);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                juli = Integer.valueOf(String.valueOf(spinner.getSelectedItem()));
                Log.i("Tag", juli - 10 + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        lineSearch = (LinearLayout) findViewById(R.id.search_line);
        editText1 = (ClearableEditText) findViewById(R.id.search_edit);
//        editText1.setClearIcon(android.R.drawable.ic_delete);
//        editText1.setCompoundDrawables(null, null, null, null);
////        String[] result_about_wuhan = {"武汉工程大学", "武汉大学", "武汉华科", "武汉医院", "武汉光谷"};
////        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, result_about_wuhan);
//        ArrayAdapter arrayAdapter1 = ArrayAdapter.createFromResource(this, R.array.places, android.R.layout.simple_dropdown_item_1line);
//        autoCompleteTextView.setAdapter(arrayAdapter1);

//        editText1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            //            完成根据输入自动提示初始化，开始发出请求
////            接着回调对应的回调函数onGetInputtips
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                keyword = charSequence.toString();
//                // 处理清除图标的显示与隐藏逻辑
//                handleClearIcon();
//                new MyInputtips(keyword);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
////                当编辑框改变了，就重新开始搜索
//                SharedRes.currentPage = -1;
//            }
//        });
//        编辑框的清除图标
//        mClearDrawable = this.getResources().getDrawable(R.drawable.ic_close_white_48dp);


        SharedRes.aMap.setOnMarkerClickListener(this);
        SharedRes.aMap.setInfoWindowAdapter(this);  // 提供自定义InfoWindow的回调方法，包括getInfoWindow和getInfoContent，前者返回null才会调用后者，否者自己绘制。

        SharedRes.aMap.setOnMapClickListener(this);

        mRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
        mRadio1 = (RadioButton) findViewById(R.id.myRadioButton1);
        mRadio2 = (RadioButton) findViewById(R.id.myRadioButton2);
        mRadio3 = (RadioButton) findViewById(R.id.myRadioButton3);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == mRadio1.getId()) {
                    //普通地图
                    SharedRes.aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                }
                if (i == mRadio2.getId()) {
                    //卫星地图
                    SharedRes.aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                }
                if (i == mRadio3.getId()) {
                    //夜景模式
                    SharedRes.aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                }
                SharedRes.aMap.setTrafficEnabled(true);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                listView.setVisibility(View.GONE);
                Log.i("Tag", "========>点击了" + view.getId() + i);
                // 移动地图到标记
//                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                        new LatLng(), 18, 30, 0
//                )));
                HashMap<String, String> hashMap = (HashMap<String, String>) listView.getItemAtPosition(i);
                if (SharedRes.currentPage < 0) {
                    editText1.setText(hashMap.get("name"));
                    SharedRes.currentPage = 0;
                    search();
                } else {
                    if (!hashMap.equals(null)) {
                        double latitude = Double.parseDouble(hashMap.get("latitude"));
                        double longtitude = Double.parseDouble(hashMap.get("longtitude"));
                        String addressName = hashMap.get("title");
                        Log.d("Tag", "========>" + latitude + "=====>" + longtitude);
//                    mapView一直都有，所以不需要下面一行
//                    mMapview.setVisibility(View.VISIBLE);
                        SharedRes.aMap.clear(true);
                        SharedRes.aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                new LatLng(latitude, longtitude), 18, 30, 0
                        )));
//                    下面一句不可以有，因为把当前editText的内容改变之后会置currentPage=-1，会重新开始搜索。
//                    editText1.setText(addressName);
                        SharedRes.marker = SharedRes.aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                                .position(new LatLng(latitude, longtitude))
                                .title(addressName)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                                .draggable(false));
                        SharedRes.marker.showInfoWindow();
                    }
                }
            }
        });

        mUiSettings = SharedRes.aMap.getUiSettings();
        //定位按钮
        mUiSettings.setMyLocationButtonEnabled(true);
        //缩放控件
        mUiSettings.setZoomControlsEnabled(false);
        //缩放控件位置
//        mUiSettings.setZoomPosition();
        //指南针
        mUiSettings.setCompassEnabled(true);
        //比例尺控件
        mUiSettings.setScaleControlsEnabled(true);

/*        改变地图中心点
        aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                new LatLng(30.457229, 30.457229),
                18,
                30,
                0
        )));*/

        //围栏设置界面初始化
        MainActivity.lineFenceSetting = (LinearLayout) findViewById(R.id.line_fence);
        btAck = (Button) findViewById(R.id.bt_ack);
        btCan = (Button) findViewById(R.id.bt_can);
        btAck.setOnClickListener(this);
        btCan.setOnClickListener(this);
        editLatitude = (EditText) findViewById(R.id.edit_latitude);
        editLongitude = (EditText) findViewById(R.id.edit_longitude);
        editRadius = (EditText) findViewById(R.id.edit_radius);
        SharedRes.fenceList = new ArrayList<>();
    }

//    /**
//     * 自定义函数
//     * 处理清除图标的逻辑
//     */
//    private void handleClearIcon() {
////        if (keyword != null) {
////            // 显示
////            editText1.setCompoundDrawables(null, null, mClearDrawable, null);
////        } else {
////            // 隐藏
////            editText1.setCompoundDrawables(null, null, null, null);
////        }
//        editText1.setCompoundDrawables(null, null, mClearDrawable, null);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivity.context = null;
        mMapview.onDestroy();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        mMapview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapview.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapview.onSaveInstanceState(outState);
    }


    long firstTime = 0;

    /**
     * 手机返回按钮回调
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (MainActivity.lineFenceSetting.getVisibility() == View.VISIBLE) {
                lineFenceSetting.setVisibility(View.GONE);
            }
            getSupportActionBar().show();
            lineSearch.setVisibility(View.GONE);
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
                SharedRes.aMap.clear(true);
                SharedRes.currentPage = -1;
                listView.setVisibility(View.GONE);
                Log.d("Tag", "===============>清空地图<===============");
                return true;
            } else {
                finish();
                Log.d("Tag", "=============>退出程序<===============");
            }
        }

        return super.onKeyUp(keyCode, event);
    }

//      不知道为啥，没有关闭抽屉，而是退出程序。。。
//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//        super.onBackPressed();
//    }

    /**
     * 工具栏右侧菜单添加菜单项,可以在R.menu.main中加入其他item,如"关于","反馈","设置"等
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 点击工具栏右侧菜单项后的回调
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            getSupportActionBar().hide();
            lineSearch.setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 主界面左侧导航视图中menuitem点击后的回调
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            //自定义围栏
            if (isFlagSignIn()) {
                startActivity(new Intent(MainActivity.this, FenceSetActivity.class));
            } else {
                Toast.makeText(this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_gallery) {
            //个人轨迹
            if (isFlagSignIn()) {
                startActivity(new Intent(this, TraceActivity.class));
            } else {
                Toast.makeText(this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_slideshow) {
            //好友操作
            if (isFlagSignIn()) {
                startActivity(new Intent(this, FriendsActivity.class));
            } else {
                Toast.makeText(this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
            if(flagSignIn) {
                imageView.setImageResource(android.R.drawable.sym_def_app_icon);
                textViewName.setText("未登录");
                item.setTitle("未登录");
                flagSignIn = false;
                Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "你还未登录！", Toast.LENGTH_SHORT).show();
            }

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /**
     * 手机横竖屏时回调
     *
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d("Tag", "横屏");
        } else {
            Log.d("Tag", "竖屏");
        }
    }

    /**
     * 点击搜索button按钮触发的事件
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.search_button:
                Log.i("Tag", "搜索操作");
                SharedRes.currentPage = 0;
                search();
                break;
            case R.id.next:
                Log.i("Tag", "下一页");
                if (SharedRes.currentPage >= 0) {
                    SharedRes.currentPage++;
                    search();
                    break;
                } else
                    Toast.makeText(this, "你还没有搜索！", Toast.LENGTH_SHORT).show();
//            case R.id.fab:
////               不同于Toast,Snackerbar的提示内容不是浮在activity上面的
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                break;
            case R.id.bt_ack:
                if (editLatitude.getText().toString().trim().equals("") ||
                        editLongitude.getText().toString().trim().equals("") ||
                        editRadius.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "请输入数据!", Toast.LENGTH_SHORT).show();
                } else {
                    //如果用户已经登录，就对围栏操作，否则提醒用户登录
                    //通过flagSignIn来判断用户是否登录
                    if (flagSignIn) {

                       //存储围栏的相关信息：中心点的经度，纬度，半径,（最后要删掉，应该根据用户数据库存取围栏）
//                        HashMap<String, String> fenceMap = new HashMap<>();
//                        fenceMap.put("经度", "经度：" + editLatitude.getText().toString().trim());
//                        fenceMap.put("纬度", "纬度：" + editLongitude.getText().toString().trim());
//                        fenceMap.put("半径", "半径：" + editRadius.getText().toString().trim());
//                        SharedRes.fenceList.add(fenceMap);
                        //把经纬度和时间存入到数据库中,用于用户间的共享，和自己的查看
//                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
//                        df.format(new Date());// new Date()为获取当前系统时间
                        address = geoCoding.getAddressName();
                        Log.d("Tag", "=======围栏地址：" + address + "=========");
                        ExecutorService executorService = Executors.newCachedThreadPool();
                        Future<Boolean> future = executorService.submit(new Insert(new Info(new Date(),
                                Double.parseDouble(editLatitude.getText().toString().trim()),
                                Double.parseDouble(editLatitude.getText().toString().trim()),
                                Double.parseDouble(editRadius.getText().toString().trim()),
                                address),
                                textViewName.getText().toString()));
                        Boolean tag = false;
                        try {
                            tag = future.get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                        if (tag) {
                            //显示围栏的相关信息：中心点的经度，纬度，半径
                            new MyGeoFence(Double.parseDouble(editLatitude.getText().toString().trim()),
                                    Double.parseDouble(editLongitude.getText().toString().trim()),
                                    Float.parseFloat(editRadius.getText().toString().trim()));
                            Toast.makeText(this, "插入围栏成功", Toast.LENGTH_SHORT).show();
                            Vibrator vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);
                        }else {
                            Toast.makeText(this, "插入围栏失败！", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "请先登陆！", Toast.LENGTH_SHORT).show();
                    }


                }
                break;
            case R.id.bt_can:
                editLatitude.setText("");
                editLongitude.setText("");
                editRadius.setText("");
                SharedRes.aMap.clear(true);
                break;
            case R.id.imageView:
                if (flagSignIn) {
                    //此处可以对用户的信息进行一些设置，如退出登录

                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                break;
        }
    }


    /**
     * 自定义函数
     * 当点击搜索按钮的时候被调用
     */
    private void search() {
        keyword = editText1.getText().toString().trim();
        Log.i("Tag", keyword);
        if ("".equals(keyword)) {
            Toast.makeText(MainActivity.this, "请输入关键字", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // doSearchQuery();
            new Poisearch(this, keyword, null);
        }
    }

    /**
     * 自定义回调函数
     * 输入提示时候被调用，用于在listview上面显示POI
     */
    public static void showPoi() {
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.context, SharedRes.arrayList, R.layout.list_item,
                new String[]{"name", "address"}, new int[]{R.id.itemTitle, R.id.itemContent});
        listView.setVisibility(View.VISIBLE);
        listView.setAdapter(simpleAdapter);
        simpleAdapter.notifyDataSetChanged();
    }

    /**
     * 自定义回调函数
     * 搜索完成后被搜索模块调用，用于在listview上面显示POI,在地图上面显示marker
     */
    public static void showPoi(ArrayList<MarkerOptions> markerOptionses) {
        SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.context,
                SharedRes.arrayList,
                R.layout.list_item,
                new String[]{"title", "ad"},
                new int[]{R.id.itemTitle, R.id.itemContent});
        listView.setAdapter(simpleAdapter);
//                        mMapview.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

//                        将PoiItems以POIOverLay的图层方式添加Marker标记。
        SharedRes.aMap.clear(true);// 清理之前的图标
//                        通过overlay的方式添加Marker，也可以通过aMap.addMarkers(...)添加
//        SharedRes.poiOverlay = new PoiOverlay(SharedRes.aMap, poiItems);
//        SharedRes.poiOverlay.removeFromMap();
//        SharedRes.poiOverlay.addToMap();
//        SharedRes.poiOverlay.zoomToSpan();
        SharedRes.aMap.addMarkers(markerOptionses, false);
//                        移动地图到标记
//                        aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                                new LatLng(poiItems.get(0).getLatLonPoint().getLatitude(), poiItems.get(0).getLatLonPoint().getLongitude()), 18, 30, 0
//                        )));
//        SharedRes.aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                new LatLng(poiItems.get(0).getLatLonPoint().getLatitude(), poiItems.get(0).getLatLonPoint().getLongitude()), 18, 30, 0
//        )));
    }


    /**
     * 点击地图上的marker标记后触发的事件
     */
    @Override
    public boolean onMarkerClick(Marker marker1) {
        SharedRes.marker = marker1;
        SharedRes.marker.showInfoWindow();
        return false;
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
//        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri, null);

        return null;
    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    /**
     * 点击地图后的回调函数
     */
    @Override
    public void onMapClick(LatLng latLng) {
//        if (poiOverlay != null) {
//            aMap.clear(true);
////            poiOverlay.removeFromMap();
//            poiOverlay = null;
//        }else {
//            if (marker != null) {
//                marker.remove();
//        }
        if (SharedRes.currentPage < 0) {
            SharedRes.aMap.clear(true);
            SharedRes.currentPage = -1;
            //逆地理编码
            geoCoding = new GeoCoding(latLng);

            address = geoCoding.getAddressName();
            //address = new GeoCoding(latLng).getAddressName();
            Log.d("Tag", "=======围栏地址：" + address + "=========");
            //地理编码
            //new GeoCoding(name);
        }

        Log.i("Tag", latLng.latitude + " : " + latLng.longitude);
        editLatitude.setText(String.valueOf(latLng.latitude));
        editLongitude.setText(String.valueOf(latLng.longitude));
    }

    public static void showAccount(String name) {
        textViewName.setText(name);
        menuItem.setTitle("退出登录");
//        textViewEmail.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.head_image);
        flagSignIn = true;
        userName = name;
    }
}
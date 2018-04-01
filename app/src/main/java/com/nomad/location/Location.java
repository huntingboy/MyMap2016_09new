package com.nomad.location;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.LocationSource;
import com.nomad.mymap2016_09.MainActivity;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nomad on 17-1-14.
 */
public class Location implements LocationSource, AMapLocationListener{
    private AMapLocationClient aMapLocationClient;
    private AMapLocationClientOption aMapLocationClientOption;
    private OnLocationChangedListener mListener;
    private Context context;
    private String lastAddress = ""; //用于记录最近一次的地址，和当前地址对比，看是否需要更新Trace表

    public Location(Context context) {
        this.context = context;
    }

    /**
     * LocationSource的方法
     * 激活位置定位
     * @param onLocationChangedListener
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (aMapLocationClient == null) {
            aMapLocationClient = new AMapLocationClient(context);

            aMapLocationClientOption = new AMapLocationClientOption();
            aMapLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            aMapLocationClientOption.setNeedAddress(true);
            aMapLocationClientOption.setOnceLocation(false);
            aMapLocationClientOption.setWifiActiveScan(true);
            aMapLocationClientOption.setMockEnable(true);
            aMapLocationClientOption.setInterval(2000);
            aMapLocationClient.setLocationOption(aMapLocationClientOption);
            aMapLocationClient.setLocationListener(this);
            aMapLocationClient.startLocation();
        }
    }

    /**
     * LocationSource的方法
     * 挂起定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        aMapLocationClient = null;
    }

    /**
     * AMapLocationListener的方法
     * @param aMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);

                //用户退出登录就重置最后保存的地址信息
                if (!MainActivity.isFlagSignIn()) {
                    lastAddress = "";
                }

                StringBuilder stringBuilder = new StringBuilder();
                int type = aMapLocation.getLocationType();
                String address = aMapLocation.getAddress();
                stringBuilder.append(address);
//                city = aMapLocation.getCity();
                Log.i("Tag", "======定位地址：" + stringBuilder.toString());

//                if (latLonPoint == null) {
//                    latLonPoint = new LatLonPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//                } else {
//                    latLonPoint.setLatitude(aMapLocation.getLatitude());
//                    latLonPoint.setLongitude(aMapLocation.getLongitude());
//                }
                Log.i("Tag", "======定位位置：" + aMapLocation.getLatitude() + ":" + aMapLocation.getLongitude());
//                Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT).show();

                //位置发生变化
                //todo 此处可以给用户的数据库Trace表添加信息（时间，经纬度，用户名， 地址描述）
                //地址不相同才添加 并且在登录状态下
                if(MainActivity.isFlagSignIn() && !lastAddress.equals(stringBuilder.toString())) {
                    lastAddress = stringBuilder.toString();
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    Future<Boolean> future = executorService.submit(new AddTrace(MainActivity.getUserName(),
                            aMapLocation.getLatitude(), aMapLocation.getLongitude(), lastAddress));
                    Log.d("Tag", "用户名：" + MainActivity.getUserName());
                    boolean tag = false;
                    try {
                        tag = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Log.d("Tag", "更新数据库Trace用户定位信息出错！！！");
                        e.printStackTrace();
                    }
                    Log.d("Tag", "更新数据库Trace用户定位信息" + tag + lastAddress);
                }
                Log.d("Tag", "用户的当前位置：" + stringBuilder.toString());

            } else {
                Log.i("Tag", aMapLocation.getErrorCode() + "====" + aMapLocation.getErrorInfo());
            }
        }
    }
}

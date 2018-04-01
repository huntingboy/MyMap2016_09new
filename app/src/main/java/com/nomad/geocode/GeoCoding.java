package com.nomad.geocode;

import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.nomad.geofence.Select;
import com.nomad.geofence.UpdateAddress;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.sharedres.SharedRes;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by nomad on 17-2-7.
 */
public class GeoCoding implements GeocodeSearch.OnGeocodeSearchListener{
    private GeocodeSearch geocodeSearch;
    private LatLonPoint latLonPoint;
    private String addressName;

    public String getAddressName() {
        return addressName;
    }

    /**
     * 地理编码构造函数
     * @param name
     */
    public GeoCoding(String name) {
        geocodeSearch = new GeocodeSearch(MainActivity.getContext());
        geocodeSearch.setOnGeocodeSearchListener(this);
        GeocodeQuery geocodeQuery = new GeocodeQuery(name, null);
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
    }

    /**
     * 逆地理编码
     * @param latLng
     */
    public GeoCoding(LatLng latLng) {
        geocodeSearch = new GeocodeSearch(MainActivity.getContext());
        geocodeSearch.setOnGeocodeSearchListener(this);
        latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
    }

    /**
     * 逆地理编码回调，坐标转地址
     * @param regeocodeResult
     * @param i
     */
    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {

        if (i == 1000) {
            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                addressName = regeocodeResult.getRegeocodeAddress().getFormatAddress()
                        + "附近";
                SharedRes.marker = SharedRes.aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                        .position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()))
                        .title(addressName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        .draggable(false));
                SharedRes.marker.showInfoWindow();

                //TODO 在这个异步方法快要结束的时候把地址信息更新到数据库Info表里面
                if(MainActivity.isFlagSignIn()) {
                    ExecutorService executorService = Executors.newCachedThreadPool();
                    Future<Boolean> future = executorService.submit(new UpdateAddress(addressName,
                            latLonPoint.getLatitude(), latLonPoint.getLongitude()));
                    Log.d("Tag", "用户名：" + MainActivity.getUserName());
                    boolean tag = false;
                    try {
                        tag = future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        Log.d("Tag", "更新数据库Info地理围栏地址信息出错！！！");
                        e.printStackTrace();
                    }
                    Log.d("Tag", "更新数据库Info地理围栏地址信息" + tag + addressName);
                }
            } else {
                Toast.makeText(MainActivity.getContext(), "逆地理编码没有结果", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.getContext(), i, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 地理编码回调，地址转坐标
     * @param geocodeResult
     * @param i
     */
    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }
}

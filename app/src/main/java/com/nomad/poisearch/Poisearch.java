package com.nomad.poisearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.nomad.mymap2016_09.MainActivity;
import com.nomad.sharedres.SharedRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nomad on 17-1-14.
 */
public class Poisearch implements PoiSearch.OnPoiSearchListener{
    private PoiSearch poiSearch;
    private PoiItem poiItem;
    private PoiSearch.Query query;
    private String keyword;
    private String city;
    private ProgressDialog progressDialog;
    private Context context;
    private PoiResult poiResults;

    public Poisearch(Context context, String keyword, String city) {
        this.keyword = keyword;
        this.city = city;
        this.context = context;

        doSearchQuery();
    }

    /**自定义函数
     * 被search调用
     * 完成初始化，开始搜索
     * poi搜索,设置poi监听,搜索条件,开始搜索
     */
    private void doSearchQuery() {
        showProgressDiaglog();
//        关键字搜索
        query = new PoiSearch.Query(keyword, "", city);
        query.setPageSize(10);
        query.setPageNum(SharedRes.currentPage);

        poiSearch = new PoiSearch(context, query);
        poiSearch.setOnPoiSearchListener(this);

//        周边搜索（圆形区域）
//        if (latLonPoint != null) {
//            poiSearch.setBound(new PoiSearch.SearchBound(latLonPoint, juli));
//        }

//        检索多边形内的POI
//        List<LatLonPoint> points = new ArrayList<LatLonPoint>();
//        points.add(new LatLonPoint(39.941711, 116.382248));
//        points.add(new LatLonPoint(39.884882, 116.359566));
//        points.add(new LatLonPoint(39.878120, 116.437630));
//        points.add(new LatLonPoint(39.941711, 116.382248));
//        poiSearch.setBound(new SearchBound(points));//设置多边形区域

//        根据ID检索POI
//        poiSearch = new PoiSearch(this, null);
//        poiSearch.setOnPoiSearchListener(this);
//        poiSearch.searchPOIIdAsyn(ID);// 异步搜索

        poiSearch.searchPOIAsyn();
    }
    /**自定义函数
     * 显示搜索对话框
     */
    private void showProgressDiaglog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("正在搜索:\n" + keyword);
        progressDialog.show();
    }
    /**自定义函数
     * 隐藏搜索对话框
     * 在onPoiSearched回调方法中被调用
     */
    private void dissmissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    /**
     * PoiSearch.OnPoiSearchListener的方法
     * 搜索的结果回调
     * @param poiResult
     * @param i
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        dissmissProgressDialog();// 隐藏对话框
        if (i == 1000) {
            Log.i("Tag", "查询结果:" + i);
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                if (poiResult.getQuery().equals(query)) {// 是否是同一条
                    poiResults = poiResult;
                    // 取得搜索到的poiitems有多少页
                    List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    SharedRes.arrayList = new ArrayList<HashMap<String, String>>();
                    ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();

                    if (poiItems != null && poiItems.size() > 0) {
//                        将PoiItems的标题和内容以列表的形式填到适配器，然后给listview显示。
                        for (int j = 0; j < poiItems.size(); j++) {
                            HashMap<String, String> hashMap = new HashMap<String, String>();
                            MarkerOptions markerOptions = new MarkerOptions();
                            hashMap.put("latitude", String.valueOf(poiItems.get(j).getLatLonPoint().getLatitude()));
                            hashMap.put("longtitude", String.valueOf(poiItems.get(j).getLatLonPoint().getLongitude()));
                            hashMap.put("title", poiItems.get(j).getTitle());
                            hashMap.put("ad", poiItems.get(j).getAdName());
                            SharedRes.arrayList.add(hashMap);
                            markerOptions.position(new LatLng(poiItems.get(j).getLatLonPoint().getLatitude(), poiItems.get(j).getLatLonPoint().getLongitude()));
                            markerOptionses.add(markerOptions);
                        }
                        MainActivity.showPoi(markerOptionses);
                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(context, "未找到结果", Toast.LENGTH_SHORT).show();
                    }
                }
            }else
                Toast.makeText(context, "未找到结果", Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(context, "错误代码：" + i, Toast.LENGTH_SHORT).show();
    }

    /**
     * 自定义函数，当搜索没有结果时候被调用
     * @param suggestionCities
     */
    private void showSuggestCity(List<SuggestionCity> suggestionCities) {
        String information = "推荐城市\n";
        for (int i = 0; i < suggestionCities.size(); i++) {
            information += "城市名称:" + suggestionCities.get(i).getCityName() + "城市区号:" +
                    suggestionCities.get(i).getCityCode() + "城市编码:" + suggestionCities.get(i).getAdCode() + "\n";
        }
        Toast.makeText(context, information, Toast.LENGTH_SHORT).show();
    }

    /**
     * PoiSearch.OnPoiSearchListener的方法
     * ID搜索的结果回调
     * @param poiItem
     * @param i
     */
    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        dissmissProgressDialog();// 隐藏对话框
        if (i == 1000) {
            if (poiItem != null) {
                SharedRes.aMap.clear(true);// 清理之前的图标
                this.poiItem = poiItem;
//                marker = aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
//                        .position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()))
//                        .title(addressName)
//                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
//                        .draggable(false));
//                marker.showInfoWindow();
//                下面这一句是错误的，marker为null，需要使用上面的方法添加Marker。
                SharedRes.marker.setPosition(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()));
//                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
//                        new LatLng(mPoi.getLatLonPoint().getLatitude(), mPoi.getLatLonPoint().getLongitude()), 18, 30, 0
//                )));
                SharedRes.aMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()), 18, 30, 0
                )));
            }
        } else {
            Toast.makeText(context, "错误代码：" + i, Toast.LENGTH_SHORT).show();
        }
    }

}

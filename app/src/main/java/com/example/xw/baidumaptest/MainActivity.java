package com.example.xw.baidumaptest;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.example.xw.baidumaptest.Model.AgencyData;
import com.example.xw.baidumaptest.Model.ProductData;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import zxing.android.CaptureActivity;

public class MainActivity extends AppCompatActivity {

    MapView mMapView = null;
    BaiduMap mBaiduMap=null;
    LatLng northeast;
    LatLng southwest;
    RequestQueue mQueue;
    Gson gson;
    AgencyData mAgencyData;
    BitmapDescriptor bitmap;
    Marker[] mMarkers;
    private PopupWindow infoPopupWindow;
    TextView nameTextView;
    TextView totalTextView;
    TextView numATextView;
    TextView numBTextView;
    List<AgencyData.Agency> list;
    FloatingActionButton list_btn;
    FloatingActionButton trace_btn;
    String product_msg;

    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

    private static final int REQUEST_CODE_SCAN = 0x0000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        list_btn= (FloatingActionButton) findViewById(R.id.list_btn);
        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"111",Toast.LENGTH_SHORT).show();
            }
        });
        trace_btn= (FloatingActionButton) findViewById(R.id.trace_btn);
        trace_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,
                        CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SCAN);
            }
        });

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap=mMapView.getMap();
         northeast = new LatLng(60, 135);
         southwest = new LatLng(15, 75);
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mBaiduMap.setMapStatusLimits(new LatLngBounds.Builder().include(northeast).include(southwest).build());
            }
        });
        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupWindow = layoutInflater.inflate(R.layout.popupwindow, null);

        nameTextView= (TextView) popupWindow.findViewById(R.id.tv_agencyname);
        totalTextView= (TextView) popupWindow.findViewById(R.id.tv_agencytotal);
        numATextView= (TextView) popupWindow.findViewById(R.id.tv_agencyAnum);
        numBTextView= (TextView) popupWindow.findViewById(R.id.tv_agencyBnum);


        bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.mark);
        mQueue = Volley.newRequestQueue(this);

        getDataFromServer();


/*        //定义Maker坐标点
        LatLng point = new LatLng(30, 116.400244);
//构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.mark);
//构建MarkerOption，用于在地图上添加Marker
        MarkerOptions option = new MarkerOptions();

               option.position(point)
                .title("江南大学")
                .icon(bitmap);
//在地图上添加Marker，并显示
      mMarker= (Marker) mBaiduMap.addOverlay(option);*/



        infoPopupWindow = new PopupWindow(popupWindow, 600, 800);
        infoPopupWindow.setFocusable(true);
        infoPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
                // TODO Auto-generated method stub
            /*    if (mMarkers!=null){
                    for (int i = 0; i <mMarkers.length ; i++) {
                        if (marker==mMarkers[i]){
                            AgencyData.Agency agency=list.get(i);
                            nameTextView.setText(agency.getName());
                            totalTextView.setText(agency.getTotal());
                            numATextView.setText(agency.getA_num());
                            numBTextView.setText(agency.getB_num());
                            infoPopupWindow.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
                        }

                    }
                }*/
                nameTextView.setText(marker.getTitle());
                totalTextView.setText("进货总量："+marker.getExtraInfo().getInt("Total"));
                numATextView.setText("A商品数量："+marker.getExtraInfo().getInt("NumA"));
                numBTextView.setText("B商品数量："+marker.getExtraInfo().getInt("NumB"));
                infoPopupWindow.showAtLocation(mMapView, Gravity.CENTER, 0, 0);
                return false;
            }
        });


    }

    private void getDataFromServer() {
        StringRequest stringRequest = new StringRequest("http://192.168.191.1:8080/BiShe/AgencyServlet?method=getAll",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        if (gson==null) {
                            gson = new Gson();
                        }
                        mAgencyData=gson.fromJson(response,AgencyData.class);
                        System.out.println(mAgencyData.getSize());
                        updateUI();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(stringRequest);
    }

    private void updateUI() {
        if (mAgencyData==null)
            return;
        mMarkers=new Marker[mAgencyData.getSize()];
        list=mAgencyData.getAgencyList();

        for (int i = 0; i <mAgencyData.getSize(); i++) {
            AgencyData.Agency agency=list.get(i);
            LatLng point = new LatLng(agency.getLatitude(), agency.getLongitude());

            MarkerOptions option = new MarkerOptions();

            option.position(point)
                    .title(agency.getName())
                    .icon(bitmap);

//在地图上添加Marker，并显示
            mMarkers[i]= (Marker) mBaiduMap.addOverlay(option);
            Bundle bundle=new Bundle();
            bundle.putInt("NumA",agency.getA_num());
            bundle.putInt("NumB",agency.getB_num());
            bundle.putInt("Total",agency.getTotal());
            mMarkers[i].setExtraInfo(bundle);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                System.out.println(content);
                search(content);



            }
        }
    }
    private void search(String content){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://192.168.191.1:8080/BiShe/ProductSearchServlet?sn="+content, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        buildDialog(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        });
        mQueue.add(jsonObjectRequest);
    }
    private void buildDialog(JSONObject jsonObject){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//设置对话框图标，可以使用自己的图片，Android本身也提供了一些图标供我们使用
        builder.setIcon(android.R.drawable.ic_dialog_alert);
//设置对话框标题
        builder.setTitle("溯源结果");
//设置对话框内的文本
        try {
           product_msg=jsonObject.getString("msg");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (product_msg.equals("wrong")){
            builder.setMessage("没有该件产品信息");
        }else{
            try {
                JSONObject product=jsonObject.getJSONObject("product");
                String sn=product.getString("sn");
                String worker=product.getString("worker");
                String agency=product.getString("agency");
                String factory=product.getString("factory");
                String date=product.getString("date");
                builder.setMessage("sn："+sn+"\n"+
                                   "出库工厂："+factory+"\n"+
                                   "出库日期："+date);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

        AlertDialog dialog = builder.create();
//显示对话框
        dialog.show();
    }
}

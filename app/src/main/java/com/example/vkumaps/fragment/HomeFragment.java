package com.example.vkumaps.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    //    private SupportMapFragment mapFragment;
    private ChangeFragmentListener listener;
    private int currentstate;

    public HomeFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        listener.changeTitle("Bản đồ");
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_view, mapFragment)
                .commit();

        mapFragment.getMapAsync(this);
        //bottom sheet
        FrameLayout sheet=rootView.findViewById(R.id.sheet);
        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(sheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //chiều cao mặc định
        bottomSheetBehavior.setPeekHeight(100);
        //onclick
        ImageView oc=rootView.findViewById(R.id.btn);
        currentstate=0;
        oc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentstate==0){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    currentstate=1;
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    currentstate=0;
                }
            }
        });

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //set vị trí
        LatLng vku_location = new LatLng(15.9754993744594, 108.25236572354167);
        //set kiểu map
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //thêm điểm đánh dấu
        googleMap.addMarker(new MarkerOptions()
                .position(vku_location)
                .title("Trường Đại học Công nghệ Thông tin và Truyên thông Viêt-Hàn")
                .snippet("Trường Đại học công lập đào tạo sâu về công nghệ thông tin và kinh tế số duy nhất tại miền trung")
                .icon(bitmapDescriptorFromVector(getActivity().getApplicationContext(), R.drawable.ic_favorite)));
        //Di chuyển máy ảnh đến tọa độ bản đồ và phóng to gần hơn.
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(vku_location));
//        googleMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000,null);
        // Lấy đối tượng UiSettings từ GoogleMap
        UiSettings uiSettings = googleMap.getUiSettings();
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);
        //
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(vku_location)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(270)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Tắt thanh công cụ bản đồ
        uiSettings.setMapToolbarEnabled(false);
        //
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        //
        LatLng lc2=new LatLng(15.977280636378287, 108.25767284270289);
        googleMap.addPolyline(new PolylineOptions().add(
                vku_location,
                new LatLng(15.97654583271555, 108.25463967304078),
                new LatLng(15.977938586228822, 108.25490284089005),
                lc2
        ).width(10).color(Color.RED));
        //hiển thị maps
        googleMap.setTrafficEnabled(true);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
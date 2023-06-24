package com.example.vkumaps.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.L;
import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PermissionsListener {
    private View rootView;
    private ImageView oc, zoomIn, zoomOut, rotate, mapType;
    private MapView mapView;
    private FrameLayout sheet;

    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private LocationComponent locationComponent;
    private LocationManager locationManager;

    private ChangeFragmentListener listener;

    public static int currentstate;
    public static BottomSheetBehavior<View> bottomSheetBehavior;

    private static final String TAG = "PERMISSION_TAG";
    private ActivityResultLauncher<String> resultLauncher;
    private FirebaseFirestore firestore;
//    public static final LatLng VKU_LOCATION = new LatLng(15.9754993744594, 108.25236572354167);

    public HomeFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Mapbox.getInstance(getContext(), getString(R.string.mapbox_access_token));

        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        // Khởi tạo SharedPreferences

        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        initializeViews();

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        requestPermission();

        return rootView;
    }

    private void initializeViews() {
        oc = rootView.findViewById(R.id.btn);
        zoomOut = rootView.findViewById(R.id.zoom_out);
        zoomIn = rootView.findViewById(R.id.zoom_in);
        rotate = rootView.findViewById(R.id.rotate);
        sheet = rootView.findViewById(R.id.sheet);
        mapView = rootView.findViewById(R.id.map_view);
        mapType = rootView.findViewById(R.id.map_type);

        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapbox) {
        mapboxMap = mapbox;
        mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                //Hiển thị vị trí hiện tại
                enableLocationComponent(style);
                //Zoom camera vào vị trí hiện tại
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    cameraSetup(latLng, 16, 0);
                }
                if (currentstate == 1) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        uiSettings();
    }

    private void uiSettings() {
//        map.getUiSettings().setZoomControlsEnabled(true);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapboxMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
            @Override
            public boolean onMapClick(@NonNull LatLng point) {
                if (currentstate == 1) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                return true;
            }
        });
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentstate == 1) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                // Tạo một đối tượng CameraPosition mới với hướng mong muốn
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mapboxMap.getCameraPosition().target) // Giữ nguyên tọa độ trung tâm của camera
                        .zoom(mapboxMap.getCameraPosition().zoom) // Giữ nguyên mức zoom của camera
                        .tilt(0) // Điều chỉnh góc nghiêng của camera
//                        .tilt(map.getCameraPosition().tilt) // Điều chỉnh góc nghiêng của camera
                        .bearing(calculateBearing()) // Điều chỉnh hướng của camera
                        .build();
                // Sử dụng animateCamera để chuyển đổi hướng của camera
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private float calculateBearing(){
        float currentBearing= (float) mapboxMap.getCameraPosition().bearing;
        float bearing=0;
        float temp=1;
        boolean left=true;
        if (currentBearing==90||currentBearing==0||currentBearing==180||currentBearing==270){
            if (currentBearing==360){
                return 0f;
            }
            return currentBearing+90;
        }else {
            if (currentBearing>0&&currentBearing<90){
                temp=1;
                if (currentBearing<45){
                    left=false;
                }else {
                    left=true;
                }
            }else if (currentBearing>90&&currentBearing<180){
                temp=2;
                if (currentBearing<135){
                    left=false;
                }else {
                    left=true;
                }
            }else if (currentBearing>180&&currentBearing<270){
                temp=3;
                if (currentBearing<225){
                    left=false;
                }else {
                    left=true;
                }
            }else if (currentBearing>270&&currentBearing<360){
                temp=4;
                if (currentBearing<315){
                    left=false;
                }else {
                    left=true;
                }
            }
            if (left){
                if (temp==1){
                    bearing=90;
                }else if (temp==2){
                    bearing=180;
                }else if (temp==3){
                    bearing=270;
                }else if (temp==4){
                    bearing=360;
                }
            }else {
                if (temp==1){
                    bearing=0;
                }else if (temp==2){
                    bearing=90;
                }else if (temp==3){
                    bearing=180;
                }else if (temp==4){
                    bearing=270;
                }
            }
            return bearing;
        }
    }

    private void cameraSetup(LatLng latLng, float zoom, int tilt) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(360)
                .tilt(tilt)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mapboxMap.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1000, null);
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.inflate(R.menu.menu_popup_maptype);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pop_normal:
                        mapboxMap.setStyle(Style.TRAFFIC_DAY);
                        return true;
                    case R.id.pop_satellite:
                        mapboxMap.setStyle(Style.SATELLITE);
                        return true;
                    case R.id.pop_dark:
                        mapboxMap.setStyle(Style.DARK);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            resultLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            enableLocationComponent(mapboxMap.getStyle());
                        } else {
                            Toast.makeText(getContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void enableLocationComponent(Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {
            locationComponent = mapboxMap.getLocationComponent();
            LocationComponentActivationOptions options = LocationComponentActivationOptions.builder(getContext(), style).build();
            locationComponent.activateLocationComponent(options);
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationComponent.setLocationComponentEnabled(true);

            locationComponent.setCameraMode(CameraMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(getContext(), "Từ chối quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.changeTitle("Bản đồ");
        //bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(sheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //chiều cao mặc định
        bottomSheetBehavior.setPeekHeight(100);
        currentstate = 0;

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Do something when the bottom sheet is expanded
                    currentstate = 1;
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Do something when the bottom sheet is collapsed
                    currentstate = 0;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Do something while the bottom sheet is sliding
            }
        });
        oc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentstate == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
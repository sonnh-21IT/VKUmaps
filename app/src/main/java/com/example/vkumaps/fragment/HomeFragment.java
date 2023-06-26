package com.example.vkumaps.fragment;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.MarkerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.geojson.Feature;

import java.util.List;

public class HomeFragment extends Fragment implements OnMapReadyCallback, PermissionsListener , View.OnClickListener {
    private View rootView;
    private ImageView oc, zoomIn, zoomOut, rotate, mapType,myLocation;
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
    private TextView shareBtn,directionBtn;
    public static final LatLng VKU_LOCATION = new LatLng(15.9754993744594, 108.25236572354167);

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
        shareBtn=rootView.findViewById(R.id.btn_share);
        directionBtn=rootView.findViewById(R.id.btn_direction);
        myLocation=rootView.findViewById(R.id.map_my_location);

        shareBtn.setOnClickListener(this);
        directionBtn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        zoomIn.setOnClickListener(this);
        rotate.setOnClickListener(this);
        mapType.setOnClickListener(this);
        oc.setOnClickListener(this);
        myLocation.setOnClickListener(this);

        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapbox) {
        mapboxMap = mapbox;
        mapboxMap.setStyle(Style.LIGHT, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                //Hiển thị vị trí hiện tại
                enableLocationComponent(style);
                cameraSetup(VKU_LOCATION, 15.5f);

                if (currentstate == 1) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
           }
        });
        mapSetup();
        uiSettings();
    }

    private void mapSetup() {
        mapboxMap.setMaxZoomPreference(23);
        mapboxMap.setMinZoomPreference(15.5f);

        //Vẽ bản đồ

        //Add marker
        firestore.collection("Khu K")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId();
                                MarkerModel markerModel = document.toObject(MarkerModel.class);
                            }
                        }
                    }
                });

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(new LatLng(15.971851, 108.248515)) // Điểm góc trái trên
                .include(new LatLng(15.977745, 108.253451)) // Điểm góc phải dưới
                .build();

        mapboxMap.setLatLngBoundsForCameraTarget(bounds);

        // Kiểm tra vị trí hiện tại có thuộc phạm vi trên không
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            LatLng currentLocation= new LatLng(location.getLatitude(), location.getLongitude());
            if (!bounds.contains(currentLocation)) {
                // Hiển thị thông báo lên màn hình
                Toast.makeText(getContext(), "Vị trí hiện tại nằm ngoài phạm vi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uiSettings() {
//        map.getUiSettings().setZoomControlsEnabled(true);
//        mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
//            @Override
//            public boolean onMapClick(@NonNull LatLng point) {
//                if (currentstate == 1) {
////                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//                return true;
//            }
//        });
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

    private void cameraSetup(LatLng latLng, float zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(360)
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
                        mapboxMap.setStyle(Style.LIGHT);
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
        bottomSheetBehavior.setPeekHeight(0);
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

    @Override
    public void onClick(View view) {
        int idView=view.getId();
        switch (idView){
            case R.id.btn_direction:{
                Toast.makeText(requireContext(),"direction licked",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.btn_share:{
                Toast.makeText(requireContext(),"share licked",Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.zoom_out:{
                mapboxMap.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            }
            case R.id.zoom_in:{
                mapboxMap.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            }
            case R.id.rotate:{
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
                break;
            }
            case R.id.map_type:{
                showPopupMenu(view);
                break;
            }
            case R.id.btn:{
                if (currentstate == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            }
            case R.id.map_my_location:{
                // Điều chỉnh camera để hiển thị vị trí của người dùng
                CameraPosition position = new CameraPosition.Builder()
                        .target(new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                                locationComponent.getLastKnownLocation().getLongitude()))
                        .zoom(20)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000);
                break;
            }
            default:
                break;
        }
    }
}
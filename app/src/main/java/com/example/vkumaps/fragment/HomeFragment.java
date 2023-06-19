package com.example.vkumaps.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private ChangeFragmentListener listener;
    public static int currentstate;
    public static BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView oc;
    private FrameLayout sheet;
    private static final String TAG = "PERMISSION_TAG";
    private GoogleMap map;
    private LocationManager locationManager;
    public static final LatLng VKU_LOCATION = new LatLng(15.9754993744594, 108.25236572354167);
    private ActivityResultLauncher<String> resultLauncher;

    public HomeFragment(ChangeFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        oc = rootView.findViewById(R.id.btn);
        sheet = rootView.findViewById(R.id.sheet);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        requestPermission();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_view, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (!(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            map.setMyLocationEnabled(true);
                        }
                    }
                }
        );
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        mapSetup();
        uiSettings();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        requestPermission();
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
        oc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentstate == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    currentstate = 1;
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    currentstate = 0;
                }
            }
        });
    }

    private void uiSettings() {
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    private void mapSetup() {
        map.clear();
        map.setPadding(0, 0, 0, 550);
        cameraSetup();
        markerSetup();
        //vị trí hiện tại
        if (!(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            map.setMyLocationEnabled(true);
        }
        //hiển thị maps
        map.setTrafficEnabled(true);
        // Cài đặt mức zoom tối thiểu là 10
        map.setMinZoomPreference(15);

        // Cài đặt mức zoom tối đa là 18
        map.setMaxZoomPreference(20);

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(15.972769, 108.251301), // Tọa độ góc tây nam của hình chữ nhật
                new LatLng(15.977739, 108.253532

                )  // Tọa độ góc đông bắc của hình chữ nhật
        );
        map.setLatLngBoundsForCameraTarget(bounds);
//        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.mymapstyle));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    private void cameraSetup() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(VKU_LOCATION)      // Sets the center of the map to Mountain View
                .zoom(16)                   // Sets the zoom
                .bearing(270)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void markerSetup() {
        map.addMarker(new MarkerOptions()
                .position(VKU_LOCATION)
                .title("Trường Đại học Công nghệ Thông tin và Truyên thông Viêt-Hàn")
                .snippet("Trường Đại học công lập đào tạo sâu về công nghệ thông tin và kinh tế số duy nhất tại miền trung"));
    }


    private BitmapDescriptor bitmapDescriptorFromVectorForMarker(Context context, int vectorId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            resultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
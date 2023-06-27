package com.example.vkumaps.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.listener.SharePlaceListener;
import com.example.vkumaps.models.MarkerModel;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private ChangeFragmentListener listener;
    private SharePlaceListener sharePlaceListener;
    public static int currentstate;
    public static BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView oc, zoomIn, zoomOut, rotate, mapType,imgPlace;
    private TextView shareBtn,directionBtn,titlePlace,desPlace;
    private FrameLayout sheet;
    private static final String TAG = "PERMISSION_TAG";
    private GoogleMap map;
    private LocationManager locationManager;
    private FirebaseFirestore firestore;
    private KmlLayer kmlLayer;
    private List<Marker> markerList=new ArrayList<>();
    private static final LatLngBounds allowedArea = new LatLngBounds(
            new LatLng(15.971851, 108.248515), // Tọa độ góc tây nam của hình chữ nhật
            new LatLng(15.977745, 108.253451)  // Tọa độ góc đông bắc của hình chữ nhật
    );
    public static final LatLng VKU_LOCATION = new LatLng(15.9754993744594, 108.25236572354167);
    private ActivityResultLauncher<String> resultLauncher;
    private SupportMapFragment mapFragment;
    private Marker shareLocation;

    public HomeFragment(ChangeFragmentListener listener,SharePlaceListener sharePlaceListener) {
        this.listener = listener;
        this.sharePlaceListener=sharePlaceListener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        oc = rootView.findViewById(R.id.btn);
        zoomOut = rootView.findViewById(R.id.zoom_out);
        zoomIn = rootView.findViewById(R.id.zoom_in);
        rotate = rootView.findViewById(R.id.rotate);
        sheet = rootView.findViewById(R.id.sheet);
        imgPlace = rootView.findViewById(R.id.img_place);
        directionBtn=rootView.findViewById(R.id.btn_direction);
        desPlace=rootView.findViewById(R.id.des_place);
        titlePlace=rootView.findViewById(R.id.title_place);
        shareBtn=rootView.findViewById(R.id.btn_share);

        firestore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();

        mapType = rootView.findViewById(R.id.map_type);
        mapFragment = SupportMapFragment.newInstance();
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
                            // Disable the My Location layer on the map
//                        map.setMyLocationEnabled(false);
                            //vị trí hiện tại
                        }
                    }
                }
        );
        requestPermission();

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
//                                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    cameraSetup(latLng, 20, 0);
                    if (currentstate == 1) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                }
                return true;
            }
        });
        try {
            mapSetup();
        } catch (XmlPullParserException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePlaceListener.onSharePlace(shareLocation);
            }
        });
    }

    private void uiSettings() {
//        map.getUiSettings().setZoomControlsEnabled(true);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        if (!(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null && allowedArea.contains(new LatLng(location.getLatitude(), location.getLongitude()))) {
                // The user's current location is within the allowed area
                map.setMyLocationEnabled(true);
            } else {
                // The user's current location is outside the allowed area
                map.setMyLocationEnabled(false);
            }
            map.setMyLocationEnabled(true);
        }
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                if (currentstate == 1) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo một đối tượng CameraPosition mới với hướng mong muốn
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(map.getCameraPosition().target) // Giữ nguyên tọa độ trung tâm của camera
                        .zoom(map.getCameraPosition().zoom) // Giữ nguyên mức zoom của camera
                        .tilt(0) // Điều chỉnh góc nghiêng của camera
//                        .tilt(map.getCameraPosition().tilt) // Điều chỉnh góc nghiêng của camera
                        .bearing(calculateBearing()) // Điều chỉnh hướng của camera
                        .build();
                // Sử dụng animateCamera để chuyển đổi hướng của camera
                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
        mapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(view);
            }
        });
    }

    private float calculateBearing() {
        float currentBearing = map.getCameraPosition().bearing;
        float bearing = 0;
        float temp = 1;
        boolean left = true;
        if (currentBearing == 360) {
            return 90;
        }else if (currentBearing == 90 || currentBearing == 0 || currentBearing == 180 || currentBearing == 270) {
            return currentBearing + 90;
        } else {
            if (currentBearing > 0 && currentBearing < 90) {
                temp = 1;
                if (currentBearing < 45) {
                    left = false;
                } else {
                    left = true;
                }
            } else if (currentBearing > 90 && currentBearing < 180) {
                temp = 2;
                if (currentBearing < 135) {
                    left = false;
                } else {
                    left = true;
                }
            } else if (currentBearing > 180 && currentBearing < 270) {
                temp = 3;
                if (currentBearing < 225) {
                    left = false;
                } else {
                    left = true;
                }
            } else if (currentBearing > 270 && currentBearing < 360) {
                temp = 4;
                if (currentBearing < 315) {
                    left = false;
                } else {
                    left = true;
                }
            }
            if (left) {
                if (temp == 1) {
                    bearing = 90;
                } else if (temp == 2) {
                    bearing = 180;
                } else if (temp == 3) {
                    bearing = 270;
                } else if (temp == 4) {
                    bearing = 360;
                }
            } else {
                if (temp == 1) {
                    bearing = 0;
                } else if (temp == 2) {
                    bearing = 90;
                } else if (temp == 3) {
                    bearing = 180;
                } else if (temp == 4) {
                    bearing = 270;
                }
            }
            return bearing;
        }
    }

    private void mapSetup() throws XmlPullParserException, IOException {
        cameraSetup(VKU_LOCATION, 16.5f, 30);

        map.setTrafficEnabled(false);

        //Vẽ các khu vực lên maps
        kmlLayer = new KmlLayer(map, R.raw.vkustudent, getContext());
        kmlLayer.addLayerToMap();

        if (kmlLayer.isLayerOnMap()) {
            // Duyệt qua các đối tượng KmlPlacemark trong lớp KML
            for (KmlPlacemark placemark : kmlLayer.getPlacemarks()) {

            }
        } else {
            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
        }

        kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geometry = feature.getGeometry();
                if (geometry != null) {
                    //khi click lên đa giác
                    if (currentstate == 1) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }else {
                        map.animateCamera(CameraUpdateFactory.zoomTo(17));
                    }
                }
            }
        });

        //Hiển thị các maker
        firestore.collection("Khu K")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId();
                                MarkerModel markerModel = document.toObject(MarkerModel.class);
                                addMarker(new LatLng(markerModel.getGeopoint().getLatitude(), markerModel.getGeopoint().getLongitude()),
                                        markerModel.getIconURL(), name);
                                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(@NonNull Marker marker) {
                                        if (marker!=null){
                                            Toast.makeText(requireContext(), "marker click", Toast.LENGTH_SHORT).show();
                                            cameraSetup(marker.getPosition(),20,0);
                                            titlePlace.setText(marker.getTitle());
                                            desPlace.setText(marker.getSnippet());
                                            shareLocation=marker;
                                            if (currentstate==0){
                                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                            }
                                            return true;
                                        }
                                        return false;
                                    }
                                });
                            }
                        }
                    }
                });
        firestore.collection("Khu V")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = document.getId();
                                MarkerModel markerModel = document.toObject(MarkerModel.class);
                                addMarker(new LatLng(markerModel.getGeopoint().getLatitude(), markerModel.getGeopoint().getLongitude()),
                                        markerModel.getIconURL(), name);
                            }
                            map.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                @Override
                                public void onCameraIdle() {
                                    // Get the current camera zoom level
                                    float zoomLevel = map.getCameraPosition().zoom;
                                    if (zoomLevel < 17) {
                                        // Hide all markers if the zoom level is less than 12
                                        for (Marker marker : markerList) {
                                            marker.setVisible(false);
                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }
                                    } else {
                                        // Show all markers if the zoom level is 12 or greater
                                        for (Marker marker : markerList) {
                                            marker.setVisible(true);
                                        }
                                    }
                                }
                            });
                        }
                    }
                });

        map.setMaxZoomPreference(23);
        map.setMinZoomPreference(16.5f);
        map.setLatLngBoundsForCameraTarget(allowedArea);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mymapstyle));
    }

    //add marker
    private void addMarker(LatLng latMarker, String imageUrl, String name) {
        Glide.with(this)
                .load(imageUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        // This is the Drawable loaded from the URL
                        // You can use the drawable here, for example, setting it as the icon for a marker
                        BitmapDescriptor bitmapDescriptor = getMarkerIconFromDrawable(drawable);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latMarker)
                                .title(name)
                                .icon(bitmapDescriptor);
                        Marker marker = map.addMarker(markerOptions);
                        marker.setVisible(false);
                        markerList.add(marker);
//                        for (Marker markerItem : markerList) {
//                            markerItem.setVisible(false);
//                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle any cleanup or placeholder Drawable here
                    }
                });
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Bitmap bitmap;

        if (drawable instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        }

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void cameraSetup(LatLng latLng, float zoom, int tilt) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
                .bearing(map.getCameraPosition().bearing)
                .tilt(tilt)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1000, null);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), v);
        popupMenu.inflate(R.menu.menu_popup_maptype);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.pop_normal:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        return true;
                    case R.id.pop_satellite:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
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
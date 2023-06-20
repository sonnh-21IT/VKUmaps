package com.example.vkumaps.fragment;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.vkumaps.R;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.google.android.gms.maps.CameraUpdate;
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
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;
import com.google.maps.android.data.kml.KmlPoint;
import com.google.maps.android.data.kml.KmlPolygon;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;


public class HomeFragment extends Fragment implements OnMapReadyCallback{
    private ChangeFragmentListener listener;
    public static int currentstate;
    public static BottomSheetBehavior<View> bottomSheetBehavior;
    private ImageView oc, zoomIn, zoomOut;
    private FrameLayout sheet;
    private static final String TAG = "PERMISSION_TAG";
    private GoogleMap map;
    private LocationManager locationManager;
    private KmlLayer kmlLayer;
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
        zoomOut = rootView.findViewById(R.id.zoom_out);
        zoomIn = rootView.findViewById(R.id.zoom_in);
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
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
//                                    LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
//                    map.animateCamera(cameraUpdate);
                    cameraSetup(latLng, 20, 0);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    currentstate = 0;
                }
                return true;
            }
        });
        try {
            mapSetup();
            kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
                @Override
                public void onFeatureClick(Feature feature) {
                    Geometry geometry = feature.getGeometry();
                    if (geometry != null && geometry.getGeometryType().equals("Polygon")) {
                        // Bỏ qua sự kiện click trên đa giác (polygon)
                        return;
                    } else if (geometry.getGeometryType().equals("Point")) {
                        KmlPoint kmlPoint = (KmlPoint) geometry;
                        LatLng pointCoordinates = kmlPoint.getGeometryObject();
                        LatLngBounds bounds = new LatLngBounds(
                                new LatLng(15.975398, 108.251979), // Tọa độ góc tây nam của hình chữ nhật
                                new LatLng(15.975481, 108.252580)  // Tọa độ góc đông bắc của hình chữ nhật
                        );
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        currentstate = 1;
//                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointCoordinates,20));
                        cameraSetup(pointCoordinates,20,0);
                    }
                }
            });
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
//        map.getUiSettings().setZoomControlsEnabled(true);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        map.getUiSettings().setMapToolbarEnabled(false);
    }

    private void mapSetup() throws XmlPullParserException, IOException {
        cameraSetup(VKU_LOCATION, 16.5f, 30);
        //vị trí hiện tại
        if (!(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            map.setMyLocationEnabled(true);
        }
        //hiển thị maps
        map.setTrafficEnabled(true);


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

        map.setMaxZoomPreference(23);
        map.setMinZoomPreference(16.5f);
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(15.971851, 108.248515), // Tọa độ góc tây nam của hình chữ nhật
                new LatLng(15.977745, 108.253451)  // Tọa độ góc đông bắc của hình chữ nhật
        );
        map.setLatLngBoundsForCameraTarget(bounds);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mymapstyle));

    }

    private void cameraSetup(LatLng latLng, float zoom, int tilt) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to Mountain View
                .zoom(zoom)                   // Sets the zoom
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

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
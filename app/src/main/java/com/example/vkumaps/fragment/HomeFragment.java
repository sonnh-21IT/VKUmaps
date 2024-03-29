package com.example.vkumaps.fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.vkumaps.R;
import com.example.vkumaps.dialog.FinishDialog;
import com.example.vkumaps.dialog.SuccessDialog;
import com.example.vkumaps.listener.BottomSheetListener;
import com.example.vkumaps.listener.ChangeFragmentListener;
import com.example.vkumaps.models.EdgeTemp;
import com.example.vkumaps.models.Graph;
import com.example.vkumaps.models.MarkerModel;
import com.example.vkumaps.models.PointModel;
import com.example.vkumaps.models.ShortestPathFinder;
import com.example.vkumaps.models.ShortestPathResult;
import com.example.vkumaps.models.Vertex;
import com.example.vkumaps.models.WeightModel;
import com.example.vkumaps.toasts.CustomToast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.Geometry;
import com.google.maps.android.data.kml.KmlLayer;
import com.google.maps.android.data.kml.KmlPlacemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener, PopupMenu.OnMenuItemClickListener, GoogleMap.OnMyLocationButtonClickListener, FinishDialog.DialogFinishListener {
    private ChangeFragmentListener changeTitleListener;
    private BottomSheetListener sharePlaceListener;
    public static int currentState;
    public static BottomSheetBehavior<View> bottomSheetBehavior;
    private TextView titlePlace;
    private ImageView imgPlace;
    private LocationManager locationManager;
    private FirebaseFirestore firestore;
    private KmlLayer kmlLayer;
    private final List<Marker> markerList = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap map;
    private String namePlace;
    private ActivityResultLauncher<String> resultLauncher;
    private float bearingStart;
    private boolean isFinishDir = true;
    private Marker markerStart, markerTarget, markerEnd, markerClicked, markerTemp;
    private static final LatLngBounds allowedArea = new LatLngBounds(
            new LatLng(15.971851, 108.248515), // Tọa độ góc tây nam của hình chữ nhật
            new LatLng(15.977745, 108.253451)  // Tọa độ góc đông bắc của hình chữ nhật
    );
    public static final LatLng VKU_LOCATION = new LatLng(15.9754993744594, 108.25236572354167);
    private Marker shareLocation;
    private View rootView;
    private CardView viewerStart, viewerFinish;
    private LatLng startLatLng;
    private Polyline polylineBg, polyline;
    private ValueAnimator blinkAnimator;
    private FinishDialog dialog;

    public HomeFragment() {
    }

    public HomeFragment(ChangeFragmentListener changeTitleListener, BottomSheetListener sharePlaceListener) {
        this.changeTitleListener = changeTitleListener;
        this.sharePlaceListener = sharePlaceListener;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews();
        dialog = new FinishDialog(requireContext(), this);
        firestore = FirebaseFirestore.getInstance();
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_view, mapFragment)
                .commit();
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        if (!(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(TaskExecutors.MAIN_THREAD, location -> {
                                        if (location != null) {
                                            LatLng area = new LatLng(location.getLatitude(), location.getLongitude());
                                            boolean isWithinArea = allowedArea.contains(area);
                                            map.setMyLocationEnabled(isWithinArea);
                                        }
                                    });
                        }
                    }
                }
        );
    }

    private void initializeViews() {

        ImageView oc = rootView.findViewById(R.id.btn);
        ImageView zoomOut = rootView.findViewById(R.id.zoom_out);
        ImageView zoomIn = rootView.findViewById(R.id.zoom_in);
        ImageView rotate = rootView.findViewById(R.id.rotate);
        FrameLayout sheet = rootView.findViewById(R.id.sheet);
        imgPlace = rootView.findViewById(R.id.img_place);
        TextView directionBtn = rootView.findViewById(R.id.btn_direction);
        titlePlace = rootView.findViewById(R.id.title_place);
        TextView shareBtn = rootView.findViewById(R.id.btn_share);
        ImageView mapType = rootView.findViewById(R.id.map_type);
        TextView startBtn = rootView.findViewById(R.id.start_dir);
        TextView finishBtn = rootView.findViewById(R.id.finish_dir);
        viewerStart = rootView.findViewById(R.id.viewer_start);
        viewerStart.setVisibility(View.GONE);
        viewerFinish = rootView.findViewById(R.id.viewer_finish);
        viewerFinish.setVisibility(View.GONE);

        shareBtn.setOnClickListener(this);
        directionBtn.setOnClickListener(this);
        zoomOut.setOnClickListener(this);
        zoomIn.setOnClickListener(this);
        rotate.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        finishBtn.setOnClickListener(this);
        mapType.setOnClickListener(this);
        oc.setOnClickListener(this);
        titlePlace.setOnClickListener(this);
        imgPlace.setOnClickListener(this);

        //bottom sheet
        bottomSheetBehavior = BottomSheetBehavior.from(sheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //chiều cao mặc định
        bottomSheetBehavior.setPeekHeight(0);
        currentState = 0;

        // Khởi tạo FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        Bundle bundle = getArguments();
        if (bundle != null) {
            MarkerModel marker = bundle.getParcelable("marker");
            if (marker != null) {
                String name = bundle.getString("name");
                namePlace = name;
                LatLng position = new LatLng(marker.getGeoPoint().getLatitude(), marker.getGeoPoint().getLongitude());
                titlePlace.setText(name);
                Glide.with(requireContext()).load(marker.getImgURL()).into(imgPlace);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                cameraSetup(position, 20f, 0);
            }
            if (bundle.getString("startPoint") != null && bundle.getString("endPoint") != null) {
                path(bundle.getString("startPoint"), bundle.getString("endPoint"));
            }
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(VKU_LOCATION.latitude, VKU_LOCATION.longitude)));
            cameraSetup(new LatLng(VKU_LOCATION.latitude, VKU_LOCATION.longitude), 16.5f, 30);
        }
        googleMap.setOnMyLocationButtonClickListener(this);
        try {
            mapSetup();
            kmlLayer.setOnFeatureClickListener(feature -> {
                Geometry geometry = feature.getGeometry();
                if (geometry != null) {
                    //khi click lên đa giác
                    if (currentState == 1) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                    }
                }
            });
        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
        uiSettings();
    }

    public void path(String startVertexLabel, String targetVertexLabel) {
        // Tạo đồ thị
        Graph graph = new Graph();

        List<Vertex> vertexList = new ArrayList<>();
        List<EdgeTemp> list = new ArrayList<>();

        Task<QuerySnapshot> pointTask = firestore.collection("point").get();
        Task<QuerySnapshot> weightTask = firestore.collection("weight").get();
        Tasks.whenAllComplete(pointTask, weightTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot pointSnapshot = pointTask.getResult();
                        QuerySnapshot weightSnapshot = weightTask.getResult();

                        for (QueryDocumentSnapshot document : pointSnapshot) {
                            String name = document.getId().trim();
                            PointModel pointModel = document.toObject(PointModel.class);
                            vertexList.add(new Vertex(name, new LatLng(pointModel.getGeo().getLatitude(), pointModel.getGeo().getLongitude())));
                        }

                        List<Vertex> vertices = new ArrayList<>();
                        for (Vertex item : vertexList) {
                            vertices.add(graph.addVertex(item));
                        }

                        for (QueryDocumentSnapshot document : weightSnapshot) {
                            WeightModel weightModel = document.toObject(WeightModel.class);
                            list.add(new EdgeTemp(weightModel.getStart().trim(), weightModel.getEnd().trim(), weightModel.getWeight()));
                        }

                        for (EdgeTemp eItem : list) {
                            Vertex a = null, b = null;
                            for (Vertex item : vertices) {
                                if (eItem.getA().equals(item.getLabel())) {
                                    a = item;
                                }
                            }
                            for (Vertex item : vertices) {
                                if (eItem.getB().equals(item.getLabel())) {
                                    b = item;
                                }
                            }
                            if (a != null && b != null) {
                                graph.addEdge(a, b, eItem.getWeight());
                            }
                        }
                        // Tiếp tục thực thi đoạn mã ở dưới sau khi cả hai tác vụ Firestore hoàn thành
                        continueExecution(graph, startVertexLabel, targetVertexLabel);
                    } else {
                        CustomToast customToast = CustomToast.makeText(requireContext(), "Lỗi tải dữ liệu.", Toast.LENGTH_SHORT);
                        customToast.show();
                    }
                });
    }

    private void continueExecution(Graph graph, String startVertexLabel, String targetVertexLabel) {
        // Tạo đối tượng DijkstraShortestPath
        ShortestPathFinder dijkstra = new ShortestPathFinder(graph);
        Vertex startVertex = null, targetVertex = null;
        for (Vertex item : graph.getVertices()) {
            if (item.getLabel().equals(startVertexLabel)) {
                startVertex = item;
            }
            if (item.getLabel().equals(targetVertexLabel)) {
                targetVertex = item;
            }
        }
        if (startVertex != null && targetVertex != null) {
            // Tìm đường đi ngắn nhất từ đỉnh bắt đầu đến đỉnh đích
            ShortestPathResult result = dijkstra.findShortestPath(startVertex, targetVertex);
            // Lấy đường đi ngắn nhất và khoảng cách cuối cùng
            List<Vertex> shortestPath = result.getPath();
            int shortestDistance = result.getDistance();

            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMarkerStart = getResources().getDrawable(R.drawable.ic_circle_white, requireActivity().getTheme());
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMarkerTarget = getResources().getDrawable(R.drawable.ic_circle_gray, requireActivity().getTheme());
            @SuppressLint("UseCompatLoadingForDrawables") Drawable drawableMarkerEnd = getResources().getDrawable(R.drawable.marker, requireActivity().getTheme());
            MarkerOptions markerOptionsStart = new MarkerOptions()
                    .position(shortestPath.get(0).getPosition()).anchor(0.5f, 0.5f).icon(getMarkerIconFromDrawable(drawableMarkerStart)).draggable(false).title("noneClick");
            MarkerOptions markerOptionsTarget = new MarkerOptions()
                    .position(shortestPath.get(shortestPath.size() - 1).getPosition()).anchor(0.5f, 0.5f).icon(getMarkerIconFromDrawable(drawableMarkerTarget)).draggable(false).title("noneClick");
            MarkerOptions markerOptionsEnd = new MarkerOptions()
                    .position(shortestPath.get(shortestPath.size() - 1).getPosition()).draggable(false).title("noneClick").icon(getMarkerIconFromDrawable(drawableMarkerEnd));
            markerOptionsEnd.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

            markerStart = map.addMarker(markerOptionsStart);
            markerTarget = map.addMarker(markerOptionsTarget);
            markerEnd = map.addMarker(markerOptionsEnd);
            PolylineOptions po = new PolylineOptions();
            po.color(Color.BLACK).width(10);
            po.startCap(new RoundCap());
            po.endCap(new RoundCap());
            // In đường đi ngắn nhất và khoảng cách cuối cùng
            for (Vertex vertex : shortestPath) {
                po.add(vertex.getPosition());
            }
            polylineBg = map.addPolyline(po);
            po.color(Color.parseColor("#4285F4")).width(8);
            showDirection(markerStart, markerTarget, po);
            polyline = map.addPolyline(po);
        } else {
            CustomToast customToast = CustomToast.makeText(requireContext(), "Không tìm thấy liên kết giữa hai vị trí này.", Toast.LENGTH_SHORT);
            customToast.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeTitleListener.changeTitle("Bản đồ");
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // Do something when the bottom sheet is expanded
                    currentState = 1;
                } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    // Do something when the bottom sheet is collapsed
                    currentState = 0;
                    cancelMarkerBlink();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Do something while the bottom sheet is sliding
            }
        });
    }

    private void uiSettings() {
        if (!(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
//                            map.setMyLocationEnabled(true);
            // Lấy vị trí cuối cùng đã biết
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng area = new LatLng(location.getLatitude(), location.getLongitude());
                            boolean isWithinArea = allowedArea.contains(area);
                            map.setMyLocationEnabled(true);
                        }
                    });
        }else {
            requestPermission();
        }
        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(this);
    }

    private float calculateBearing() {
        float currentBearing = map.getCameraPosition().bearing;
        float bearing;
        float temp = 1;
        boolean left = true;
        if (currentBearing == 360) {
            return 90;
        } else if (currentBearing == 90 || currentBearing == 0 || currentBearing == 180 || currentBearing == 270) {
            return currentBearing + 90;
        } else {
            if (currentBearing > 0 && currentBearing < 90) {
                left = !(currentBearing < 45);
            } else if (currentBearing > 90 && currentBearing < 180) {
                temp = 2;
                left = !(currentBearing < 135);
            } else if (currentBearing > 180 && currentBearing < 270) {
                temp = 3;
                left = !(currentBearing < 225);
            } else if (currentBearing > 270 && currentBearing < 360) {
                temp = 4;
                left = !(currentBearing < 315);
            }
            if (left) {
                if (temp == 1) {
                    bearing = 90;
                } else if (temp == 2) {
                    bearing = 180;
                } else if (temp == 3) {
                    bearing = 270;
                } else {
                    bearing = 360;
                }
            } else {
                if (temp == 1) {
                    bearing = 0;
                } else if (temp == 2) {
                    bearing = 90;
                } else if (temp == 3) {
                    bearing = 180;
                } else {
                    bearing = 270;
                }
            }
            return bearing;
        }
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void mapSetup() throws XmlPullParserException, IOException {

        map.setTrafficEnabled(false);

        //Vẽ các khu vực lên maps
        kmlLayer = new KmlLayer(map, R.raw.vkustudent, requireContext());
        kmlLayer.addLayerToMap();

        if (kmlLayer.isLayerOnMap()) {
            // Duyệt qua các đối tượng KmlPlacemark trong lớp KML
            for (KmlPlacemark placemark : kmlLayer.getPlacemarks()) {

            }
        } else {
            CustomToast customToast = CustomToast.makeText(requireContext(), "Lỗi tải bản đồ.", Toast.LENGTH_SHORT);
            customToast.show();
        }

        kmlLayer.setOnFeatureClickListener(new KmlLayer.OnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                Geometry geometry = feature.getGeometry();
                if (geometry != null) {
                    //khi click lên đa giác
                    if (currentState == 1) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        map.animateCamera(CameraUpdateFactory.zoomTo(17));
                    }
                }
            }
        });

        // Thêm chữ vào bản đồ
//        GroundOverlayOptions overlayOptions = new GroundOverlayOptions()
//                .position(VKU_LOCATION, 5f, 5f) // Điều chỉnh kích thước theo nhu cầu của bạn
//                .image(createTextOverlay("A110", 20, Color.BLACK, Typeface.DEFAULT));
//        GroundOverlay overlay = map.addGroundOverlay(overlayOptions);
//        overlay.setBearing(-90);
//        overlay.setZIndex(2);

        //Hiển thị các maker
        firestore.collection("Marker")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String name = document.getId();
                            MarkerModel markerModel = document.toObject(MarkerModel.class);
                            if (markerModel.getGeoPoint() != null && markerModel.getIconURL() != null && markerModel.getImgURL() != null
                                    && markerModel.getSubname() != null) {
                                addMarker(new LatLng(markerModel.getGeoPoint().getLatitude(), markerModel.getGeoPoint().getLongitude()),
                                        markerModel.getIconURL(), name, markerModel.getImgURL());
                            }
                            map.setOnMarkerClickListener(marker -> {
                                if (!isFinishDir) {
                                    CustomToast customToast = CustomToast.makeText(requireContext(), "Hãy hoàn thành chỉ dẫn này.", Toast.LENGTH_SHORT);
                                    customToast.show();
                                } else {
                                    cancelMarkerBlink();
                                    markerClicked = marker;
                                    if (Objects.requireNonNull(marker.getTitle()).equalsIgnoreCase("noneClick")) {
                                        cameraSetup(marker.getPosition(), 20, 0);
                                        if (currentState == 1) {
                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        }
                                        return true;
                                    } else {
                                        animateMarkerBlink(marker);
                                        cameraSetup(marker.getPosition(), 20, 0);
                                        titlePlace.setText(marker.getTitle());
                                        Glide.with(requireContext()).load(marker.getSnippet()).into(imgPlace);
                                        shareLocation = marker;
                                        namePlace = marker.getTitle();
                                        if (currentState == 0) {
                                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                                        }
                                    }
                                    return true;
                                }
                                return true;
                            });
                            map.setOnCameraIdleListener(() -> {
                                // Get the current camera zoom level
                                float zoomLevel = map.getCameraPosition().zoom;
                                if (zoomLevel < 17) {
                                    if (markerClicked != null) {
                                        if (Objects.requireNonNull(markerClicked.getTitle()).equalsIgnoreCase("noneClick")) {
                                            cancelMarkerBlink();

                                        }
                                    }
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
                            });
                        }
                    } else {
                        CustomToast customToast = CustomToast.makeText(requireContext(), "Lỗi tải dữ liệu.", Toast.LENGTH_SHORT);
                        customToast.show();
                    }
                });

        map.setMaxZoomPreference(23);
        map.setMinZoomPreference(16.5f);
        map.setLatLngBoundsForCameraTarget(allowedArea);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mymapstyle));
    }

    private BitmapDescriptor createTextOverlay(String text, float fontSize, int textColor, Typeface typeface) {
        Paint paint = new Paint();
        paint.setTextSize(fontSize);
        paint.setColor(textColor);
        paint.setTypeface(typeface);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        float textWidth = paint.measureText(text);
        float textHeight = paint.descent() - paint.ascent();

        Bitmap bitmap = Bitmap.createBitmap((int) textWidth, (int) textHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, textWidth / 2, textHeight - paint.descent(), paint);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //add marker
    private void addMarker(LatLng latMarker, String iconUrl, String name, String imgUrl) {
        Glide.with(this)
                .load(iconUrl)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
                        // This is the Drawable loaded from the URL
                        // You can use the drawable here, for example, setting it as the icon for a marker
                        BitmapDescriptor bitmapDescriptor = getMarkerIconFromDrawable(drawable);
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(latMarker)
                                .title(name)
                                .snippet(imgUrl)
                                .icon(bitmapDescriptor);
                        Marker marker = map.addMarker(markerOptions);
                        assert marker != null;
                        marker.setVisible(false);
                        markerList.add(marker);
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
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        resultLauncher.unregister();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resultLauncher.unregister();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn: {
                if (currentState == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            }
            case R.id.btn_direction: {
                currentState = 0;
                changeTitleListener.onDirectionClick(namePlace);
                break;
            }
            case R.id.btn_share: {
                sharePlaceListener.onSharePlace(shareLocation);
                break;
            }
            case R.id.zoom_in: {
                map.animateCamera(CameraUpdateFactory.zoomIn());
                break;
            }
            case R.id.zoom_out: {
                map.animateCamera(CameraUpdateFactory.zoomOut());
                break;
            }
            case R.id.rotate: {
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
                break;
            }
            case R.id.map_type: {
                showPopupMenu(view);
                break;
            }
            case R.id.start_dir: {
                if (startLatLng != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(startLatLng)
                            .zoom(20)
                            .bearing(bearingStart)
                            .build();
                    CameraUpdate rotateCamera = CameraUpdateFactory.newCameraPosition(cameraPosition);
                    map.animateCamera(rotateCamera);
                }
                break;
            }
            case R.id.finish_dir: {
                dialog.showDialog();
                break;
            }
        }
    }

    public void showDirection(Marker markerStart, Marker markerTarget, PolylineOptions po) {
        isFinishDir = false;
        if (po.getPoints().size() <= 1) {
            cameraSetup(new LatLng(po.getPoints().get(0).latitude, po.getPoints().get(0).longitude), 20, 0);
        } else {
            viewerStart.setVisibility(View.VISIBLE);
            viewerFinish.setVisibility(View.VISIBLE);

            LatLng firstPoint = po.getPoints().get(0);
            LatLng secondPoint = po.getPoints().get(1);
            startLatLng = po.getPoints().get(0);

            bearingStart = (float) Math.toDegrees(Math.atan2(secondPoint.longitude - firstPoint.longitude, secondPoint.latitude - firstPoint.latitude));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(markerStart.getPosition());
            builder.include(markerTarget.getPosition());
            LatLngBounds bounds = builder.build();

            int padding = 150; // Khoảng cách từ biên của màn hình đến giới hạn
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            map.animateCamera(cameraUpdate);
//            map.animateCamera(cameraUpdate, new GoogleMap.CancelableCallback() {
//                @Override
//                public void onFinish() {
//                    CameraPosition cameraPosition = new CameraPosition.Builder()
//                            .target(po.getPoints().get(0))
//                            .zoom(map.getCameraPosition().zoom)
//                            .bearing(bearingStart)
//                            .build();
//                    CameraUpdate rotateCamera = CameraUpdateFactory.newCameraPosition(cameraPosition);
//                    map.animateCamera(rotateCamera);
//                }
//
//                @Override
//                public void onCancel() {
//                    // Xử lý khi hủy animation (nếu cần thiết)
//                }
//            });
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (currentState == 1) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            cameraSetup(map.getCameraPosition().target, 17, 0);
        }
    }

    @SuppressLint("NonConstantResourceId")
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

    @Override
    public boolean onMyLocationButtonClick() {
        if (!(ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            cameraSetup(latLng, 20, 0);
            if (currentState == 1) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return true;
    }

    private void animateMarkerBlink(final Marker marker) {
        final long duration = 1000; // Thời gian mỗi chu kỳ nhấp nháy (milliseconds)

        blinkAnimator = ValueAnimator.ofFloat(1f, 0.5f, 1f); // Tạo animator với các giá trị alpha tương ứng
        blinkAnimator.setDuration(duration);
        blinkAnimator.setRepeatCount(ValueAnimator.INFINITE); // Lặp vô hạn
        blinkAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                marker.setAlpha(alpha);
            }
        });
        markerTemp = markerClicked;
        blinkAnimator.start();
    }

    //    private void animateMarkerBounceInfinite(final Marker marker) {
//        final long duration = 1500; // Thời gian mỗi chu kỳ nhấp nhô (milliseconds)
//        final double bounceHeight = 0.003; // Độ cao của hiệu ứng nhấp nhô (từ 0 đến 1)
//        final LatLng originalPosition = marker.getPosition();
//
//        // Tính toán tọa độ mới cho marker
//        final double offsetY = bounceHeight * 0.01; // Điều chỉnh giá trị offset cho khoảng cách nhấp nháy nhỏ hơn
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                marker.setRotation(map.getCameraPosition().bearing);
//                long elapsed = SystemClock.uptimeMillis() % duration;
//                float t = (float) elapsed / duration;
//
//                // Áp dụng hiệu ứng nhấp nhô lên vị trí dọc theo trục y
//                double offset = Math.sin(t * Math.PI * 2) * offsetY;
//                LatLng newPosition = new LatLng(originalPosition.latitude + offset, originalPosition.longitude);
//                marker.setPosition(newPosition);
////                // Tiếp tục chạy hiệu ứng vô hạn
//                handler.postDelayed(this, 16);
//            }
//        });
//    }
    private void cancelMarkerBlink() {
        if (blinkAnimator != null) {
            blinkAnimator.cancel();
            if (markerTemp != null)
                markerTemp.setAlpha(1);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onFinish() {
        if (markerEnd != null && markerStart != null && markerTarget != null) {
            markerStart.remove();
            markerEnd.remove();
            markerTarget.remove();
            polyline.remove();
            polylineBg.remove();
            viewerFinish.setVisibility(View.GONE);
            viewerStart.setVisibility(View.GONE);
            dialog.close();
            isFinishDir = true;
        }
    }
}
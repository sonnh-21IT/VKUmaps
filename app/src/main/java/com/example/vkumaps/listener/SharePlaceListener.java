package com.example.vkumaps.listener;

import android.location.Location;

import com.example.vkumaps.models.News;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public interface SharePlaceListener {
    void onSharePlace(Marker marker);
}

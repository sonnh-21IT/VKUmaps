package com.example.vkumaps.listener;

import com.google.android.gms.maps.model.Marker;

public interface SharePlaceListener {
    default void onSharePlace(Marker marker) {

    }
}

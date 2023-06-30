package com.example.vkumaps.listener;

import com.example.vkumaps.models.MarkerModel;

public interface ChangeFragmentListener {
    void changeTitle(String title);
    void onNestedClick(MarkerModel markerModel,String name);
}

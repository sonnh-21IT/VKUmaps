package com.example.vkumaps.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;

public class MarkerModel implements Parcelable {
    String iconURL;
    GeoPoint geopoint;
    String imgURL;
    String subname;

    public MarkerModel() {
    }

    public MarkerModel(String iconURL, GeoPoint geopoint, String imgURL, String subname) {
        this.iconURL = iconURL;
        this.geopoint = geopoint;
        this.imgURL = imgURL;
        this.subname = subname;
    }

    protected MarkerModel(Parcel in) {
        iconURL = in.readString();
        imgURL = in.readString();
    }

    public static final Creator<MarkerModel> CREATOR = new Creator<MarkerModel>() {
        @Override
        public MarkerModel createFromParcel(Parcel in) {
            return new MarkerModel(in);
        }

        @Override
        public MarkerModel[] newArray(int size) {
            return new MarkerModel[size];
        }
    };

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public GeoPoint getGeoPoint() {
        return geopoint;
    }

    public void setGeoPoint(GeoPoint geopoint) {
        this.geopoint = geopoint;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname =subname;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(iconURL);
        parcel.writeString(imgURL);
    }
}

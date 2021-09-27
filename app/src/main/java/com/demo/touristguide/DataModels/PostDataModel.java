package com.demo.touristguide.DataModels;

import java.util.ArrayList;

public class PostDataModel {

    String postKey;
    String postLabel;
    String postDescription;
    String postAddressTxt;
    String postDistance;
    String postSection;
    double lat , lon;
    ArrayList<String> postImageURL;

    public PostDataModel() {
    }

    public PostDataModel(String postKey, String postLabel, String postDescription, String postAddressTxt , String postDistance, String postSection,
                         ArrayList<String> postImageURL, double lat , double lon) {
        this.postKey = postKey;
        this.postLabel = postLabel;
        this.postDescription = postDescription;
        this.postDistance = postDistance;
        this.postSection = postSection;
        this.postImageURL = postImageURL;
        this.postAddressTxt = postAddressTxt;
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getPostLabel() {
        return postLabel;
    }

    public void setPostLabel(String postLabel) {
        this.postLabel = postLabel;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getPostDistance() {
        return postDistance;
    }

    public void setPostDistance(String postDistance) {
        this.postDistance = postDistance;
    }

    public String getPostSection() {
        return postSection;
    }

    public void setPostSection(String postSection) {
        this.postSection = postSection;
    }

    public ArrayList<String> getPostImageURL() {
        return postImageURL;
    }

    public void setPostImageURL(ArrayList<String> postImageURL) {
        this.postImageURL = postImageURL;
    }

    public String getPostAddressTxt() {
        return postAddressTxt;
    }

    public void setPostAddressTxt(String postAddressTxt) {
        this.postAddressTxt = postAddressTxt;
    }
}

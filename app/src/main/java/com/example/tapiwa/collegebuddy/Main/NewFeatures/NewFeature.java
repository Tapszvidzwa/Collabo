package com.example.tapiwa.collegebuddy.Main.NewFeatures;

/**
 * Created by tapiwa on 10/21/17.
 */

public class NewFeature {

    private String feature;

    public NewFeature(String feature, String starType) {
        this.feature = feature;
        this.starType = starType;
    }

    private String starType;

    public NewFeature() {
    }

    public String getStarType() {
        return starType;
    }


    public String getFeature() {
        return feature;
    }


}

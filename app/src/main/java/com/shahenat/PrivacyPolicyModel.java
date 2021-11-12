package com.shahenat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrivacyPolicyModel {

    @SerializedName("result")
    @Expose
    public Result result;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("status")
    @Expose
    public String status;

    public class Result {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("name")
        @Expose
        public String name;
        @SerializedName("description")
        @Expose
        public String description;
        @SerializedName("status")
        @Expose
        public String status;

    }
}


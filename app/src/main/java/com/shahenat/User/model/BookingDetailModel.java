package com.shahenat.User.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BookingDetailModel {

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
        @SerializedName("user_id")
        @Expose
        public String userId;
        @SerializedName("driver_id")
        @Expose
        public String driverId;
        @SerializedName("pickup_add")
        @Expose
        public String pickupAdd;
        @SerializedName("p_lat")
        @Expose
        public String pLat;
        @SerializedName("p_lon")
        @Expose
        public String pLon;
        @SerializedName("drop_add")
        @Expose
        public String dropAdd;
        @SerializedName("d_lat")
        @Expose
        public String dLat;
        @SerializedName("d_lon")
        @Expose
        public String dLon;
        @SerializedName("vehicle_id")
        @Expose
        public String vehicleId;
        @SerializedName("total_price")
        @Expose
        public String totalPrice;
        @SerializedName("total_km")
        @Expose
        public String totalKm;
        @SerializedName("payment_type")
        @Expose
        public String paymentType;
        @SerializedName("status")
        @Expose
        public String status;
        @SerializedName("req_datetime")
        @Expose
        public String reqDatetime;
        @SerializedName("booktype")
        @Expose
        public String booktype;
        @SerializedName("from_date")
        @Expose
        public String fromDate;
        @SerializedName("to_date")
        @Expose
        public String toDate;
        @SerializedName("from_time")
        @Expose
        public String fromTime;
        @SerializedName("negotiation_amount")
        @Expose
        public String negotiationAmount;
        @SerializedName("send_drivers")
        @Expose
        public String sendDrivers;
        @SerializedName("equipment_id")
        @Expose
        public String equipmentId;
        @SerializedName("cancel_time")
        @Expose
        public String cancelTime;



    }
}


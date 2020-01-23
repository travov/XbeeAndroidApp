package com.example.xbeeandroidapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class XbeeDevice implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("nodeId")
    @Expose
    private String nodeId;

    @SerializedName("xBee64BitAddress")
    @Expose
    private String xBee64BitAddress;

    @SerializedName("xBee16BitAddress")
    @Expose
    private String xBee16BitAddress;

    @SerializedName("role")
    @Expose
    private String role;

    @SerializedName("active")
    @Expose
    private String active;

    public XbeeDevice(Integer id, String nodeId, String xBee64BitAddress, String xBee16BitAddress, String role) {
        this.id = id;
        this.nodeId = nodeId;
        this.xBee64BitAddress = xBee64BitAddress;
        this.xBee16BitAddress = xBee16BitAddress;
        this.role = role;
    }

    protected XbeeDevice(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        nodeId = in.readString();
        xBee64BitAddress = in.readString();
        xBee16BitAddress = in.readString();
        role = in.readString();
        active = in.readString();
    }

    public static final Creator<XbeeDevice> CREATOR = new Creator<XbeeDevice>() {
        @Override
        public XbeeDevice createFromParcel(Parcel in) {
            return new XbeeDevice(in);
        }

        @Override
        public XbeeDevice[] newArray(int size) {
            return new XbeeDevice[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getxBee64BitAddress() {
        return xBee64BitAddress;
    }

    public void setxBee64BitAddress(String xBee64BitAddress) {
        this.xBee64BitAddress = xBee64BitAddress;
    }

    public String getxBee16BitAddress() {
        return xBee16BitAddress;
    }

    public void setxBee16BitAddress(String xBee16BitAddress) {
        this.xBee16BitAddress = xBee16BitAddress;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(nodeId);
        dest.writeString(xBee64BitAddress);
        dest.writeString(xBee16BitAddress);
        dest.writeString(role);
        dest.writeString(active);
    }
}

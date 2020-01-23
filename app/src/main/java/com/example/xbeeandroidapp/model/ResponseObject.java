package com.example.xbeeandroidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ResponseObject<K, V> {

    @SerializedName("adr64bit")
    @Expose
    private String adr64bit;

    @SerializedName("parameters")
    @Expose
    private Map<K, V> parameters;

    public ResponseObject(String adr64bit, Map<K, V> parameters) {
        this.adr64bit = adr64bit;
        this.parameters = parameters;
    }

    public String getAdr64bit() {
        return adr64bit;
    }

    public void setAdr64bit(String adr64bit) {
        this.adr64bit = adr64bit;
    }

    public Map<K, V> getParameters() {
        return parameters;
    }

    public void setParameters(Map<K, V> parameters) {
        this.parameters = parameters;
    }
}

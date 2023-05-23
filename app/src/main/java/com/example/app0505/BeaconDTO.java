package com.example.app0505;

public class BeaconDTO {
    String name;
    String address;
    int rssi;

    public BeaconDTO(String name,String address, int rssi){
        this.name=name;
        this.address=address;
        this.rssi=rssi;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getRssi() {
        return rssi;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}

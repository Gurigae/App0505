package com.example.app0505;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BeaconDTO> mLeDevices;
    private LayoutInflater mInflater;

    public LeDeviceListAdapter(Context context) {
        super();
        mLeDevices = new ArrayList<BeaconDTO>();
        mInflater = LayoutInflater.from(context);
    }

    @SuppressLint("MissingPermission")
    public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

        for(int i=0; i<mLeDevices.size(); i++){
            BeaconDTO dto = mLeDevices.get(i);
            if(dto.getAddress().equals(device.getAddress())){
                dto.setName(device.getName());
                dto.setRssi(rssi);
                return;
            }
        }
        mLeDevices.add(new BeaconDTO(device.getName(), device.getAddress(), rssi));
        Log.d("DEVICE", "Device found: " + device.getName() + " " + device.getAddress());
     }

    public BeaconDTO getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflater.inflate(R.layout.device_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BeaconDTO beaconDTO = mLeDevices.get(i);
        @SuppressLint("MissingPermission") final String deviceName = beaconDTO.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText("Unknown device");
        viewHolder.deviceAddress.setText(beaconDTO.getAddress());
        viewHolder.deviceRssi.setText("RSSI: " + beaconDTO.getRssi() + "dBm");

        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }
}

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
    private ArrayList<BluetoothDevice> mLeDevices;
    private ArrayList<Integer> mRssis;
    private ArrayList<byte[]> mScanRecords;
    private LayoutInflater mInflater;

    public LeDeviceListAdapter(Context context) {
        super();
        mLeDevices = new ArrayList<BluetoothDevice>();
        mRssis = new ArrayList<Integer>();
        mScanRecords = new ArrayList<byte[]>();
        mInflater = LayoutInflater.from(context);
    }

    @SuppressLint("MissingPermission")
    public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device);
            mRssis.add(rssi);
            mScanRecords.add(scanRecord);
            Log.d("DEVICE", "Device found: " + device.getName() + " " + device.getAddress());
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
        mRssis.clear();
        mScanRecords.clear();
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

        BluetoothDevice device = mLeDevices.get(i);
        @SuppressLint("MissingPermission") final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        else
            viewHolder.deviceName.setText("Unknown device");
        viewHolder.deviceAddress.setText(device.getAddress());
        viewHolder.deviceRssi.setText("RSSI: " + mRssis.get(i) + "dBm");

        return view;
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }
}

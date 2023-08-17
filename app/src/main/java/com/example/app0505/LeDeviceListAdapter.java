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
    // 스캔 결과를 담은 BeaconDTO 타입의 ArrayList ScanDevices. rssi, 주소 등이 담겨있다.
    private ArrayList<BeaconDTO> ScanDevices;
    // 인플래터
    private LayoutInflater mInflater;

    public LeDeviceListAdapter(Context context) {
        super();
        ScanDevices = new ArrayList<BeaconDTO>();
        mInflater = LayoutInflater.from(context);
    }

    // 스캔한 결과를 저장한다.
    @SuppressLint("MissingPermission")
    public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
        // 이미 리스트에 존재하는 장치인지 확인하고, 존재하면 해당 장치의 정보를 업데이트한다.
        for (int i = 0; i < ScanDevices.size(); i++) {
            BeaconDTO dto = ScanDevices.get(i);
            if (dto.getAddress().equals(device.getAddress())) {
                dto.setName(device.getName());
                dto.setRssi(rssi);
                return;
            }
        }
        // 리스트에 존재하지 않는 새로운 장치인 경우, 새로운 BeaconDTO 객체를 생성하여 리스트에 추가한다.
        ScanDevices.add(new BeaconDTO(device.getName(), device.getAddress(), rssi));
        Log.d("DEVICE", "Device found: " + device.getName() + " " + device.getAddress());
    }

    // 주어진 위치의 장치를 가져온다.
    public BeaconDTO getDevice(int position) {
        return ScanDevices.get(position);
    }

    // 리스트를 비운다.
    public void clear() {
        ScanDevices.clear();
    }

    @Override
    public int getCount() {
        return ScanDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return ScanDevices.get(i);
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
            // 새로운 뷰를 생성하고 뷰 홀더를 설정한다.
            view = mInflater.inflate(R.layout.device_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
            view.setTag(viewHolder);
        } else {
            // 기존 뷰를 재사용하고 뷰 홀더를 가져온다.
            viewHolder = (ViewHolder) view.getTag();
        }

        // 해당 위치에 있는 BeaconDTO 객체를 가져온다.
        BeaconDTO beaconDTO = ScanDevices.get(i);
        // 장치 이름을 가져와서 텍스트뷰에 설정한다.
        @SuppressLint("MissingPermission") final String deviceName = beaconDTO.getName();
        if (deviceName != null && deviceName.length() > 0) {
            viewHolder.deviceName.setText(deviceName);
        } else {
            viewHolder.deviceName.setText("Unknown device");
        }
        // 장치 주소를 텍스트뷰에 설정한다.
        viewHolder.deviceAddress.setText(beaconDTO.getAddress());
        // 장치의 RSSI 값을 텍스트뷰에 설정한다.
        viewHolder.deviceRssi.setText("RSSI: " + beaconDTO.getRssi() + "dBm");

        return view;
    }

    // 뷰 홀더 클래스
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRssi;
    }
}


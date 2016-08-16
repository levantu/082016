package com.julio.smartkey.events;

/**
 * Created by tulv2 on 8/15/2016.
 */
public interface IConnectListener {
    public void onConnectSuccess();
    public void readRawData(byte[] bytes);
    public void onToastMessage(String message);
}

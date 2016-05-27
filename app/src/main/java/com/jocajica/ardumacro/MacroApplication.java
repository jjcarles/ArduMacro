package com.jocajica.ardumacro;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * Created by Falken on 20/02/2016.
 */
public class MacroApplication extends Application {

    BluetoothSocket mSocket;

    public synchronized BluetoothSocket getBluetoothSocket() {
        return mSocket;
    }

    public synchronized void setBluetoothSocket(BluetoothSocket socket) {
        mSocket = socket;
    }
}

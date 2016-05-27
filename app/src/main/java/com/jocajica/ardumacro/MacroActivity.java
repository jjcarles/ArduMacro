package com.jocajica.ardumacro;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by jcarles on 25/02/2016.
 */
public class MacroActivity extends AppCompatActivity {

    static final boolean mISDEBUG = false;

    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    static final char mEOT = ';';

    protected ProgressDialog mProgressDialog;
    protected BluetoothAdapter mBluetooth = null;
    protected BluetoothSocket mBluetoothSocket = null;
    protected boolean mIsBtConnected = false;
    protected String mDireccion;

    protected MacroApplication mApp;

    protected void ConectarRail(String direccion) {
        if (mBluetoothSocket == null)
        {
            new ConectarDispositivo(direccion).execute();
        }
    }

    protected void DesconectarRail()
    {
        if (mBluetoothSocket != null)
        {
            try
            {
                mBluetoothSocket.close();
            }
            catch (IOException e)
            {
                Toast.makeText(MacroActivity.this, getResources().getString(R.string.BtError02), Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void Espera(int segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void EnviarComando(String comando)
    {
        if (mBluetoothSocket != null)
        {
            try
            {
                comando += mEOT;
                mBluetoothSocket.getOutputStream().write(comando.getBytes());
                Espera(1);
            }
            catch (IOException e)
            {
                Toast.makeText(MacroActivity.this, getResources().getString(R.string.BtError02), Toast.LENGTH_LONG).show();
            }
        }
    }

    protected class ConectarDispositivo extends AsyncTask<Void, Void, Void>
    {
        private boolean mConnectSuccess = true;

        public ConectarDispositivo(String direccion) {
            mDireccion = direccion;
        }

        @Override
        protected void onPreExecute()
        {
            mProgressDialog = ProgressDialog.show(MacroActivity.this, getResources().getString(R.string.Conectando), getResources().getString(R.string.Espere));  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices)
        {
            try
            {
                if (mBluetoothSocket == null || !mIsBtConnected)
                {
                    mBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = mBluetooth.getRemoteDevice(mDireccion);//connects to the device's direccion and checks if it's available
                    mBluetoothSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(mUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                mConnectSuccess = false;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);

            if (!mConnectSuccess)
            {
                Toast.makeText(MacroActivity.this, getResources().getString(R.string.BtError01), Toast.LENGTH_LONG).show();
                finish();
            }
            else
            {
                Toast.makeText(MacroActivity.this, getResources().getString(R.string.Conectado), Toast.LENGTH_LONG).show();
                mIsBtConnected = true;

                // Guarda la conexion
                mApp = (MacroApplication) getApplication();
                mApp.setBluetoothSocket(mBluetoothSocket);
            }

            mProgressDialog.dismiss();
        }
    }
}

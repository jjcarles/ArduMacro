package com.jocajica.ardumacro;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ConexionActivity extends MacroActivity {

    Button mButtonSig;
    Button mButtonBuscar;
    ListView mListViewDispositivos;

    BluetoothAdapter mBluetooth = null;
    Set<BluetoothDevice> mSetDispositivos;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion);

        mButtonSig = (Button)findViewById(R.id.buttonSig);
        mButtonBuscar = (Button)findViewById(R.id.buttonBuscar);
        mListViewDispositivos = (ListView)findViewById(R.id.listViewDispositivos);

        mBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(mBluetooth == null)
        {
            Toast.makeText(getApplicationContext(), getResources().getText(R.string.BtError03), Toast.LENGTH_LONG).show();
            finish();
        }
        else if(!mBluetooth.isEnabled())
        {
            Intent activarBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(activarBluetooth, 1);
        }

        mButtonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Muestra los dispositivos que ya temos emparejados
                MostrarDispositivosEmparejados();
            }
        });

        mButtonSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abre la actividad para posicionar el motor
                AbrirMotorPos();
            }
        });

        mButtonSig.setEnabled(mISDEBUG);
    }

    private void AbrirMotorPos() {
        // Abre la actividad
        Intent i = new Intent(this, MotorPosActivity.class);
        startActivity(i);
    }

    private void MostrarDispositivosEmparejados()
    {
        mSetDispositivos = mBluetooth.getBondedDevices();
        ArrayList list = new ArrayList();

        if (mSetDispositivos.size() > 0)
        {
            for(BluetoothDevice bt : mSetDispositivos)
            {
                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.BtError04), Toast.LENGTH_LONG).show();
        }

        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        mListViewDispositivos.setAdapter(adapter);
        mListViewDispositivos.setOnItemClickListener(mListClickListener); //Method called when the device from the list is clicked
    }

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView<?> av, View v, int arg2, long arg3)
        {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String direccion = info.substring(info.length() - 17);

            // Inicia la conexión
            ConectarRail(direccion);

            // Podemos avanzar
            mButtonSig.setEnabled(true);
        }
    };

}

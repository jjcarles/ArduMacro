package com.jocajica.ardumacro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MotorPosActivity extends MacroActivity {

    Button mButtonAnt;
    Button mButtonSig;

    ToggleButton mTButtonAvance;
    ToggleButton mTButtonRetroceso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_pos);

        mApp = (MacroApplication) getApplication();
        mBluetoothSocket = mApp.getBluetoothSocket();

        mTButtonAvance = (ToggleButton)findViewById(R.id.toggleButtonAvance);
        mTButtonRetroceso = (ToggleButton)findViewById(R.id.toggleButtonRetroceso);
        mButtonAnt = (Button)findViewById(R.id.buttonAnt);
        mButtonSig = (Button)findViewById(R.id.buttonSig);

        mButtonSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirConfiguracion();
            }
        });

        mButtonAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirBluetooth();
            }
        });

        mTButtonAvance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((!mTButtonAvance.isChecked()) || mTButtonRetroceso.isChecked()) {
                    PararMotor();
                }

                if (mTButtonRetroceso.isChecked()) {
                    mTButtonRetroceso.setChecked(false);
                }

                if (mTButtonAvance.isChecked()) {
                    AvanzarMotor();
                }
            }
        });

        mTButtonRetroceso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mTButtonAvance.isChecked()) || !mTButtonRetroceso.isChecked()) {
                    PararMotor();
                }

                if (mTButtonAvance.isChecked()) {
                    mTButtonAvance.setChecked(false);
                }

                if (mTButtonRetroceso.isChecked()) {
                    RetrocederMotor();
                }
            }
        });
}

    private void AbrirBluetooth() {
        // Para el motor y actualiza la GUI
        ReseteaEstado();

        // Cierra la actividad
        finish();
    }

    private void PararMotor() {
        EnviarComando("STOP");

        if (mISDEBUG) {
            Toast.makeText(this, "Motor Parado", Toast.LENGTH_LONG).show();
        }
    }

    private void AvanzarMotor() {
        EnviarComando("SINGLE");
        EnviarComando("STEPS=200");
        EnviarComando("FORWARD");

        if (mISDEBUG) {
            Toast.makeText(this, "Motor Avanzando", Toast.LENGTH_LONG).show();
        }
    }

    private void RetrocederMotor() {
        EnviarComando("SINGLE");
        EnviarComando("STEPS=200");
        EnviarComando("BACKWARD");

        if (mISDEBUG) {
            Toast.makeText(this, "Motor Retrocediendo", Toast.LENGTH_LONG).show();
        }
    }

    protected void AbrirConfiguracion() {
        // Para el motor y actualiza la GUI
        ReseteaEstado();

        // Abre la actividad
        Intent i = new Intent(this, ConfiguracionActivity.class);
        startActivity(i);
    }

    protected void ReseteaEstado() {
        // Para el motor si está en marcha
        if ((mTButtonAvance.isChecked()) || mTButtonRetroceso.isChecked()) {
            PararMotor();
        }

        // Actualiza el estado del botón
        if (mTButtonAvance.isChecked()) {
            mTButtonAvance.setChecked(false);
        }

        // Actualiza el estado del botón
        if (mTButtonRetroceso.isChecked()) {
            mTButtonRetroceso.setChecked(false);
        }
    }

    @Override
    public void onStop () {
        // Para el motor si está en marcha
        if ((mTButtonAvance.isChecked()) || mTButtonRetroceso.isChecked()) {
            PararMotor();
        }

        super.onStop();
    }
}

package com.jocajica.ardumacro;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

public class SesionActivity extends MacroActivity {

    Button mButtonAnt;

    ToggleButton mTButtonEjecutar;
    Button mButtonCancelar;

    boolean mFotografiando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion);

        mFotografiando = false;

        mApp = (MacroApplication) getApplication();
        mBluetoothSocket = mApp.getBluetoothSocket();

        mTButtonEjecutar = (ToggleButton)findViewById(R.id.toggleButtonEjecutando);
        mButtonCancelar = (Button)findViewById(R.id.buttonCancelar);
        mButtonAnt = (Button)findViewById(R.id.buttonAnt);

        mTButtonEjecutar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mFotografiando) {
                    IniciarSesion();
                }
                else {
                    PausarSesion();
                }
            }
        });

        mButtonCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelarSesion();
            }
        });

        mButtonAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirDetalles();
            }
        });
    }

    private void CancelarSesion() {
        // Cancela la sesión
        EnviarComando("CANCEL");
        mFotografiando = false;
        mTButtonEjecutar.setTextOff(getResources().getString(R.string.Cancelada));

        // deshabilita los botones
        mTButtonEjecutar.setChecked(false);
        mTButtonEjecutar.setEnabled(false);
        mButtonCancelar.setEnabled(false);
    }

    private void PausarSesion() {
        // Pausa la sesión
        EnviarComando("STOP");
        mFotografiando = false;
        mTButtonEjecutar.setTextOff(getResources().getString(R.string.Continuar));

        // Permite cancelar la sesión
        mButtonCancelar.setEnabled(true);
    }

    private void IniciarSesion() {
        // Inicia la sesión
        mButtonCancelar.setEnabled(false);
        mFotografiando = true;
        EnviarComando("START");
    }

    protected void AbrirDetalles() {
        if (mTButtonEjecutar.isChecked()) {
            mFotografiando = false;
            EnviarComando("CANCEL");
        }

        // Cierra la actividad
        finish();
    }
}

package com.jocajica.ardumacro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DetallesActivity extends MacroActivity {

    Button mButtonAnt;
    Button mButtonSig;

    float mCoC;
    int mSolapamiento;
    float mAvance;
    float mPasos;
    int mFocal;
    int mFocalExt;
    float mDiafragma;
    int mDistancia;
    int mProfundidad;

    int mFotosSesion;
    int mPasosFoto;

    EditText mEditMag;
    EditText mEditPdc;
    EditText mEditNdf;
    EditText mEditPxf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        mApp = (MacroApplication) getApplication();
        mBluetoothSocket = mApp.getBluetoothSocket();

        mEditMag = (EditText)findViewById(R.id.editTextMag);
        mEditPdc = (EditText)findViewById(R.id.editTextPdc);
        mEditNdf = (EditText)findViewById(R.id.editTextNdf);
        mEditPxf = (EditText)findViewById(R.id.editTextPxf);
        mButtonAnt = (Button)findViewById(R.id.buttonAnt);
        mButtonSig = (Button)findViewById(R.id.buttonSig);

        //Recupera parametros y los muestra en el TextView
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if ( bundle != null ) {
            mCoC = bundle.getFloat(ConfiguracionActivity.VAL_COC);
            mSolapamiento = bundle.getInt(ConfiguracionActivity.VAL_SOLAPAMIENTO);
            mAvance = bundle.getFloat(ConfiguracionActivity.VAL_AVANCE);
            mPasos = bundle.getInt(ConfiguracionActivity.VAL_PASOS);
            mFocal = bundle.getInt(ConfiguracionActivity.VAL_FOCAL);
            mFocalExt = bundle.getInt(ConfiguracionActivity.VAL_FOCALEXT);
            mDiafragma = bundle.getFloat(ConfiguracionActivity.VAL_DIAFRAGMA);
            mDistancia = bundle.getInt(ConfiguracionActivity.VAL_DISTANCIA);
            mProfundidad = bundle.getInt(ConfiguracionActivity.VAL_PROFUNDIDAD);

            float r = (float) Math.sqrt(((mDistancia * mDistancia) / 4) - mFocal * mDistancia);
            float g = (mDistancia / 2) + r;
            float m = mFocal / (g - mFocal);
            float mext = (((m * mFocal) + mFocalExt) / mFocal);

            float pdct = 2 * mCoC * mDiafragma * ((mext + 1) / (mext * mext));
            float pdc = (pdct / (1 + mSolapamiento / (float)100.0));

            mFotosSesion = Math.round(mProfundidad / pdc + (float)0.5);
            mPasosFoto = Math.round(pdc / mAvance * mPasos - (float)0.5);

            mEditMag.setText(String.format("%.2fx", mext));
            mEditPdc.setText(String.format("%.3f", pdc));
            mEditNdf.setText(String.valueOf(mFotosSesion));
            mEditPxf.setText(String.valueOf(mPasosFoto));
        }

        mButtonAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirConfiguracion();
            }
        });

        mButtonSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirSesion();
            }
        });
    }

    protected void AbrirSesion() {
        // Envia la configuración al rail
        EnviarComando(String.format("PHOTOS=%d", mFotosSesion));
        EnviarComando(String.format("STEPS=%d", mPasosFoto));

        // Abre la actividad
        Intent i = new Intent(this, SesionActivity.class);
        startActivity(i);
    }

    protected void AbrirConfiguracion() {
        // Cierra la actividad
        finish();
    }

}

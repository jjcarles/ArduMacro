package com.jocajica.ardumacro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ConfiguracionActivity extends MacroActivity {

    public static final String VAL_COC = "coc";
    public static final String VAL_SOLAPAMIENTO = "solapamiento";
    public static final String VAL_AVANCE = "avance";
    public static final String VAL_PASOS = "pasos";
    public static final String VAL_FOCAL = "focal";
    public static final String VAL_FOCALEXT = "focalext";
    public static final String VAL_DIAFRAGMA = "diafragma";
    public static final String VAL_DISTANCIA = "distancia";
    public static final String VAL_PROFUNDIDAD = "profundidad";

    Button mButtonAnt;
    Button mButtonSig;

    EditText mEditCoc;
    EditText mEditSolapamiento;
    EditText mEditAvance;
    EditText mEditPasos;
    EditText mEditFocal;
    EditText mEditFocalExt;
    EditText mEditDiafragma;
    EditText mEditDistancia;
    EditText mEditProfundidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        mEditCoc = (EditText)findViewById(R.id.editTextCoC);
        mEditSolapamiento = (EditText)findViewById(R.id.editTextPct);
        mEditAvance = (EditText)findViewById(R.id.editTextAvance);
        mEditPasos = (EditText)findViewById(R.id.editTextPasos);
        mEditFocal = (EditText)findViewById(R.id.editTextFocal);
        mEditFocalExt = (EditText)findViewById(R.id.editTextFocalExt);
        mEditDiafragma = (EditText)findViewById(R.id.editTextAbertura);
        mEditDistancia = (EditText)findViewById(R.id.editTextDistancia);
        mEditProfundidad = (EditText)findViewById(R.id.editTextProfundidad);
        mButtonAnt = (Button)findViewById(R.id.buttonAnt);
        mButtonSig = (Button)findViewById(R.id.buttonSig);

        // Los valores iniciales son los de la última sesión
        if (savedInstanceState == null) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            mEditCoc.setText(String.valueOf(sharedPref.getFloat(VAL_COC, (float)0.02)));
            mEditSolapamiento.setText(String.valueOf(sharedPref.getInt(VAL_SOLAPAMIENTO, 20)));
            mEditAvance.setText(String.valueOf(sharedPref.getFloat(VAL_AVANCE, (float) 1.5)));
            mEditPasos.setText(String.valueOf(sharedPref.getInt(VAL_PASOS, 200)));
            mEditFocal.setText(String.valueOf(sharedPref.getInt(VAL_FOCAL, 105)));
            mEditFocalExt.setText(String.valueOf(sharedPref.getInt(VAL_FOCALEXT, 0)));
            mEditDiafragma.setText(String.valueOf(sharedPref.getFloat(VAL_DIAFRAGMA, (float)5.4)));
            mEditDistancia.setText(String.valueOf(sharedPref.getInt(VAL_DISTANCIA, 450)));
            mEditProfundidad.setText(String.valueOf(sharedPref.getInt(VAL_PROFUNDIDAD, 15)));
        }

        mButtonAnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirMotorPos();
            }
        });

        mButtonSig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirDetalles();
            }
        });
    }

    protected void AbrirDetalles() {
        // Valida los valores
        int codigo = DatosValidos();

        switch (codigo) {
            case 1:
                Toast.makeText(ConfiguracionActivity.this, "No se permiten campos vacios.", Toast.LENGTH_LONG).show();
                return;
            case 2:
                Toast.makeText(ConfiguracionActivity.this, "La distancia al sujeto no es suficiente.", Toast.LENGTH_LONG).show();
                return;
        }

        // Guarda los datos
        GuardarDatos();

        // Parametros que se pasan a la siguiente actividad
        Bundle bundle = new Bundle();
        bundle.putFloat(VAL_COC, Float.parseFloat(mEditCoc.getText().toString()));
        bundle.putInt(VAL_SOLAPAMIENTO, Integer.parseInt(mEditSolapamiento.getText().toString()));
        bundle.putFloat(VAL_AVANCE, Float.parseFloat(mEditAvance.getText().toString()));
        bundle.putInt(VAL_PASOS, Integer.parseInt(mEditPasos.getText().toString()));
        bundle.putInt(VAL_FOCAL, Integer.parseInt(mEditFocal.getText().toString()));
        bundle.putInt(VAL_FOCALEXT, Integer.parseInt(mEditFocalExt.getText().toString()));
        bundle.putFloat(VAL_DIAFRAGMA, Float.parseFloat(mEditDiafragma.getText().toString()));
        bundle.putInt(VAL_DISTANCIA, Integer.parseInt(mEditDistancia.getText().toString()));
        bundle.putInt(VAL_PROFUNDIDAD, Integer.parseInt(mEditProfundidad.getText().toString()));

        // Abre la actividad
        Intent i = new Intent(this, DetallesActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    protected int DatosValidos() {
        boolean bResult;

        bResult = !mEditCoc.getText().toString().isEmpty()
            && !mEditSolapamiento.getText().toString().isEmpty()
            && !mEditAvance.getText().toString().isEmpty()
            && !mEditPasos.getText().toString().isEmpty()
            && !mEditFocal.getText().toString().isEmpty()
            && !mEditFocalExt.getText().toString().isEmpty()
            && !mEditDiafragma.getText().toString().isEmpty()
            && !mEditDistancia.getText().toString().isEmpty()
            && !mEditProfundidad.getText().toString().isEmpty();

        if (!bResult) {
            // Campos vacios
            return 1;
        }

        int distancia = Integer.parseInt(mEditDistancia.getText().toString());
        int distancia_minima = Integer.parseInt(mEditFocal.getText().toString()) * 4;

        if (distancia < distancia_minima) {
            // Distancia insuficiente
            return 2;
        }

        return 0;
    }

    protected void AbrirMotorPos() {
        // Cierra la actividad
        finish();
    }

    protected void GuardarDatos() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(VAL_COC, Float.parseFloat(mEditCoc.getText().toString()));
        editor.putInt(VAL_SOLAPAMIENTO, Integer.parseInt(mEditSolapamiento.getText().toString()));
        editor.putFloat(VAL_AVANCE, Float.parseFloat(mEditAvance.getText().toString()));
        editor.putInt(VAL_PASOS, Integer.parseInt(mEditPasos.getText().toString()));
        editor.putInt(VAL_FOCAL, Integer.parseInt(mEditFocal.getText().toString()));
        editor.putInt(VAL_FOCALEXT, Integer.parseInt(mEditFocalExt.getText().toString()));
        editor.putFloat(VAL_DIAFRAGMA, Float.parseFloat(mEditDiafragma.getText().toString()));
        editor.putInt(VAL_DISTANCIA, Integer.parseInt(mEditDistancia.getText().toString()));
        editor.putInt(VAL_PROFUNDIDAD, Integer.parseInt(mEditProfundidad.getText().toString()));
        editor.commit();
    }
}

package com.beastek.tareas;

import android.content.SharedPreferences;
import android.os.Bundle;


import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivityNote extends AppCompatActivity {


    private SharedPreferences mPrefs; //In order to read data saved on disk ,Para leer datos guardados en disco
    private SharedPreferences.Editor mEditor; //In order to write in Shared Prefs, Para escribir datos en las Shared Prefs

    private boolean mSound; //To activate/deactivates sound, Para activar / desactivar el sonido de la app

    public static final int FAST = 0; //Quick animations, Animaciones rápidas
    public static final int SLOW = 1; //Slow animations Animaciones lentas
    public static final int NONE = 2; //Without animations Sin animaciones
    //public static final String GSAPI = "https://sheet.best/api/sheets/b706d82d-0d00-475f-a9d4-626566535083";  //GoogleSheets API
    public static final String GSAPI = "https://sheetdb.io/api/v1/ahhtehepl6e9f";
    //public static final String GSAPIKEY = "VyTblD#cpn1zfx%LxC6sY1nmxW9q9UOpEzvYtZIcNMvuFiV7a@DY3OkQsWuDVAXB"; // GoogleSheets ApiKey
    private int mAnimationOption; //In order to chang animation type, Para cambiar el tipo de animación en la app



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_note);

        mPrefs = getSharedPreferences("EOL", MODE_PRIVATE);
        mEditor = mPrefs.edit();




        //Lógica de activar y desactivar sonido
        mSound = mPrefs.getBoolean("sound", true);

        CheckBox checkBoxSound = (CheckBox) findViewById(R.id.sound_checkbox);

        if (mSound){
            checkBoxSound.setChecked(true);
        }else {
            checkBoxSound.setChecked(false);
        }

        checkBoxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                //Si el sonido estaba en marcha, lo apagamos
                //Si el sonido estaba apagado, lo ponemos en marcha
                mSound = !mSound;

                mEditor.putBoolean("sound", mSound);
            }
        });



        //Lógica de cambiar de tipo de animación
        mAnimationOption = mPrefs.getInt("anim option", FAST);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group_animation);
        radioGroup.clearCheck();//Deselecciono cualquier radio button

        //En función, de la preferencia del jugador, selecciono uno de los 3 modos de animación
        switch (mAnimationOption){
            case FAST:
                radioGroup.check(R.id.rbFast);
                break;
            case SLOW:
                radioGroup.check(R.id.rbSlow);
                break;
            case NONE:
                radioGroup.check(R.id.rbNone);
                break;
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
                //Recuperamos el radio button del radio group que ha sido seleccionado por el usuario a travs del checked ID
                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);

                if (null != rb && checkedId > -1){

                    switch (rb.getId()) {
                        case R.id.rbFast:
                            mAnimationOption = FAST;
                            break;
                        case R.id.rbSlow:
                            mAnimationOption = SLOW;
                            break;
                        case R.id.rbNone:
                            mAnimationOption = NONE;
                            break;
                    }

                    mEditor.putInt("anim option", mAnimationOption);
                }

            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();

        mEditor.commit();
    }
}

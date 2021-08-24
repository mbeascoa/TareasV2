package com.beastek.tareas.Consultas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ImageButton;
import android.widget.TextView;

import com.beastek.tareas.R;
import com.beastek.tareas.Consultas.ToDos;

public class MainActivityConsultas extends AppCompatActivity {

    private ImageButton botonleer, botonalta, botonnavegar, botonregistro;
    private TextView txtdatos;
    private ToDos existingToDo;

    //01
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_consultas);
        this.txtdatos = (TextView) findViewById(R.id.txExamenAndroid);
        this.botonalta = (ImageButton) findViewById(R.id.button_topleft);
        this.botonleer = (ImageButton) findViewById(R.id.button_downleft);
        this.botonnavegar = (ImageButton) findViewById(R.id.button_topright);
        this.botonregistro = (ImageButton) findViewById(R.id.button_downright);


        // gestionamos la accion del boton insertar
        this.botonalta.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                insertarRegistro();
            }

        });
        //gestionamos la accion del boton leer
        this.botonleer.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                leerRegistros();
            }

        });
        // gestionamos el boton navegar
        this.botonnavegar.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //navegar();
                Intent i = new Intent(MainActivityConsultas.this, Buscarporid.class);
                startActivity(i);
            }

        });

        // gestionamos el registro del usuario
        this.botonregistro.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                registrosingup();
            }

        });



    }



    //04  insertamos un registro nuevo
    public void insertarRegistro() {

        Intent i = new Intent(this, Alta_registro_todo.class);
        startActivity(i);
    }

    //05 método para leer los registros

    public void leerRegistros() {

        Intent i = new Intent(this, Listado_registros_ToDo.class);
        startActivity(i);

    }


    // 06 método  para inflar el menu superior

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navegacion, menu);
        return true;
    }

    //07 método para gestionar el item seleccionado

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        Intent accion;

        if (id == R.id.item_consultar) {
            leerRegistros();
        } else if (id == R.id.item_alta_registro) {
            insertarRegistro();
        } else if (id == R.id.item_signup) {
            registrosingup();
        } else if (id == R.id.item_consultarporId) {
            //accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
            accion = new Intent(this, Buscarporid.class);
            startActivity(accion);
        } else if (id == R.id.item_consultarpornombre) {
            accion = new Intent(this, BuscarporNombre.class);
            startActivity(accion);
        }

        return true;

    }

    //08 Método para navegar por internet
    public void navegar() {
        Intent accion;
        accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
        startActivity(accion);
    }

    // 09 Método para SIGNUP del usuario de la aplicación
    public void registrosingup() {
        Intent accion;
        accion = new Intent(this, SignUp_Registro.class);
        startActivity(accion);
    }


}

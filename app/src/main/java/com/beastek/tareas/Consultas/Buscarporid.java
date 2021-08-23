package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.AsyncTask;


import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beastek.tareas.R;

public class Buscarporid extends AppCompatActivity {

    private TextView resultado;
    private EditText numdep;
    private Button botonRegresar, btnBuscarPorId;
    private static final String TAG= MainActivityConsultas.class.getSimpleName();

    //01 metodo inicial al Crearse la actividad
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscarporid);

        this.numdep = (EditText) findViewById(R.id.et_numero_buscar);
        this.botonRegresar=(Button) findViewById(R.id.btn_regresar_buscar);
        this.btnBuscarPorId=(Button) findViewById(R.id.btn_buscarporid);
        this.resultado=(TextView)findViewById(R.id.tv_ResultadoBusqueda);

        //gestionamos la accion del boton Buscar por departamento
        this.btnBuscarPorId.setOnClickListener(new View.OnClickListener()  {
            public void onClick(View v) {
                String idedept = numdep.getText().toString();
                Log.i(TAG, "onCreate Buscamos por Departamento  : " + idedept);
                leerservicio(idedept);
            }
        });

    }

    //02
    public void cerrarVentana(View view) {
        finish();
    }

    //03
    public void leerservicio(String idedept) {
        try {
            String urlbase = GSAPI;
            String url = urlbase + idedept;
            Log.i(TAG, "La url de acceso a los servicios web Restfull es : "+url);
            new HttpAsyncTask().execute(url);

        } catch (Exception e){
            //manage exceptions
            System.out.println(e.toString());
            System.out.println("Error leyendo del Web Api RestFull");

        }
    }

    //04
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return recuperarContenido(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getBaseContext(), "Datos recibidos!", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject objetojson = new JSONObject(result);
                    com.beastek.tareas.Consultas.ToDos  todo = new com.beastek.tareas.Consultas.ToDos();
                    todo = convertirJsonObjectToDos(objetojson);
                    String tit, des, idea, tod, imp, has, dat,col,iden;
                    tit = todo.getTitle();
                    des = todo.getDescription();
                    if (todo.isIdea()) {
                        idea= "The task is an idea";
                    } else {
                        idea = "";
                    }
                    if(todo.isImportant()) {
                        tod= "The task is a Todo";
                    } else {
                        tod="";
                    }
                    if(todo.isImportant()) {
                        imp= "The task is Important";
                    } else {
                        imp="";
                    }
                    if(todo.HasReminder()) {
                        has= "The task has setted a reminder for :";
                    } else {
                        has="";
                    }
                    if(todo.getToDoDate()!=null){

                        // Se pueden definir formatos diferentes con la clase DateFormat
                        // Obtenemos la fecha y la hora con el formato yyyy-MM-dd HH:mm:ss
                        SimpleDateFormat fechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String strDateti = fechaHora.format(todo.getToDoDate());
                        dat= strDateti;
                        System.out.println(strDateti);

                    } else {
                        dat ="";
                    }
                    iden = todo.getTodoIdentifier()+"";

                    resultado.setText( iden+ " " + tit + " " + des + "  " + idea + " "+ tod +" " + imp +" " + has +" " + dat  );
                    resultado.setTextColor(todo.getTodoColor());


                } catch (JSONException e) {
                    System.out.println(e.toString());
                    System.out.println("onPostExecute");
                }
            } else {
                resultado.setText( "This ToDo does not exists , please try again");
                }

        }
    }

    //05
    public String recuperarContenido (String url) {
        HttpClient httpclient = new DefaultHttpClient();
        String resultado = null;
        HttpGet httpget = new HttpGet(url);
        HttpResponse respuesta = null;
        InputStream stream = null;
        try {
            respuesta = httpclient.execute(httpget);
            HttpEntity entity = respuesta.getEntity();
            if (entity != null ){
                stream= entity.getContent();
                resultado = convertirInputToString(stream);
            }
        } catch (Exception e){
            System.out.println( e.toString());
            System.out.println(" en método recuperancontenido(url");
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        System.out.println("Se capturo lo siguiente " + resultado);
        return resultado;
    }

    //06
    private String convertirInputToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line= "";
        String resultado = "";
        while ((line = bufferedReader.readLine()) != null)
            resultado += line;
        inputStream.close();
        return resultado;
    }

    //07
    public com.beastek.tareas.Consultas.ToDos convertirJsonObjectToDos(JSONObject jsonObject) throws JSONException {
        com.beastek.tareas.Consultas.ToDos todito = new com.beastek.tareas.Consultas.ToDos();
        String tit, des, loc, idea, tod, imp, has, dat,col,iden;
        tit = jsonObject.optString("JSON_TITLE").toString();
        des = jsonObject.optString ("JSON_DESCRIPTION").toString();
        idea= jsonObject.optString("JSON_IDEA").toString();
        tod = jsonObject.optString("JSON_TODO").toString();
        imp = jsonObject.optString ("JSON_IMPORTANT").toString();
        has= jsonObject.optString("JSON_HASREMINDER").toString();
        dat = jsonObject.optString("JSON_DATETIME").toString();
        col = jsonObject.optString ("JSON_COLOR").toString();
        iden= jsonObject.optString("JSON_IDENTIFIER").toString();

        todito.setTitle(tit);
        todito.setDescription(des);
        todito.setIdea(Boolean.valueOf(idea));
        todito.setTodo(Boolean.valueOf(tod));
        todito.setImportant(Boolean.valueOf(imp));
        todito.setReminder(Boolean.valueOf(has));
        // we parse to date from a date formated in mode dd-MM-yyyy HH:mm:ss as string
        SimpleDateFormat formatofecha = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try {
            Date dataFormateada = formatofecha.parse(dat);
            todito.setToDoDate(dataFormateada);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        todito.setTodoColor(Integer.parseInt(col));
        todito.setTodoIdentifier(iden);


        return todito;
    }   // final convertirJsonObjectToDos



    // 08 método  para infrar el menu superior

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navegacion, menu);
        return true;
    }

    //09 método para gestionar el item seleccionado

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        Intent accion;

        if (id== R.id.item_consultar)
        {
            Intent i = new Intent(this, Listado_registros_ToDo.class);
            startActivity(i);

        }else if (id== R.id.item_alta_registro) {
            Intent i = new Intent(this, Alta_registro_todo.class);
            startActivity(i);
        } else if( id== R.id.item_signup){
            Intent i = new Intent(this, SignUp_Registro.class);
            startActivity(i);
        }else if (id== R.id.item_navegar){

            //accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
            accion = new Intent(this, Buscarporid.class);
            startActivity(accion);
        }
        return true;
    }

}

package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.util.Log;

import android.os.Bundle;
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

public class Detalle_ToDo extends AppCompatActivity {
    private TextView tvtit, tvdes, tvrem, tvdatetime;
    private CheckBox chkbIdea, chkbTodo, chkbImportant;
    private Button botonRegresar;
    private static final String TAG= MainActivityConsultas.class.getSimpleName();

    //01
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_todo);
        this.tvtit = (TextView) findViewById(R.id.txtTitle_show_ToDo);
        this.tvdes =(TextView) findViewById(R.id.txtDescription_show_ToDo);
        this.tvrem = (TextView) findViewById(R.id.txtReminder_show_ToDo);
        this.tvdatetime = (TextView) findViewById(R.id.txtDate_show_ToDo);
        this.chkbIdea = (CheckBox) findViewById(R.id.checkBoxIdea);
        this.chkbTodo = (CheckBox) findViewById(R.id.checkBoxTodo);
        this.chkbImportant = (CheckBox) findViewById(R.id.checkBoxImportant);


        //Recogemos los parámetros enviados por el primer Activity a través del método getExtras
        Bundle bundle = getIntent().getExtras();
        String ideToDo = bundle.getString( "NUMEROTODO");
        Log.i(TAG, "onCreate Detalle ToDo  : " + ideToDo);
        leerservicio(ideToDo);
    }

    //02
    public void cerrarVentana(View view) {
        finish();
    }

    //03
    public void leerservicio(String ideToDo) {
        try {
            String urlbase = GSAPI;
            String url = urlbase + ideToDo;
            Log.i(TAG, "La url de acceso a los servicios web Restfull es : "+url);
            new com.beastek.tareas.Consultas.Detalle_ToDo.HttpAsyncTask().execute(url);


        } catch (Exception e){
            //manage exceptions
            System.out.println(e.toString());
            System.out.println("Error leyendo del Web Api RestFull");
            Toast.makeText(getBaseContext(), "Error fetching data from Web Api Restfull!", Toast.LENGTH_SHORT).show();

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
            Toast.makeText(getBaseContext(), "Datos recibidos!", Toast.LENGTH_SHORT).show();
            try {
                JSONObject objetojson = new JSONObject (result);
                com.beastek.tareas.Consultas.ToDos toda = new com.beastek.tareas.Consultas.ToDos();
                toda = convertirJsonObjectToDos(objetojson);

                tvtit.setText(toda.getTitle());
                tvdes.setText(toda.getDescription());


                // settle the user Has reminder or not
                if (toda.HasReminder()){
                    tvrem.setText("This ToDo has a reminder Date, but you can change the Reminder Date if required");
                } else {
                    tvrem.setText("This ToDo has NOT a reminder Date.");
                }

                // pasar de Date a String
                Date mUserReminderDate = toda.getToDoDate();
                // Se pueden definir formatos diferentes con la clase DateFormat
                // Obtenemos la fecha y la hora con el formato yyyy-MM-dd HH:mm:ss
                SimpleDateFormat fechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String strDateti = fechaHora.format(mUserReminderDate);
                System.out.println(strDateti);
                tvdatetime.setText(strDateti);

            } catch (JSONException e) {
                System.out.println(e.toString());
                System.out.println("onPostExecute");
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
            Intent i = new Intent(this, com.beastek.tareas.Consultas.Listado_registros_ToDo.class);
            startActivity(i);

        }else if (id== R.id.item_alta_registro) {
            Intent i = new Intent(this, com.beastek.tareas.Consultas.Alta_registro_todo.class);
            startActivity(i);
        } else if( id== R.id.item_signup){
            Intent i = new Intent(this, com.beastek.tareas.Consultas.SignUp_Registro.class);
            startActivity(i);
        }else if (id== R.id.item_navegar){

            //accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
            accion = new Intent(this, com.beastek.tareas.Consultas.Buscarporid.class);
            startActivity(accion);
        }
        return true;
    }

}
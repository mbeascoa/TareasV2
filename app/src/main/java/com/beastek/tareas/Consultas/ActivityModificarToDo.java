package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



import com.beastek.tareas.R;


import org.apache.http.HttpResponse;

import org.apache.http.client.HttpClient;


import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;

import java.util.Date;

public class ActivityModificarToDo extends AppCompatActivity {
    private String taskid;
    private static final String TAG = ActivityModificarToDo.class.getSimpleName();
    private JSONObject jsonObject;
    private ToDos existingToDo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_todo);

        Intent inta = getIntent();
        ToDos data = (ToDos) inta.getSerializableExtra("data");
        if (data != null) {
            System.out.println("Se ha recibido en la activitymodificartodo.class el objeto todo siguiente " + data.toString());
            System.out.println("Nos vamos al DialogModificarTodo pasando este ToDo " + data.toString());

            //Creamos una instancia de show note
            DialogModificarTodo dialog = new DialogModificarTodo();
            dialog.sendNoteSelected(data);
            dialog.show(getSupportFragmentManager(), "modify_note");
        } else {
            System.out.println("Se ha finalizado porque el ToDo era null");
            finish();
        }




    }


    //03 modify existing Tarea
    public void modifyExistingToDo(ToDos existingToDo) throws JSONException {
        //we convert to JSON the existingToDo object
        jsonObject = convertToDosToJSON(existingToDo);
        System.out.println("El objeto JSON es :" + jsonObject.toString());
        //we assign the unique identifier in order to edit that note
        taskid = existingToDo.getTodoIdentifier();
        System.out.println("estamos haciendo el put al identificador :" + taskid);
        leerServicio(taskid);
        mensajePersonalizado("Trying to update data to Google Sheets");

        finish();
    }

    //04 modify existing Tarea
    public void leerServicio(String taskid) {
        try {

            String urlbase = GSAPI;
            String param = "/search?JSON_IDENTIFIER=" + taskid;
            String url = urlbase + param;
            //https://sheetdb.io/api/v1/ahhtehepl6e9f/search?JSON_IDENTIFIER=1
            Log.i(TAG, "La url de acceso a los servicios web Restfull es : " + url);
            mensajePersonalizado("Accediendo al todo por su identificador :"+ url);
            new HttpAsyncTask().execute(url);
        } catch (Exception e) {
            //manage exception
            System.out.println(e.toString());
            Log.i(TAG, "Error updating ToDos, url incorrect or network error");
            e.printStackTrace();
        }
    }

    // 05 crete JSONObject from ToDos

    public JSONObject convertToDosToJSON(ToDos todo) throws JSONException {
        JSONObject jo = new JSONObject();

        jo.put("JSON_TITLE", todo.getTitle());
        jo.put("JSON_DESCRIPTION", todo.getDescription());
        jo.put("JSON_IDEA", String.valueOf(todo.isIdea()));
        jo.put("JSON_TODO", String.valueOf(todo.isTodo()));
        jo.put("JSON_IMPORTANT", String.valueOf(todo.isImportant()));
        jo.put("JSON_HASREMINDER", String.valueOf(todo.HasReminder()));

        Date mUserReminderDate = todo.getToDoDate();
        if (mUserReminderDate != null) {
            // Se pueden definir formatos diferentes con la clase DateFormat
            // Obtenemos la fecha y la hora con el formato yyyy-MM-dd HH:mm:ss
            SimpleDateFormat fechaHora = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String strDateti = fechaHora.format(mUserReminderDate);
            jo.put("JSON_DATETIME", strDateti);
            System.out.println("Este es el formato de fecha que hemos guardado :" + strDateti);
        }
        String col = todo.getTodoColor() + "";
        jo.put("JSON_COLOR", col);
        jo.put("JSON_IDENTIFIER", todo.getTodoIdentifier());
        System.out.println("La cadena JSON creada del objeto recibido es la siguiente : " +  jo.toString());
        return jo;
    }

    //06 generate the asincronous task to
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            System.out.println("Entrando a generar el PUT dentro del doInBackgroung");
            try {

                // Create a new HttpClient and Post Header
                HttpClient httpclient = new DefaultHttpClient();
                HttpPut httpPut = new HttpPut(urls[0]);

                httpPut.setHeader("Content-Type", "application/json");
                //HttpPost httppost = new HttpPost(urls[0]);
                //HttpPost httppost = new HttpPost("https://sheet.best/api/sheets/b706d82d-0d00-475f-a9d4-626566535083)");
                //Construimos el objeto cliente en formato JSON

                StringEntity entity = new StringEntity(jsonObject.toString());
                httpPut.setEntity(entity);

                HttpResponse resp = httpclient.execute(httpPut);
                String respStr = EntityUtils.toString(resp.getEntity());
                int codigo = resp.getStatusLine().getStatusCode();
                String strCodigo = codigo+"";
                System.out.print("Se obtuvo el siguiente mensaje de respuesta : "+respStr);
                System.out.println("Se obtuvo el siguiente código de respuesta : "+ strCodigo );

                if (respStr.equals("true")) {
                    mensajePersonalizado("ToDos Updated OK.");
                    System.out.println("respStre es true, ToDos Updated OK.");
                }
                if(codigo== 200){
                    System.out.println("Status code 200");
                    mensajePersonalizado("Status code 200.");
                }



            } catch (Exception ex) {
                Log.e("ServicioRest", "Error!", ex);
            }


            return "Executed";
            // return recuperarContenido(urls[0]);

        }




        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {


        }
    }


    //08 creación del mensaje personalizado Toast según sea aceptado o cancelado

    public void mensajePersonalizado(String opcion) {
        Toast mensaje = new Toast(getApplicationContext());

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.capa_toast,
                (ViewGroup) findViewById(R.id.lytLayout));

        TextView txtMsg = (TextView) layout.findViewById(R.id.txtMensaje);
        txtMsg.setText(opcion);

        mensaje.setDuration(Toast.LENGTH_SHORT);
        mensaje.setView(layout);
        mensaje.show();
    }
}
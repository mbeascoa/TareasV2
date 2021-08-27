package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.beastek.tareas.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

public class DeleteToDo {



    public void DeleteTodo(String taskid) {
        String TAG = DeleteToDo.class.getSimpleName();


        try {

            String urlbase = GSAPI;
            String param = "/JSON_IDENTIFIER/" + taskid;
            String url = urlbase + param;

            new HttpAsyncTask().execute(url);
        } catch (Exception e) {
            //manage exception
            System.out.println(e.toString());
            Log.i(TAG, "Error updating ToDos, url incorrect or network error");
            e.printStackTrace();
        }
    }

    //Asyncronous task

        private class HttpAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... urls) {

                System.out.println("Entrando a generar el PUT dentro del doInBackgroung");
                try {

                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpDelete httpDelete = new HttpDelete(urls[0]);

                    httpDelete.setHeader("Content-Type", "application/json");


                    HttpResponse resp = httpclient.execute(httpDelete);

                    int codigo = resp.getStatusLine().getStatusCode();
                    String strCodigo = codigo + "";

                    System.out.println("Se obtuvo el siguiente código de respuesta : " + strCodigo);

                    if (codigo == 200) {
                        System.out.println("Status code 200");


                    } else {
                        System.out.println("Status code not equal to 200");
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



}

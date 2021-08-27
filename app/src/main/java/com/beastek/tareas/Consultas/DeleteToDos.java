package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import androidx.appcompat.app.AppCompatActivity;

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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;

public class DeleteToDos extends AppCompatActivity {
    private TextView tvmessage;
    private String taskid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_todos);


        Bundle bundle=getIntent().getExtras();

        taskid = bundle.getString("IDENTIFIERTODO");

        tvmessage = (TextView) findViewById(R.id.txtMensaje);
        tvmessage.setText("Deleting ... !!!");
        DialogoConfirmacionDeleteToDo confirmacion = new DialogoConfirmacionDeleteToDo();
        confirmacion.show(getFragmentManager(), "Cuadro confirmación borrar");
    }

    public void DeleteTodos(String taskid) {
        String TAG = DeleteToDos.class.getSimpleName();


        try {

            String urlbase = GSAPI;
            String param = "/JSON_IDENTIFIER/" + taskid;
            String url = urlbase + param;

            new DeleteToDos.HttpAsyncTask().execute(url);
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




    //06
    public void accionAceptar() {

        DeleteTodos(taskid);
        mensajePersonalizado("Deleting a ToDo in Google Sheets, thanks!");


    }

    //07
    public void accionCancelar() {
        mensajePersonalizado("Cancelling , not deleting the ToDo");

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
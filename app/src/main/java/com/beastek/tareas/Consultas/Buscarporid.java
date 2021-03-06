package com.beastek.tareas.Consultas;

import static com.beastek.tareas.SettingsActivityNote.GSAPI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
import java.text.ParseException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beastek.tareas.R;


public class Buscarporid extends AppCompatActivity {
    private RecyclerView miRecicler;
    private RecyclerView.Adapter miAdapter;
    private TextView resultado;
    private EditText title;
    private Button botonRegresar, btnBuscarPorId;
    private static final String TAG = MainActivityConsultas.class.getSimpleName();
    private String taskid;


    //01
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscarporid);

        this.title = (EditText) findViewById(R.id.et_numero_buscar);
        this.botonRegresar = (Button) findViewById(R.id.btn_regresar_buscar);
        this.btnBuscarPorId = (Button) findViewById(R.id.btn_buscarporid);
        this.resultado = (TextView) findViewById(R.id.tv_ResultadoBusqueda);

        //gestionamos la accion del boton Buscar por departamento
        this.btnBuscarPorId.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                taskid = title.getText().toString();
                Log.i(TAG, "onCreate Buscamos por id Todo  : " + taskid);
                leerServicio(taskid);
            }
        });

        //Buscamos el control para cargar los datos
        miRecicler = (RecyclerView) findViewById(R.id.rv_ToDos_detalle);
        //a??adimos que el tama??o del RecyclerView no se cambiar??
        //que tiene hijos (items) que tienen anchura y altura fijas
        //permite que el recyclerView optimice mejor.
        miRecicler.setHasFixedSize(true);
        miRecicler.setLayoutManager((new LinearLayoutManager(this)));

    }

    //02
    public void leerServicio(String taskid) {
        try {

            String urlbase = GSAPI;
            String param = "/search?JSON_IDENTIFIER=" + taskid;
            String url = urlbase + param;
            //https://sheetdb.io/api/v1/ahhtehepl6e9f/search?JSON_IDENTIFIER=1
            Log.i(TAG, "La url de acceso a los servicios web Restfull es : " + url);
            mensajePersonalizado(url);
            new com.beastek.tareas.Consultas.Buscarporid.HttpAsyncTask().execute(url);
        } catch (Exception e) {
            //manage exception
            System.out.println(e.toString());
            Log.i(TAG, "Error leyendo datos del servicio");
            e.printStackTrace();
        }
    }

    //03
    public String recuperarContenido(String url) {
        HttpClient httpclient = new DefaultHttpClient();
        String resultado = null;
        HttpGet httpget = new HttpGet(url);
        HttpResponse respuesta = null;
        InputStream stream = null;
        try {
            respuesta = httpclient.execute(httpget);
            HttpEntity entity = respuesta.getEntity();


            if (entity != null) {
                stream = entity.getContent();
                resultado = convertirInputToString(stream);
            }

        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
        return resultado;
    }

    //04

    private String convertirInputToString(InputStream inputStream) throws IOException {
        BufferedReader buferredReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String resultado = "";
        while ((line = buferredReader.readLine()) != null)
            resultado += line;
        inputStream.close();
        return resultado;
    }

    //05
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return recuperarContenido(urls[0]);
        }

        //onPostExecute displays the results of the AsyncTask.
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Datos recibidos", Toast.LENGTH_SHORT).show();
            try {
                JSONArray jsonarray = new JSONArray(result);
                List<ToDos> lista = convertirJsonToDos(jsonarray);
                //Especificamos el adaptador con la lista a visualizar
                miAdapter = new Adaptador(lista);
                miRecicler.setAdapter(miAdapter);

            } catch (JSONException e) {
                System.out.println(e.toString());
            }

        }


        public List<ToDos> convertirJsonToDos(JSONArray jsonarray) throws JSONException {
            List<ToDos> lista = new ArrayList<>();

            for (int i = 0; i < jsonarray.length(); i++) {
                ToDos todito = new ToDos();
                String tit, des, idea, tod, imp, has, dat, col, iden;

                tit = jsonarray.getJSONObject(i).optString("JSON_TITLE").toString();
                des = jsonarray.getJSONObject(i).optString("JSON_DESCRIPTION").toString();
                idea = jsonarray.getJSONObject(i).optString("JSON_IDEA").toString();
                tod = jsonarray.getJSONObject(i).optString("JSON_TODO").toString();
                imp = jsonarray.getJSONObject(i).optString("JSON_IMPORTANT").toString();
                has = jsonarray.getJSONObject(i).optString("JSON_HASREMINDER").toString();
                dat = jsonarray.getJSONObject(i).optString("JSON_DATETIME").toString();
                col = jsonarray.getJSONObject(i).optString("JSON_COLOR").toString();
                iden = jsonarray.getJSONObject(i).optString("JSON_IDENTIFIER").toString();


                todito.setTitle(tit);
                todito.setDescription(des);
                todito.setIdea(Boolean.valueOf(idea));
                todito.setTodo(Boolean.valueOf(tod));
                todito.setImportant(Boolean.valueOf(imp));
                todito.setReminder(Boolean.valueOf(has));
                todito.setToDoDate(ParseFecha(dat));
                todito.setTodoColor(Integer.parseInt(col));
                todito.setTodoIdentifier(iden);

                lista.add(todito);


            }

            return lista;

        }

    }


    /**
     * 05.5
     * Permite convertir un String en fecha (Date).
     *
     * @param fecha Cadena de fecha "dd-MM-yyyy HH:mm:ss"
     * @return Objeto Date
     */
    public static Date ParseFecha(String fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return fechaDate;
    }

    // 06 m??todo  para infrar el menu superior

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navegacion, menu);
        return true;
    }

    //07 m??todo para gestionar el item seleccionado

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int id = item.getItemId();

        Intent accion;

        if (id == R.id.item_consultar) {
            Intent i = new Intent(this, com.beastek.tareas.Consultas.Listado_registros_ToDo.class);
            startActivity(i);

        } else if (id == R.id.item_alta_registro) {
            Intent i = new Intent(this, Alta_registro_todo.class);
            startActivity(i);
        } else if (id == R.id.item_signup) {
            Intent i = new Intent(this, SignUp_Registro.class);
            startActivity(i);
        } else if (id == R.id.item_consultarporId) {

            //accion = new Intent("android.intent.action.VIEW", Uri.parse("http://developer.android.com"));
            accion = new Intent(this, com.beastek.tareas.Consultas.Buscarporid.class);
            startActivity(accion);
        } else if (id== R.id.item_consultarpornombre){
            accion = new Intent (this, BuscarporNombre.class);
            startActivity(accion);
        }
        return true;
    }

    //08 creaci??n del mensaje personalizado Toast seg??n sea aceptado o cancelado

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

    //09
    public void cerrarVentana(View view) {
        finish();
    }


}



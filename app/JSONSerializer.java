package com.juangabrielgomila.todolistjb;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuanGabriel on 4/12/17.
 */

public class JSONSerializer {

    private String mFilename; //Nombre del fichero JSON que va a guardar la clase
    private Context mContext; //Contexto de dónde se va a guardar ese fichero JSON


    //Constructor del objeto que va a serializar en ficheros JSON
    public JSONSerializer(String filename, Context context){
        this.mFilename = filename;
        this.mContext = context;
    }


    public void save(List<Note> notes) throws IOException, JSONException{

        //Array de objetos JSON
        JSONArray jArray = new JSONArray();

        //Convertir cada una de las Note en objetos JSONObject y guardarlos en el JSON Array
        for (Note n: notes) {
            jArray.put(n.convertNoteToJSON());
        }

        //Para guardar el fichero de objetos JSON, hay que usar un Writer
        Writer writer = null;
        try {
            //Output Stream abre el fichero donde guardaremos el JSON
            OutputStream out = mContext.openFileOutput(mFilename, mContext.MODE_PRIVATE);
            //El escritor, ya sabe donde escribir su contenido, en qué fichero JSON
            writer = new OutputStreamWriter(out);
            //El escritor escribe en el disco tooodo el array pasado a formato String
            writer.write(jArray.toString());
        } finally {
            //Si el writer había podido abrir el fichero, es importante que lo cierre para evitar que se corrompa...
            if (writer != null){
                writer.close();
            }
        }
    }



    public ArrayList<Note> load() throws  IOException, JSONException{

        //Array de objetos Note en Java
        ArrayList<Note> notes = new ArrayList<Note>();

        //Buffered reader para leer el fichero de JSON
        BufferedReader reader = null;
        try {
            //Input Stream abre el fichero JSON que vamos a leer y procesar
            InputStream in = mContext.openFileInput(mFilename);
            //El lector, ya sabe de donde leer los datos, de qué fichero JSON
            reader = new BufferedReader(new InputStreamReader(in));
            //Leemos los strings del fichero JSON con un String Builder
            StringBuilder jsonString = new StringBuilder();
            //Variable para leer la línea actual...
            String currentLine = null;

            //Leer el fichero JSON entero, hasta acabarlo y pasarlo todo a String
            //Mientras la línea actual no sea nula...
            while ( (currentLine = reader.readLine())!=null){
                jsonString.append(currentLine);
            }

            //Hemos pasado de un fichero JSON -> String largo largo, con todos los objetos Note

            //Pasamos de un array entero de Strings a un array de objetos JSON
            JSONArray jArray = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i <jArray.length(); i++) {
                notes.add(new Note(jArray.getJSONObject(i)));
            }
            //Llegados aquí, ya tenemos el array de notes con todos los objetos de la clase Note...

        }catch(FileNotFoundException e){
            //La primera vez nos va a petar si o si, porque no hay fichero de notas que leer.
            //En este caso, nos basta ignorar la excepción ya que es normal...
        } finally {
            //Si el reader había abierto el fichero, es hora de cerrarlo para que no se corrompa...
            if (reader != null){
                reader.close();
            }
        }
        return notes;
    }






















}
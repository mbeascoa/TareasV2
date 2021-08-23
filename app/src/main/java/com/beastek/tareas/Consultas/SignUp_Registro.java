package com.beastek.tareas.Consultas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.beastek.tareas.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUp_Registro extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText TextEmail, TextPassword;
    private TextView resultado;
    private Button btnRegistrar, btnSalirRegistro;
    private ProgressDialog progressDialog;

    //Declaramos un objeto firebaseAuth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_registro);

        resultado = (TextView) findViewById(R.id.tv_resultado_registro);
        btnRegistrar= (Button) findViewById(R.id.btn_volver_signup);
        btnRegistrar=(Button)findViewById(R.id.botonRegistrar_Signup);

        //inicializamos el objeto firebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //Referenciamos los views
        TextEmail = (EditText) findViewById(R.id.TxtEmail);
        TextPassword = (EditText) findViewById(R.id.TxtPassword);
        resultado = (TextView) findViewById(R.id.tv_resultado_registro);
        btnSalirRegistro= (Button) findViewById(R.id.btn_volver_signup);
        btnRegistrar=(Button)findViewById(R.id.botonRegistrar_Signup);


        progressDialog = new ProgressDialog(this);

        //attaching listener to button
        btnRegistrar.setOnClickListener(this);


    }
    /*
    // 02 check current auth state
    // When initializing your Activity, check to see if the user is currently signed in
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

     */

    private void registrarUsuario(){

        //Obtenemos el email y la contraseña desde las cajas de texto
        String email = TextEmail.getText().toString().trim();
        String password  = TextPassword.getText().toString().trim();

        //Verificamos que las cajas de texto no estén vacías
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Se debe ingresar un email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Falta ingresar la contraseña",Toast.LENGTH_LONG).show();
            return;
        }


        progressDialog.setMessage("Realizando registro en linea...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){

                            Toast.makeText(SignUp_Registro.this,"Se ha registrado el usuario con el email: "+ TextEmail.getText(),Toast.LENGTH_LONG).show();
                            resultado.setText("Se ha registrado el usuario con el mail : " + TextEmail.getText());
                        }else{

                            Toast.makeText(SignUp_Registro.this,"No se pudo registrar el usuario ",Toast.LENGTH_LONG).show();
                            resultado.setText("No se pudo registrar el usuario con el mail : " + TextEmail.getText());
                        }
                        progressDialog.dismiss();
                    }
                });

    }
    // 07 para la acción de presionar el boton
    @Override
    public void onClick(View view) {
        //Invocamos al método:
        registrarUsuario();
    }

    //08 para regresar

    public void cerrarVentana(View view) {
          finish();
      }

    // 09 método  para infrar el menu superior

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navegacion, menu);
        return true;
    }

    //10 método para gestionar el item seleccionado

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
            accion = new Intent(this, com.beastek.tareas.Consultas.Buscarporid.class);
            startActivity(accion);
        }
        return true;
    }

}
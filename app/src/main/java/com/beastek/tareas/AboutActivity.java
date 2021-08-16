package com.beastek.tareas;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    private TextView mVersionTextView;
    private String appVersion = "0.1";
    private TextView contactMe;
    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mVersionTextView = (TextView) findViewById(R.id.aboutVersionTextView);
        mVersionTextView.setText(String.format(getResources().getString(R.string.app_version), appVersion));
        exit =(Button) findViewById(R.id.btn_salir_about);


        contactMe = (TextView) findViewById(R.id.aboutContactMe);

        contactMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void aboutExit (View view){
            finish();
    }

}

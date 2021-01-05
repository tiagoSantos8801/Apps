package com.example.appifood.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.appifood.R;

public class SplashActivity extends AppCompatActivity {

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_splash);

          //getSupportActionBar().hide();//Esconde actionBar

          new Handler().postDelayed(new Runnable() {//Tempo de exibicao da splash na tela
               @Override
               public void run() {
                    abrirAutenticacao();
               }
          }, 3000);//Tempo
     }

     public void abrirAutenticacao(){//Nova activity

          Intent intent = new Intent(SplashActivity.this, AutenticationActivity.class);
          startActivity(intent);//Inicialisando a AutenticationActivity.class
          finish();//Encerrando essa activity
     }
}
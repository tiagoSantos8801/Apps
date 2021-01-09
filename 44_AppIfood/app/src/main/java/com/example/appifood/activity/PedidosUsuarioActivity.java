package com.example.appifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.appifood.R;
import com.example.appifood.model.Empresa;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosUsuarioActivity extends AppCompatActivity {

     private AlertDialog dialog;
     private DatabaseReference databaseRef;
     private List<Empresa> empresas = new ArrayList<>();
     private List<Empresa> pedidos = new ArrayList<>();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_pedidos_usuario);

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Histórico de pedidos");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity

//          //Pegando Lista de Empresas
//          recuperaEmpresas();
//
//          //Recperando pedidos - Finalizados
//          recuperarPedidosFinalizasdos();

     }

//     private void recuperarPedidosFinalizasdos(){
//
//          dialog = new SpotsDialog.Builder()
//                  .setContext(this)
//                  .setMessage("Carregando dados...")
//                  .setCancelable(false)
//                  .build();
//          dialog.show();
//
//          DatabaseReference pedidosRef = databaseRef.child("pedidos");
//          pedidosRef.addValueEventListener(new ValueEventListener() {
//               @Override
//               public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    empresas.clear();
//                    for (DataSnapshot empresasBanco: dataSnapshot.getChildren()) {
//                         empresas.add(empresasBanco.getValue(Empresa.class));
//
//                    }
//                    //adapterEmpresa.notifyDataSetChanged();
//                    dialog.dismiss();
//               }
//
//               @Override
//               public void onCancelled(DatabaseError databaseError) {
//
//               }
//          });
//     }

//     private void recuperaEmpresas(){
//
//          dialog = new SpotsDialog.Builder()
//                  .setContext(this)
//                  .setMessage("Carregando dados...")
//                  .setCancelable(false)
//                  .build();
//          dialog.show();
//
//          DatabaseReference empresaRef = databaseRef.child("empresas");
//          empresaRef.addValueEventListener(new ValueEventListener() {
//               @Override
//               public void onDataChange(DataSnapshot dataSnapshot) {
//
//                    pedidos.clear();
//                    for (DataSnapshot empresasBanco: dataSnapshot.getChildren()) {
//                         pedidos.add(empresasBanco.getValue(Empresa.class));
//
//                    }
//                    //adapterEmpresa.notifyDataSetChanged();
//                    dialog.dismiss();
//               }
//
//               @Override
//               public void onCancelled(DatabaseError databaseError) {
//
//               }
//          });
//     }

     @Override//Criando o menu
     public boolean onCreateOptionsMenu(Menu menu) {

          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.menu_historico_pedidos_usuario, menu);
          return super.onCreateOptionsMenu(menu);
     }

     @Override//Opcoes selecionadas
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

          switch (item.getItemId()){
               case R.id.menuSair:
                    fecharHistorico();
                    break;
          }
          return super.onOptionsItemSelected(item);
     }

     private void fecharHistorico() {
          finish();
     }
}
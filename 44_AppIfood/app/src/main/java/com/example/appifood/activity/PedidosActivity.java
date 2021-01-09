package com.example.appifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.appifood.R;
import com.example.appifood.adapter.AdapterPedido;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.helper.RecyclerItemClickListener;
import com.example.appifood.helper.UsuarioFireBase;
import com.example.appifood.model.Pedido;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidosActivity extends AppCompatActivity {

     private RecyclerView recyclerPedidos;
     private AdapterPedido adapterPedido;
     private List<Pedido> pedidos = new ArrayList<>();
     private AlertDialog dialog;
     private DatabaseReference databaseRef;
     private String idEmp;
     private RecyclerItemClickListener recycleList =  null;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_pedidos);
          
          inicializarComponentes();
          
          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Pedidos");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Volta para o parent

          //Cinfigura RecyclerView - Passando parametros para adapter
          recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
          recyclerPedidos.setHasFixedSize(true);
          adapterPedido = new AdapterPedido(pedidos);
          recyclerPedidos.setAdapter(adapterPedido);
          //Configurando click no recyclerview
          inicializarListener();
          recyclerPedidos.addOnItemTouchListener(recycleList);

          recuperarPedidos();
     }

     private void inicializarListener(){
          recycleList = new RecyclerItemClickListener(
                  this,
                  recyclerPedidos,
                  new RecyclerItemClickListener.OnItemClickListener() {
                       @Override
                       public void onItemClick(View view, int position) {

                       }

                       @Override
                       public void onLongItemClick(View view, int position) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(PedidosActivity.this);//this - do recycler view
                            builder.setTitle("Deseja retirar da lista de pedidos ?");
                            builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {
                                      if (pedidos.size()==1){
                                           recyclerPedidos.setVisibility(View.GONE);
                                      }
                                      Pedido pedido = pedidos.get(position);
                                      pedido.setStatus("Finalizado!");
                                      pedido.atualizarStatus();
                                 }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                 @Override
                                 public void onClick(DialogInterface dialogInterface, int i) {

                                 }
                            });

                            AlertDialog dialog = builder.create();
                            dialog.show();

                       }

                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                       }
                  }
          );
     }

     private void recuperarPedidos() {

          //Dialog de efeito de carregamento
          dialog = new SpotsDialog.Builder()
                                   .setContext(this)
                                   .setMessage("Carregando dados...")
                                   .setCancelable(false)
                                   .build();
          dialog.show();

          DatabaseReference pedidoRef = databaseRef.child("pedidos")
                         .child(idEmp);//Referencia do banco

          Query pedidosPesquisa = pedidoRef.orderByChild("status").equalTo("Confirmado");//Pesquisa pedidos confirmados

          pedidosPesquisa.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                    pedidos.clear();
                    if (dataSnapshot.getValue() != null){
                         for (DataSnapshot ds : dataSnapshot.getChildren()){//Pegando lista de pedidos
                              Pedido pedido = ds.getValue(Pedido.class);//Pegango response nesse formato
                              pedidos.add(pedido);
                         }
                         recyclerPedidos.setVisibility(View.VISIBLE);
                         adapterPedido.notifyDataSetChanged();
                         dialog.dismiss();
                    } else {
                         dialog.dismiss();
                         Toast.makeText(PedidosActivity.this,
                                 "Não há pedidos! ", Toast.LENGTH_LONG).show();
                    }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PedidosActivity.this,
                            "Erro no banco! ", Toast.LENGTH_SHORT).show();
                    Log.i("DataBase", "Firebase dados: ");
               }
          });

     }

     private void inicializarComponentes() {

          recyclerPedidos = findViewById(R.id.recyclerPedidos);
          idEmp = UsuarioFireBase.getIdUsuario();

          databaseRef = ConfiguracaoFireBase.getFireBase();

     }
}
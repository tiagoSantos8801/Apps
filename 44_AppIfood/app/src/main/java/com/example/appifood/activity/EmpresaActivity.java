package com.example.appifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.appifood.R;
import com.example.appifood.adapter.AdapterProduto;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.helper.RecyclerItemClickListener;
import com.example.appifood.helper.UsuarioFireBase;
import com.example.appifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

     private FirebaseAuth auth;
     private RecyclerView recyclerProdutos;
     private AdapterProduto adapterProduto;
     private List<Produto> produtos = new ArrayList<>();
     private DatabaseReference databaseRef;
     private String idUsuarioLogado;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_empresa);

          inicializarComponentes();

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Ifood - Empresa");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity

          auth = ConfiguracaoFireBase.getFirebaseAutentificacao();//Pegando acesso ao usuario

          //Cinfigura RecyclerView - Passando parametros para adapter
          recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
          recyclerProdutos.setHasFixedSize(true);
          adapterProduto = new AdapterProduto(produtos, this);
          recyclerProdutos.setAdapter(adapterProduto);

          //Recuperando produtos da empresa
          recuperaProdutos();

          //Adcionar evneto de click no reciclerView
          recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                  this,
                  recyclerProdutos,
                  new RecyclerItemClickListener.OnItemClickListener() {
                       @Override
                       public void onItemClick(View view, int position) {

                       }
                       //Excluindo produto
                       @Override
                       public void onLongItemClick(View view, int position) {

                            Produto produtoSelecionado = produtos.get(position);
                            produtoSelecionado.remover();

                            Toast.makeText(EmpresaActivity.this,
                                    "Produto removido com sucesso!", Toast.LENGTH_SHORT).show();
                       }

                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                       }
                  }
          ));

     }

     public void recuperaProdutos(){

          DatabaseReference produtosRef = databaseRef.child("produtos")
                                                       .child(idUsuarioLogado);
          produtosRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                    produtos.clear();//limpaLista
                    for (DataSnapshot produtosBanco : dataSnapshot.getChildren()){
                         produtos.add(produtosBanco.getValue(Produto.class));//listando os produtos
                    }
                    adapterProduto.notifyDataSetChanged();//recarrega onbindViewHolder
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });
     }

     @Override//Criando o menu
     public boolean onCreateOptionsMenu(Menu menu) {

          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.menu_empresa, menu);

          return super.onCreateOptionsMenu(menu);
     }

     @Override//Opcoes selecionadas
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

          switch (item.getItemId()){
               case R.id.menuSair:
                    deslogarUsuario();
                    break;
               case R.id.menuConfiguracoes:
                    abrirConfiguracoes();
                    break;
               case R.id.menuNovoProduto:
                    abrirNovoProduto();
                    break;
               case R.id.menuPedidos:
                    abrirPedidos();
                    break;
          }

          return super.onOptionsItemSelected(item);
     }

     private void abrirPedidos() {
          startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
     }

     private void abrirNovoProduto() {
          startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
     }

     private void abrirConfiguracoes() {
          startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
     }

     private void deslogarUsuario() {
          try {//Tratando possivel erro ao deslogar usuario
               auth.signOut();
               startActivity(new Intent(EmpresaActivity.this, AutenticationActivity.class));
               finish();
          } catch (Exception e){
               e.printStackTrace();
          }
     }

     public void inicializarComponentes(){
          recyclerProdutos = findViewById(R.id.recyclerProdutos);

          idUsuarioLogado = UsuarioFireBase.getIdUsuario();
          databaseRef = ConfiguracaoFireBase.getFireBase();
     }
}
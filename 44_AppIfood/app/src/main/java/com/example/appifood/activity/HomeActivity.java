package com.example.appifood.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appifood.R;
import com.example.appifood.adapter.AdapterEmpresa;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.helper.RecyclerItemClickListener;
import com.example.appifood.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity {

     private FirebaseAuth auth;//Para acessar
     private MaterialSearchView searchView;
     private RecyclerView recyclerView;
     private List<Empresa> empresas = new ArrayList<>();
     private DatabaseReference fireBaseRef;
     private AdapterEmpresa adapterEmpresa;
     private AlertDialog dialog;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_home);

          //Inicializar componentes
          inicializarComponentes();

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Ifood");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity

          //Recupera lista de Obj empresas do baco
          recuperaEmpresas();

          //Configurando recyclerView
          recyclerView.setLayoutManager(new LinearLayoutManager(this));
          recyclerView.setHasFixedSize(true);
          adapterEmpresa = new AdapterEmpresa(empresas);
          recyclerView.setAdapter(adapterEmpresa);

          //Configuracao do searchView
          searchView.setHint("Pesquisar por restaurantes ");
          searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {//Listner de pesquisa
               @Override
               public boolean onQueryTextSubmit(String query) {//No enter
                    return false;
               }

               @Override
               public boolean onQueryTextChange(String newText) {//Ao digitar - (newText - texto digitado pelo usuario)
                    pesquisarEmpresas(newText);
                    return true;
               }
          });

          //Configurando evento de clck no recyclerView
          recyclerView.addOnItemTouchListener(
                  new RecyclerItemClickListener(
                          this,
                          recyclerView,
                          new RecyclerItemClickListener.OnItemClickListener() {
                               @Override
                               public void onItemClick(View view, int position) {

                                    Empresa empresaSelecionada = empresas.get(position);//Pega empresa da lista
                                    Intent intent = new Intent(HomeActivity.this, CardapioActivity.class);//Abri actv
                                    intent.putExtra("empresa", empresaSelecionada);//Passa classe(empreza) serializable
                                    startActivity(intent);//Inicia nova activity
                               }
                               @Override
                               public void onLongItemClick(View view, int position) {

                               }
                               @Override
                               public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                               }
                          }
                  )
          );

     }

     private void pesquisarEmpresas(String pesquisa){

          DatabaseReference empresaRef = fireBaseRef.child("empresas");//Caminho
          Query query = empresaRef.orderByChild("nome").startAt(pesquisa).endAt(pesquisa + "\uf8ff");//Pesquisa - query
          query.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {//Dados retornados

                    empresas.clear();
                    for (DataSnapshot empresasBaco: dataSnapshot.getChildren()) {
                         empresas.add(empresasBaco.getValue(Empresa.class));
                    }
                    adapterEmpresa.notifyDataSetChanged();//Atualiza o onBind - mudancas na lista que vai para o adapter
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });
     }

     private void recuperaEmpresas(){

          dialog = new SpotsDialog.Builder()
                  .setContext(this)
                  .setMessage("Carregando dados...")
                  .setCancelable(false)
                  .build();
          dialog.show();

          DatabaseReference empresaRef = fireBaseRef.child("empresas");
          empresaRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                    empresas.clear();
                    for (DataSnapshot empresasBanco: dataSnapshot.getChildren()) {
                         empresas.add(empresasBanco.getValue(Empresa.class));

                    }
                    adapterEmpresa.notifyDataSetChanged();
                    dialog.dismiss();
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });
     }

     @Override//Criando o menu
     public boolean onCreateOptionsMenu(Menu menu) {

          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.menu_usuario, menu);

          //Configurando botao de pesquisa
          MenuItem menuItem = menu.findItem(R.id.menuPesquisa);//Pega o id do item de menu
          searchView.setMenuItem(menuItem);//Item do menu que sera a searchView

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
          }
          return super.onOptionsItemSelected(item);
     }

     private void abrirConfiguracoes() {
          startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuarioActivity.class));
     }

     private void deslogarUsuario() {
          try {//Tratando possivel erro ao deslogar usuario
               auth.signOut();
               startActivity(new Intent(HomeActivity.this, AutenticationActivity.class));
               finish();
          } catch (Exception e){
               e.printStackTrace();
          }
     }

     private void inicializarComponentes(){
          searchView = findViewById(R.id.materialSearchView);
          recyclerView = findViewById(R.id.recyclerEmpresa);

          fireBaseRef = ConfiguracaoFireBase.getFireBase();
          auth = ConfiguracaoFireBase.getFirebaseAutentificacao();//Pegando acesso ao usuario

     }
}
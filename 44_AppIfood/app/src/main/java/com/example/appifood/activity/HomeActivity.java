package com.example.appifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.appifood.R;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.google.firebase.auth.FirebaseAuth;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class HomeActivity extends AppCompatActivity {

     private FirebaseAuth auth;//Para acessar
     private MaterialSearchView searchView;

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

          auth = ConfiguracaoFireBase.getFirebaseAutentificacao();//Pegando acesso ao usuario

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
     }
}
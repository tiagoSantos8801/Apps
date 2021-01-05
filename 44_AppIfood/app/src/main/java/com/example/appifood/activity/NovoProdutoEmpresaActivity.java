package com.example.appifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appifood.R;
import com.example.appifood.helper.UsuarioFireBase;
import com.example.appifood.model.Empresa;
import com.example.appifood.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

     private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;

     private String idUsuarioLogado;
     private String urlImagemSelecionada = "";

     private FirebaseAuth auth;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_novo_produto_empresa);

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Novo Produto");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Volta para o parent

          inicializarComponemtes();
     }

     public void validarDadosProduto(View view){

          String nome = editProdutoNome.getText().toString();
          String descricao = editProdutoDescricao.getText().toString();
          String preco = editProdutoPreco.getText().toString();

          //Validacao
          if (!nome.isEmpty()){
               if (!descricao.isEmpty()){
                    if (!preco.isEmpty()){

                         //Configurando model
                         Produto produto = new Produto();
                         produto.setIdUsuario(idUsuarioLogado);
                         produto.setNome(nome);
                         produto.setDescricao(descricao);
                         produto.setPreco(Double.parseDouble(preco));
                         produto.salvar();
                         finish();
                         exibeMensagem("Produto salvo com sucesso!");

                    }else {
                         exibeMensagem("Preencha o preco! ");
                    }
               }else {
                    exibeMensagem("Preencha a descricao! ");
               }
          }else {
               exibeMensagem("Preencha o nome! ");
          }
     }

     private void exibeMensagem(String mensagem){
          Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
     }
     public void inicializarComponemtes(){

          editProdutoNome = findViewById(R.id.editProdutoNome);
          editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
          editProdutoPreco = findViewById(R.id.editProdutoPreco);

          idUsuarioLogado = UsuarioFireBase.getIdUsuario();
     }
}
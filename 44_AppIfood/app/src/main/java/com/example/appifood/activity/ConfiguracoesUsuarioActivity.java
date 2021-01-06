package com.example.appifood.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appifood.R;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.helper.UsuarioFireBase;
import com.example.appifood.model.Empresa;
import com.example.appifood.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

     ImageView imagePerfilUsuario;
     EditText nomeUsuario, cidadeBairroUsuario, ruaNumeroUsuario;
     Button buttonSalvarUsuario;

     private static final int SELECAO_GALERIA = 200;
     private String idUsuarioLogado;
     private String urlImagemSelecionada = "";

     private StorageReference storageRef;
     private DatabaseReference databaseRef;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_configuracoes_usuario);

          inicializarComponentes();

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Configurações Usuário");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Volta para o parent (Home)

          buttonSalvarUsuario.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    validarDadosEmpresa(view);
               }
          });

          //Comfigurando imagem de perfil do usuario - onClick
          imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    );//A acao da activity(pick == pegar) e o endereco de onde ela vai agir - caminho

                    if (i.resolveActivity(getPackageManager()) != null){//Checa se conseguio resoler a acao pedida
                         startActivityForResult(i, SELECAO_GALERIA);//Acessando de fato - SELECAO_GALERIA - "iddaActivity"
                    }
               }
          });

          recuperaDadosUsuario();
     }

     public void recuperaDadosUsuario(){

          DatabaseReference empresaRef = databaseRef.child("usuarios").child(idUsuarioLogado);
          empresaRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){//Campos preenchidos

                         Usuario usuario = dataSnapshot.getValue(Usuario.class);//Response no formato Json

                         nomeUsuario.setText(usuario.getNomeUsuario());//Setando dados que vieram do banco
                         cidadeBairroUsuario.setText(usuario.getCidadeBairro());
                         ruaNumeroUsuario.setText(usuario.getRuaNumero());

                         urlImagemSelecionada = usuario.getUrlImagem();
                         if (urlImagemSelecionada != ""){
                              Picasso.get()//Biblioteca importada
                                      .load(urlImagemSelecionada)//carrega a img
                                      .into(imagePerfilUsuario);//recupera
                         }
                    }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if (resultCode == RESULT_OK){//Status code
               Bitmap imagem = null;//Cria variavel que recebe o mapa de pixes da imagem

               try {

                    switch (requestCode){//SELECAO_GALERIA
                         case SELECAO_GALERIA:
                              Uri localImagem = data.getData();//caminho relativo - local do mapa de bytes da imagem

                              //pega mapa de pixels da imagem
                              imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                              break;
                    }
                    if (imagem != null){
                         imagePerfilUsuario.setImageBitmap(imagem);//Setenado imagem na view

                         //Upload da imagem - fireStorage
                         //Formato de trafego https
                         ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                         byte[] dadosImagem = baos.toByteArray();

                         StorageReference imageRef = storageRef.child("imagens")
                                                                 .child("usuarios")
                                                                 .child(idUsuarioLogado + "jpeg");//No executavel
                         UploadTask task = imageRef.putBytes(dadosImagem);
                         task.addOnFailureListener(new OnFailureListener() {//Falha
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                           "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                              }
                         }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {//Sucesso

                                   //urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();//Salvando url da imagem
                                   //Recuperando url da forma nova - so pega a url da imagem quem aponta,que sabe o caminho
                                   imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                             Uri uri = task.getResult();//URI e a URL interna
                                             urlImagemSelecionada = uri.toString();
                                        }
                                   });

                                   Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                           "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                              }
                         });
                    }
               }catch (Exception e){
                    e.printStackTrace();
               }
          }
     }

     public void validarDadosEmpresa(View view){

          String nome = nomeUsuario.getText().toString();
          String cidadeBairro = cidadeBairroUsuario.getText().toString();
          String ruaNumero = ruaNumeroUsuario.getText().toString();

          //Validacao
          if (!nome.isEmpty()){
               if (!cidadeBairro.isEmpty()){
                    if (!ruaNumero.isEmpty()){

                         //Configurando model
                         Usuario usuario = new Usuario();

                         usuario.setNomeUsuario(nome);
                         usuario.setCidadeBairro(cidadeBairro);
                         usuario.setRuaNumero(ruaNumero);
                         usuario.setIdUsuario(idUsuarioLogado);
                         usuario.setUrlImagem(urlImagemSelecionada);
                         usuario.salvar();

                         exibeMensagem("Dados atualizados com sucesso!");
                         finish();

                    }else {
                         exibeMensagem("Preencha a Rua e o Número da residêcia! ");
                    }
               }else {
                    exibeMensagem("Preencha a Cidade e o Bairro! ");
               }
          }else {
               exibeMensagem("Preencha o nome! ");
          }
     }

     private void exibeMensagem(String mensagem){
          Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
     }

     public void inicializarComponentes(){

          imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);
          nomeUsuario = findViewById(R.id.nomeUsuario);
          cidadeBairroUsuario = findViewById(R.id.cidadeBairro);
          ruaNumeroUsuario = findViewById(R.id.ruaBairro);
          buttonSalvarUsuario = findViewById(R.id.buttonSalvarUsuario);

          //FireBase
          databaseRef = ConfiguracaoFireBase.getFireBase();
          storageRef = ConfiguracaoFireBase.getFirebaseStorage();
          idUsuarioLogado = UsuarioFireBase.getIdUsuario();
     }

}
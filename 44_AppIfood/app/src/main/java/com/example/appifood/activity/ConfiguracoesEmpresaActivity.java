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

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

     private ImageView fotoPerfilEmp;
     private EditText nomeEmp, catedoriaEmp,tempoEntregaEmp, taxaEmp;
     private Button buttonSalvarEmp;

     private static final int SELECAO_GALERIA = 100;
     private String idUsuarioLogado;
     private String urlImagemSelecionada = "";

     private StorageReference storageReference;
     private DatabaseReference databaseReference;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_configuracoes_empresa);

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Configurações");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Volta para o parent

          inicializarComponemtes();

          buttonSalvarEmp.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    validarDadosEmpresa(view);
               }
          });

          fotoPerfilEmp.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    );//A acao da activity e o endereco de onde ela vai agir - caminho

                    if (i.resolveActivity(getPackageManager()) != null){//Checa se conseguio resoler a acao pedida
                         startActivityForResult(i, SELECAO_GALERIA);//Acessando de fato
                    }
               }
          });

          //Recuperar dados da empresa
          recuperaDadosEmpresa();
     }

     public void recuperaDadosEmpresa(){

          DatabaseReference empresaRef = databaseReference.child("empresas").child(idUsuarioLogado);
          empresaRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null){
                         Empresa empresa = dataSnapshot.getValue(Empresa.class);//Response no formato Json
                         nomeEmp.setText(empresa.getNome());
                         catedoriaEmp.setText(empresa.getCategoria());
                         taxaEmp.setText((empresa.getTaxa()).toString());
                         tempoEntregaEmp.setText(empresa.getTempo());

                         urlImagemSelecionada = empresa.getUrlImagem();
                         if (urlImagemSelecionada != ""){
                              Picasso.get()//Biblioteca importada
                                      .load(urlImagemSelecionada)//carrega a img
                                      .into(fotoPerfilEmp);//recupera
                         }
                    }
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });
     }

     //Pegando o resultado
     @Override                      //id requisicao
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if (resultCode == RESULT_OK){//Status code
               Bitmap imagem = null;

               try {
                    switch (requestCode){//SELECAO_GALERIA
                         case SELECAO_GALERIA:
                              Uri localImagem = data.getData();//caminho relativo - local do mapa
                              imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);//pega mapa de pixels da imagem
                              break;
                    }
                    if (imagem != null){
                         fotoPerfilEmp.setImageBitmap(imagem);//Setenado imagem na view

                         //Upload da imagem - fireStorage
                         //Formato de trafego https
                         ByteArrayOutputStream baos = new ByteArrayOutputStream();
                         imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                         byte[] dadosImagem = baos.toByteArray();//Formato para saida de arquivos

                         StorageReference imageRef = storageReference.child("imagens")
                                                                      .child("empresas")
                                                                      .child(idUsuarioLogado + "jpeg");//No executavel
                         UploadTask task = imageRef.putBytes(dadosImagem);
                         task.addOnFailureListener(new OnFailureListener() {//Falha
                              @Override
                              public void onFailure(@NonNull Exception e) {
                                   Toast.makeText(ConfiguracoesEmpresaActivity.this,
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

                                   Toast.makeText(ConfiguracoesEmpresaActivity.this,
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

          String nome = nomeEmp.getText().toString();
          String categoria = catedoriaEmp.getText().toString();
          String tempo = tempoEntregaEmp.getText().toString();
          String taxa = taxaEmp.getText().toString();

          //Validacao
          if (!nome.isEmpty()){
               if (!categoria.isEmpty()){
                    if (!tempo.isEmpty()){
                         if (!taxa.isEmpty()){

                              //Configurando model
                              Empresa empresa = new Empresa();
                              empresa.setNome(nome);
                              empresa.setCategoria(categoria);
                              empresa.setTempo(tempo);
                              empresa.setTaxa(Double.parseDouble(taxa));
                              empresa.setIdUsuario(idUsuarioLogado);
                              empresa.setUrlImagem(urlImagemSelecionada);
                              empresa.salvar();
                              finish();

                         }else {
                              exibeMensagem("Preencha a taxa de entrega! ");
                         }
                    }else {
                         exibeMensagem("Preencha o tempo de entrega! ");
                    }
               }else {
                    exibeMensagem("Preencha a categoria! ");
               }
          }else {
               exibeMensagem("Preencha o nome! ");
          }
     }

     private void exibeMensagem(String mensagem){
          Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
     }

     public void inicializarComponemtes(){

          nomeEmp = findViewById(R.id.nomeUsuario);
          catedoriaEmp = findViewById(R.id.cidadeBairro);
          tempoEntregaEmp = findViewById(R.id.ruaBairro);
          taxaEmp = findViewById(R.id.taxaEmp);
          buttonSalvarEmp = findViewById(R.id.buttonSalvarUsuario);
          fotoPerfilEmp = findViewById(R.id.imagePerfilUsuario);

          idUsuarioLogado = UsuarioFireBase.getIdUsuario();

          storageReference = ConfiguracaoFireBase.getFirebaseStorage();
          databaseReference = ConfiguracaoFireBase.getFireBase();

     }
}
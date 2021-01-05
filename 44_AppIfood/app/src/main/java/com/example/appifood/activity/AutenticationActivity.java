package com.example.appifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.appifood.R;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.helper.UsuarioFireBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class AutenticationActivity extends AppCompatActivity {

     private EditText editTextSenha, editTextEmail;
     private Switch tipoAcesso, tipoUsuario;
     private Button buttonAcessar;
     private LinearLayout linearTipoUsuario;

     private FirebaseAuth autentificacao;


     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_autentication);

         //getSupportActionBar().hide();//Escondendo ActionBar

          inicializarComponentes();

          //Inicializando
          autentificacao = ConfiguracaoFireBase.getFirebaseAutentificacao();//Agora pode-se acessar metodos de autentificacao
          autentificacao.signOut();//Deslogando usuario

          //Verificar usuario logado
          verificarUsuarioLogado();

          //Pegar Tipo de acesso - Mostrar todos os switchs
          tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton compoundButton, boolean isCheckd) {//boolean b - informa se o switch esta marcado
                    if (isCheckd){//Empresa
                         linearTipoUsuario.setVisibility(View.VISIBLE);//Se marcado, mostra os tipos de cadastro
                    } else {//Usuario
                         linearTipoUsuario.setVisibility(View.GONE);
                    }
               }
          });

          buttonAcessar.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {

                    String email = editTextEmail.getText().toString();
                    String senha = editTextSenha.getText().toString();

                    if (!email.isEmpty()){//Chacer se os campos estao vazios
                         if (!senha.isEmpty()){

                              //Verificando estado do switch
                              if (tipoAcesso.isChecked()){//Cadastro

                                   autentificacao.createUserWithEmailAndPassword(email, senha)//A ordem dos parametros e relevante
                                           .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {//Task traz o resultado de listnere
                                             if (task.isSuccessful()){//Caso tenha sucesso

                                                  Toast.makeText(AutenticationActivity.this,
                                                          "Cadastro realizado com sucesso! ",
                                                          Toast.LENGTH_SHORT).show();

                                                  String tipoUsuario = getTipoUsuario();//Checa se o usuario e (Usu/Emp)
                                                  UsuarioFireBase.atualizarTipoUsuario(tipoUsuario);//Passa para o firebase
                                                  abrirtelaPrincipal(tipoUsuario);//Abrir nova Activity - Finalisa atual

                                             }else{//Caso falhe

                                                  String errorExcecao = "";
                                                  try {
                                                       throw task.getException();//Lanca o erro
                                                  }catch (FirebaseAuthWeakPasswordException e){//Tratando possiveis erros
                                                       errorExcecao = "Digite uma senha mais forte!";
                                                  }catch (FirebaseAuthInvalidCredentialsException e){
                                                       errorExcecao = "Informe um email válido!";
                                                  }catch (FirebaseAuthUserCollisionException e){
                                                       errorExcecao = "Esta conta já foi cadastrada";
                                                  }catch (Exception e){//Tratando genericamete
                                                       errorExcecao = "ao cadastra usuario" + e.getMessage();
                                                  }
                                                  Toast.makeText(AutenticationActivity.this,
                                                          "Erro: " + errorExcecao,
                                                          Toast.LENGTH_SHORT).show();//Exibindo o erro em um Toast
                                             }
                                        }
                                   });
                              } else {//Logar

                                   autentificacao.signInWithEmailAndPassword(email, senha)
                                   .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                             if (task.isSuccessful()){

                                                  Toast.makeText(AutenticationActivity.this,
                                                          "Logado com sucesso! ",
                                                          Toast.LENGTH_SHORT).show();

                                                  String tipoUsuario = task.getResult().getUser().getDisplayName();//a task ja traz
                                                  abrirtelaPrincipal(tipoUsuario);//Abrir nova Activity - Finalisa atual

                                             } else {

                                                  Toast.makeText(AutenticationActivity.this,
                                                          "Falha ao logar usuário, cheque os dados informados! ",
                                                          Toast.LENGTH_SHORT).show();
                                             }
                                        }
                                   });
                              }
                         }else {
                              Toast.makeText(AutenticationActivity.this,
                                      "Informe a senha! ", Toast.LENGTH_SHORT).show();
                         }
                    } else {
                         Toast.makeText(AutenticationActivity.this,
                                 "Informe o email! ", Toast.LENGTH_SHORT).show();
                    }
               }
          });
     }

     private void verificarUsuarioLogado(){
          FirebaseUser usuarioAtual = autentificacao.getCurrentUser();
          if (usuarioAtual != null){
               String tipoUsuario = usuarioAtual.getDisplayName();//getTipoUsuario() - Passa para UsuarioFireBase.atualizarTipoUsuario()
               abrirtelaPrincipal(tipoUsuario);
          }
     }

     private String getTipoUsuario(){
          return tipoUsuario.isChecked() ? "E" : "U" ;//Informa o tipo do usuario a getDisplayName()
     }

     private void abrirtelaPrincipal(String tipoUsuario){//Tela empresa / Tela usuario

          if (tipoUsuario.equals("E")){//Empresa

               startActivity(new Intent(
                       AutenticationActivity.this, EmpresaActivity.class
               ));//Iniciando - HomeActivity.class
               finish();//Finalizando Activity atual

          } else {//Usuario

               startActivity(new Intent(
                       AutenticationActivity.this, HomeActivity.class
               ));//Iniciando - HomeActivity.class
               finish();//Finalizando Activity atual
          }
     }

     private void inicializarComponentes(){

          editTextEmail = findViewById(R.id.editCadastroEmail);
          editTextSenha = findViewById(R.id.editCadastroSenha);
          tipoAcesso = findViewById(R.id.switchAcesso);
          buttonAcessar = findViewById(R.id.buttonAcesso);
          tipoUsuario = findViewById(R.id.switchTipoUsuario);
          linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
     }
}
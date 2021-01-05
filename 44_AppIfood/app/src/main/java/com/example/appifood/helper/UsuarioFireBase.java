package com.example.appifood.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UsuarioFireBase {

     public static String getIdUsuario(){
          FirebaseAuth autentificacao = ConfiguracaoFireBase.getFirebaseAutentificacao();//Acesso aos metodos
          return autentificacao.getCurrentUser().getUid();//Pegando id
     }

     public static FirebaseUser getUsuarioAtual(){
          FirebaseAuth usuario = ConfiguracaoFireBase.getFirebaseAutentificacao();//Acesso aos metodos (Pega a instancia)
          return usuario.getCurrentUser();//Pegando usuario atual
     }

     public static boolean atualizarTipoUsuario(String tipo){

          try {

               FirebaseUser user = getUsuarioAtual();//Pega usuario atual
               UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                       .setDisplayName(tipo)//Tipo U - usuario/ E - empresa -> OUTRO METODOS PODEM PEGAR O TIPO getDisplayName()
                       .build();
               user.updateProfile(profile);//Atualiza o tipo so usuario atual - Setando um apelidp
               return true;

          } catch ( Exception e){
               e.printStackTrace();
               return false;
          }
     }
}

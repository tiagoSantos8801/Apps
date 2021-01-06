package com.example.appifood.model;

import com.example.appifood.helper.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;

public class Usuario {

     private String idUsuario, nomeUsuario,
                    cidadeBairro, ruaNumero, urlImagem;

     public Usuario() {

     }

     public void salvar(){
          DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference usuarioRef = databaseRef.child("usuarios").child(getIdUsuario());

          usuarioRef.setValue(this);//passando esse obj para o banco
     }

     public String getIdUsuario() {
          return idUsuario;
     }

     public void setIdUsuario(String idUsuario) {
          this.idUsuario = idUsuario;
     }

     public String getNomeUsuario() {
          return nomeUsuario;
     }

     public void setNomeUsuario(String nomeUsuario) {
          this.nomeUsuario = nomeUsuario;
     }

     public String getCidadeBairro() {
          return cidadeBairro;
     }

     public void setCidadeBairro(String cidadeBairro) {
          this.cidadeBairro = cidadeBairro;
     }

     public String getRuaNumero() {
          return ruaNumero;
     }

     public void setRuaNumero(String ruaNumero) {
          this.ruaNumero = ruaNumero;
     }

     public String getUrlImagem() {
          return urlImagem;
     }

     public void setUrlImagem(String urlImagem) {
          this.urlImagem = urlImagem;
     }
}

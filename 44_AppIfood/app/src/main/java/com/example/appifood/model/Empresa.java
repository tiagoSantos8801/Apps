package com.example.appifood.model;

import com.example.appifood.helper.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Empresa implements Serializable {//Passar a empresa escolhida para cardapio

     private String idUsuario;
     private String urlImagem;
     private String nome;
     private String tempo;
     private String categoria;
     private Double taxa;

     public Empresa() {

     }

     public void salvar(){

          DatabaseReference fireBasearef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference empresaRef = fireBasearef.child("empresas").child(getIdUsuario());

          empresaRef.setValue(this);//Passando o obj
     }
     
     public String getIdUsuario() {
          return idUsuario;
     }

     public void setIdUsuario(String idUsuario) {
          this.idUsuario = idUsuario;
     }

     public String getUrlImagem() {
          return urlImagem;
     }

     public void setUrlImagem(String urlImagem) {
          this.urlImagem = urlImagem;
     }

     public String getNome() {
          return nome;
     }

     public void setNome(String nome) {
          this.nome = nome;
     }

     public String getTempo() {
          return tempo;
     }

     public void setTempo(String tempo) {
          this.tempo = tempo;
     }

     public String getCategoria() {
          return categoria;
     }

     public void setCategoria(String categoria) {
          this.categoria = categoria;
     }

     public Double getTaxa() {
          return taxa;
     }

     public void setTaxa(Double taxa) {
          this.taxa = taxa;
     }
}
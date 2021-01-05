	package com.example.appifood.model;

import com.example.appifood.helper.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

public class Produto {

     String nome, descricao, idUsuario, idProduto;
     Double preco;

     public Produto() {
          DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference produtoRef = databaseRef.child("produtos");
          setidProduto(produtoRef.push().getKey());//Ja seta o id do produto no obj
     }

     public void salvar(){//Produtos separados de empreasas, mas o id do usuario a comum a ambos na identificacao

          DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference produtoRef = databaseRef.child("produtos")
                                                       .child(getIdUsuario())
                                                       .child(getidProduto());//Id fireBase
          produtoRef.setValue(this);

     }

     public void remover(){
          DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference produtoRef = databaseRef.child("produtos")
                  .child(getIdUsuario())
                  .child(getidProduto());
          produtoRef.removeValue();//Removendo no da referencia
     }

     public String getidProduto() {
          return idProduto;
     }

     public void setidProduto(String idProduto) {
          this.idProduto = idProduto;
     }

     public String getNome() {
          return nome;
     }

     public void setNome(String nome) {
          this.nome = nome;
     }

     public String getDescricao() {
          return descricao;
     }

     public void setDescricao(String descricao) {
          this.descricao = descricao;
     }
     @Exclude
     public String getIdUsuario() {
          return idUsuario;
     }

     public void setIdUsuario(String idUsuario) {
          this.idUsuario = idUsuario;
     }

     public Double getPreco() {
          return preco;
     }

     public void setPreco(Double preco) {
          this.preco = preco;
     }
}

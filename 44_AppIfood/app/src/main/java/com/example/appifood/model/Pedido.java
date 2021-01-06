package com.example.appifood.model;

import com.example.appifood.helper.ConfiguracaoFireBase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Pedido {

     private String idusuario, idEmpresa, idPedido,
                    nome, cidadeBairro, ruaNumero, status = "pendente",
                    observacao;
     private List<ItemPedido> itens;
     private Double total;
     private int metodoPagamento;

     public Pedido() {

     }

     public Pedido(String idusuario, String idEmpresa) {
          setIdusuario(idEmpresa);
          setIdEmpresa(idusuario);

          //Referencia do fireBase - Nos de armazenamento
          DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference pedidoRef = databaseRef.child("pedidos_usuario")
                                                       .child(idusuario)
                                                       .child(idEmpresa);
          setIdPedido(pedidoRef.push().getKey());//Pega o idPedido

     }

     public void salvar(){//Envia a si proprio para o banco
          DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
          DatabaseReference pedidoRef = databaseRef.child("pedidos_usuario")
                                                       .child(getIdEmpresa())
                                                       .child(getIdusuario());
          pedidoRef.setValue(this);
     }

     public String getIdusuario() {
          return idusuario;
     }

     public void setIdusuario(String idusuario) {
          this.idusuario = idusuario;
     }

     public String getIdEmpresa() {
          return idEmpresa;
     }

     public void setIdEmpresa(String idEmpresa) {
          this.idEmpresa = idEmpresa;
     }

     public String getIdPedido() {
          return idPedido;
     }

     public void setIdPedido(String idPedido) {
          this.idPedido = idPedido;
     }

     public String getNome() {
          return nome;
     }

     public void setNome(String nome) {
          this.nome = nome;
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

     public String getStatus() {
          return status;
     }

     public void setStatus(String status) {
          this.status = status;
     }

     public String getObservacao() {
          return observacao;
     }

     public void setObservacao(String observacao) {
          this.observacao = observacao;
     }

     public List<ItemPedido> getItens() {
          return itens;
     }

     public void setItens(List<ItemPedido> itens) {
          this.itens = itens;
     }

     public Double getTotal() {
          return total;
     }

     public void setTotal(Double total) {
          this.total = total;
     }

     public int getMetodoPagamento() {
          return metodoPagamento;
     }

     public void setMetodoPagamento(int metodoPagamento) {
          this.metodoPagamento = metodoPagamento;
     }
}

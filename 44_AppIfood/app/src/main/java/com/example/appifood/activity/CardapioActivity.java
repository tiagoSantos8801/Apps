package com.example.appifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appifood.R;
import com.example.appifood.adapter.AdapterProduto;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.helper.RecyclerItemClickListener;
import com.example.appifood.helper.UsuarioFireBase;
import com.example.appifood.model.Empresa;
import com.example.appifood.model.ItemPedido;
import com.example.appifood.model.Pedido;
import com.example.appifood.model.Produto;
import com.example.appifood.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

     private TextView textNomeEmpresaCardapio;
     private ImageView imageEmpresaCardapio;
     private RecyclerView recyclerProdutoCardapio;
     private Empresa empresaSelecionada;
     private AlertDialog dialog;

     private AdapterProduto adapterProduto;
     private List<Produto> produtos = new ArrayList<>();
     private List<ItemPedido> itensCarrinho = new ArrayList<>();
     private DatabaseReference databaseRef;
     private String idEmpresa;
     private String idusuariologado;
     private Pedido pedidoRecuperado;
     private Usuario usuario;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_cardapio);

          //Iniciando componentes
          inicializarComponentes();

          //Recuperando empresa selecionada
          Bundle bundle = getIntent().getExtras();//Obj serializable passado
          if (bundle != null){
               empresaSelecionada = (Empresa) bundle.getSerializable("empresa");//Pega pela chave
               textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
               idEmpresa = empresaSelecionada.getIdUsuario();//O metodo pega o id da empreza, embora tenha usuario - olha no banco

               String urlImagem = empresaSelecionada.getUrlImagem();
               Picasso.get().load(urlImagem).into(imageEmpresaCardapio);//Setando imagem com picasso
          }

          //Configuracoes Toolbar
          Toolbar toolbar = findViewById(R.id.toolbar);
          toolbar.setTitle("Cardápio");
          setSupportActionBar(toolbar);//Toolbar como o suporte para essa activity
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);//Volta para o parent

          //Cinfigura RecyclerView - Passando parametros para adapter
          recyclerProdutoCardapio.setLayoutManager(new LinearLayoutManager(this));
          recyclerProdutoCardapio.setHasFixedSize(true);
          adapterProduto = new AdapterProduto(produtos, this);
          recyclerProdutoCardapio.setAdapter(adapterProduto);

          //Configurando evento de click no recyclerProdutoCardapio - Cardapio empresa
          recyclerProdutoCardapio.addOnItemTouchListener(new RecyclerItemClickListener(
                  this,
                  recyclerProdutoCardapio,
                  new RecyclerItemClickListener.OnItemClickListener() {
                       @Override
                       public void onItemClick(View view, int position) {//Quando clickar em um iteem da listta do cardapio
                            confirmarQuantidade(position);
                       }

                       @Override
                       public void onLongItemClick(View view, int position) {

                       }

                       @Override
                       public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                       }
                  }
          ));

          //Recuperando produtos da empresa
          recuperaProdutos();

          //Recuperando dados do usuario
          recuperarDadosUsuario();

     }

     private void   confirmarQuantidade(int position) {

          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setTitle("Informe a quantidade");
          builder.setMessage("Digite a quantidade: ");

          //Criando obj View aqui mesmo
          EditText editQunatidade = new EditText(this);
          editQunatidade.setText("0");

          builder.setView(editQunatidade);
          builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                    String quantidade = editQunatidade.getText().toString();//Qtd

                    if (Integer.parseInt(quantidade) > 0){
                         Produto produtoSelecionado = produtos.get(position);//Posicao do produto da empresa selecionada

                         ItemPedido itemPedido = new ItemPedido();
                         itemPedido.setIdProduto(produtoSelecionado.getidProduto());
                         itemPedido.setNomeProduto(produtoSelecionado.getNome());
                         itemPedido.setPreco(produtoSelecionado.getPreco());
                         itemPedido.setQuantidade(Integer.parseInt(quantidade));

                         itensCarrinho.add(itemPedido);

                         if (pedidoRecuperado == null){
                              pedidoRecuperado = new Pedido(idusuariologado, idEmpresa);
                         }

                         //Cinfigurando a model
                         pedidoRecuperado.setNome(usuario.getNomeUsuario());
                         pedidoRecuperado.setCidadeBairro(usuario.getCidadeBairro());
                         pedidoRecuperado.setRuaNumero(usuario.getRuaNumero());
                         pedidoRecuperado.setItens(itensCarrinho);//Lista de itens no carrinho
                         pedidoRecuperado.salvar();

                    } else {
                         Toast.makeText(CardapioActivity.this,
                                 "Informe um valor maior que zero!", Toast.LENGTH_SHORT).show();
                    }
               }
          });
          builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {

               }
          });
          AlertDialog dialog = builder.create();
          dialog.show();
     }

     private void recuperarDadosUsuario() {

          dialog = new SpotsDialog.Builder()
                  .setContext(this)
                  .setMessage("Carregando dados...")
                  .setCancelable(false)
                  .build();
          dialog.show();

          DatabaseReference usuarioRef = databaseRef.child("usuarios").child(idusuariologado);

          //(addListenerForSingleValueEvent != addValueEventListener) - ouve apnas uma vez
          usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null){
                         usuario = dataSnapshot.getValue(Usuario.class);
                    }
                    recuperarPedido();
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });

     }

     private void recuperarPedido() {
          dialog.dismiss();
     }

     public void recuperaProdutos(){

          DatabaseReference produtosRef = databaseRef.child("produtos")
                  .child(idEmpresa);
          produtosRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                    produtos.clear();//limpaLista
                    for (DataSnapshot produtosBanco : dataSnapshot.getChildren()){
                         produtos.add(produtosBanco.getValue(Produto.class));//listando os produtos
                    }
                    adapterProduto.notifyDataSetChanged();//recarrega onbindViewHolder
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });
     }

     //Criando o menu
     @Override
     public boolean onCreateOptionsMenu(Menu menu) {

          MenuInflater inflater = getMenuInflater();
          inflater.inflate(R.menu.menu_cardapio, menu);
          return super.onCreateOptionsMenu(menu);
     }

     //Opcoes selecionadas
     @Override
     public boolean onOptionsItemSelected(@NonNull MenuItem item) {

          switch (item.getItemId()){
               case R.id.menuPedido:
                    confirmarPedido();
                    break;
          }
          return super.onOptionsItemSelected(item);
     }

     //Quando o item de menu e selecionado
     private void confirmarPedido() {

     }

     public void inicializarComponentes(){

          textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
          imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
          recyclerProdutoCardapio = findViewById(R.id.recyclerProdutoCardapio);

          databaseRef = ConfiguracaoFireBase.getFireBase();
          idusuariologado = UsuarioFireBase.getIdUsuario();
     }
}
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class CardapioActivity extends AppCompatActivity {

     private TextView textNomeEmpresaCardapio;
     private ImageView imageEmpresaCardapio;
     private RecyclerView recyclerProdutoCardapio;
     private Empresa empresaSelecionada;
     private AlertDialog dialog;
     private TextView textCarrinhoQtde, textCarrinhoTotal;

     private AdapterProduto adapterProduto;
     private List<Produto> produtos = new ArrayList<>();
     private List<ItemPedido> itensCarrinho = new ArrayList<>();
     private DatabaseReference databaseRef;
     private String idEmpresa;
     private String idusuariologado;
     private Pedido pedidoRecuperado;
     private Usuario usuario;
     private int qtdItensCarrinhos;
     private double totalCarrinho;
     private  int metodoPagamento;

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
          getSupportActionBar().setDisplayShowHomeEnabled(true);

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
          //Recuperando pedido
          recuperarPedido();
     }

     private void   confirmarQuantidade(int position) {

          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setTitle("Informe a quantidade");
          builder.setMessage("Digite a quantidade: ");

          //Criando View aqui mesmo
          EditText editQunatidade = new EditText(this);
          editQunatidade.setText("1");

          builder.setView(editQunatidade);
          builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                    int quantidade = Integer.parseInt(editQunatidade.getText().toString());//Qtd -> int

                    if (quantidade > 0){
                         Produto produtoSelecionado = produtos.get(position);//Produto selecionado

                         ItemPedido itemPedido = new ItemPedido();//Pega informacoes do obj produto
                         itemPedido.setIdProduto(produtoSelecionado.getIdProduto());
                         itemPedido.setNomeProduto(produtoSelecionado.getNome());
                         itemPedido.setPreco(produtoSelecionado.getPreco());
                         itemPedido.setQuantidade(quantidade);

                         //Validando pedidos repetidos
                         boolean pedidoRepetido = false;
                         if (!itensCarrinho.isEmpty()){
                              for (ItemPedido item : itensCarrinho){
                                   if (item.getIdProduto() == itemPedido.getIdProduto()){
                                        item.setQuantidade(item.getQuantidade() + itemPedido.getQuantidade());
                                        pedidoRepetido = true;
                                   }
                              }
                         } else {
                              itensCarrinho.add(itemPedido);//Primeiro produto
                              pedidoRepetido = true;
                         }

                         if (!pedidoRepetido)//Nao adciona pedidos repetidos
                              itensCarrinho.add(itemPedido);


                         if (pedidoRecuperado == null){//Novo pedido
                              pedidoRecuperado = new Pedido(idusuariologado, idEmpresa);
                         }

                         //Cinfigurando a model - Informacoes do pedido
                         pedidoRecuperado.setNome(usuario.getNomeUsuario());
                         pedidoRecuperado.setCidadeBairro(usuario.getCidadeBairro());
                         pedidoRecuperado.setRuaNumero(usuario.getRuaNumero());
                         pedidoRecuperado.setItens(itensCarrinho);//Lista de itens do pedido (carrinho   )
                         pedidoRecuperado.salvar();

                         Toast.makeText(CardapioActivity.this,
                                 "Adcionado a lista de pedidos!", Toast.LENGTH_SHORT).show();

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
                         dialog.dismiss();
                    } else {
                         dialog.dismiss();
                         Toast.makeText(CardapioActivity.this,
                                 "Não há usuarios! ", Toast.LENGTH_LONG).show();
                    }

               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });

     }

     private void recuperarPedido() {

          DatabaseReference pedidoRef = databaseRef.child("pedidos_usuario")
                                                  .child(idEmpresa)
                                                  .child(idusuariologado);
          //Listner nos pedidos
          pedidoRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {

                    qtdItensCarrinhos = 0;
                    totalCarrinho = 0.0;
                    itensCarrinho = new ArrayList<>();

                    if (dataSnapshot.getValue() != null){//Checa se tem pedidos

                         pedidoRecuperado = dataSnapshot.getValue(Pedido.class);//Formato response
                         itensCarrinho = pedidoRecuperado.getItens();//Pega itens carrinho

                         for (ItemPedido itemPedido :itensCarrinho){
                              int qtde = itemPedido.getQuantidade();
                              double preco = itemPedido.getPreco();

                              qtdItensCarrinhos += qtde;
                              totalCarrinho += qtde * preco;
                         }
                    }

                    DecimalFormat format = new DecimalFormat("0.00");

                    textCarrinhoQtde.setText("Qtde: " + qtdItensCarrinhos);
                    textCarrinhoTotal.setText("R$:" + format.format(totalCarrinho));

                    dialog.dismiss();
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {

               }
          });

     }

     public void recuperaProdutos(){

          //Traz informacoes do banco
          DatabaseReference produtosRef = databaseRef.child("produtos")
                  .child(idEmpresa);

          //Listner
          produtosRef.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {//Todos os produtos

                    produtos.clear();//limpaLista
                    for (DataSnapshot produtosBanco : dataSnapshot.getChildren()){
                         produtos.add(produtosBanco.getValue(Produto.class));//Response no fprmato desse Obj
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
                    confirmarPedido();//Dialog de confirmacao pedido
                    break;
          }
          return super.onOptionsItemSelected(item);
     }

     //Quando o item de menu e selecionado
     private void confirmarPedido() {

          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setTitle("Informe a forma de pagamento ");
          CharSequence[] formasPag = new CharSequence[]{//Array de formas de pagamento
               "Dinheiro", "Máquina de Cartão"
          };
          builder.setSingleChoiceItems(formasPag, 0, new DialogInterface.OnClickListener() {//listner forma de pagamento
               @Override
               public void onClick(DialogInterface dialogInterface, int formaPagSelecionada) {
                    metodoPagamento = formaPagSelecionada;//Forma de pagamento pre-selecionada
               }
          });

          //Elemento da View local
          EditText editObservacao = new EditText(this);
          editObservacao.setHint("Informe a observação ...");

          builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                    String observacao = editObservacao.getText().toString();
                    pedidoRecuperado.setMetodoPagamento(metodoPagamento);
                    pedidoRecuperado.setObservacao(observacao);
                    pedidoRecuperado.setStatus("Confirmado");
                    pedidoRecuperado.confirmar();
                    pedidoRecuperado.remover();
                    pedidoRecuperado = null;

                    Toast.makeText(CardapioActivity.this,
                            "Pedido confirmado com sucesso!", Toast.LENGTH_SHORT).show();
               }
          });
          builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {

               }
          });

          builder.setView(editObservacao);//Setando EditText
          AlertDialog dialog = builder.create();
          dialog.show();
     }

     public void pedidosUsuario(View view){

          startActivity(new Intent(CardapioActivity.this, PedidosUsuarioActivity.class));
     }

     public void inicializarComponentes(){

          textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
          imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
          recyclerProdutoCardapio = findViewById(R.id.recyclerProdutoCardapio);
          textCarrinhoQtde = findViewById(R.id.textCarrinhoQtd);
          textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

          databaseRef = ConfiguracaoFireBase.getFireBase();
          idusuariologado = UsuarioFireBase.getIdUsuario();
     }
}
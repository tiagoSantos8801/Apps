package com.example.appifood.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appifood.R;
import com.example.appifood.adapter.AdapterProduto;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.model.Empresa;
import com.example.appifood.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardapioActivity extends AppCompatActivity {

     private TextView textNomeEmpresaCardapio;
     private ImageView imageEmpresaCardapio;
     private RecyclerView recyclerProdutoCardapio;
     private Empresa empresaSelecionada;

     private AdapterProduto adapterProduto;
     private List<Produto> produtos = new ArrayList<>();
     private DatabaseReference databaseRef;
     private String idEmpresa;
     private String idusuariologado;

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

          //Recuperando produtos da empresa
          recuperaProdutos();

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

     public void inicializarComponentes(){

          textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);
          imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
          recyclerProdutoCardapio = findViewById(R.id.recyclerProdutoCardapio);

          databaseRef = ConfiguracaoFireBase.getFireBase();
     }
}
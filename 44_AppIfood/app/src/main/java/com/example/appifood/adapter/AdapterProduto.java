package com.example.appifood.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appifood.R;
import com.example.appifood.helper.ConfiguracaoFireBase;
import com.example.appifood.model.Produto;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder> {

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$ " + produto.getPreco());
//        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                Produto produto = produtos.get(i);
//
//                    DatabaseReference databaseRef = ConfiguracaoFireBase.getFireBase();
//                    DatabaseReference produtoRef = databaseRef.child("produtos")
//                            .child(produto.getIdUsuario());
//                    produtoRef.child(produto.getPush()).removeValue();
//
//                    produtos.remove(produto);
//
//                return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome;
        TextView descricao;
        TextView valor;
        //CardView card;

        public MyViewHolder(View itemView) {
            super(itemView);
            //card = itemView.findViewById(R.id.card_produto);
            nome = itemView.findViewById(R.id.textNomeRefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
        }
    }
}

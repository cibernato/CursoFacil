package com.proyecto.jerbo.cursofacil.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.proyecto.jerbo.cursofacil.R;

import java.io.Serializable;
import java.util.ArrayList;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.ViewHolder>  implements View.OnClickListener,Serializable {
    private ArrayList<String> fotos;
    private View.OnClickListener listener;

    public FotosAdapter(ArrayList<String> fotos) {
        this.fotos = fotos;
    }

    @NonNull
    @Override

    public FotosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.foto_item_list, viewGroup, false);
        v.setOnClickListener(this);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FotosAdapter.ViewHolder viewHolder, int i) {
        Glide.with(viewHolder.v).load(fotos.get(i)).into(viewHolder.foto);
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onClick(v);
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        View v;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.item_foto);
            this.v = itemView;
        }
    }

}

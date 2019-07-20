package com.proyecto.jerbo.cursofacil.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.proyecto.jerbo.cursofacil.Class.Curso;
import com.proyecto.jerbo.cursofacil.R;
import java.util.ArrayList;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.ViewHolder> implements View.OnClickListener {
    private View.OnClickListener listener;
    private ArrayList<Curso> cursos;

    public CursoAdapter(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }

    @NonNull
    @Override
    public CursoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.curso_item_list, viewGroup, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CursoAdapter.ViewHolder viewHolder, int i) {
        viewHolder.name.setText(cursos.get(i).getName());
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onClick(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name ;
        View v;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.curso_item_list_text);
            this.v=itemView;
        }
    }
    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}

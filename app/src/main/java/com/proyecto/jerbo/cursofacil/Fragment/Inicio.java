package com.proyecto.jerbo.cursofacil.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.proyecto.jerbo.cursofacil.Activities.VerCurso;
import com.proyecto.jerbo.cursofacil.Adapters.CursoAdapter;
import com.proyecto.jerbo.cursofacil.Class.Curso;
import com.proyecto.jerbo.cursofacil.R;

import java.io.File;
import java.util.ArrayList;


public class Inicio extends Fragment {
    RecyclerView recyclerView;
    ArrayList<Curso> cursos;
    CursoAdapter cursoAdapter;
    private OnFragmentInteractionListener mListener;


    public Inicio() {
        // Required empty public constructor
    }


    public static Inicio newInstance(String param1, String param2) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cursos = new ArrayList<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_inicio, container, false);
        recyclerView = v.findViewById(R.id.recycler_view_cursos);
        llenarCursos();
        cursoAdapter = new CursoAdapter(cursos);
        recyclerView.setAdapter(cursoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        cursoAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a = new Intent(getContext(), VerCurso.class);
                a.putExtra("curso", cursos.get(recyclerView.getChildAdapterPosition(view)));
                startActivity(a);
            }
        });


        return v;
    }

    public void añadirCurso(String name) {
        Log.e("kappa", Environment.getExternalStorageDirectory() + " ");

        if (cursos.size()!=0) {
            for (Curso c : cursos) {
                if (c.getName() != name && c != null) {
                    File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Curso Facil" + File.separator + name);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                }
            }
        } else {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Curso Facil" + File.separator + name);
            folder.mkdirs();
        }
        cursos.clear();
        llenarCursos();
        cursoAdapter.notifyDataSetChanged();

    }

    private void llenarCursos() {
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "Curso Facil");
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                cursos.add(new Curso(inFile.getName(), inFile));
            }
        }
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

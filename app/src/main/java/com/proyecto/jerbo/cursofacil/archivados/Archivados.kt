package com.proyecto.jerbo.cursofacil.archivados

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.curso_details.CursoAdapter
import com.proyecto.jerbo.cursofacil.curso_details.VerCurso
import com.proyecto.jerbo.cursofacil.models.Curso
import kotlinx.android.synthetic.main.fragment_archivados.*


class Archivados : Fragment() {

    private lateinit var archivados: ArrayList<Curso>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cursoAdapter: CursoAdapter
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var gson: Gson
    private val collectionType = object : TypeToken<ArrayList<Curso>>() {}.type
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gson = Gson()
        archivados = ArrayList()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        llenarCursosArchivados()
        cursoAdapter = CursoAdapter(archivados)
        recycler_cursos_archivados?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recycler_cursos_archivados?.adapter = cursoAdapter
        cursoAdapter.setOnClickListener { view ->
            val cursoSelected = Intent(context, VerCurso::class.java)
            cursoSelected.putExtra("curso", archivados[recycler_cursos_archivados.getChildAdapterPosition(view)])
            startActivity(cursoSelected)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_archivados, container, false)
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Archivados().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

    private fun llenarCursosArchivados() {
        val cursosSharedPrefrences = sharedPreferences.getString(getString(R.string.cursos_archivados), "[]")
//        editor = sharedPreferences.edit()
        archivados = gson.fromJson(cursosSharedPrefrences, collectionType)
//        val archivadosTemp = ArrayList<Curso>()
//        archivadosTemp.addAll(archivados)
//        archivadosTemp.forEach{
//            val f = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
//            val files = f.listFiles()
//            try {
//                for (inFile in files) {
//                    if (inFile.name != it.name && inFile.isDirectory){
//                        archivados.remove(it)
//                    }
//                }
//            } catch (e: Exception) {
//
//            }
//        }
        Log.e("Archivados", "$archivados")
        editor = sharedPreferences.edit()
        editor.putString(getString(R.string.cursos_archivados), gson.toJson(archivados))
        editor.apply()
    }
}

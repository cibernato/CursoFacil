package com.proyecto.jerbo.cursofacil.archivados

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.curso_details.CursoAdapter
import com.proyecto.jerbo.cursofacil.models.Curso
import kotlinx.android.synthetic.main.activity_archivados.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class Archivados : AppCompatActivity() {

    private lateinit var archivados: ArrayList<Curso>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var cursoAdapter: CursoAdapter
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var gson: Gson
    private val collectionType = object : TypeToken<ArrayList<Curso>>() {}.type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archivados)
        gson = Gson()
        archivados = ArrayList()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        llenarCursosArchivados()
        cursoAdapter = CursoAdapter(archivados)
        recycler_cursos_archivados?.adapter = cursoAdapter
        recycler_cursos_archivados?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

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
        Log.e("Archivados","$archivados")
        editor = sharedPreferences.edit()
        editor.putString(getString(R.string.cursos_archivados),gson.toJson(archivados))
        editor.apply()
    }

}

package com.proyecto.jerbo.cursofacil.activos

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.curso_details.CursoAdapter
import com.proyecto.jerbo.cursofacil.curso_details.VerCurso
import com.proyecto.jerbo.cursofacil.models.Curso
import kotlinx.android.synthetic.main.fragment_activos.*
import leakcanary.LeakSentry
import java.io.File


class Activos : Fragment() {

    private lateinit var cursos: ArrayList<Curso>
    private lateinit var cursoAdapter: CursoAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var gson: Gson
    private val collectionType = object : TypeToken<ArrayList<Curso>>() {}.type
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for context fragment

        return inflater.inflate(R.layout.fragment_activos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gson = Gson()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        checkPermision()
        crearSharedPreferencesVariables()
        LeakSentry.config = LeakSentry.config.copy()
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        cursos = ArrayList()
        cursoAdapter = CursoAdapter(cursos)
        llenarCursos()
        recycler_view_cursos?.adapter = cursoAdapter
        recycler_view_cursos?.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        cursoAdapter.setOnClickListener { view ->
            val cursoSelected = Intent(context, VerCurso::class.java)
            cursoSelected.putExtra("curso", cursos[recycler_view_cursos.getChildAdapterPosition(view)])
            startActivity(cursoSelected)
        }
    }

    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    private fun crearSharedPreferencesVariables() {
        val cursos = sharedPreferences.getString(getString(R.string.cursos_activos), "0")
        val archivados = sharedPreferences.getString(getString(R.string.cursos_archivados), "0")
        editor = sharedPreferences.edit()
        if (cursos == "0") {
            editor.putString(getString(R.string.cursos_activos), "[]")
        }
        if (archivados == "0") {
            editor.putString(getString(R.string.cursos_archivados), "[]")
        }
        editor.apply()

    }

    private fun checkPermision() {
        ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    private fun addCurso(name: String) {
        Log.e("kappa", Environment.getExternalStorageDirectory().toString() + " ")
        if (cursos.size != 0) {
            for (c in cursos) {
                if (c.name !== name) {
                    val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil" + File.separator + name.trim())
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                }
            }
        } else {
            val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil" + File.separator + name.trim())
            folder.mkdirs()
        }
        cursos.clear()
        llenarCursos()

    }

    private fun llenarCursos() {

        var convertido: ArrayList<Curso> = ArrayList()

        val cursosSharedPrefrences = sharedPreferences.getString(getString(R.string.cursos_activos), "[]")
        val archivadosPrefrences = sharedPreferences.getString(getString(R.string.cursos_archivados), "[]")
        editor = sharedPreferences.edit()
        try {
            if (cursosSharedPrefrences != "[]") {
                convertido = gson.fromJson(cursosSharedPrefrences, collectionType)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Metodo parseado da como resultado: $cursosSharedPrefrences")
        }
        Log.e(TAG, "SharedPreferences: $convertido")
        val archivados: java.util.ArrayList<Curso> = gson.fromJson(archivadosPrefrences, collectionType)
        if (cursos.isEmpty()) {
            val f = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
            val files = f.listFiles()
            try {
                for (inFile in files) {
                    if (inFile.isDirectory && !archivados.contains(Curso(inFile.name, inFile, true))) {
                        cursos.add(Curso(inFile.name, inFile, false))
                    }
                }
                cursos.sortBy { it.name }
            } catch (e: Exception) {

            }
        } else {
            cursos.clear()
            convertido.forEach {
                if (it.archivado != true) {
                    cursos.add(it)
                } else {
                    archivados.add(it)
                }
            }
            cursos.sortBy { it.name }
        }
//        Log.e(TAG, "Cursos Activos $cursos")
        Log.e(TAG, "Cursos Archivados $archivados")

        editor.putString(getString(R.string.cursos_archivados), gson.toJson(archivados))
        editor.putString(getString(R.string.cursos_activos), gson.toJson(cursos))
        editor.apply()
        cursoAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        llenarCursos()
        super.onResume()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.curso_añadir) {
            if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                val editText = EditText(context)
                val builder = AlertDialog.Builder(context)
                builder.setView(editText)
                builder.setMessage("Ingrese nombre: ")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { _, _ ->
                            if (editText.toString() == "") {
                                editText.setText("Ingrese nombre aqui")
                            } else {
                                addCurso(editText.text.toString().trim())
                            }
                        }
                        .setNegativeButton("no") { dialog, _ -> dialog.cancel() }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private val TAG = this::class.simpleName
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                Activos().apply {
                    arguments = Bundle().apply {

                    }
                }
    }

}

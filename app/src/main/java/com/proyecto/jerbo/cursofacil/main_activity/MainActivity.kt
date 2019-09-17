package com.proyecto.jerbo.cursofacil.main_activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.jerbo.cursofacil.models.Curso
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.archivados.Archivados
import com.proyecto.jerbo.cursofacil.curso_details.CursoAdapter
import com.proyecto.jerbo.cursofacil.curso_details.VerCurso
import kotlinx.android.synthetic.main.activity_main.*
import leakcanary.LeakSentry
import java.io.File
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var cursos: ArrayList<Curso>
    private lateinit var cursoAdapter: CursoAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var gson: Gson
    private val collectionType = object : TypeToken<ArrayList<Curso>>() {}.type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        gson = Gson()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        checkPermision()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.app_name,
                R.string.name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
        crearSharedPreferencesVariables()
        LeakSentry.config = LeakSentry.config.copy()
        //supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        cursos = ArrayList()
        cursoAdapter = CursoAdapter(cursos)
        llenarCursos()
        recycler_view_cursos?.adapter = cursoAdapter
        recycler_view_cursos?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cursoAdapter.setOnClickListener { view ->
            val cursoSelected = Intent(this, VerCurso::class.java)
            cursoSelected.putExtra("curso", cursos[recycler_view_cursos.getChildAdapterPosition(view)])
            startActivity(cursoSelected)
        }

    }

    private fun crearSharedPreferencesVariables() {
        val cursos =sharedPreferences.getString(getString(R.string.cursos_activos),"0")
        val archivados =sharedPreferences.getString(getString(R.string.cursos_archivados),"0")
        editor = sharedPreferences.edit()
        if (cursos =="0"){
            editor.putString(getString(R.string.cursos_activos), "[]")
        }
        if (archivados =="0"){
            editor.putString(getString(R.string.cursos_archivados), "[]")
        }
        editor.apply()

    }

    private fun checkPermision() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.curso_añadir) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                val editText = EditText(this)
                val builder = AlertDialog.Builder(this)
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

    override fun onBackPressed() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setMessage("¿Desea salir de la aplicacion?")
                .setCancelable(false)
                .setPositiveButton("Si") { _, _ ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()

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
        editor = sharedPreferences.edit()
        try {
            if (cursosSharedPrefrences != "[]") {
                convertido = gson.fromJson(cursosSharedPrefrences, collectionType)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Metodo parseado da como resultado: $cursosSharedPrefrences")
        }
        val archivados: ArrayList<Curso> = ArrayList()
        if (cursos.isEmpty()) {
            val f = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
            val files = f.listFiles()
            try {
                for (inFile in files) {
                    if (inFile.isDirectory) {
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
                }else{
                    archivados.add(it)
                }
            }
            cursos.sortBy { it.name }
        }
        Log.e(TAG, "Cursos Activos $cursos")
        Log.e(TAG, "Cursos Archivados $archivados")
        editor.putString(getString(R.string.cursos_archivados),gson.toJson(archivados))
        editor.putString(getString(R.string.cursos_activos), gson.toJson(cursos))
        editor.apply()
        cursoAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        llenarCursos()
        super.onResume()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.archivados -> {
                val intent = Intent(this,Archivados::class.java)
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        private val TAG = this::class.simpleName
    }

}

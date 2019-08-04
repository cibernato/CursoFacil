package com.proyecto.jerbo.cursofacil.main_activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.jerbo.cursofacil.models.Curso
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.calculadora_promedios.CalculadoraPromedio
import com.proyecto.jerbo.cursofacil.curso_details.CursoAdapter
import com.proyecto.jerbo.cursofacil.curso_details.VerCurso
import kotlinx.android.synthetic.main.activity_main.*
import leakcanary.LeakSentry
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var cursos: ArrayList<Curso>
    private lateinit var cursoAdapter: CursoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        LeakSentry.config = LeakSentry.config.copy()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        cursos = ArrayList()
        llenarCursos()
        cursoAdapter = CursoAdapter(cursos)
        recycler_view_cursos?.adapter = cursoAdapter
        recycler_view_cursos?.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        cursoAdapter.setOnClickListener { view ->
            val cursoSelected = Intent(this, VerCurso::class.java)
            cursoSelected.putExtra("curso", cursos[recycler_view_cursos.getChildAdapterPosition(view)])
            startActivity(cursoSelected)
        }

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.curso_añadir) {
            val editText = EditText(this)
            val builder = AlertDialog.Builder(this)
            builder.setView(editText)
            builder.setMessage("Ingrese nombre: ")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { _, _ ->
                        if (editText.toString() == "") {
                            editText.setText("Ingrese nombre aqui")
                        } else {
                            addCurso(editText.text.toString())
                        }
                    }
                    .setNegativeButton("no") { dialog, _ -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setMessage("¿Desea salir de la aplicacion?")
                .setCancelable(false)
                .setPositiveButton("Si") { dialog, which ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    fun addCurso(name: String) {
        Log.e("kappa", Environment.getExternalStorageDirectory().toString() + " ")
        if (cursos.size != 0) {
            for (c in cursos) {
                if (c.name !== name && c != null) {
                    val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil" + File.separator + name)
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                }
            }
        } else {
            val folder = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil" + File.separator + name)
            folder.mkdirs()
        }
        cursos.clear()
        llenarCursos()
        cursoAdapter.notifyDataSetChanged()
    }

    private fun llenarCursos() {
        val f = File(Environment.getExternalStorageDirectory().toString() + File.separator + "Curso Facil")
        val files = f.listFiles()
        for (inFile in files) {
            if (inFile.isDirectory) {
                cursos.add(Curso(inFile.name, inFile))
            }
        }
        cursos.sortBy { it.name }
    }

    companion object {
        private val TAG = this.javaClass.name
    }

}

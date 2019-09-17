package com.proyecto.jerbo.cursofacil.curso_details

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.calculadora_promedios.CalculadoraPromedio
import com.proyecto.jerbo.cursofacil.main_activity.MainActivity
import com.proyecto.jerbo.cursofacil.models.*
import kotlinx.android.synthetic.main.activity_ver_curso.*
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

class VerCurso : AppCompatActivity(), PhotoItem.EpoxyClickListener {


    private lateinit var fotos: ArrayList<String>
    private lateinit var imgController: PhotoItemController
    private lateinit var pathToFile: String
    private lateinit var mCurrentPhotpPath: String
    private lateinit var elegido: Curso
    private lateinit var listPromedios: ArrayList<Promedio>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        gson = Gson()
        elegido = intent.getSerializableExtra("curso") as Curso
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
        setContentView(R.layout.activity_ver_curso)
        fotos = ArrayList()
        llenarFotos()
        var asd= R.string.action_settings
        imgController = PhotoItemController(fotos, this)
        imgController.isDebugLoggingEnabled = true
        imgController.requestModelBuild()
        recycler_view_cursos_fotos.adapter = imgController.adapter
        recycler_view_cursos_fotos.layoutManager = GridLayoutManager(this, 3)

        tomar_foto.setOnClickListener {
            if (checkPermissionGranted()) {
                dispatchTakePictureIntent()
            } else {
                if (Build.VERSION.SDK_INT >= 23) {
                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 2)
                }
            }
        }

        listPromedios = ArrayList()
        checkSharedPreferences()
        calculadora_promedios.setOnClickListener {
            val promedio = Intent(this, CalculadoraPromedio::class.java)
            promedio.putExtra("curso_name", elegido.name)
            startActivity(promedio)
        }

    }

    private fun checkPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkSharedPreferences() {
        val collectionType = object : TypeToken<ArrayList<Promedio>>() {}.type
        val toParse = sharedPreferences.getString(elegido.name, "[]")
        editor = sharedPreferences.edit()
        Log.e(TAG, "valor recibido $toParse")
        try {
            if (toParse == "[]") {
                editor.putString("${elegido.name}", gson.toJson(listPromedios))
                editor.apply()
            } else {
                listPromedios = gson.fromJson(toParse, collectionType)
            }
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Metodo parseado da como resultado: $toParse")
        }


    }

    private fun llenarFotos() {
        val f = elegido.path
        val files = f!!.listFiles(IMAGE_FILTER)
        for (inFile in files)
            try {
                if (inFile.length() != 0L) {
                    println("image: " + inFile.name)
                    println(" size  : " + f.length())
                    fotos.add(inFile.absolutePath)

                } else {
                    inFile.delete()
                }
            } catch (e: Exception) {
            }
        fotos.sortWith(Comparator { o1, o2 ->
            val a = File(o1)
            val b = File(o2)
            when {
                a.lastModified() > b.lastModified() -> -1
                a.lastModified() < b.lastModified() -> +1
                else -> 0
            }
        })
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("ddMMyyyy").format(Date())
        val imageFileName = elegido.name + timeStamp + "_"
        val storgeDir = elegido.path
        var image: File? = null
        try {
            image = File.createTempFile(imageFileName, ".jpg", storgeDir)
        } catch (e: IOException) {
            Log.d("my log", "Excep: $e")
        }

        mCurrentPhotpPath = "file:" + image!!.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {

            }

            if (photoFile != null) {
                pathToFile = photoFile.absolutePath
                val photoURI = FileProvider.getUriForFile(this,
                        "com.proyecto.jerbo.cursofacil.fileprovider",
                        photoFile)
                Log.e("mmmm", " $photoURI    $pathToFile")
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(R.menu.ver_curso_menu, menu)
        this.supportActionBar?.title=elegido.name?.capitalize()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val context = this
        if (item.itemId == R.id.borrar_curso) {

            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Desea eliminar este curso?")
                    .setCancelable(false)
                    .setPositiveButton("Si") { _, _ ->
                        borrarCurso(elegido.path!!)
                        val a = Intent(context, MainActivity::class.java)
                        eliminarDeSharedPrefrences(1)
                        editor.remove(elegido.name)
                        editor.commit()
                        Log.e(TAG, "Elemets after delete: ${sharedPreferences.all}")
                        startActivity(a)
                    }
                    .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()

        }
        if (item.itemId == R.id.archivar_curso){
            eliminarDeSharedPrefrences(0)

            Toast.makeText(this,"${elegido.name?.capitalize()} fue archivado.",Toast.LENGTH_SHORT).show()
//            val a = Intent(context, MainActivity::class.java)
//            startActivity(a)

        }
        return super.onContextItemSelected(item)
    }

    private fun eliminarDeSharedPrefrences(flag :Int) {
        val collectionType = object : TypeToken<ArrayList<Curso>>() {}.type
        val cursosSharedPrefrences = sharedPreferences.getString(getString(R.string.cursos_activos), "[]")
        val convertido : ArrayList<Curso>
        convertido = gson.fromJson(cursosSharedPrefrences, collectionType)
        convertido.remove(convertido.find { it.name == elegido.name })
        if (flag ==0){
            elegido.archivado= true
            convertido.add(elegido)
        }
        Log.e("TAG",convertido.toString())
        editor.putString(getString(R.string.cursos_activos), gson.toJson(convertido))
        editor.apply()

    }

    private fun borrarCurso(f: File) {
        if (f.isDirectory)
            for (child in f.listFiles())
                borrarCurso(child)
        f.delete()

    }

    override fun onClick(view: KotlinModel) { // From EpoxyListener
        val a = Intent(baseContext, ViewPagerFotos::class.java)
        a.putExtra("fotos", fotos)
        a.putExtra("num", imgController.adapter.copyOfModels.indexOf(view))
        startActivityForResult(a, ELIMINADO)
    }

    companion object {
        internal const val REQUEST_IMAGE_CAPTURE = 1
        private val EXTENSIONS = arrayOf("jpg")
        internal val IMAGE_FILTER: FilenameFilter = FilenameFilter { _, name ->
            for (ext in EXTENSIONS) {
                if (name.endsWith(".$ext")) {
                    return@FilenameFilter true
                }
            }
            false
        }
        private const val ELIMINADO = 3
        private val TAG = this::class.java.name
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            fotos.clear()
            llenarFotos()
            imgController.requestModelBuild()
        }

        if (requestCode == ELIMINADO) {
            if (resultCode == Activity.RESULT_OK) {
                fotos.clear()
                llenarFotos()
                imgController.requestModelBuild()
            }
        }
    }

    override fun onBackPressed() {
        Glide.get(this).clearMemory()
        super.onBackPressed()
    }

}


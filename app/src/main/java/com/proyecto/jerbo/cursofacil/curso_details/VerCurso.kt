package com.proyecto.jerbo.cursofacil.curso_details

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.proyecto.jerbo.cursofacil.main_activity.MainActivity
import com.proyecto.jerbo.cursofacil.models.Curso
import com.proyecto.jerbo.cursofacil.R
import kotlinx.android.synthetic.main.activity_ver_curso.*
import java.io.File
import java.io.FilenameFilter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import kotlin.Boolean
import kotlin.Comparator
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.arrayOf
import kotlin.plus

class VerCurso : AppCompatActivity() {
    internal lateinit var fotos: ArrayList<String>
    internal lateinit var pathToFile: String
    internal lateinit var mCurrentPhotpPath: String
    internal lateinit var elegido: Curso
    internal lateinit var fotosAdapter: FotosAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val temp = intent
        elegido = temp.getSerializableExtra("curso") as Curso
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white)
        setContentView(R.layout.activity_ver_curso)
        fotos = ArrayList()
        llenarFotos()
        fotosAdapter = FotosAdapter(fotos)
        recycler_view_cursos_fotos.adapter = fotosAdapter
        recycler_view_cursos_fotos.layoutManager = GridLayoutManager(this, 3)
        fotosAdapter.setOnClickListener { v ->
            val a = Intent(baseContext, ViewPagerFotos::class.java)
            a.putExtra("fotos", fotos)
            a.putExtra("num", recycler_view_cursos_fotos.getChildAdapterPosition(v))
            startActivityForResult(a, ELIMINADO)
        }
        tomar_foto.setOnClickListener { dispatchTakePictureIntent() }
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 2)
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
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.borrar_curso) {
            val context = this
            val builder = AlertDialog.Builder(context)
            builder.setMessage("¿Desea eliminar este compromiso?")
                    .setCancelable(false)
                    .setPositiveButton("Si") { dialog, which ->
                        borrarCurso(elegido.path!!)
                        val a = Intent(context, MainActivity::class.java)
                        startActivity(a)
                    }
                    .setNegativeButton("No") { dialog, which -> dialog.cancel() }
            val alertDialog = builder.create()
            alertDialog.show()

        }
        return super.onContextItemSelected(item)
    }

    private fun borrarCurso(f: File) {
        if (f.isDirectory)
            for (child in f.listFiles())
                borrarCurso(child)
        f.delete()

    }

    companion object {
        internal val REQUEST_IMAGE_CAPTURE = 1
        internal val REQUEST_TAKE_PHOTO = 1
        internal val EXTENSIONS = arrayOf("jpg")
        internal val IMAGE_FILTER: FilenameFilter = FilenameFilter { dir, name ->
            for (ext in EXTENSIONS) {
                if (name.endsWith(".$ext")) {
                    return@FilenameFilter true
                }
            }
            false
        }
        private val ELIMINADO = 3
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            fotos.clear()
            llenarFotos()
            fotosAdapter.notifyDataSetChanged()
        }

        if (requestCode == ELIMINADO) {
            if (resultCode == Activity.RESULT_OK) {
                fotos.clear()
                llenarFotos()
                fotosAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onBackPressed() {
        Glide.get(this).clearMemory()
        super.onBackPressed()
    }
}


package com.proyecto.jerbo.cursofacil.calculadora_promedios

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.models.Promedio
import kotlinx.android.synthetic.main.activity_calculadora_promedio.*

class CalculadoraPromedio : AppCompatActivity(), PromedioAdapter.OnClickListeners {

    lateinit var promedio_adapter: PromedioAdapter
    var CABECERA = "Usted tiene %.2f acumulado y tiene el  %d  de sus notas"
    private lateinit var lista_promedios: ArrayList<Promedio>
    private lateinit var curso_name: String
    private lateinit var gson: Gson
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora_promedio)
        gson = Gson()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        lista_promedios = ArrayList()
        getActivityDetails()
        promedio_adapter = PromedioAdapter(this, lista_promedios, this)
        recycler_view_promedios.adapter = promedio_adapter
        recycler_view_promedios.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        add_promedio.setOnClickListener { addPromedio() }
        mostrarTotal()
    }

    private fun getActivityDetails() {
        val collectionType = object : TypeToken<java.util.ArrayList<Promedio>>() {}.type
        curso_name = intent.getStringExtra("curso_name")
        lista_promedios = gson.fromJson(sharedPreferences.getString(curso_name,"[]"),collectionType)
    }

    private fun mostrarTotal() {
        var pTotal = 0
        var nTotal = 0.0
        lista_promedios.forEach {
            pTotal += it.porcentaje
            nTotal += (it.nota.toDouble() * (it.porcentaje.toDouble()) / 100.0)
        }
        promedio_calculado.text = String.format(CABECERA, nTotal, pTotal)
    }

    private fun addPromedio() {
        lista_promedios.add(Promedio(0, 0))
        promedio_adapter.notifyItemInserted(lista_promedios.size - 1)
        mostrarTotal()
    }

    override fun deletePromedio(promedio: Promedio, pos: Int) {
        lista_promedios.remove(promedio)
        promedio_adapter.notifyItemRemoved(pos)
        mostrarTotal()
    }

    override fun calcularProm() {
        mostrarTotal()
    }

    companion object {
        private val TAG = this::class.java.name
    }

    override fun onBackPressed() {
        saveSharedPreferences()
        super.onBackPressed()
    }

    private fun saveSharedPreferences() {
        editor = sharedPreferences.edit()
        editor.putString(curso_name,gson.toJson(lista_promedios))
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(R.menu.menu_calculadora,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.reset_calculadora){
            lista_promedios.clear()
            promedio_adapter.notifyDataSetChanged()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

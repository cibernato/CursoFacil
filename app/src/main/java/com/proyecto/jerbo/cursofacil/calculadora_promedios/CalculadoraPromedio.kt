package com.proyecto.jerbo.cursofacil.calculadora_promedios

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_calculadora_promedio.*
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.models.Promedio

class CalculadoraPromedio : AppCompatActivity(), PromedioAdapter.OnClickListeners {

    lateinit var promedio_adapter: PromedioAdapter
    var CABECERA = "Usted tiene %.2f acumulado y tiene el  %d  de sus notas"
    private lateinit var lista_promedios: ArrayList<Promedio>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadora_promedio)
        lista_promedios = ArrayList()
        promedio_adapter = PromedioAdapter(this,lista_promedios, this)
        recycler_view_promedios.adapter = promedio_adapter
        recycler_view_promedios.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        add_promedio.setOnClickListener { addPromedio() }
        mostrarTotal()
    }

    private fun mostrarTotal() {
        var pTotal = 0
        var nTotal:Double = 0.0
        lista_promedios.forEach {
            pTotal += it.porcentaje
            nTotal += (it.nota.toDouble() * (it.porcentaje.toDouble()) / 100.0)
        }
        promedio_calculado.text = String.format(CABECERA, nTotal, pTotal)
    }

    private fun addPromedio() {
        lista_promedios.add(Promedio(0, 0))

        //  promedio_adapter.notifyDataSetChanged()
        recycler_view_promedios.swapAdapter(promedio_adapter,true)
        recycler_view_promedios.smoothScrollToPosition(lista_promedios.size-1)
        mostrarTotal()
    }

    override fun deletePromedio(promedio: Promedio) {
        lista_promedios.remove(promedio)
        promedio_adapter.notifyDataSetChanged()
        mostrarTotal()
    }

    override fun calcularProm() {
        mostrarTotal()
    }
}

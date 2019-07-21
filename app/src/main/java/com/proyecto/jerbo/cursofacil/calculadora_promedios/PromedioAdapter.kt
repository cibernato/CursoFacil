package com.proyecto.jerbo.cursofacil.calculadora_promedios

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.models.Promedio
import java.lang.Exception


class PromedioAdapter(var ctx: Context,  var  promedios_list: ArrayList<Promedio>, var mListener: OnClickListeners) : RecyclerView.Adapter<PromedioAdapter.PromedioViewHolder>() {
    val TAG = this.javaClass.name
    init {
        list = promedios_list
    }
    companion object {
        lateinit var list:  ArrayList<Promedio>
        fun getlist(): ArrayList<Promedio> { return list }
    }
    interface OnClickListeners {
        fun deletePromedio(promedio: Promedio)
        fun calcularProm()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromedioViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.promedio_item_list, parent, false)
        return PromedioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromedioViewHolder, position: Int) {
        holder.bind(promedios_list[position], mListener)
        Log.e(TAG,"promedios, $promedios_list")
        holder.notify(this,position)
    }


    override fun getItemCount(): Int {
        return promedios_list.size
    }

    class PromedioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var onClickListeners: OnClickListeners
        var nota: TextInputLayout = itemView.findViewById(R.id.nota_promedio)
        var porcentaje: TextInputLayout = itemView.findViewById(R.id.porcentaje_promedio)
        var delete: Button = itemView.findViewById(R.id.delete_promedio)
        fun bind(promedio: Promedio, listener: OnClickListeners) {
            onClickListeners = listener
            delete.setOnClickListener { listener.deletePromedio(promedio) }
            if (promedio.nota!=0)
                nota.editText?.setText(promedio.nota.toString())
            if (promedio.porcentaje!=0)
                porcentaje.editText?.setText(promedio.porcentaje.toString())

            nota.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    nota.error=""
                    try {
                        val temp = Integer.parseInt(p0.toString())
                        if (temp<0) nota.error="Numero mayor a 0"
                        else getlist()[adapterPosition].nota= temp
                        if (!porcentaje.editText?.text?.isBlank()!!) {
                            listener.calcularProm()
                            nota.error=""
                        }
                    }catch (e:Exception){
                        nota.error="Solo numeros"
                    }

                }
            })
            porcentaje.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    porcentaje.error=""
                    try {
                        val temp = Integer.parseInt(p0.toString())
                        if (temp<0) porcentaje.error="Numero mayor a 0"
                        else getlist()[adapterPosition].porcentaje= temp
                        if (!nota.editText?.text?.isBlank()!!) {
                            listener.calcularProm()
                            porcentaje.error=""
                        }
                    }catch (e:Exception){
                        porcentaje.error="Solo numeros"
                    }
                }
            })


        }
        lateinit var promedioAdapter: PromedioAdapter
        var  p:Int =0
        fun notify(promedioAdapter: PromedioAdapter, position: Int) {
            this.promedioAdapter = promedioAdapter
            this.p=position
        }


    }
}




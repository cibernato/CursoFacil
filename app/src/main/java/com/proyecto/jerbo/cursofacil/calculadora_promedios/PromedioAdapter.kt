package com.proyecto.jerbo.cursofacil.calculadora_promedios

import android.content.ContentValues.TAG
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.models.Promedio
import java.lang.Exception


class PromedioAdapter(var ctx: Context, var promedios_list: ArrayList<Promedio>, var mListener: OnClickListeners) : RecyclerView.Adapter<PromedioAdapter.PromedioViewHolder>() {
    val TAG = this.javaClass.name

    interface OnClickListeners {
        fun deletePromedio(promedio: Promedio, pos: Int)
        fun calcularProm()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PromedioViewHolder {
        val view = LayoutInflater.from(ctx).inflate(R.layout.promedio_item_list, parent, false)
        return PromedioViewHolder(view)
    }

    override fun onBindViewHolder(holder: PromedioViewHolder, position: Int) {
        holder.bind(promedios_list[position], mListener)
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
            delete.setOnClickListener { onClickListeners.deletePromedio(promedio, adapterPosition) }
            if (promedio.nota == 0) {
                nota.editText?.text?.clear()
                nota.isErrorEnabled = false

            } else {
                nota.editText?.setText(promedio.nota.toString())
            }

            if (promedio.porcentaje == 0) {
                porcentaje.editText?.text?.clear()
                porcentaje.isErrorEnabled = false

            } else {
                porcentaje.editText?.setText(promedio.porcentaje.toString())
            }

            nota.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    nota.error = ""
                    try {

                        val temp = Integer.parseInt(p0.toString())
                        when {
                            temp < 0 -> nota.error = "Numero mayor a 0"
                            temp > 20 -> nota.error = "Nota menor a 20"
                            else -> promedio.nota = temp
                        }
                        if (!porcentaje.editText?.text?.isBlank()!!) {
                            onClickListeners.calcularProm()
                            nota.error = ""
                        }
                    } catch (e: Exception) {
                        nota.error = "Ingrese nota"
                    }
                }
            })
            porcentaje.editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    porcentaje.error = ""

                    try {

                        val temp = Integer.parseInt(p0.toString())
                        when {
                            temp < 0 -> porcentaje.error = "Numero mayor a 0"
                            temp > 100 -> porcentaje.error = "Porcentaje menor a 100"
                            else -> promedio.porcentaje = temp
                        }
                        if (!nota.editText?.text?.isBlank()!!) {
                            onClickListeners.calcularProm()
                            porcentaje.error = ""
                        }
                    } catch (e: Exception) {
                        porcentaje.error = "Solo numeros"
                    }
                }
            })


        }
    }
}




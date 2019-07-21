package com.proyecto.jerbo.cursofacil.models

import java.io.Serializable

class Promedio(var nota: Int, var porcentaje: Int):Serializable{
    override fun toString(): String {
        return "Nota: $nota, Porcentaje: $porcentaje%"
    }
}

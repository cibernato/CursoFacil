package com.proyecto.jerbo.cursofacil.Class

import java.io.File
import java.io.Serializable

class Curso(var name: String?, var path: File?) : Serializable{
    override fun toString(): String {
        return "Nombre: $name , Ruta: $path"
    }
}

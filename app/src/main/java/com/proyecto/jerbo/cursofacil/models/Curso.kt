package com.proyecto.jerbo.cursofacil.models

import java.io.File
import java.io.Serializable

class Curso(var name: String?, var path: File? ,var archivado:Boolean?) : Serializable{
    override fun toString(): String {
        return "Nombre: $name , Ruta: $path , Archivado: $archivado \n"
    }
}

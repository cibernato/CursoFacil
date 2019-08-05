package com.proyecto.jerbo.cursofacil.models

import com.airbnb.epoxy.EpoxyController

class PhotoItemController(private val list: ArrayList<String>, private val epoxyClickListener: PhotoItem.EpoxyClickListener) : EpoxyController() {
    override fun buildModels() {
        list.forEach {
            PhotoItem(it, epoxyClickListener).id(it).addTo(this)
        }
    }
}
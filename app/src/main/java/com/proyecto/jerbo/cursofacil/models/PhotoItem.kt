package com.proyecto.jerbo.cursofacil.models

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.proyecto.jerbo.cursofacil.R


data class PhotoItem(val uri: String, val listener: EpoxyClickListener) : KotlinModel(R.layout.image_test_photo) {

    val view by bind<ImageView>(R.id.photo_item)
    lateinit var epoxyClickListener: EpoxyClickListener

    interface EpoxyClickListener {
        fun onClick(view: KotlinModel)
    }

    override fun bind() {
        Glide.with(view).load(uri).thumbnail(0.1f).into(view)

        epoxyClickListener = listener
        view.setOnClickListener {
            epoxyClickListener.onClick(getKotlinModel())
        }

    }
}





package com.proyecto.jerbo.cursofacil.curso_details;

import android.media.ExifInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.proyecto.jerbo.cursofacil.R;

import java.io.File;
import java.io.IOException;

public class Dialog_detalles extends DialogFragment {
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    String photoPath;
    private static final String TAG = "Dialog_detalles";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_detalles_foto,container,false);
        TextView fecha,path,tamaño;
        fecha = v.findViewById(R.id.fecha_foto_detalles);

        path = v.findViewById(R.id.ruta_local_foto_detalles);
        tamaño = v.findViewById(R.id.tamaño_foto_detalles);
        File p = new File(photoPath);
        try {
            ExifInterface exifInterface = new ExifInterface(photoPath);
            fecha.setText(exifInterface.getAttribute(ExifInterface.TAG_DATETIME));

            path.setText(p.getPath());
            tamaño.setText(p.length()/1024+" KB.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }
}

package com.proyecto.jerbo.cursofacil.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.proyecto.jerbo.cursofacil.Adapters.ViewPagerAdapter;
import com.proyecto.jerbo.cursofacil.Fragment.Dialog_detalles;
import com.proyecto.jerbo.cursofacil.R;

import java.io.File;
import java.util.ArrayList;

public class ViewPagerFotos extends AppCompatActivity {
    ArrayList<String> fotos;
    String[] foto;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager viewPager;
    ImageButton eliminar, compartir, detalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_fotos);
        Intent a = getIntent();
        fotos = a.getStringArrayListExtra("fotos");
        foto = new String[fotos.size()];
        for (int i = 0; i < fotos.size(); i++) {
            foto[i] = fotos.get(i);
        }
        viewPager = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(ViewPagerFotos.this, foto);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("num", 0));
        eliminar = findViewById(R.id.foto_eliminar);
        compartir = findViewById(R.id.foto_compartir);
        detalles = findViewById(R.id.foto_detalle);
        final Context context = this;
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("¿Desea eliminar esta foto?")
                        .setCancelable(false)
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarFoto(viewPager.getCurrentItem());

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File f = new File(fotos.get(viewPager.getCurrentItem()));
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.proyecto.jerbo.cursofacil.fileprovider",
                        f);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, photoURI);
                intent.setType("image/*");
                startActivity(Intent.createChooser(intent, "Share Image:"));
            }
        });
        registerForContextMenu(detalles);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //getMenuInflater().inflate(R.menu.menu_detalles, menu);
        if (v.getId() == detalles.getId()) {
            getMenuInflater().inflate(R.menu.menu_detalles, menu);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detalles_foto_menu:
                detallesImagen();
                return true;
            case R.id.editar_foto_menu:
                Toast.makeText(this, "Por implementar xd", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void detallesImagen() {

        Dialog_detalles dialog_detalles = new Dialog_detalles();
        dialog_detalles.setPhotoPath(fotos.get(viewPager.getCurrentItem()));
        dialog_detalles.show(getSupportFragmentManager(),"kappa");
    }

    private void eliminarFoto(int currentItem) {
        fotos.remove(currentItem);
        try {
            File f = new File(foto[currentItem]);
            f.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}

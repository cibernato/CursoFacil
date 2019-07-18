package com.proyecto.jerbo.cursofacil.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.customgallery.galleryapp.GalleryAct;
import com.proyecto.jerbo.cursofacil.Adapters.FotosAdapter;
import com.proyecto.jerbo.cursofacil.Class.Curso;
import com.proyecto.jerbo.cursofacil.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class VerCurso extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final String[] EXTENSIONS = new String[]{
            "jpg"};
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    private static final int ELIMINADO = 3;
    ArrayList<String> fotos;
    RecyclerView recyclerView;
    String pathToFile;
    String mCurrentPhotpPath;
    Curso elegido;
    FotosAdapter fotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent temp = getIntent();
        elegido = (Curso) temp.getSerializableExtra("curso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);


        setContentView(R.layout.activity_ver_curso);
        fotos = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler_view_cursos_fotos);
        llenarFotos();
        fotosAdapter = new FotosAdapter(fotos);
        recyclerView.setAdapter(fotosAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        fotosAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(getBaseContext(), ViewPagerFotos.class);
                a.putExtra("fotos", fotos);
                a.putExtra("num", recyclerView.getChildAdapterPosition(v));

                startActivityForResult(a,ELIMINADO);
            }
        });

        Button tomar = findViewById(R.id.tomar_foto);
        tomar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
        }
    }

    private void llenarFotos() {
        File f = elegido.getPath();
        File[] files = f.listFiles(IMAGE_FILTER);
        for (File inFile : files)
            try {
                if (inFile.length() != 0) {
                    System.out.println("image: " + inFile.getName());
                    System.out.println(" size  : " + f.length());
                    fotos.add(inFile.getAbsolutePath());
                } else {
                    inFile.delete();
                }
            } catch (Exception e) {
            }

    }

    ImageView p1_1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            fotos.clear();
            llenarFotos();
            fotosAdapter.notifyDataSetChanged();
        }
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> imagePath = data.getStringArrayListExtra(GalleryAct.IMAGE_KEY);
            p1_1 = findViewById(R.id.p1_i);
            Glide.with(this).load(imagePath.get(0)).into(p1_1);
        }
        if (requestCode == ELIMINADO) {
            if (resultCode == Activity.RESULT_OK) {
                fotos.clear();
                llenarFotos();
                fotosAdapter.notifyDataSetChanged();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy").format(new Date());
        String imageFileName = elegido.getName() + timeStamp + "_";
        File storgeDir = elegido.getPath();
        File image = null;
        try {
            image = File.createTempFile(imageFileName, ".jpg", storgeDir);
        } catch (IOException e) {
            Log.d("my log", "Excep: " + e.toString());
        }
        mCurrentPhotpPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.proyecto.jerbo.cursofacil.fileprovider",
                        photoFile);
                Log.e("mmmm", " " + photoURI.toString() + "    " + pathToFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.ver_curso_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.borrar_curso) {
            final Context context = this;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("¿Desea eliminar este compromiso?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            borrarCurso(elegido.getPath());
                            Intent a = new Intent(context, MainActivity.class);
                            startActivity(a);
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
        return super.onContextItemSelected(item);
    }

    private void borrarCurso(File f) {
        if (f.isDirectory())
            for (File child : f.listFiles())
                borrarCurso(child);
        f.delete();

    }



}


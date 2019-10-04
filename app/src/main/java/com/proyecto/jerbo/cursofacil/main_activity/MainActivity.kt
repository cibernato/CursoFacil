package com.proyecto.jerbo.cursofacil.main_activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.proyecto.jerbo.cursofacil.R
import com.proyecto.jerbo.cursofacil.activos.Activos
import com.proyecto.jerbo.cursofacil.archivados.Archivados
import kotlinx.android.synthetic.main.main_activity_container.*

class MainActivity : AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        Archivados.OnFragmentInteractionListener,
        Activos.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.contentMain, Activos(),"Mal Sea").commit()
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.app_name,
                R.string.name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)
    }


    override fun onBackPressed() {
        val context = this
        val builder = AlertDialog.Builder(context)
        builder.setMessage("¿Desea salir de la aplicacion?")
                .setCancelable(false)
                .setPositiveButton("Si") { _, _ ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        when (item.itemId) {
            R.id.archivados -> {
//                val intent = Intent(this,Archivados::class.java)
//                startActivity(intent)
                supportFragmentManager.beginTransaction().replace(R.id.contentMain, Archivados()).commit()
            }
            R.id.activos -> {
                supportFragmentManager.beginTransaction().replace(R.id.contentMain, Activos()).commit()
            }
            R.id.extras -> {
                Toast.makeText(this, "Por implementar", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        private val TAG = this::class.simpleName
    }

}

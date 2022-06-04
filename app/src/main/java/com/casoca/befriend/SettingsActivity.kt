package com.casoca.befriend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.casoca.befriend.databinding.ActivitySettingsBinding
import com.casoca.befriend.databinding.FragmentHomeBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var navController:NavController
    private lateinit var toolbar:Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        toolbar = findViewById(R.id.toolbar)
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_settings)as NavHostFragment
        navController=navHostFragment.findNavController()
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu,menu)
        return true

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home-> finish()
            R.id.usuario -> navController.navigate(R.id.usuarioSettingsFragment)

            R.id.frases -> navController.navigate(R.id.frasesSettingsFragment)
            R.id.conversaciones -> navController.navigate(R.id.conversacionesSettingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}

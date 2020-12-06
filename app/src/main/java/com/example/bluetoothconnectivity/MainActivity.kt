package com.example.bluetoothconnectivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    var navHostController:NavController?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      //  navHostController=Navigation.findNavController(this,R.id.nav_host_fragment)
       // setupActionBarWithNavController(navController)
       // NavigationUI.setupActionBarWithNavController(this, navHostController!!)
     // N  setupActionBarWithNavController(this,navController)
    }

//    override fun onSupportNavigateUp(): Boolean {
//        val navController=findNavController(R.id.nav_host_fragment)
//
//        return navController.navigateUp()|| super.onSupportNavigateUp()
//    }

}
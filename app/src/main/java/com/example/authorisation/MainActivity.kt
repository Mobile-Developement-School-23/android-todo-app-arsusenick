package com.example.authorisation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.authorisation.internetThings.MyWorkManager
import com.example.authorisation.internetThings.network.RetrofitClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//мейн активити...
class MainActivity : AppCompatActivity() {

    private lateinit var controller: NavController

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        (applicationContext as App).appComponent.inject(this)
        controller = getRootNavController()


        if (savedInstanceState != null) {
            controller.restoreState(savedInstanceState.getBundle("state"))
        } else {
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                        as NavHostFragment
            val graphInflater = navHostFragment.navController.navInflater
            val navGraph = graphInflater.inflate(R.navigation.rg)

            controller = navHostFragment.navController
            val destination = if (sharedPreferencesHelper.getToken() == "no_token"
            ) R.id.loginFragment else R.id.blankFragment

            navGraph.setStartDestination(destination)

            controller.graph = navGraph
        }
    }
    private fun getRootNavController(): NavController {
        val navHost = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        return navHost.navController
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle("state", controller.saveState())
    }
}
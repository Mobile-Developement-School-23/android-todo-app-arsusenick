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
import com.example.authorisation.internetThings.network.Client
import java.util.concurrent.TimeUnit

//мейн активити...
class MainActivity : AppCompatActivity() {

    private lateinit var controller: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    as NavHostFragment
        controller = navHostFragment.navController

        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.rg)
        controller = navHostFragment.navController

        val destination = if (Client.token == "no_token"
        ) R.id.loginFragment else R.id.blankFragment
        navGraph.setStartDestination(destination)
        controller.graph = navGraph

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun internetConnectionUP(){
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val myWorkRequest = PeriodicWorkRequest.Builder(
            MyWorkManager::class.java,
            8,
            TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag("update_data")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "update_data",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            myWorkRequest
        )
    }
}
package com.example.authorisation.internetThings

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.authorisation.data.rep.TodoItemsRepository
import kotlinx.coroutines.runBlocking

class MyWorkManager(
    context: Context,
    workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {

    private val repository: TodoItemsRepository by localeLazy()

    override fun doWork(): Result {
        return when (mergeData()) {
            is StateLoad.Success -> Result.success()
            else -> {
                Result.failure()
            }
        }
    }

    private fun mergeData() = runBlocking {
        return@runBlocking repository.getNetworkData()
    }

}

package com.example.authorisation.internetThings

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.authorisation.data.rep.TodoItemsRepository
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class MyWorkManager(
    context: Context,
    workerParameters: WorkerParameters,
) : Worker(context, workerParameters) {

    @Inject
    lateinit var repository: TodoItemsRepository

    override fun doWork(): Result {
        mergeData()
        return Result.success()
    }

    private fun mergeData() = runBlocking {
        return@runBlocking repository.getNetworkTasks()
    }

}

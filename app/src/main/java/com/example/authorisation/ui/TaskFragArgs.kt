package com.example.authorisation.ui

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import java.lang.IllegalArgumentException

public data class TaskFragArgs (
    public val id: String?
) : NavArgs {
    public fun toBundle(): Bundle {
        val result = Bundle()
        result.putString("id", this.id)
        return result
    }

    public fun toSavedStateHandle(): SavedStateHandle {
        val result = SavedStateHandle()
        result.set("id", this.id)
        return result
    }

    public companion object {
        @JvmStatic
        public fun fromBundle(bundle: Bundle): TaskFragArgs {
            bundle.setClassLoader(TaskFragArgs::class.java.classLoader)
            val __id : String?
            if (bundle.containsKey("id")) {
                __id = bundle.getString("id")
            } else {
                throw IllegalArgumentException("Required argument \"id\" is missing and does not have an android:defaultValue")
            }
            return TaskFragArgs(__id)
        }

        @JvmStatic
        public fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): TaskFragArgs {
            val __id : String?
            if (savedStateHandle.contains("id")) {
                __id = savedStateHandle["id"]
            } else {
                throw IllegalArgumentException("Required argument \"id\" is missing and does not have an android:defaultValue")
            }
            return TaskFragArgs(__id)
        }
    }
}
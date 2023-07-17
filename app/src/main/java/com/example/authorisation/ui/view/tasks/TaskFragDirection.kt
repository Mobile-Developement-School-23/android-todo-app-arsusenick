package com.example.authorisation.ui.view.tasks

import android.os.Bundle
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.authorisation.R

public class TaskFragDirection private constructor() {
    private data class ActionManageTask(
        public val id: String?
    ) : NavDirections {
        public override val actionId: Int = R.id.action_blankFragment_to_blankFragment2

        public override val arguments: Bundle
            get() {
                val result = Bundle()
                result.putString("id", this.id)
                return result
            }
    }

    public companion object {
        public fun actionManageTask(id: String?): NavDirections = ActionManageTask(id)

        public fun actionSetting(): NavDirections = ActionOnlyNavDirections(R.id.action_blankFragment_to_settingsFragment)
    }
}
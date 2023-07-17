package com.example.authorisation.ui.view.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.authorisation.R

public class LoginFragDirection private constructor() {
    public companion object {
        public fun actionMainTasks(): NavDirections = ActionOnlyNavDirections(R.id.action_loginFragment_to_blankFragment)
    }
}
package com.example.authorisation.ui.view.settings

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.example.authorisation.R


public class SettFragDirection private constructor() {
  public companion object {
    public fun actionLogOut(): NavDirections = ActionOnlyNavDirections(R.id.action_settingsFragment_to_loginFragment)
  }
}

package com.example.authorisation.ui.view.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.authorisation.App
import com.example.authorisation.R
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.databinding.FragmentLoginBinding
import com.example.authorisation.ui.stateHold.LoginViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthSdk
import javax.inject.Inject


class LoginFragment : Fragment() {

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    @Inject
    lateinit var sdk : YandexAuthSdk

    private val viewModel: LoginViewModel by viewModels {
        (requireContext().applicationContext as App).appComponent.viewModelsFactory()
    }

    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentLoginBinding.inflate(layoutInflater).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireContext().applicationContext as App).appComponent.loginFragmentComponentBuilder()
            .create().inject(this)

        if (sharedPreferencesHelper.getToken() == "no_token"
            && sharedPreferencesHelper.getNotificationPermission() == "none"
        ) {
            if (Build.VERSION.SDK_INT >= 33) {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showSettingDialog()
            }
        }

        val register: ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == -1) {
                val data: Intent? = result.data
                if (data != null) {
                    try {
                        val yandexAuthToken = sdk.extractToken(result.resultCode, data)
                        if (yandexAuthToken != null) {
                            val curToken = yandexAuthToken.value
                            if (curToken != sharedPreferencesHelper.getToken()) {
                                sharedPreferencesHelper.putToken(curToken)
                                sharedPreferencesHelper.putRevision(0)
                                viewModel.deleteCurrentItems()
                            }
                            moveToTasks()
                        }
                    } catch (exception: YandexAuthException) {
                        Toast.makeText(context, exception.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        views {
            loginWithYandexButton.setOnClickListener {
                register.launch(sdk.createLoginIntent(YandexAuthLoginOptions.Builder().build()))
            }
            loginButton.setOnClickListener {
                if(sharedPreferencesHelper.getToken() != "unaffordable") {
                    sharedPreferencesHelper.putToken("unaffordable")
                    sharedPreferencesHelper.putRevision(0)
                    viewModel.deleteCurrentItems()
                }
                moveToTasks()
            }
        }
    }

    private fun showSettingDialog() {
        MaterialAlertDialogBuilder(
            ContextThemeWrapper(
                context,
                R.style.AlertDialogCustom
            )
        )
            .setTitle("Разрешение на показ уведомлений")
            .setMessage("Показывать уведомления о ближайших событиях?")
            .setPositiveButton("Да") { _, _ ->
                sharedPreferencesHelper.putNotificationPermission(true)
            }
            .setNegativeButton("Нет") { _, _ ->
                sharedPreferencesHelper.putNotificationPermission(false)
            }
            .show()
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            sharedPreferencesHelper.putNotificationPermission(isGranted)
        }

    private fun moveToTasks() {
        val action = LoginFragDirection.actionMainTasks()
        findNavController().navigate(action)
    }

    private fun <T : Any> views(block: FragmentLoginBinding.() -> T): T? = binding?.block()


}
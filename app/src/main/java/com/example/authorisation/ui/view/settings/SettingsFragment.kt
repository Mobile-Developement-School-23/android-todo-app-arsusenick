package com.example.authorisation.ui.view.settings

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.authorisation.App
import com.example.authorisation.R
import com.example.authorisation.SharedPreferencesHelper
import com.example.authorisation.databinding.FragmentSettingsBinding
import com.example.authorisation.internetThings.Mode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireContext().applicationContext as App).appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)

        binding.autoDownloadSwitch.setOnCheckedChangeListener { _, checked ->
            binding.autoDownloadSwitch.isClickable = checked
            if(!checked){
                sharedPreferencesHelper.putNotificationPermission(false)
            }
        }


        if(sharedPreferencesHelper.getNotificationPermission() == "true"){
            binding.autoDownloadSwitch.isChecked = true
            binding.autoDownloadSwitch.isClickable = true
        }




        binding.autoDownload.setOnClickListener {
            if(!binding.autoDownloadSwitch.isChecked){
                if (Build.VERSION.SDK_INT >= 33) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    showSettingDialog()
                }
            }
        }


        when(sharedPreferencesHelper.getMode()){
            Mode.NIGHT -> binding.themeName.text = "Тёмная"
            Mode.LIGHT -> binding.themeName.text = "Светлая"
            Mode.SYSTEM -> binding.themeName.text = "Из настроек системы"
        }

        binding.theme.setOnClickListener {
            BottomSheetFragment(object : ThemeInterface {
                override fun changeTheme() {
                    when(sharedPreferencesHelper.getMode()){
                        Mode.NIGHT -> binding.themeName.text = "Тёмная"
                        Mode.LIGHT -> binding.themeName.text = "Светлая"
                        Mode.SYSTEM -> binding.themeName.text = "Из настроек системы"
                    }
                }

            }).show(parentFragmentManager, "change_task")
        }

        binding.logOut.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(
                ContextThemeWrapper(
                    context,
                    R.style.AlertDialogCustom
                )
            )
            builder.apply {
                val title = "Вы уверены, что хотите выйти? Возможна потеря данных оффлайн режима."
                setMessage(title)
                setPositiveButton(
                    "Выйти"
                ) { _, _ ->

                    val action = SettFragDirection.actionLogOut()
                    findNavController().navigate(action)
                }
            }
            builder.show()
                .create()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    interface ThemeInterface {
        fun changeTheme()
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
                binding.autoDownloadSwitch.isChecked = true
            }
            .setNegativeButton("Нет") { _, _ ->
                sharedPreferencesHelper.putNotificationPermission(false)
            }
            .show()
    }


    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            sharedPreferencesHelper.putNotificationPermission(isGranted)
            binding.autoDownloadSwitch.isChecked = isGranted
        }

}
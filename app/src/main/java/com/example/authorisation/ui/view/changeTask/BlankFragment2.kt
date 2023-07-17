package com.example.authorisation.ui.view.changeTask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.authorisation.App
import com.example.authorisation.databinding.FragmentBlank2Binding
import com.example.authorisation.ui.stateHold.ManageTaskViewModel
class BlankFragment2 : Fragment(){

    private val model: ManageTaskViewModel by viewModels {
        (requireContext().applicationContext as App).appComponent.viewModelsFactory()
    }

    private lateinit var binding: FragmentBlank2Binding
    private val args: TaskFragArgs by navArgs()
    private var fragmentViewController: BlankFragmentController2? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlank2Binding.inflate(LayoutInflater.from(context))
        fragmentViewController = BlankFragmentController2(
            requireContext(),
            findNavController(),
            binding,
            viewLifecycleOwner,
            model,
            args
        ).apply {
            onCreate()
        }

        return binding.root
    }

}
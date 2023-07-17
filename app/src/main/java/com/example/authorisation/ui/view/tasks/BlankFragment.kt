package com.example.authorisation.ui.view.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.authorisation.App
import com.example.authorisation.databinding.FragmentBlankBinding
import com.example.authorisation.ui.stateHold.MyViewModel

class BlankFragment : Fragment() {

    private val viewModel: MyViewModel by viewModels {
        (requireContext().applicationContext as App).appComponent.viewModelsFactory()
    }

    private var fragmentController: BlankFragmentController? = null

    private lateinit var binding: FragmentBlankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBlankBinding.inflate(LayoutInflater.from(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = binding.root
        fragmentController = BlankFragmentController(
            requireContext(),
            findNavController(),
            binding,
            viewLifecycleOwner,
            viewModel,
            layoutInflater
        ).apply {
            setUpViews()
        }

        viewModel.loadData()
        return root
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentController = null
    }

}
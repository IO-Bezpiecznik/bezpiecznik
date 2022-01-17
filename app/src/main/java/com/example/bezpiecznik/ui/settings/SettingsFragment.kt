package com.example.bezpiecznik.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bezpiecznik.R
import com.example.bezpiecznik.databinding.FragmentHomeBinding
import com.example.bezpiecznik.databinding.GridListFragmentBinding
import com.example.bezpiecznik.databinding.SettingsFragmentBinding
import com.example.bezpiecznik.ui.gridList.GridListViewModel
import com.example.bezpiecznik.ui.home.HomeViewModel


class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnExport.setOnClickListener {
            findNavController().navigate(R.id.action_settings_to_nav_export)
        }
        return root
    }

}
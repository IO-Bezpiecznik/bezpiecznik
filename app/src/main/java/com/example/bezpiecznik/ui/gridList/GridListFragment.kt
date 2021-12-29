package com.example.bezpiecznik.ui.gridList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bezpiecznik.databinding.GridListFragmentBinding

class GridListFragment : Fragment() {
    private var _binding: GridListFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GridListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[GridListViewModel::class.java]

        viewModel.fetchList()

        _binding = GridListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.list.observe(viewLifecycleOwner, {
            it
            // it - List<Grid>
        })
    }
}
package com.example.bezpiecznik.ui.gridList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bezpiecznik.R
import com.example.bezpiecznik.databinding.GridListFragmentBinding
import com.example.bezpiecznik.types.Grid
import com.example.bezpiecznik.ui.home.HomeViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement

class GridListFragment : Fragment(), GridListItemClickInterface {
    private var _binding: GridListFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: GridListViewModel
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        viewModel =
            ViewModelProvider(this)[GridListViewModel::class.java]

        viewModel.fetchList()

        _binding = GridListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = GridListAdapter(this)

        binding.list.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewModel.list.observe(viewLifecycleOwner, {
            (binding.list.adapter as GridListAdapter).updateList(it)
        })
    }

    override fun onGridListItemClicked(gridListItem: Grid) {
        val board = Json.decodeFromJsonElement<List<List<Int>>>(Json.parseToJsonElement(gridListItem.board) as JsonArray)
        homeViewModel.gridID = gridListItem._id
        homeViewModel.gridPoints = board
        homeViewModel.size = listOf(board.count(), board[0].count())
        homeViewModel.pattern = ""
        homeViewModel.loadedFromList = false
        findNavController().navigate(R.id.action_nav_grid_list_to_nav_home)
    }
}
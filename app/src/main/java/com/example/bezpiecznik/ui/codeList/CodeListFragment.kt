package com.example.bezpiecznik.ui.codeList

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.bezpiecznik.R
import com.example.bezpiecznik.databinding.CodeListFragmentBinding
import com.example.bezpiecznik.types.Code
import com.example.bezpiecznik.ui.home.HomeViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement


class CodeListFragment : Fragment(), CodeListItemClickInterface {
    private var _binding: CodeListFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CodeListViewModel
    private lateinit var homeViewModel: HomeViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        viewModel =
            ViewModelProvider(this)[CodeListViewModel::class.java]
        viewModel.fetchList()
        _binding = CodeListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = CodeListAdapter(this)
        binding.listCode.apply {
            this.adapter = adapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        viewModel.list.observe(viewLifecycleOwner, {
            (binding.listCode.adapter as CodeListAdapter).updateList(it)
        })
    }


    override fun onCodeListItemClicked(codeListItem: Code) {
        val board = Json.decodeFromJsonElement<List<List<Int>>>(Json.parseToJsonElement(codeListItem.grid.board) as JsonArray)
        homeViewModel.gridID = codeListItem.grid._id
        homeViewModel.gridPoints = board
        homeViewModel.size = listOf(board.count(), board[0].count())
        homeViewModel.pattern = codeListItem.pattern
        homeViewModel.loadedFromList = true
        findNavController().navigate(R.id.action_nav_code_list_to_nav_home)
    }

}
package com.example.bezpiecznik.ui.gridCreator

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.example.bezpiecznik.R
import com.example.bezpiecznik.databinding.GridCreatorFragmentBinding


class GridCreatorFragment : Fragment() {
    private lateinit var viewModel: GridCreatorViewModel

    private var _binding: GridCreatorFragmentBinding? = null
    private val binding get() = _binding!!

    inner class ChangeListener(private val update: (Int) -> Unit) : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            update(GridCreatorViewModel.Constants.VALUES[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[GridCreatorViewModel::class.java]

        _binding = GridCreatorFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinner.adapter = viewModel.getGridSizeAdapter(requireContext())
        binding.spinner2.adapter = viewModel.getGridSizeAdapter(requireContext())

        binding.spinner.onItemSelectedListener = ChangeListener { viewModel.updateColCount(it) }
        binding.spinner2.onItemSelectedListener = ChangeListener { viewModel.updateRowCount(it) }

        viewModel.grid.observe(viewLifecycleOwner, {
            binding.gridContainer.removeAllViews()

            val width = requireContext().resources.displayMetrics.widthPixels
            val totalSpace = width - 300 - 4 * it.row
            val spacePerItem = totalSpace / if (it.row > it.col) it.row else it.col

            binding.gridContainer.columnCount = it.col
            binding.gridContainer.rowCount = it.row

            for (index in it.grid.indices) {
                val imageView = ImageView(requireContext())
                imageView.setImageResource(R.drawable.grid_point)
                val params = GridLayout.LayoutParams()
                params.width = spacePerItem
                params.height = spacePerItem
                imageView.layoutParams = params

                imageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
                if (!it.grid[index]) imageView.alpha = 0.2F
                imageView.setOnClickListener {
                    viewModel.handleGridPointClick(index)
                }
                binding.gridContainer.addView(imageView)
            }
            binding.gridContainer.invalidate()
        })

        binding.button.setOnClickListener {
            viewModel.saveGrid()
            val text = "Dodano siatkę"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(requireContext(), text, duration)
            toast.show()
            view.findNavController().navigate(R.id.nav_grid_list)
        }
    }
}
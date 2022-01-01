package com.example.bezpiecznik.ui.exportImportData

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.bezpiecznik.databinding.ExportImportFragmentBinding
import java.nio.charset.Charset

class ExportImportFragment : Fragment() {
    private lateinit var viewModel: ExportImportViewModel

    private var _binding: ExportImportFragmentBinding? = null
    private val binding get() = _binding!!

    private val getSaveUri = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri ->
        val stream = requireContext().contentResolver.openOutputStream(uri)
        stream?.let {
            viewModel.saveBackup(it)
        }

        if (stream == null) {
            Toast.makeText(requireContext(), "Wystąpił błąd", Toast.LENGTH_SHORT).show()
            viewModel.stopLoad()
        }
    }

    private val getLoadUri = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        val stream = requireContext().contentResolver.openInputStream(uri)
        stream?.let {
            viewModel.loadBackup(it)
        }

        if (stream == null) {
            Toast.makeText(requireContext(), "Wystąpił błąd", Toast.LENGTH_SHORT).show()
            viewModel.stopLoad()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this)[ExportImportViewModel::class.java]

        _binding = ExportImportFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.toast.observe(viewLifecycleOwner, {
            it?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.showLoad.observe(viewLifecycleOwner, {
            binding.importBtn.isEnabled = !it
        })

        viewModel.showLoad.observe(viewLifecycleOwner, {
            binding.exportBtn.isEnabled = !it
        })

        binding.exportBtn.setOnClickListener {
            viewModel.startLoad()
            getSaveUri.launch("backup.txt")
        }

        binding.importBtn.setOnClickListener {
            viewModel.startLoad()
            getLoadUri.launch(arrayOf("text/plain"))
        }
    }
}
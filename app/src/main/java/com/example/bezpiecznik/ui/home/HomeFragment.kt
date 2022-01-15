package com.example.bezpiecznik.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bezpiecznik.databinding.FragmentHomeBinding
import com.example.bezpiecznik.types.CodeCreateDto
import com.example.bezpiecznik.ui.lockPatternView.LockPatternView
import kotlinx.serialization.json.*
import org.json.JSONArray
import android.provider.Settings.Secure
import androidx.core.content.ContextCompat.getSystemService

import android.telephony.TelephonyManager
import android.widget.ProgressBar
import androidx.core.content.ContextCompat


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(requireActivity())[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val pbPoints: ProgressBar = binding.pbScore

        val lpv: LockPatternView = binding.lpvMain
        lpv.setGridPoints(homeViewModel.gridPoints)
        lpv.setSize(homeViewModel.size[0], homeViewModel.size[1])
        lpv.setPattern(homeViewModel.pattern)
        lpv.render()

        lpv.score.observe(this.viewLifecycleOwner,{
            pbPoints.progress = it
        })

        val resetButton: Button = binding.btnReset
        val saveButton: Button = binding.btnSave

        if(homeViewModel.loadedFromList){
            saveButton.isEnabled = false
        }

        resetButton.setOnClickListener {
            lpv.reset()
            saveButton.isEnabled = true
            homeViewModel.pattern = ""
        }

        saveButton.setOnClickListener {
            save(lpv)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun save(lpv:LockPatternView){
        if(lpv.getFinish()){
            var i = 1
            val selectedPoint = lpv.generateSelectedIds()
            val arrayPoint = mutableMapOf<String, Int>()
            for(j in selectedPoint){
                arrayPoint[i.toString()] = j
                i++
            }
            val json = arrayPoint.toJsonObject().toString()
            val android_id = Secure.getString(
                requireContext().contentResolver,
                Secure.ANDROID_ID
            )
            val code = CodeCreateDto(pattern = json, gridId = homeViewModel.gridID, username = android_id, points = lpv.score.value!!)
            homeViewModel.saveCode(code)
            Toast.makeText(context, "Zapisano", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Nie narysowano kodu", Toast.LENGTH_SHORT).show()
        }
    }
    //json convert
    fun Any?.toJsonElement(): JsonElement {
        return when (this) {
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is String -> JsonPrimitive(this)
            is Array<*> -> this.toJsonArray()
            is List<*> -> this.toJsonArray()
            is MutableMap<*, *> -> this.toJsonObject()
            is JsonElement -> this
            else -> JsonNull
        }
    }

    fun Array<*>.toJsonArray(): JsonArray {
        val array = mutableListOf<JsonElement>()
        this.forEach { array.add(it.toJsonElement()) }
        return JsonArray(array)
    }

    fun List<*>.toJsonArray(): JsonArray {
        val array = mutableListOf<JsonElement>()
        this.forEach { array.add(it.toJsonElement()) }
        return JsonArray(array)
    }

    fun MutableMap<*, *>.toJsonObject(): JsonObject {
        val map = mutableMapOf<String, JsonElement>()
        this.forEach {
            if (it.key is String) {
                map[it.key as String] = it.value.toJsonElement()
            }
        }
        return JsonObject(map)
    }
}
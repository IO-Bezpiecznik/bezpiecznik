package com.example.bezpiecznik.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bezpiecznik.service.CodeApi
import com.example.bezpiecznik.service.GridApi
import com.example.bezpiecznik.types.CodeCreateDto
import com.example.bezpiecznik.types.GridCreateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val codeApi = CodeApi()
    var gridPoints: List<List<Int>> = listOf(
        listOf(1,1,1),
        listOf(1,1,1),
        listOf(1,1,1)
    )

    var size: List<Int> = listOf(3, 3)

    lateinit var gridID: String

    fun saveCode(code: CodeCreateDto) {
        CoroutineScope(Dispatchers.IO).launch {
            codeApi.create(code)
        }
    }
}
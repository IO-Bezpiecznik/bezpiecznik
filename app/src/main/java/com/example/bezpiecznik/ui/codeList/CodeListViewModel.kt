package com.example.bezpiecznik.ui.codeList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bezpiecznik.service.CodeApi
import com.example.bezpiecznik.types.Code
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CodeListViewModel: ViewModel() {
    private val codeApi = CodeApi()

    private val _list = MutableLiveData<List<Code>>().apply {
        value = emptyList()
    }
    val list: LiveData<List<Code>> = _list

    fun fetchList() {
        CoroutineScope(Dispatchers.IO).launch {
            val code = codeApi.getAll()
            _list.postValue(code)
        }
    }
}
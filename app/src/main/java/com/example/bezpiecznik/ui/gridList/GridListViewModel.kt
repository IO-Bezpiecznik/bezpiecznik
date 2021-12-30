package com.example.bezpiecznik.ui.gridList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bezpiecznik.service.GridApi
import com.example.bezpiecznik.types.Grid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class GridListViewModel : ViewModel() {
    private val gridApi = GridApi()

    private val _list = MutableLiveData<List<Grid>>().apply {
        value = emptyList()
    }
    val list: LiveData<List<Grid>> = _list

    fun fetchList() {
        CoroutineScope(IO).launch {
            val grids = gridApi.getAll()
            _list.postValue(grids)
        }
    }
}
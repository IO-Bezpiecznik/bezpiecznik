package com.example.bezpiecznik.ui.gridCreator

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bezpiecznik.R
import com.example.bezpiecznik.service.GridApi
import com.example.bezpiecznik.types.GridCreateDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class GridCreatorViewModel : ViewModel() {
    private val gridApi = GridApi()

    object Constants {
        const val DEFAULT_SIZE = 3
        val VALUES = arrayOf(3, 4, 5, 6)
    }

    data class GridState (val row: Int, val col: Int, val grid: BooleanArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GridState

            if (row != other.row) return false
            if (col != other.col) return false
            if (!grid.contentEquals(other.grid)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = row
            result = 31 * result + col
            result = 31 * result + grid.contentHashCode()
            return result
        }
    }

    private val _grid = MutableLiveData<GridState>().apply {
        val initSize = Constants.DEFAULT_SIZE
        value = GridState(
            initSize,
            initSize,
            BooleanArray(initSize * initSize) { true }
        )
    }
    val grid: LiveData<GridState> = _grid


    fun initGrid() {
        val initSize = Constants.DEFAULT_SIZE
        val value = GridState(
            initSize,
            initSize,
            BooleanArray(initSize * initSize) { true }
        )
        _grid.postValue(value)
    }

    fun getGridSizeAdapter(context: Context): ArrayAdapter<Int> {
        return ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, Constants.VALUES)
    }

    fun updateColCount(count: Int) {
        val s1 = _grid.value!!.row

        _grid.postValue(
            GridState(
                s1,
                count,
                BooleanArray(s1 * count) { true }
            )
        )
    }

    fun updateRowCount(count: Int) {
        val s2 = _grid.value!!.col

        _grid.postValue(
            GridState(
                count,
                s2,
                BooleanArray(count * s2) { true }
            )
        )
    }

    fun handleGridPointClick(index: Int) {
        val currentVal = _grid.value!!.grid
        currentVal[index] = !currentVal[index]

        _grid.postValue(
            GridState(
                _grid.value!!.row,
                _grid.value!!.col,
                currentVal
            )
        )
    }

    fun saveGrid() {
        val gridState = _grid.value!!

        val convertedGrid = gridState.grid.map { if (it) 1 else 0 }

        val parsedArray = arrayOfNulls<Array<Int>>(gridState.row)
        for (index in parsedArray.indices) {
            val padding = index * gridState.col
            parsedArray[index] = convertedGrid.slice((padding + 0) until padding + gridState.col).toTypedArray()
        }

        CoroutineScope(IO).launch {
            gridApi.create(GridCreateDto(board = parsedArray.contentDeepToString()))
        }
    }
}
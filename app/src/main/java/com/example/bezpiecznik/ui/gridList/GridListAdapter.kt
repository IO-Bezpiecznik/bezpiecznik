package com.example.bezpiecznik.ui.gridList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.bezpiecznik.R
import com.example.bezpiecznik.types.Grid
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement

class GridListAdapter(
    private val gridListItemClickInterface: GridListItemClickInterface
): RecyclerView.Adapter<GridListAdapter.ViewHolder>() {
    private var dataSet: List<Grid> = emptyList()

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val gridSize: TextView = view.findViewById(R.id.size_text)
        private val grid: GridLayout = view.findViewById(R.id.grid)
        val btn: Button = view.findViewById(R.id.grid_item_btn)

        fun setSize(s1: Int, s2: Int) {
            gridSize.text = view.resources.getString(R.string.grid_size, s1, s2)
        }

        fun generateGrid(board: List<List<Int>>) {
            grid.removeAllViews()

            val rows = board.size
            val cols = board[0].size

            val width = view.context.resources.displayMetrics.widthPixels
            val totalSpace = width - 750 - 4 * rows
            val spacePerItem = totalSpace / if (rows > cols) rows else cols

            grid.columnCount = cols
            grid.rowCount = rows

            val list = board.flatten()

            for (index in list.indices) {
                val imageView = ImageView(view.context)
                imageView.setImageResource(R.drawable.grid_point)
                val params = GridLayout.LayoutParams()
                params.width = (16 * view.resources.displayMetrics.density).toInt()
                params.height = (16 * view.resources.displayMetrics.density).toInt()
                imageView.layoutParams = params

                imageView.setColorFilter(ContextCompat.getColor(view.context, R.color.normalColor))
                if (list[index] == 0) imageView.alpha = 0F
                grid.addView(imageView)
            }
            grid.invalidate()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_list_item_fragment, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = dataSet[position]
        val boardElement = Json.parseToJsonElement(data.board) as JsonArray
        val board = Json.decodeFromJsonElement<List<List<Int>>>(boardElement)
        viewHolder.setSize(board[0].size, board.size)
        viewHolder.generateGrid(board)
        viewHolder.btn.setOnClickListener {
            gridListItemClickInterface.onGridListItemClicked(data)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateList(grids: List<Grid>) {
        this.dataSet = grids
        notifyDataSetChanged()
    }
}

interface GridListItemClickInterface{
    fun onGridListItemClicked(gridListItem: Grid)
}
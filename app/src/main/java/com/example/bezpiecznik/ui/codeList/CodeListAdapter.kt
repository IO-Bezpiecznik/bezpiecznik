package com.example.bezpiecznik.ui.codeList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bezpiecznik.R
import com.example.bezpiecznik.types.Code
import com.example.bezpiecznik.ui.lockPatternView.LockPatternView
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.decodeFromJsonElement


class CodeListAdapter(
    private val codeListItemClickInterface: CodeListItemClickInterface
): RecyclerView.Adapter<CodeListAdapter.ViewHolder>() {
    private var dataSet: List<Code> = emptyList()
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val gridSize: TextView = view.findViewById(R.id.size_text)
        private val lpv: LockPatternView = view.findViewById(R.id.lpv_code)
        val btn: Button = view.findViewById(R.id.code_item_btn)
        private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
        fun setSize(s1: Int, s2: Int) {
            gridSize.text = view.resources.getString(R.string.grid_size, s1, s2)
        }
        fun generateBar(value: Int){
            progressBar.max = 100
            progressBar.progress = value
        }
        fun generateCode(board: List<List<Int>>,pattern: String) {
            lpv.setGridPoints(board)
            lpv.setSize(board.size, board[0].size)
            lpv.setPattern(pattern)
            lpv.render()
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.code_list_item_fragment, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = dataSet[position]
        val boardElement = Json.parseToJsonElement(data.grid.board) as JsonArray
        val board = Json.decodeFromJsonElement<List<List<Int>>>(boardElement)
        viewHolder.setSize(board[0].size, board.size)
        viewHolder.generateCode(board,data.pattern)
        viewHolder.generateBar(data.points)

        viewHolder.btn.setOnClickListener {
            codeListItemClickInterface.onCodeListItemClicked(data)
        }
    }

    override fun getItemCount() = dataSet.size

    fun updateList(codes: List<Code>) {
        this.dataSet = codes
        notifyDataSetChanged()
    }
}

interface CodeListItemClickInterface{
    fun onCodeListItemClicked(codeListItem: Code)
}
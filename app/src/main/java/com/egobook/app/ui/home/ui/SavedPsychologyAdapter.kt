package com.egobook.app.ui.home.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.R
import com.egobook.app.ui.home.repository.SavedPsychologyDto

class SavedPsychologyAdapter(
    private val onItemClick: (SavedPsychologyDto) -> Unit
) : ListAdapter<SavedPsychologyDto, SavedPsychologyAdapter.ItemViewHolder>(ItemDiffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_holder_psychology, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onItemClick)
    }

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.tv_psychology_date)
        val contentTextView: TextView = view.findViewById(R.id.tv_psychology_content)
        val sourceTextView: TextView = view.findViewById(R.id.tv_psychology_source)
        val deleteButton: View = view.findViewById(R.id.iv_delete)

        fun bind(item: SavedPsychologyDto, onItemClick: (SavedPsychologyDto) -> Unit) {
            dateTextView.text = item.savedAt
            contentTextView.text = item.title
            sourceTextView.text = item.source
            contentTextView.text = item.preview

            deleteButton.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    companion object {
        private val ItemDiffCallback = object : DiffUtil.ItemCallback<SavedPsychologyDto>() {
            override fun areItemsTheSame(oldItem: SavedPsychologyDto, newItem: SavedPsychologyDto): Boolean {
                return oldItem.knowledgeId == newItem.knowledgeId
            }

            override fun areContentsTheSame(oldItem: SavedPsychologyDto, newItem: SavedPsychologyDto): Boolean {
                return oldItem == newItem // 전체 객체 내용 비교
            }
        }
    }
}

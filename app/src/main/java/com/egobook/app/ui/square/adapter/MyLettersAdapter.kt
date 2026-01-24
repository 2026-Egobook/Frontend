package com.egobook.app.ui.square.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.databinding.ItemSquareLetterBinding
import com.egobook.app.ui.square.model.LetterModel

class MyLettersAdapter(private val onClicked: (LetterModel) -> Unit): ListAdapter<LetterModel, MyLettersAdapter.MyLetterViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyLetterViewHolder {
        return MyLetterViewHolder(ItemSquareLetterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(
        holder: MyLetterViewHolder,
        position: Int
    ) {
        return holder.bind(getItem(position))
    }

    inner class MyLetterViewHolder(private val binding: ItemSquareLetterBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LetterModel) = with(binding) {
            tvItemSquareLetterDatetime.text = item.dateTime
            root.setOnClickListener {
                onClicked(item)
            }
        }
    }

    companion object {
        val diffUtil = object: DiffUtil.ItemCallback<LetterModel>() {
            override fun areItemsTheSame(
                oldItem: LetterModel,
                newItem: LetterModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: LetterModel,
                newItem: LetterModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}
package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toDrawable
import com.egobook.app.R
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.egobook.app.databinding.DialogDeleteFriendBinding
import com.egobook.app.ui.square.model.FriendModel
import com.egobook.app.ui.square.viewmodel.SquareViewModel

class FriendDeleteDialog(private val deleteItem: FriendModel): DialogFragment(R.layout.dialog_delete_friend) {

    private lateinit var binding: DialogDeleteFriendBinding
    private val viewModel: SquareViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogDeleteFriendBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        tvDeleteFriendTitle.text = "${deleteItem.name}님을\n삭제할까요?"
    }

    private fun initListeners() = with(binding) {
        btnDeleteFriendBack.setOnClickListener { dismiss() }
        btnDeleteFriendConfirm.setOnClickListener {
            viewModel.deleteFriend(deleteId = deleteItem.id)
        }
    }

    companion object {
        val TAG = "FriendDeleteDialog"
    }
}
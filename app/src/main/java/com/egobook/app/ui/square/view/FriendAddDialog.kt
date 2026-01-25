package com.egobook.app.ui.square.view

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import com.egobook.app.R
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.egobook.app.databinding.DialogSquareAddFriendBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.model.SearchUserModel
import com.egobook.app.ui.square.viewmodel.FriendsViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class FriendAddDialog: DialogFragment(R.layout.dialog_square_add_friend) {

    private lateinit var binding: DialogSquareAddFriendBinding
    private val viewModel: FriendsViewModel by activityViewModels()
    private var receiverId: Long? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSquareAddFriendBinding.bind(view)
        initListeners()
        initObservers()
    }

    private fun initListeners() = with(binding) {
        ivAddFriendBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
        tvAddFriendSearch.setOnClickListener {
            val keyword = etAddFriendUserKeyword.text.toString()
            viewModel.searchUser(keyword = keyword)
        }
        btnAddFriendSearchResultApply.setOnClickListener {
            viewModel.requestFriendship(receiverId = receiverId ?: -1L)
        }
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.searchUserResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<SearchUserModel?> -> {
                                val searchUserResult = state.data
                                if(searchUserResult == null) {
                                    clAddFriendSearchResultFound.visibility = View.GONE
                                    tvAddFriendSearchResultEmpty.visibility = View.VISIBLE
                                } else {
                                    receiverId = searchUserResult.userId
                                    tvAddFriendSearchResultEmpty.visibility = View.GONE
                                    clAddFriendSearchResultFound.visibility = View.VISIBLE
                                    tvAddFriendSearchName.text = searchUserResult.nickname
                                    tvAddFriendSearchLevel.text = "LV ${searchUserResult.level}"
                                    Glide.with(ivAddFriendSearchLevelImage).load(searchUserResult.profileImageUrl).into(ivAddFriendSearchLevelImage)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.requestFriendshipResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Unit> -> {
                                Toast.makeText(context, "친구 신청이 완료되었습니다", Toast.LENGTH_SHORT).show()
                                btnAddFriendSearchResultApply.isEnabled = false
                                btnAddFriendSearchResultApply.alpha = 0.5f
                                btnAddFriendSearchResultApply.text = "신청완료"
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        val TAG = "FriendAddDialog"
    }
}
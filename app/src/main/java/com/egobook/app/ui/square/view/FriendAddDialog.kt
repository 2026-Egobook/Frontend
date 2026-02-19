package com.egobook.app.ui.square.view

import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.content.ClipboardManager
import android.view.inputmethod.InputMethodManager
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.DialogSquareAddFriendBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.adapter.FriendSearchAdapter
import com.egobook.app.ui.square.model.friend.SearchUserModel
import com.egobook.app.ui.square.viewmodel.FriendsViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch

class FriendAddDialog: DialogFragment(R.layout.dialog_square_add_friend) {

    private lateinit var binding: DialogSquareAddFriendBinding
    private val viewModel: FriendsViewModel by activityViewModels()

    private var lastAppliedPosition: Int? = null
    private val adapter by lazy {
        FriendSearchAdapter { userId, position ->
            lastAppliedPosition = position
            viewModel.requestFriendship(receiverId = userId)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext()).apply {
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DialogSquareAddFriendBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getUserId()
    }

    private fun initViews() = with(binding) {
        rvAddFriendSearchResultFound.adapter = adapter
    }

    private fun initListeners() = with(binding) {
        ivAddFriendBack.setOnClickListener {
            removeScreenBlur()
            dismiss()
        }
        tvAddFriendSearch.setOnClickListener {
            val keyword = etAddFriendUserKeyword.text.toString()
            if(keyword.isEmpty()) {
                Toast.makeText(context, "ID를 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else if(keyword == tvAddFriendAccountId.text.toString()) {
                Toast.makeText(context, "자신의 ID는 검색할 수 없습니다!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.searchUserWithoutRequest(keyword = keyword)
            }
        }
        btnAddFriendCopyId.setOnClickListener {
            val copyId = tvAddFriendAccountId.text.toString()
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User ID", copyId)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "계정 ID가 복사되었습니다.", Toast.LENGTH_SHORT).show()
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
                            is UiState.Success<List<SearchUserModel>> -> {
                                val searchUserList = state.data
                                if(searchUserList.isEmpty()) {
                                    Toast.makeText(context, "검색 결과가 존재하지 않습니다!", Toast.LENGTH_SHORT).show()
                                    rvAddFriendSearchResultFound.isVisible = false
                                    tvAddFriendSearchResultEmptyMain.isVisible = true
                                    tvAddFriendSearchResultEmptySub.isVisible = true
                                } else {
                                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.hideSoftInputFromWindow(etAddFriendUserKeyword.windowToken, 0) // flags = 0 → "조건 따지지 않고 무조건 키보드를 내린다"
                                    adapter.submitList(searchUserList.toMutableList())
                                    tvAddFriendSearchResultEmptyMain.isVisible = false
                                    tvAddFriendSearchResultEmptySub.isVisible = false
                                    rvAddFriendSearchResultFound.isVisible = true
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
                                lastAppliedPosition?.let {
                                    val viewHolder = rvAddFriendSearchResultFound.findViewHolderForAdapterPosition(it) as? FriendSearchAdapter.FriendSearchViewHolder
                                    viewHolder?.binding?.apply {
                                        btnAddFriendSearchResultApply.isEnabled = false
                                        btnAddFriendSearchResultApply.alpha = 0.5f
                                        btnAddFriendSearchResultApply.text = "신청완료"
                                    }
                                }
                                lastAppliedPosition = null
                            }
                        }
                    }
                }
                launch {
                    viewModel.userId.collect { state ->
                        when (state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<String> -> {
                                val userId = state.data
                                tvAddFriendAccountId.text = userId
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
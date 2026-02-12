package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentFriendsListBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.square.adapter.FriendsListAdapter
import com.egobook.app.ui.square.model.friend.FriendListModel
import com.egobook.app.ui.square.viewmodel.FriendsViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch
import kotlin.getValue

class FriendsListFragment : Fragment(R.layout.fragment_friends_list) {
    private lateinit var binding: FragmentFriendsListBinding
    private val viewModel: FriendsViewModel by activityViewModels()

    private lateinit var dialog: FriendDeleteDialog
    private val adapter = FriendsListAdapter { deleteItem ->
        applyScreenBlur(BlurLevel.BASE)
        dialog = FriendDeleteDialog(deleteItem = deleteItem)
        dialog.show(childFragmentManager, FriendDeleteDialog.TAG)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFriendsListBinding.bind(view)
        fetchData()
        initViews()
        initObservers()
    }

    private fun fetchData() {
        viewModel.fetchFriendList()
    }

    private fun initViews() = with(binding) {
        rvFriendsList.adapter = adapter
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.friendList.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<FriendListModel> -> {
                                val friendList = state.data
                                tvFriendsValue.text = "${friendList.count}/${MAX_FRIEND_COUNT}명"
                                if(friendList.count == 0) {
                                    tvFriendsListPlaceholder.isVisible = true
                                    rvFriendsList.isVisible = false
                                } else {
                                    tvFriendsListPlaceholder.isVisible = false
                                    rvFriendsList.isVisible = true
                                    adapter.submitList(friendList.friends)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.deleteFriendStatus.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Long> -> {
                                Toast.makeText(context, "친구를 삭제했습니다.", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                                removeScreenBlur()
                                val deleteId = state.data
                                val updateList = adapter.currentList.filter { it.id != deleteId }
                                adapter.submitList(updateList.toList())
                                tvFriendsValue.text = "${updateList.size}/${MAX_FRIEND_COUNT}명"
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MAX_FRIEND_COUNT = 10
    }
}
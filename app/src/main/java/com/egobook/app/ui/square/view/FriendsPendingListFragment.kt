package com.egobook.app.ui.square.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.FragmentFriendsPendingListBinding
import com.egobook.app.databinding.ItemSquareFriendPendingReceivedListBinding
import com.egobook.app.databinding.ItemSquareFriendPendingSentListBinding
import com.egobook.app.ui.square.model.friend.FriendRequestModel
import com.egobook.app.ui.square.viewmodel.FriendsViewModel
import com.egobook.app.util.UiState
import kotlinx.coroutines.launch
import kotlin.getValue

class FriendsPendingListFragment : Fragment(R.layout.fragment_friends_pending_list) {
    private lateinit var binding: FragmentFriendsPendingListBinding
    private val viewModel: FriendsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFriendsPendingListBinding.bind(view)
        fetchData()
        initObservers()
    }

    private fun fetchData() {
        viewModel.fetchIncomingFriendRequestList()
        viewModel.fetchOutgoingFriendRequestList()
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.incomingFriendRequestList.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<List<FriendRequestModel>> -> {
                                val friendRequestList = state.data
                                llFriendsPendingListReceived.removeAllViews() // 기존에 추가되어 있던 뷰들 모두 제거
                                friendRequestList.forEach { friendRequest ->
                                    val itemView = layoutInflater.inflate(
                                        R.layout.item_square_friend_pending_received_list,
                                        llFriendsPendingListReceived,
                                        false
                                    ).apply {
                                        tag = friendRequest.requestId
                                    }
                                    val itemBinding = ItemSquareFriendPendingReceivedListBinding.bind(itemView)
                                    itemBinding.tvItemFriendPendingListName.text = friendRequest.nickname
                                    itemBinding.btnItemSquareFriendPendingListDeny.setOnClickListener {
                                        viewModel.rejectFriendRequest(requestId = friendRequest.requestId)
                                    }
                                    itemBinding.btnItemSquareFriendPendingListAccept.setOnClickListener {
                                        viewModel.acceptFriendRequest(requestId = friendRequest.requestId)
                                    }
                                    llFriendsPendingListReceived.addView(itemView)
                                }
                                tvFriendsPendingListReceivedNum.text = friendRequestList.size.toString()
                            }
                        }
                    }
                }
                launch {
                    viewModel.outgoingFriendRequestList.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<List<FriendRequestModel>> -> {
                                val friendRequestList = state.data
                                llFriendsPendingListSent.removeAllViews() // 기존에 추가되어 있던 뷰들 모두 제거
                                friendRequestList.forEach { friendRequest ->
                                    val itemView = layoutInflater.inflate(
                                        R.layout.item_square_friend_pending_sent_list,
                                        llFriendsPendingListSent,
                                        false
                                    ).apply {
                                        tag = friendRequest.requestId
                                    }
                                    val itemBinding = ItemSquareFriendPendingSentListBinding.bind(itemView)
                                    itemBinding.tvItemFriendPendingSentListName.text = friendRequest.nickname
                                    itemBinding.btnItemSquareFriendPendingSentListCancel.setOnClickListener {
                                        viewModel.cancelFriendRequest(requestId = friendRequest.requestId)
                                    }
                                    llFriendsPendingListSent.addView(itemView)
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.rejectFriendRequestResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Long> -> {
//                                viewModel.fetchIncomingFriendRequestList() 이 방법은 어떨까? (실제 연결 시 고려해보기)
                                Toast.makeText(context, "요청된 친구 신청이 거절되었습니다.", Toast.LENGTH_SHORT).show()
                                val requestId = state.data
                                val viewToRemove = llFriendsPendingListReceived.findViewWithTag<View>(requestId)
                                llFriendsPendingListReceived.removeView(viewToRemove)
                                tvFriendsPendingListReceivedNum.text = (tvFriendsPendingListReceivedNum.text.toString().toInt() - 1).toString()
                            }
                        }
                    }
                }
                launch {
                    viewModel.acceptFriendRequestResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Long> -> {
//                                viewModel.fetchIncomingFriendRequestList() 이 방법은 어떨까? (실제 연결 시 고려해보기)
                                Toast.makeText(context, "요청된 친구 신청이 수락되었습니다.", Toast.LENGTH_SHORT).show()
                                val requestId = state.data
                                val viewToRemove = llFriendsPendingListReceived.findViewWithTag<View>(requestId)
                                llFriendsPendingListReceived.removeView(viewToRemove)
                                tvFriendsPendingListReceivedNum.text = (tvFriendsPendingListReceivedNum.text.toString().toInt() - 1).toString()
                            }
                        }
                    }
                }
                launch {
                    viewModel.cancelFriendRequestResult.collect { state ->
                        when(state) {
                            is UiState.Failure -> {}
                            UiState.Idle -> {}
                            UiState.Loading -> {}
                            is UiState.Success<Long> -> {
//                                viewModel.fetchOutgoingFriendRequestList()
                                Toast.makeText(context, "요청된 친구 신청이 취소되었습니다.", Toast.LENGTH_SHORT).show()
                                val requestId = state.data
                                val viewToRemove = llFriendsPendingListSent.findViewWithTag<View>(requestId)
                                llFriendsPendingListSent.removeView(viewToRemove)
                            }
                        }
                    }
                }
            }
        }
    }
}
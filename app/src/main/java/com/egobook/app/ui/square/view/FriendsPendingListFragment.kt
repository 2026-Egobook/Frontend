package com.egobook.app.ui.square.view

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.MetricAffectingSpan
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.egobook.app.R
import com.egobook.app.databinding.FragmentFriendsPendingListBinding
import com.egobook.app.databinding.ItemSquareFriendPendingReceivedListBinding
import com.egobook.app.databinding.ItemSquareFriendPendingSentListBinding
import com.egobook.app.ui.square.model.friend.FriendRequestModel
import com.egobook.app.ui.square.adapter.loadProfileBackground
import com.egobook.app.ui.square.adapter.loadProfileTurtle
import com.egobook.app.ui.square.levelBadgeDrawable
import com.egobook.app.ui.square.ProfileImagePlacement
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
        initViews()
        fetchData()
        initObservers()
    }

    private fun initViews() = with(binding) {
        changePlaceholderTextStyle(text = "받은 친구 신청이 없어요\n신청이 도착하면 바로 알려드릴게요", view = tvFriendsPendingListReceivedPlaceholder)
        changePlaceholderTextStyle(text = "보낸 친구 신청이 없어요\n친구의 ID를 입력해 신청을 보내보세요", view = tvFriendsPendingListSentPlaceholder)
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
                                tvFriendsPendingListReceivedNum.text = friendRequestList.size.toString()
                                if(friendRequestList.isEmpty()) {
                                    llFriendsPendingListReceived.removeAllViews()
                                    tvFriendsPendingListReceivedPlaceholder.isVisible = true
                                } else {
                                    tvFriendsPendingListReceivedPlaceholder.isVisible = false
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
                                        itemBinding.tvItemFriendPendingListLevel.text = "LV ${friendRequest.level}"
                                        itemBinding.ivItemFriendPendingListLevel.setImageResource(levelBadgeDrawable(friendRequest.level))
                                        itemBinding.ivItemFriendPendingListBackground.loadProfileBackground(friendRequest.backgroundImageUrl, ProfileImagePlacement.FRIEND_BACKGROUND)
                                        itemBinding.ivItemFriendPendingListImage.loadProfileTurtle(friendRequest.turtleImageUrl, R.drawable.default_turtle, ProfileImagePlacement.FRIEND_TURTLE)
                                        itemBinding.btnItemSquareFriendPendingListDeny.setOnClickListener {
                                            viewModel.rejectFriendRequest(requestId = friendRequest.requestId)
                                        }
                                        itemBinding.btnItemSquareFriendPendingListAccept.setOnClickListener {
                                            viewModel.acceptFriendRequest(requestId = friendRequest.requestId)
                                        }
                                        llFriendsPendingListReceived.addView(itemView)
                                    }
                                }
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
                                if(friendRequestList.isEmpty()) {
                                    llFriendsPendingListSent.removeAllViews()
                                    tvFriendsPendingListSentPlaceholder.isVisible = true
                                } else {
                                    tvFriendsPendingListSentPlaceholder.isVisible = false
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
                                        itemBinding.tvItemFriendPendingSentListLevel.text = "LV ${friendRequest.level}"
                                        itemBinding.ivItemFriendPendingSentListLevel.setImageResource(levelBadgeDrawable(friendRequest.level))
                                        itemBinding.ivItemFriendPendingSentListBackground.loadProfileBackground(friendRequest.backgroundImageUrl, ProfileImagePlacement.FRIEND_BACKGROUND)
                                        itemBinding.ivItemFriendPendingSentListImage.loadProfileTurtle(friendRequest.turtleImageUrl, R.drawable.default_turtle, ProfileImagePlacement.FRIEND_TURTLE)
                                        itemBinding.btnItemSquareFriendPendingSentListCancel.setOnClickListener {
                                            viewModel.cancelFriendRequest(requestId = friendRequest.requestId)
                                        }
                                        llFriendsPendingListSent.addView(itemView)
                                    }
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
                                viewModel.fetchFriendList()
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

    private fun changePlaceholderTextStyle(text: String, view: TextView) {
        val spannable = SpannableStringBuilder(text)
        val newLineIndex = text.indexOf("\n")
        val start = newLineIndex + 1
        val end = text.length
        val sizeInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics).toInt()
        spannable.setSpan(
            AbsoluteSizeSpan(sizeInPx),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        val font = ResourcesCompat.getFont(requireContext(), R.font.arita_medium)
        font?.let { typeface ->
            spannable.setSpan(
                object : MetricAffectingSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.typeface = typeface
                    }
                    override fun updateMeasureState(paint: TextPaint) {
                        paint.typeface = typeface
                    }
                },
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        view.text = spannable
    }
}

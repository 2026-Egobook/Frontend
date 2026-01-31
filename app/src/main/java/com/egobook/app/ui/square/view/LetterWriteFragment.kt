package com.egobook.app.ui.square.view

import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.databinding.FragmentLetterWriteBinding
import com.egobook.app.databinding.LayoutPopupFriendListBinding
import com.egobook.app.ui.square.adapter.FriendPopupListAdapter
import com.egobook.app.ui.square.model.friend.FriendModel
import com.egobook.app.ui.square.model.letter.LetterSendType
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import com.egobook.app.util.UiState
import com.google.android.material.card.MaterialCardView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LetterWriteFragment : Fragment(R.layout.fragment_letter_write) {
    private lateinit var binding: FragmentLetterWriteBinding
    private val letterColorList by lazy {
        listOf(binding.cvLetterColorBeige, binding.cvLetterColorPink, binding.cvLetterColorLeaf, binding.cvLetterColorMint, binding.cvLetterColorLavender)
    }

    private val viewModel: LetterViewModel by activityViewModels()

    private lateinit var friendList: List<FriendModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLetterWriteBinding.bind(view)
        fetchData()
        initViews()
        initListeners()
        initObservers()
    }

    private fun fetchData() {
        viewModel.getFriendList()
    }

    private fun initViews() = with(binding) {
        cvLetterColorBeige.isSelected = true
        cvLetterColorBeige.getChildAt(0).isVisible = true
    }

    private fun initListeners() = with(binding) {
        ivLetterWriteBack.setOnClickListener { findNavController().popBackStack() }
        letterColorList.forEach { letterColor ->
            letterColor.setOnClickListener { clickedView ->
                letterColorList.forEach { card ->
                    card.isSelected = false
                    card.getChildAt(0).isVisible = false
                }
                clickedView.isSelected = true
                (clickedView as MaterialCardView).getChildAt(0).isVisible = true
                when(clickedView.id) {
                    R.id.cv_letter_color_beige -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_beige, null)}
                    R.id.cv_letter_color_pink -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_pink, null)}
                    R.id.cv_letter_color_leaf -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_leaf, null)}
                    R.id.cv_letter_color_mint -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_mint, null)}
                    R.id.cv_letter_color_lavender -> { cvLetterContainer.backgroundTintList = resources.getColorStateList(R.color.letter_bg_lavender, null)}
                }
            }
        }
        etLetterWriteContent.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) = Unit
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) = Unit
            override fun onTextChanged(
                text: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                btnLetterSendAnonymous.isEnabled = !text.isNullOrBlank()
                btnLetterSendFriend.isEnabled = !text.isNullOrBlank()
                tvLetterWriteContentLength.text = "${text?.length}/$MAX_LENGTH"
            }
        })
        btnLetterSendFriend.setOnClickListener {
            showFriendListPopup(anchorView = btnLetterSendFriend)
        }
        btnLetterSendAnonymous.setOnClickListener {
            applyScreenBlur(BlurLevel.BASE)
            val dialog = LetterSendDialog(LetterSendType.RANDOM).apply {
                isCancelable = false
            }
            dialog.show(childFragmentManager, LetterSendDialog.TAG)
        }
    }

    /**
     * 1. 첫 번째 인자 width = 0 : DividerItemDecoration이 세로 방향(VERTICAL)일 때는 구분선의 너비를 부모의 너비에 맞게 자동으로 늘리므로, 0으로 두어도 괜찮다.
     * 두 번재 인자 height : 구분선의 두께가 된다.
     * 안드로이드의 dp 단위를 pixel 단위로 변환하는 공식이다. (1dp를 기기의 해상도에 맞는 실제 픽셀값으로 계산)
     * 어떤 해상도의 기기에서든 똑같이 1dp 두께로 보이게끔, 그에 맞는 픽셀로 변환하라는 의미이다.
     * 1 = dp 단위의 값, resources.displayMetrics.density = 현재 기기의 화면 밀도 계수 (기기마다 다름)
     * 1 * resources.displayMetrics.density = 기기마다 1dp 에 대응하는 픽셀값
     * toInt()를 붙이는 이유는 픽셀은 정수 단위여야 하기 때문이다.
     * 2. onDraw는 아이템의 배경색이 구분선을 덮어버리는 문제가 발생할 수 있기에, 아이템보다 위쪽에 구분선을 그리도록 하기 위해 onDrawOver을 쓰는 것을 권장한다.
     * 3. dividerDrawable의 intrinsicHeight(고유 높이)는 setSize에 설정한 두 번째 인자(height)와 동일한 값이다.
     */
    private fun showFriendListPopup(anchorView: View) = with(binding) {
        val popupBinding = LayoutPopupFriendListBinding.inflate(layoutInflater)
        val popupHeight = (200*resources.displayMetrics.density).toInt()
        val popupWindow = PopupWindow(
            popupBinding.root, btnLetterSendFriend.width, popupHeight, true
        )
        with(popupBinding.rvPopupFriendList) {
            adapter = FriendPopupListAdapter { friendInfo ->
                tvLetterWriteReceiver.text = "To ${friendInfo.name}"
                tvLetterWriteSender.text = "From 로그인한 유저" // TODO: 나중에 유저 정보 받아오기
                popupWindow.dismiss()
            }
            val dividerDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                color = resources.getColorStateList(R.color.layer_white, null)
                setSize(0, (1*resources.displayMetrics.density).toInt()) // 1
            }
            addItemDecoration(object: RecyclerView.ItemDecoration() {
                override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) { // 2
                    val left = parent.paddingLeft
                    val right = parent.width - parent.paddingRight
                    for(i in 0 until parent.childCount - 1) {
                        val child = parent.getChildAt(i)
                        val top = child.bottom
                        val bottom = top + dividerDrawable.intrinsicHeight // 3
                        dividerDrawable.setBounds(left, top, right, bottom)
                        dividerDrawable.draw(c)
                    }
                }
            })
        }
        (popupBinding.rvPopupFriendList.adapter as FriendPopupListAdapter).submitList(friendList)
        popupWindow.showAsDropDown(
            anchorView,
            0,
            -(anchorView.height + popupHeight + 40)
        )
    }

    private fun initObservers() = with(binding) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.friendList.collect { state ->
                    when(state) {
                        is UiState.Failure -> {}
                        UiState.Idle -> {}
                        UiState.Loading -> {}
                        is UiState.Success<List<FriendModel>> -> {
                            val friendList = state.data
                            if(friendList.isEmpty()) {
                                btnLetterSendFriend.isVisible = false
                                val params = btnLetterSendAnonymous.layoutParams as ConstraintLayout.LayoutParams
                                params.startToEnd = ConstraintLayout.LayoutParams.UNSET
                                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                                params.topToTop = ConstraintLayout.LayoutParams.UNSET
                                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                                params.marginStart = (16 * resources.displayMetrics.density).toInt()
                                params.topToBottom = cvLetterContainer.id
                                params.topMargin = (28 * resources.displayMetrics.density).toInt()
                                btnLetterSendAnonymous.layoutParams = params
                            } else {
                                this@LetterWriteFragment.friendList = friendList
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val MAX_LENGTH = 360
    }
}
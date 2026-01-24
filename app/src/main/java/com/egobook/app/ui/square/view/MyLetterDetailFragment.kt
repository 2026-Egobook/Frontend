package com.egobook.app.ui.square.view

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.egobook.app.R
import com.egobook.app.databinding.FragmentMyLetterDetailBinding

class MyLetterDetailFragment : Fragment(R.layout.fragment_my_letter_detail) {
    private lateinit var binding: FragmentMyLetterDetailBinding
    private val args: MyLetterDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMyLetterDetailBinding.bind(view)
        initViews()
        initListeners()
    }

    private fun initViews() = with(binding) {
        val item = args.letterItem
        tvMyLetterDetailDatetime.text = item.dateTime
        tvMyLetterDetailSentContent.text = item.sentContent
        tvMyLetterDetailReceiver.text = "To. ${item.receivedContent.receiverNickname}"
        tvMyLetterDetailReceivedContent.text = item.receivedContent.letterContent
        tvMyLetterDetailSender.text = "To. ${item.receivedContent.senderNickname}"
    }

    private fun initListeners() = with(binding) {
        ivMyLetterDetailBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
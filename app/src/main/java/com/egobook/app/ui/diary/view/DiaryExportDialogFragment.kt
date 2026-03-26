package com.egobook.app.ui.diary.view

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.PermissionChecker
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.egobook.app.R
import com.egobook.app.databinding.FragmentDiaryExportDialogBinding
import com.egobook.app.removeScreenBlur
import com.egobook.app.ui.diary.model.DiaryExportUiForm
import com.egobook.app.ui.diary.viewmodel.DiariesViewModel
import com.egobook.app.ui.diary.viewmodel.ExportEvent
import com.egobook.app.ui.diary.viewmodel.TermType
import kotlinx.coroutines.launch

class DiaryExportDialogFragment : DialogFragment() {

    private var _binding: FragmentDiaryExportDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var downloadManager: DownloadManager
    private var downloadId: Long = 0

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireContext(), "권한 허용됨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "권한 거부됨", Toast.LENGTH_SHORT).show()
            }
        }
    private val viewmodel: DiariesViewModel by activityViewModels()

    // 다운로드 완료 상태를 감지하는 브로드캐스트 리시버. 다운로드 완료 시 유저에 완료 알림 표시
    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadId == id) {
                Toast.makeText(requireContext(), "다운로드 완료!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentDiaryExportDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDateInputWatchers()
        setupObservers()
        setClickListener()
        updateButtonState()
        setupDownloadManager()
        observeDownloadEvent()
        observeErrorMessage()
    }

    private fun observeErrorMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.errorMessage.collect { message ->
                setGuide("내보낼 수 있는 감정 일기가 없어요", R.color.critical)
            }
        }
    }

    private fun observeDownloadEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.downloadUrl.collect { url ->
                downloadFile(url)
            }
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewmodel.isValidDate.collect { state ->
                when (state) {
                    TermType.InvalidBothDate -> {
                        setStartDateColor(R.color.critical)
                        setEndDateColor(R.color.critical)
                        setButtonsEnabled(false)
                        setGuide("유효하지 않은 날짜입니다.\n다시 입력해주세요", R.color.critical)
                    }
                    TermType.InvalidStartDate -> {
                        setStartDateColor(R.color.critical)
                        setEndDateColor(R.color.neutral)
                        setButtonsEnabled(false)
                        setGuide("유효하지 않은 날짜입니다.\n다시 입력해주세요", R.color.critical)
                    }
                    TermType.InvalidEndDate -> {
                        setStartDateColor(R.color.neutral)
                        setEndDateColor(R.color.critical)
                        setButtonsEnabled(false)
                        setGuide("유효하지 않은 날짜입니다.\n다시 입력해주세요", R.color.critical)
                    }
                    TermType.StartFuture -> {
                        setStartDateColor(R.color.critical)
                        setEndDateColor(R.color.neutral)
                        setGuide("미래 날짜는 내보낼 수 없어요", R.color.critical)
                    }
                    TermType.EndFuture -> {
                        setStartDateColor(R.color.neutral)
                        setEndDateColor(R.color.critical)
                        setGuide("미래 날짜는 내보낼 수 없어요", R.color.critical)
                    }
                    TermType.Reverse -> {
                        setStartDateColor(R.color.critical)
                        setButtonsEnabled(false)
                        setGuide("시작 날짜가 끝 날짜보다 이전이거나\n같아야 해요", R.color.critical)
                    }
                    TermType.MoreThanOneYear -> {
                        setStartDateColor(R.color.critical)
                        setEndDateColor(R.color.critical)
                        setGuide("최대 1년 단위로 끊어서 내보낼 수 있어요", R.color.critical)
                    }
                    TermType.Valid -> {
                        setStartDateColor(R.color.neutral)
                        setEndDateColor(R.color.neutral)
                        setButtonsEnabled(true)
                        setGuide("최대 1년 단위로 내보낼 수 있어요", R.color.neutral)
                    }
                    TermType.None -> {
                        setStartDateColor(R.color.neutral)
                        setEndDateColor(R.color.neutral)
                        setButtonsEnabled(false)
                        setGuide("최대 1년 단위로 내보낼 수 있어요", R.color.neutral)
                    }
                }
            }
        }
    }

    private fun setupDownloadManager() {
        downloadManager = requireContext()
            .getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        ContextCompat.registerReceiver(
            requireContext(),
            downloadReceiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun downloadFile(url: String) {
        when {
            // 안드로이드 10(api 29+) 이상에선 scoped storage를 통해 다운로드한 파일 처리
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // 다운로드 매니저는 api 29+에서 별도 권한 없이 Downloads 폴더에 파일을 저장할 수 있음
                startDownload(url)
            }
            // 안드로이드 9(api 28) 이하에선 권한 허용 여부에 따라 바로 파일을 다운로드하거나 권한 먼저 요청
            else -> {
                if (checkStoragePermission()) {
                    startDownload(url)
                } else {
                    requestStoragePermission()
                }
            }
        }
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() =
        requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private fun startDownload(url: String) {
        val request = DownloadManager.Request(url.toUri())
            .setTitle("일기 다운로드")
            .setDescription("일기를 다운로드 중입니다...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            URLUtil
                .guessFileName(url, null, null)
        )
        downloadId = downloadManager.enqueue(request)
        Toast.makeText(requireContext(), "다운로드를 시작합니다...", Toast.LENGTH_SHORT).show()
    }

    private fun setStartDateColor(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(requireContext(), colorRes)
        binding.tvStartDate.setTextColor(color)
        binding.icStartDate.setColorFilter(color)
    }

    private fun setEndDateColor(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(requireContext(), colorRes)
        binding.tvLastDate.setTextColor(color)
        binding.icEndDate.setColorFilter(color)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnPdf.isEnabled = enabled
        binding.btnText.isEnabled = enabled
    }

    private fun setGuide(text: String, @ColorRes colorRes: Int) {
        binding.tvExportGuide.text = text
        binding.tvExportGuide.setTextColor(ContextCompat.getColor(requireContext(), colorRes))
    }

    private fun setClickListener() {
        binding.btnPdf.setOnClickListener {
            val form = buildExportForm(binding.btnPdf.text.toString())
            viewmodel.onExport(ExportEvent.PDFExport(form))
            //Toast.makeText(requireContext(), "내보내기 기능은 준비중입니다!", Toast.LENGTH_SHORT).show()
        }
        binding.btnText.setOnClickListener {
            val form = buildExportForm(binding.btnText.text.toString())
            viewmodel.onExport(ExportEvent.TextExport(form))
            //Toast.makeText(requireContext(), "내보내기 기능은 준비중입니다!", Toast.LENGTH_SHORT).show()
        }
    }

    //입력한 ui의 시작 일자, 끝 일자, 누른 버튼의 텍스트뷰로 DiaryExportUiForm 인스턴스를 만드는 함수
    private fun buildExportForm(format: String): DiaryExportUiForm {
        return DiaryExportUiForm(
            format = format,
            startDate = binding.tvStartDate.text.toString(),
            endDate = binding.tvLastDate.text.toString()
        )
    }

    private fun setupDateInputWatchers() {

        fun createDateWatcher(editText: EditText): TextWatcher {
            return object : TextWatcher {
                private var isFormatting = false

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting || s == null) return

                    isFormatting = true

                    val original = s.toString()
                    val cursorPosition = editText.selectionStart

                    val digits = original.replace(".", "").take(8)

                    // 커서를 digit 기준으로 변환
                    val digitsBeforeCursor = original
                        .substring(0, cursorPosition)
                        .count { it.isDigit() }

                    val formatted = StringBuilder()
                    var newCursor = 0

                    for (i in digits.indices) {
                        if (i == 4 || i == 6) {
                            formatted.append('.')
                        }
                        formatted.append(digits[i])

                        // cursor 위치 계산 (정확한 방식)
                        if (i < digitsBeforeCursor) {
                            newCursor = formatted.length
                        }
                    }

                    s.replace(0, s.length, formatted.toString())

                    try {
                        editText.setSelection(newCursor)
                    } catch (e: Exception) {
                        editText.setSelection(formatted.length)
                    }

                    isFormatting = false
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            }
        }

        // watcher 각각 따로 붙이기
        binding.tvStartDate.addTextChangedListener(createDateWatcher(binding.tvStartDate))
        binding.tvLastDate.addTextChangedListener(createDateWatcher(binding.tvLastDate))

        // validation
        binding.tvStartDate.doAfterTextChanged { updateButtonState() }
        binding.tvLastDate.doAfterTextChanged { updateButtonState() }
    }

    private fun updateButtonState() {
        val startDate = binding.tvStartDate.text.toString()
        val lastDate = binding.tvLastDate.text.toString()
        
        // 뷰모델에서 날짜 선후 관계 및 형식 검사 수행
        viewmodel.validateDates(startDate, lastDate)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        removeScreenBlur()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(downloadReceiver)
        _binding = null
    }
}

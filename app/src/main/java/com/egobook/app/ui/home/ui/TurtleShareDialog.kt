package com.egobook.app.ui.home.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.egobook.app.R
import com.egobook.app.data.share.TurtleShareStorage
import com.egobook.app.databinding.DialogTurtleShareBinding
import com.egobook.app.removeScreenBlur
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TurtleShareDialog : DialogFragment(R.layout.dialog_turtle_share) {
    @Inject lateinit var storage: TurtleShareStorage
    private var binding: DialogTurtleShareBinding? = null
    private var saveJob: Job? = null
    private val fileName get() = requireArguments().getString("file") ?: error("이미지가 없습니다.")
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (binding != null) {
            if (granted) saveImage() else toast(R.string.turtle_save_permission)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val ui = DialogTurtleShareBinding.bind(view)
        binding = ui
        val file = runCatching { storage.previewFile(fileName) }.getOrElse {
            toast(R.string.turtle_share_expired); dismiss(); return
        }
        val metrics = resources.displayMetrics
        ui.ivTurtleSharePreview.maxHeight = minOf(metrics.heightPixels * 0.58f,
            metrics.heightPixels - 240 * metrics.density).coerceAtLeast(80 * metrics.density).toInt()
        ui.btnTurtleShare.isEnabled = false
        ui.btnTurtleSave.isEnabled = false
        ui.ivTurtleSharePreview.load(file) {
            listener(
                onSuccess = { _, _ ->
                    if (binding === ui) {
                        ui.btnTurtleShare.isEnabled = true
                        ui.btnTurtleSave.isEnabled = true
                    }
                },
                onError = { _, _ -> if (binding === ui) toast(R.string.turtle_share_failed) }
            )
        }
        ui.btnTurtleShareClose.setOnClickListener { dismiss() }
        ui.btnTurtleShare.setOnClickListener {
            try {
                val uri = storage.shareUri(fileName)
                val send = Intent(Intent.ACTION_SEND).apply {
                    type = "image/png"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    clipData = ClipData.newUri(requireContext().contentResolver, getString(R.string.turtle_share_title), uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(Intent.createChooser(send, getString(R.string.turtle_share)))
            } catch (_: ActivityNotFoundException) { toast(R.string.turtle_share_no_app) }
            catch (_: Exception) { toast(R.string.turtle_share_failed) }
        }
        ui.btnTurtleSave.setOnClickListener {
            if (Build.VERSION.SDK_INT < 29 && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else saveImage()
        }
    }

    override fun onStart() {
        super.onStart()
        val density = resources.displayMetrics.density
        dialog?.window?.setLayout(minOf((360 * density).toInt(), resources.displayMetrics.widthPixels - (32 * density).toInt()), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun saveImage() {
        if (saveJob?.isActive == true) return
        saveJob = viewLifecycleOwner.lifecycleScope.launch {
            binding?.btnTurtleSave?.isEnabled = false
            try { storage.saveToGallery(fileName); toast(R.string.turtle_saved) }
            catch (error: CancellationException) { throw error }
            catch (_: Exception) { toast(R.string.turtle_save_failed) }
            finally { binding?.btnTurtleSave?.isEnabled = true }
        }
    }

    private fun toast(message: Int) { context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() } }
    override fun onDestroyView() { binding = null; super.onDestroyView() }
    override fun onDismiss(dialog: DialogInterface) { super.onDismiss(dialog); removeScreenBlur() }

    companion object {
        const val TAG = "TurtleShareDialog"
        fun newInstance(file: String) = TurtleShareDialog().apply { arguments = bundleOf("file" to file) }
    }
}

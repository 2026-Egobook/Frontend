package com.egobook.app.ui.square.view

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.egobook.app.BlurLevel
import com.egobook.app.R
import com.egobook.app.applyScreenBlur
import com.egobook.app.ui.square.viewmodel.LetterViewModel
import kotlinx.coroutines.launch

internal fun Fragment.showLetterRestriction(reason: String?, until: String?) {
    if (!isAdded) return
    val manager = if (this is androidx.fragment.app.DialogFragment) parentFragmentManager else childFragmentManager
    if (manager.isStateSaved || manager.findFragmentByTag(LetterRestrictionDialog.TAG) != null) return
    LetterRestrictionDialog.newInstance(reason, until).show(manager, LetterRestrictionDialog.TAG)
    applyScreenBlur(BlurLevel.BASE)
}

internal fun Fragment.withLetterAccess(viewModel: LetterViewModel, onDenied: () -> Unit = {}, onAllowed: () -> Unit) {
    val root = view ?: return
    if (root.getTag(R.id.letter_access_request) == true) return
    root.setTag(R.id.letter_access_request, true)
    viewLifecycleOwner.lifecycleScope.launch {
        try {
            val result = viewModel.refreshLetterAccess()
            if (!isAdded || parentFragmentManager.isStateSaved ||
                !viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) return@launch
            result.onSuccess { access ->
                if (access.restricted) {
                    onDenied()
                    showLetterRestriction(access.reason, access.restrictedUntil)
                } else onAllowed()
            }.onFailure {
                onDenied()
                Toast.makeText(context, R.string.letter_restriction_load_failed, Toast.LENGTH_SHORT).show()
            }
        } finally {
            root.setTag(R.id.letter_access_request, null)
        }
    }
}

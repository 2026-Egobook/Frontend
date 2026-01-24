package com.egobook.app

import androidx.fragment.app.Fragment

interface BlurController {
    fun activateBlur(blurLevel: BlurLevel)

    fun deactivateBlur()
}

fun Fragment.applyScreenBlur(blurLevel: BlurLevel) {
    val blurController = checkNotNull(activity as? BlurController) {
        "액티비티(${activity?.javaClass})가 BlurController를 구현하지 않았습니다"
    }
    blurController.activateBlur(blurLevel)
}

fun Fragment.removeScreenBlur() {
    val blurController = checkNotNull(activity as? BlurController) {
        "액티비티(${activity?.javaClass})가 BlurController를 구현하지 않았습니다"
    }
    blurController.deactivateBlur()
}

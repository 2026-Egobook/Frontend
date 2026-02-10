package com.egobook.app.ui.util

import android.util.Base64
import org.json.JSONObject

object GoogleTokenUtils {

    /**
     * ID 토큰에서 이메일 추출
     * @param idToken 구글 로그인/회원가입 시 받은 ID 토큰
     * @return 이메일, 없으면 null
     */
    fun parseEmailFromIdToken(idToken: String): String? {
        try {
            // ID 토큰은 "header.payload.signature" 형태
            val parts = idToken.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val payloadJson = JSONObject(String(decodedBytes))
            return payloadJson.optString("email")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
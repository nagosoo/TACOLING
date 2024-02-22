package com.eundmswlji.tacoling.util

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.eundmswlji.tacoling.TacolingApplication
import com.eundmswlji.tacoling.presentation.Const
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase

object LinkUtil {

    private var shortLink: Uri? = null

    fun setDynamicLinks(shopId: Int, shopName: String): Uri? {
        val shopUrl = "https://tacoling.com/?shopId=$shopId"
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse(shopUrl)
            domainUriPrefix = Const.appDomainUriPrefix
            androidParameters(TacolingApplication.appPackageName) {}
            socialMetaTagParameters {
                title = "$shopName | ${TacolingApplication.appLabel}"
                description = "${shopName}에서 맛있는 타코야키를 즐기세요!"
                imageUrl = "https://cdn-icons-png.flaticon.com/512/2322/2322305.png".toUri()
            }
        }.addOnSuccessListener {
            shortLink = it.shortLink
        }
            .addOnFailureListener { e ->
                Log.d("LOGGING", "${e.message}")
            }
        return shortLink
    }
}
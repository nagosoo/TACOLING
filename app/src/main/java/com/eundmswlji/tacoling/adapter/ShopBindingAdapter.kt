package com.eundmswlji.tacoling.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object ShopBindingAdapter {

    @BindingAdapter("zeroWaste")
    @JvmStatic
    fun setZeroWaste(textView: TextView, isZeroWaste: Boolean) {
        textView.text = if (isZeroWaste) "용기내 챌린지 참여" else "용기내 챌린지 미참여"
    }

    @BindingAdapter("likedButtonName")
    @JvmStatic
    fun setLikedButtonName(textView: TextView, isLikedShop: Boolean) {
        textView.text = if (isLikedShop) "찜 해지💔" else "찜 하기❤️"
    }

}
package com.eundmswlji.tacoling.presentation.adapter

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.text.toSpannable
import androidx.databinding.BindingAdapter
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.domain.model.ShopModel
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.util.Util.todayDate
import kotlinx.coroutines.flow.StateFlow


object ShopBindingAdapter {

    @BindingAdapter("zeroWaste")
    @JvmStatic
    fun setZeroWaste(textView: TextView, isZeroWaste: Boolean) {
        textView.text = if (isZeroWaste) "용기내 챌린지 참여" else "용기내 챌린지 미참여"
    }

    @BindingAdapter("uiState", "date")
    @JvmStatic
    fun bindLocation(textView: TextView, uiState: StateFlow<UiState<ShopModel?>>, date: Int) {
        if (uiState.value is UiState.Success<ShopModel?>) {
            val shop = (uiState.value as UiState.Success<ShopModel?>).value!!
            textView.text = shop.location[date].locationName
        }
    }

    @BindingAdapter("offText", "context")
    @JvmStatic
    fun setOffText(textView: TextView, isOff: Boolean, context: Context) {

        val date = when (todayDate) {
            0 -> "일요일"
            1 -> "월요일"
            2 -> "화요일"
            3 -> "수요일"
            4 -> "목요일"
            5 -> "금요일"
            else -> "토요일"
        }.run {
            this + "은 휴무입니다."
        }.toSpannable()

        val spannableText = SpannableStringBuilder(date).apply {
            setSpan(
                ForegroundColorSpan(context.getColor(R.color.subRed)),
                5,
                7,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }

        textView.text = if (isOff) spannableText else ""
    }

}
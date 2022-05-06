package com.eundmswlji.tacoling.ui.common

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.Util.wonFormat
import com.eundmswlji.tacoling.databinding.ItemMenuBinding

class MenuCommon : LinearLayoutCompat {
    private lateinit var binding: ItemMenuBinding
    private lateinit var tvMenu: AppCompatTextView
    private lateinit var tvPrice: AppCompatTextView

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) { //AttributeSet 사용
        initView()
        getAttrs(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super( //AttributeSet , 기본값 사용
        context,
        attributeSet,
        defStyleAttr
    ) {
        initView()
        getAttrs(attributeSet, defStyleAttr)
    }

    private fun initView() {
        val inflater = LayoutInflater.from(context)
        binding = ItemMenuBinding.inflate(inflater, this, false)
        tvMenu = binding.tvMenu
        tvPrice = binding.tvPrice
        addView(binding.root)
    }

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuCommon) //AttributeSet 에서 MenuCommon에 해당하는 declare-styleable 가져오기
        setTypeArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MenuCommon, defStyleAttr, 0)
        setTypeArray(typedArray)
    }

    private fun setTypeArray(typedArray: TypedArray) {
        val menu = typedArray.getString(R.styleable.MenuCommon_tvMenu)
        val price = typedArray.getString(R.styleable.MenuCommon_tvPrice)

        this.tvMenu.text = menu ?: ""
        this.tvPrice.text = price ?: ""

        typedArray.recycle()//TypedArray를 캐싱해 재활용함. 이 함수 호출 이후로는 typeArray 변경 x
    }

    fun setMenu(menu: String?) {
        this.tvMenu.text = menu
    }

    fun setPrice(price: Int) {
        this.tvPrice.text = wonFormat.format(price)
    }

}
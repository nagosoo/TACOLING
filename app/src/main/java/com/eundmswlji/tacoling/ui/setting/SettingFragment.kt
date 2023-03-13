package com.eundmswlji.tacoling.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.CompoundButton
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.EventObserver
import com.eundmswlji.tacoling.databinding.FragmentSettingBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import com.eundmswlji.tacoling.util.Util
import com.eundmswlji.tacoling.util.Util.toast
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingFragment : BaseFragment<FragmentSettingBinding>(FragmentSettingBinding::inflate),
    View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private val viewModel: SettingViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewModel = this@SettingFragment.viewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setAppBar()
        (requireActivity() as? MainActivity)?.showBottomNav()
        observer()
        viewModel.getAlarmInfo()
        binding.suggest.setOnClickListener(this)
        binding.withdrawal.setOnClickListener(this)
        binding.myLiked.setOnClickListener(this)
        binding.alarm.setOnCheckedChangeListener(this)
    }

    private fun observer() {
        viewModel.toastHelper.observe(viewLifecycleOwner, EventObserver {
            toast(it)
        })

        viewModel.isUserDeletedSuccessful.observe(viewLifecycleOwner) {
            val action = SettingFragmentDirections.actionSettingFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.suggest.id -> {
                sendEmailToAdmin()
            }
            binding.withdrawal.id -> {
                showWithdrawalDialog()
            }
            binding.myLiked.id -> {
                val action = SettingFragmentDirections.actionSettingFragmentToLikedShopFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewModel.patchAlarm(isChecked)
    }

    private fun setAppBar() {
        binding.appBar.buttonBack.isVisible = false
        binding.appBar.tv.text = "마이페이지"
    }

    private fun sendEmailToAdmin() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_SUBJECT, "건의하기")
            putExtra(Intent.EXTRA_EMAIL, arrayOf("nagosoo@kakao.com"))
            putExtra(
                Intent.EXTRA_TEXT,
                "App Version : ${BuildConfig.VERSION_NAME}\nDevice : ${Build.MODEL}\nAndroid(SDK) : ${Build.VERSION.SDK_INT}(${Build.VERSION.RELEASE})\n내용 : ",
            )
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun showWithdrawalDialog() {
        val message = Html.fromHtml(
            "정말 탈퇴하시겠습니까? <img src=\"ic_taco\"> ${String(Character.toChars(0x1F4A6))}",
            HtmlCompat.FROM_HTML_MODE_COMPACT,
            Util.ImageGetter(requireContext(), 34, 34),
            null
        )
        NormalDialog(
            title = "탈퇴",
            spannedMessage = message,
            positiveMessage = "네",
            negativeMessage = "아니요",
            positiveButtonListener = {
                disconnectKaKao()
            }).show(childFragmentManager, null)
    }

    private fun disconnectKaKao() {
        UserApiClient.instance.unlink { error ->
            if (error != null) {
                toast("카카오 연결 해제에 실패했습니다.")
            } else {
                viewModel.deleteUser()
            }
        }
    }

}

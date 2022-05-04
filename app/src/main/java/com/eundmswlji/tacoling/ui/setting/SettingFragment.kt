package com.eundmswlji.tacoling.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.repository.JusoRepository
import com.eundmswlji.tacoling.databinding.FragmentSettingBinding
import com.eundmswlji.tacoling.ui.dialog.NormalDialog
import javax.inject.Inject


class SettingFragment : Fragment() {
    @Inject
    lateinit var jusoRepository: JusoRepository
    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
        setOnClickListener()
        setObserver()
    }

    private fun setObserver() {
        //TODO::alarmOnOff DB 처리
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "마이페이지"
    }

    private fun setOnClickListener() {
        binding.suggest.setOnClickListener {
            sendEmailToAdmin()
        }
        binding.withdrawal.setOnClickListener {
            showWithdrawalDialog()
        }
        binding.myZzim.setOnClickListener {
            findNavController().navigate(R.id.myZzimFragment)
        }

        binding.alarm.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setAlarmOnOff(isChecked)
        }
        binding.logout.setOnClickListener { }
    }

    private fun sendEmailToAdmin() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.apply {
            data = Uri.parse("mailto:") // only email apps should handle this. no message, sns app
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
        val message = "정말 탈퇴하시겠습니까? ${R.string.octopusEmoji}${R.string.sweatEmoji}"
        NormalDialog(title = "탈퇴", message = message, positiveMessage = "네", negativeMessage = "아니요", positiveButtonListener = {}).show(childFragmentManager, null)
    }
}

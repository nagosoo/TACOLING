package com.eundmswlji.tacoling.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eundmswlji.tacoling.BuildConfig
import com.eundmswlji.tacoling.Util.toast
import com.eundmswlji.tacoling.data.repository.JusoRepository
import com.eundmswlji.tacoling.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment : Fragment() {
    @Inject
    lateinit var jusoRepository: JusoRepository
    private lateinit var binding: FragmentSettingBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar()
        setOnClickListener()
    }

    private fun setAppBar() {
        binding.appBar.tv.text = "마이페이지"
        binding.logout.tv.text = "로그아웃"
        binding.alarm.tv.text = "알림 설정"
        binding.myZzim.tv.text = "내가 찜한 가게"
        binding.suggest.tv.text = "건의하기"
        binding.withdrawal.tv.text = "탈퇴하기"
    }

    private fun setOnClickListener() {
        binding.suggest.root.setOnClickListener {
            sendEmailToAdmin()
        }
    }

    private fun sendEmailToAdmin() {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.apply {
            data = Uri.parse("mailto:") // only email apps should handle this. no message, sns app
            putExtra(Intent.EXTRA_SUBJECT, "건의하기")
            putExtra(Intent.EXTRA_EMAIL, "nagosoo@kakao.com")
            putExtra(
                Intent.EXTRA_TEXT,
                "App Version : ${BuildConfig.VERSION_NAME}\nDevice : ${Build.DEVICE}\nAndroid(SDK) : ${Build.VERSION.SDK_INT}(${Build.VERSION.RELEASE})\n내용 : ",
            )
            type = "*/*"
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }
}

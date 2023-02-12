package com.eundmswlji.tacoling.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.data.model.UserInfo
import com.eundmswlji.tacoling.databinding.FragmentLoginBinding
import com.eundmswlji.tacoling.ui.BaseFragment
import com.eundmswlji.tacoling.ui.MainActivity
import com.eundmswlji.tacoling.util.Util.toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        setOnClickListener()
        (activity as? MainActivity)?.hideBottomNav()
    }

    private fun setOnClickListener() {
        binding.buttonKakao.setOnClickListener {
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        val defaultUser = UserInfo()
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                toast("로그인 실패")
            } else if (token != null) {
                viewModel.postUser(defaultUser)
            }
        }

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    toast("로그인 실패")
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) return@loginWithKakaoTalk

                    UserApiClient.instance.loginWithKakaoAccount(
                        requireContext(),
                        callback = callback
                    )
                } else if (token != null) viewModel.postUser(defaultUser)
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                requireContext(),
                callback = callback
            )
        }
    }

    private fun observe() {
        viewModel.loginSuccess.observe(viewLifecycleOwner) {
            if (it) findNavController().navigate(R.id.mapFragment)
        }
    }
}

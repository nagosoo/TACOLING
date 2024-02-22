package com.eundmswlji.tacoling.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.FragmentLoginBinding
import com.eundmswlji.tacoling.domain.status.UiState
import com.eundmswlji.tacoling.presentation.ui.BaseFragment
import com.eundmswlji.tacoling.presentation.ui.MainActivity
import com.eundmswlji.tacoling.presentation.ui.MainViewModel
import com.eundmswlji.tacoling.util.Util.toast
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

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
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                toast("로그인 실패")
            } else if (token != null) {
                viewModel.postUser()
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
                } else if (token != null) viewModel.postUser()
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(
                requireContext(),
                callback = callback
            )
        }
    }

    private fun observe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is UiState.None -> {}
                        is UiState.Loading -> {}
                        is UiState.Success -> {
                            val userKey = uiState.value!!.userKey
                            mainViewModel.saveUserKey(userKey)
                            viewModel.saveUserKey(userKey) {
                                findNavController().navigate(R.id.action_loginFragment_to_mapFragment)
                            }
                        }

                        is UiState.Error -> {
                            toast(uiState.errorMessage)
                        }
                    }
                }
            }
        }
    }
}

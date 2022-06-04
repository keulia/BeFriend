package com.casoca.befriend

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.databinding.FragmentLoggingBinding
import com.casoca.befriend.utilidades.BeFriendApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class LoggingFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentLoggingBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoggingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        // Carga el fragment del sign in
        binding.btnSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_loggingFragment2_to_signInFragment2)
        }
        // Carga el fragment del sign up
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_loggingFragment2_to_signUpFragment2)
        }

        checkRemember()
    }

    fun checkRemember(){
        if (BeFriendApplication.prefs.getRecordar()){
            val currentUser = auth.currentUser
            if(currentUser != null){
                startActivity(Intent (requireActivity(),HomeActivity::class.java))
                requireActivity().finish()
            }
        }
    }

}
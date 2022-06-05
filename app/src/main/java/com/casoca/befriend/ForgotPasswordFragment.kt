package com.casoca.befriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.databinding.FragmentForgotPasswordBinding
import com.casoca.befriend.databinding.FragmentSignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ForgotPasswordFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentForgotPasswordBinding?=null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        binding.btnSend.setOnClickListener {
            recoverAccount()
        }

        binding.tvBackSignIn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    // Función para mandar correo recuperación
    private fun recoverAccount() {
        if (binding.etEmail.text.toString().isNotEmpty()){
            auth.sendPasswordResetEmail(binding.etEmail.text.toString()).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(requireContext(), "El E-mail de recuperación fue mandado " +
                            "correctamente!", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "Error, E-mail no " +
                            "registrado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
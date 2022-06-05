package com.casoca.befriend

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.databinding.FragmentSignInBinding
import com.casoca.befriend.utilidades.BeFriendApplication.Companion.prefs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentSignInBinding?=null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        binding.btnSignIn.setOnClickListener {
            if (binding.etEmail.text.toString().isEmpty() && binding.etPassword.text.toString().isEmpty()){
                requireActivity().toast("No pueden haber campos vacíos")
            } else {
                // Mando email y password a funcion
                signin(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }
        }

        binding.tvForgetPwd.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment2_to_forgotPasswordFragment)
        }

        // Al pulsar sobre el campo muestra la contraseña
        /*
        binding.etPassword.setOnClickListener {
            if(binding.etPassword.transformationMethod == PasswordTransformationMethod.getInstance()){
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else{
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            }
        }
         */
    }

    // Mantener la sesión iniciada
    private fun signin(email: String,pass: String){
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    prefs.saveRecordar(binding.cbRememberMe.isChecked)
                    prefs.saveUser(email)
                    prefs.savePass(pass)
                    // Lleva a la home activity
                    startActivity(Intent (requireActivity(),HomeActivity::class.java))
                    // Finalizar el Main activity
                    requireActivity().finish()
                }else{
                    binding.etEmail.error = "Email incorrecto"
                }

            }
    }


}
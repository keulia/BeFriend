package com.casoca.befriend.utilidades

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.R
import com.casoca.befriend.databinding.FragmentConversacionesSettingsBinding
import com.casoca.befriend.databinding.FragmentNewContactBinding
import com.casoca.befriend.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class newContactFragment : Fragment() {
    private var _binding:   FragmentNewContactBinding?=null
    private val binding get() = _binding!!
    var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentNewContactBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        binding.btnSend.setOnClickListener {
            if(binding.etConversacion.text.toString().isNotEmpty()){
                db.collection("users").document(auth.currentUser?.uid.toString()).update("conversaciones",
                    FieldValue.arrayUnion(binding.etConversacion.text.toString()))
                findNavController().popBackStack()
            } else {
                requireActivity().toast("Error: El campo no puede estar vacío")
            }
        }
    }


}
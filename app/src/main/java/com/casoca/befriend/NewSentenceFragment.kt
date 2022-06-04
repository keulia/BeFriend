package com.casoca.befriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.databinding.FragmentNewContactBinding
import com.casoca.befriend.databinding.FragmentNewSentenceBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NewSentenceFragment : Fragment() {
    private var _binding:   FragmentNewSentenceBinding?=null
    private val binding get() = _binding!!
    var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentNewSentenceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        binding.btnSend.setOnClickListener {
            if(binding.etFrase.text.toString().isNotEmpty()){
                db.collection("users").document(auth.currentUser?.uid.toString()).update("frases",
                    FieldValue.arrayUnion(binding.etFrase.text.toString()))
                findNavController().popBackStack()
            } else {
                requireActivity().toast("Error")
            }
        }
    }

}
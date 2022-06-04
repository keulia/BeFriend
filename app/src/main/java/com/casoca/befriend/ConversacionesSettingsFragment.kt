package com.casoca.befriend

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.casoca.befriend.databinding.FragmentConversacionesSettingsBinding
import com.casoca.befriend.databinding.FragmentFrasesSettingsBinding
import com.casoca.befriend.recyclerview.RecyclerviewConversaciones
import com.casoca.befriend.recyclerview.RecyclerviewFrases
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ConversacionesSettingsFragment : Fragment(), RecyclerviewConversaciones.OnConversacionClickListener {
    private var _binding:   FragmentConversacionesSettingsBinding?=null
    private val binding get() = _binding!!
    private var listaConversaciones: MutableList<String> = mutableListOf()
    var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
// Inflate the layout for this fragment
        _binding= FragmentConversacionesSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        loadSentence()
        binding.btnNewSentence.setOnClickListener {
            conversacionNuevo()
        }

    }

    private fun conversacionNuevo() {
        findNavController().navigate(com.casoca.befriend.R.id.action_conversacionesSettingsFragment_to_newContactFragment)
    }

    fun loadSentence(){
        db.collection("users").document(auth.currentUser?.uid.toString())
            .get().addOnSuccessListener {

                listaConversaciones = it["conversaciones"] as MutableList<String>


            }.addOnCompleteListener {
                binding.tvConversacion.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = RecyclerviewConversaciones(requireContext(),listaConversaciones, this@ConversacionesSettingsFragment)
                }
            }
    }

    override fun onConversacionClick(position: Int) {
        var builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar!")
        builder.setMessage("¿Estás seguro de que deseas borrar el tema de conversación?")
        builder.setPositiveButton("Borrar"){ dialog,li ->
            db.collection("users").document(auth.currentUser?.uid.toString())
                .update("conversaciones",FieldValue.arrayRemove(listaConversaciones[position]))
            listaConversaciones.removeAt(position)
            binding.tvConversacion.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RecyclerviewConversaciones(requireContext(),listaConversaciones, this@ConversacionesSettingsFragment)
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar"){ dialog,li ->
            dialog.dismiss()
        }
        var alerta = builder.create()
        alerta.show()
    }

}
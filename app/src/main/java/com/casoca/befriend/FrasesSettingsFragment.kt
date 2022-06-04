package com.casoca.befriend

import android.R
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.core.view.marginBottom
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.casoca.befriend.databinding.FragmentFrasesSettingsBinding
import com.casoca.befriend.recyclerview.RecyclerviewConversaciones
import com.casoca.befriend.recyclerview.RecyclerviewFrases
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.grpc.InternalChannelz.id

class FrasesSettingsFragment : Fragment(), RecyclerviewFrases.OnFraseClickListener {
    private var _binding:   FragmentFrasesSettingsBinding?=null
    private val binding get() = _binding!!
    private var listaFrases: MutableList<String> = mutableListOf()
    var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentFrasesSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        loadSentence()
        binding.btnNewSentence.setOnClickListener {
            fraseNueva()
        }

    }

    private fun fraseNueva() {
        findNavController().navigate(com.casoca.befriend.R.id.action_frasesSettingsFragment_to_newSentenceFragment)
    }

    fun loadSentence(){
        db.collection("users").document(auth.currentUser?.uid.toString())
            .get().addOnSuccessListener {

                listaFrases = it["frases"] as MutableList<String>


            }.addOnCompleteListener {
                binding.rvFrases.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = RecyclerviewFrases(requireContext(),listaFrases,this@FrasesSettingsFragment)
                }
            }
    }

    override fun onFraseClick(position: Int) {
        var builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar!")
        builder.setMessage("¿Estás seguro de que deseas borrar el tema de conversación?")
        builder.setPositiveButton("Borrar"){ dialog,li ->
            db.collection("users").document(auth.currentUser?.uid.toString())
                .update("frases", FieldValue.arrayRemove(listaFrases[position]))
            listaFrases.removeAt(position)
            binding.rvFrases.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = RecyclerviewFrases(requireContext(),listaFrases, this@FrasesSettingsFragment)
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
package com.casoca.befriend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SharedMemory
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.casoca.befriend.databinding.FragmentHomeBinding
import com.casoca.befriend.utilidades.BeFriendApplication
import com.casoca.befriend.utilidades.Frases
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

//TODO sentence doesn't change

class HomeFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentHomeBinding?=null
    private val binding get() = _binding!!
    var randomNumber =0;
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth

    var listSentence = mutableListOf<String>()

    private lateinit var  sharedPreference:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = Firebase.auth
        _binding = FragmentHomeBinding.inflate(inflater, container,false)




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        sharedPreference= requireContext().getSharedPreferences("App Data", Context.MODE_PRIVATE)
        val username=sharedPreference.getString("UserName","");
        if(username!!.isNotEmpty()) {
            binding.tvBienvenida.text = "Hola " + username
        }
        else {
            getName(auth.currentUser?.uid.toString())
        }

        //binding.tvMensajePositivo.text = frases.get(randomNumber)
        loadListSentence()

        // Events/Notifications list
        binding.ivBtnNoti.setOnClickListener { 
            findNavController().navigate(R.id.action_homeFragment_to_eventsFragment)
        }

        // Contact list
        binding.ivBtnPersona.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_contactsFragment)
        }


        // Help fragment
        binding.ivBtnAyuda.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_helpFragment)
        }

        // Settings fragment
        binding.ivBtnSettings.setOnClickListener {
            //findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
           startActivity(Intent(HomeActivity.contexto,SettingsActivity::class.java))
        }


        //btn log out
        binding.ivBtnLogout.setOnClickListener {
            sharedPreference.edit().putString("UserName", null).commit()
            BeFriendApplication.prefs.saveRecordar(false)
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(),MainActivity::class.java))
            requireActivity().finish()
        }
    }

    fun getName(id: String){
        db.collection("users").document(id).get().addOnSuccessListener {
            binding.tvBienvenida.text = "Hola " + it.data?.get("nombre").toString()

            sharedPreference.edit().putString("UserName",it.data?.get("nombre").toString()).commit()
        }
    }

    fun loadListSentence(){
        db.collection("users").document(auth.currentUser!!.uid.toString()).get().addOnSuccessListener {
            //listSentence = value!!.getField("frases")!!
            listSentence = it.data?.get("frases") as MutableList<String>?: mutableListOf()
           //Toast.makeText(requireContext(), listSentence.toString(), Toast.LENGTH_SHORT).show()

        }
            .addOnCompleteListener {
                binding.tvMensajePositivo.text = Frases.generateRandom(listSentence).toString()
            }
    }


}
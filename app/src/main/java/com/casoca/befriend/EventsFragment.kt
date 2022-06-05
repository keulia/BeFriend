package com.casoca.befriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.casoca.befriend.databinding.FragmentEventsBinding
import com.casoca.befriend.databinding.FragmentNewContactoBinding
import com.casoca.befriend.utilidades.Noti
import com.casoca.befriend.utilidades.RecyclerNotificaciones
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EventsFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentEventsBinding?=null
    private val binding get() = _binding!!


    private lateinit var auth:FirebaseAuth

    private  var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        cargarNotis()


    }




    private fun cargarNotis(){

        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("notificaciones")
            .addSnapshotListener { value, error ->

                var notis = value!!.toObjects(Noti::class.java)


                binding.rvNotificaciones.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(HomeActivity.contexto)
                    adapter = RecyclerNotificaciones(HomeActivity.contexto, notis)
                }

            }
    }





}
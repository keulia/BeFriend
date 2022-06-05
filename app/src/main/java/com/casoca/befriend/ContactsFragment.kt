package com.casoca.befriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.casoca.befriend.databinding.FragmentContactsBinding
import com.casoca.befriend.databinding.FragmentSignUpBinding
import com.casoca.befriend.utilidades.Contacto
import com.casoca.befriend.utilidades.RecyclerContacts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ContactsFragment : Fragment(),RecyclerContacts.OnCajaClickListener {
    private var _binding: FragmentContactsBinding?=null
    private val binding get() = _binding!!
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth


    private  var listaContactos: MutableList<Contacto> = mutableListOf()
    private  var listaCpmtactosFiltro: MutableList<Contacto> = mutableListOf()

    private var contactList = mutableListOf<Contacto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        loadContacts()

        binding.ivAnadirContacto.setOnClickListener {
            findNavController().navigate(R.id.action_contactsFragment_to_newContactoFragment)
        }



        binding.buscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean  = true


            override fun onQueryTextChange(nuevotexto: String?): Boolean {

                if (nuevotexto!!.isNotEmpty()){
                    listaCpmtactosFiltro.clear()
                    val busqueda = nuevotexto.lowercase()

                    contactList.forEach {
                        if (it.name?.lowercase()?.contains(busqueda) ==true){
                            listaCpmtactosFiltro.add(it)
                        }
                    }

                }else{
                    listaCpmtactosFiltro.clear()
                    listaCpmtactosFiltro.addAll(contactList)



                }

                binding.rvListaContactos.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = RecyclerContacts(requireContext(), listaCpmtactosFiltro, this@ContactsFragment)
                }


                return true
            }

        })




    }

    override fun OnCajaClick(id: String) {
        var bundle = Bundle()
        bundle.putString("idContact",id)
        findNavController().navigate(R.id.action_contactsFragment_to_infoContactFragment,bundle)
    }

    fun loadContacts(){
        db.collection("users").document(auth.currentUser?.uid.toString())
            .collection("contacts").addSnapshotListener { value, error ->
                contactList = value!!.toObjects(Contacto::class.java)
                binding.rvListaContactos.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(HomeActivity.contexto)
                    adapter = RecyclerContacts(HomeActivity.contexto, contactList, this@ContactsFragment)
                }
            }
    }


}
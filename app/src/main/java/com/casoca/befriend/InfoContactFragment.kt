package com.casoca.befriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.casoca.befriend.databinding.FragmentContactsBinding
import com.casoca.befriend.databinding.FragmentInfoContactBinding
import com.casoca.befriend.utilidades.Contacto
import com.casoca.befriend.utilidades.RecyclerContacts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class InfoContactFragment : Fragment(){
    private var _binding: FragmentInfoContactBinding?=null
    private val binding get() = _binding!!
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth
    private lateinit var contact:Contacto
    private var idContacto = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInfoContactBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        idContacto = arguments?.getString("idContact").toString()

        loadContacts()
        binding.ivBtnSettingsUser.setOnClickListener {
            var bundle = Bundle()
            bundle.putString("idContact",idContacto)
            findNavController().navigate(R.id.action_infoContactFragment_to_editContactFragment,bundle)


        }

    }

    fun loadContacts(){


       db.collection("users").document(auth.currentUser?.uid.toString())
            .collection("contacts").document(idContacto)
           .get().addOnSuccessListener {
               binding.tvName.setText(it["name"].toString())
               binding.tvBirthday.setText(it["birthday"].toString())


               val radius = HomeActivity.contexto.resources.getDimensionPixelSize(R.dimen.corner_radius)
               Glide.with(this).load(it["img"].toString())
                   .transforms(CenterCrop(), RoundedCorners(radius))
                   .placeholder(R.drawable.user)
                   .transition(DrawableTransitionOptions.withCrossFade())
                   .into(binding.ivContact)

               //todo PROX NOTIFICATION
               binding.tvNotes.setText(it["notes"].toString())

           }

    }







}
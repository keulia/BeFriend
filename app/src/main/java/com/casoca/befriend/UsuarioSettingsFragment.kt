package com.casoca.befriend

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.databinding.FragmentContactsBinding
import com.casoca.befriend.databinding.FragmentUsuarioSettingsBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class UsuarioSettingsFragment : Fragment() {
    private var _binding: FragmentUsuarioSettingsBinding?=null
    private val binding get() = _binding!!
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var user: FirebaseUser?  = null

    // actualizar nombre
    private lateinit var sharedPreference:SharedPreferences

    private var autenticado = false
    private var emailPrevio:String = ""
    private var passPrevio:String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUsuarioSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        user = auth.currentUser
        sharedPreference = requireContext().getSharedPreferences("App Data", Context.MODE_PRIVATE)
        //setContentView(binding.root) (no sé)
        //obtenerDatos()

        binding.btnAuth.setOnClickListener {
            if (user != null && binding.etEmail.text.toString().isNotEmpty() && binding.etPassword.text.toString().isNotEmpty() ){

                val credential  = EmailAuthProvider
                    .getCredential(binding.etEmail.text.toString(), binding.etPassword.text.toString())

                user!!.reauthenticate(credential).addOnCompleteListener {
                    autenticado = it.isSuccessful
                    if (autenticado){
                        binding.etEmail.visibility = View.GONE
                        binding.etPassword.visibility = View.GONE
                        binding.btnAuth.visibility = View.GONE
                        //binding.tvMessage.visibility = View.GONE

                        binding.btnUpdate.visibility = View.VISIBLE
                        binding.etName.visibility = View.VISIBLE
                        binding.etEmailUpdate.visibility = View.VISIBLE
                        binding.etPasswordUpdate.visibility = View.VISIBLE
                        binding.tvMessage.text = "Escribe la nueva información a actualizar"

                    } else {
                        requireActivity().toast("Error al autenticar el usuario")
                    }
                }.addOnFailureListener { //Para que sucede esto y que diferencia hay de arriba?
                    requireActivity().toast("Error al autenticar el usuario")
                }
            }else{
                requireActivity().toast("El campo email y contraseña no pueden estar vacíos")
            }
        }

        binding.btnUpdate.setOnClickListener {
            if(binding.etPassword.text.toString().isNotEmpty() || binding.etEmailUpdate.text.toString().isNotEmpty() || binding.etName.text.toString().isNotEmpty()){
                updateInfo()
            }else{
                requireActivity().toast("Error al actualizar")
            }

        }


    }


    private fun cambiarEmail(){
        user!!.updateEmail(binding.etEmailUpdate.text.toString()).addOnCompleteListener {
            if (it.isSuccessful){
                requireActivity().toast("Email fue actualizada correctamente")
            }else{
                requireActivity().toast("Error al actualziar la contraseña")
            }
        }
    }

    private fun cambiarPass(){
        try{
            user?.updatePassword(binding.etPasswordUpdate.text.toString())
                ?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        requireActivity().toast("Contraseña fue actualizada correctamente")
                    } else {
                        requireActivity().toast("Error al actualziar la contraseña")
                    }
                }
        }catch (e:Exception){
            requireActivity().toast("No se pudieron actualizar los datos")
        }
    }



    fun updateInfo(){
        // Si email
        if(binding.etEmailUpdate.text.toString() != "")
            db.collection("users").document(auth.currentUser?.uid.toString()).update("email", binding.etEmail.text.toString())

        // Si nombre
        if(binding.etName.text.toString() != "") {
            db.collection("users").document(auth.currentUser?.uid.toString())
                .update("nombre", binding.etName.text.toString())
                    sharedPreference.edit().putString("UserName",binding.etName.text.toString()).apply()
        }
        if (autenticado) {
            if (binding.etEmailUpdate.text.toString() != "") cambiarEmail()
            if (binding.etPasswordUpdate.text.toString() != "") cambiarPass()
        }

                    requireActivity().toast("Datos actualizados correctamente")
                    startActivity(Intent (requireActivity(),HomeActivity::class.java))
                    requireActivity().finish()

    }

}
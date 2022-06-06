package com.casoca.befriend

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.casoca.befriend.databinding.FragmentEditContactBinding
import com.casoca.befriend.databinding.FragmentSignInBinding
import com.casoca.befriend.recyclerview.RecyclerviewConversaciones
import com.casoca.befriend.utilidades.Contacto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage


class EditContactFragment : Fragment(), DatePickerDialog.OnDateSetListener  {
    private var _binding: FragmentEditContactBinding?=null
    private val binding get() = _binding!!
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth
    private lateinit var contact:Contacto
    private var idContacto = ""
    // Access to Storage
    private lateinit var storage: FirebaseStorage
    // Save image from contact
    private var imgUrl= ""
    private var selectedImg: Uri?=null
    // abrir para seleccionar la imagen
    private var responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode== AppCompatActivity.RESULT_OK){
            selectedImg=it.data?.data
            val radius = HomeActivity.contexto.resources.getDimensionPixelSize(R.dimen.corner_radius)
            Glide.with(this).load(selectedImg)
                .transforms(CenterCrop(), RoundedCorners(radius))
                .placeholder(R.drawable.user)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivFotoContacto)
        }
    }

    private var day=""
    private var month=""
    private var daydefault=0
    private var monthdefault=0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEditContactBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        idContacto = arguments?.getString("idContact").toString()
        loadContacts()

        binding.ivFotoContacto.setOnClickListener {
            uploadPhoto()
        }
        binding.etCumpleanos.setOnClickListener {
            DatePickerDialog(requireContext(),this,2022,0,1).show()
        }

        // Save a contact
        binding.btnGuardarContacto.setOnClickListener {
            if(binding.etNombre.text.toString().isNotEmpty()){
                saveContact()
                requireActivity().toast("Usuario actualizado correctamente")
            }else{
                requireActivity().toast("error")
            }

        }

        // Delete a contact
        binding.btnDeleteContact.setOnClickListener{
            deleteContact()
        }
    }


    fun loadContacts(){

        db.collection("users").document(auth.currentUser?.uid.toString())
            .collection("contacts").document(idContacto)
            .get().addOnSuccessListener {
                binding.etNombre.setText(it["name"].toString())
                binding.etCumpleanos.setText(it["birthday"].toString())

                val radius = HomeActivity.contexto.resources.getDimensionPixelSize(R.dimen.corner_radius)
                Glide.with(this).load(it["img"].toString())
                    .transforms(CenterCrop(), RoundedCorners(radius))
                    .placeholder(R.drawable.user)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(binding.ivFotoContacto)

                binding.etNotas.setText(it["notes"].toString())

            }


    }


    override fun onDateSet(view: DatePicker?, ano: Int, mes: Int, dia: Int) {

        if(dia < 10){
            day="0"+dia
        }else{
            day = dia.toString()
        }

        if(mes < 10){
            month="0"+(mes+1)
        }else{
            month = (mes+1).toString()
        }

        daydefault=dia
        monthdefault=mes+1


        binding.etCumpleanos.setText("$day/$month")
    }

    // Save picture in cloud storage
    private fun uploadPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        storage = Firebase.storage
        intent.type = "image/*"
        responseLauncher.launch(intent)
    }

    //Save contact to database
    fun saveContact(){
        var idContact = idContacto
        var idContactImg = idContacto
        var folder: StorageReference = FirebaseStorage.getInstance("gs://befriend-d8a37.appspot.com").reference.child(idContact)
        var fileName:StorageReference = folder.child(idContactImg)


        if(selectedImg!=null)
        {
            fileName.putFile(selectedImg!!).addOnSuccessListener {
                fileName.downloadUrl.addOnSuccessListener {
                    imgUrl = it.toString()
                }.addOnCompleteListener {
                    db.collection("users").document(auth.currentUser?.uid.toString())
                        .collection("contacts").document(idContact)
                        .set(
                            mapOf(
                                "idContact" to idContact,
                                "name" to binding.etNombre.text.toString(),
                                "birthday" to binding.etCumpleanos.text.toString(),
                                "notes" to binding.etNotas.text.toString(),
                                "img" to imgUrl
                            )
                        ).addOnCompleteListener {

                            findNavController().popBackStack()
                        }
                }
            }
        }
        else
        {
            db.collection("users").document(auth.currentUser?.uid.toString())
                .collection("contacts").document(idContact)
                .update(
                    mapOf(
                        "idContact" to idContact,
                        "name" to binding.etNombre.text.toString(),
                        "birthday" to binding.etCumpleanos.text.toString(),
                        "notes" to binding.etNotas.text.toString(),
                    )
                ).addOnCompleteListener {
                    findNavController().popBackStack()
                }
        }

    }

    fun deleteContact(){
        var builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar!")
        builder.setMessage("¿Estás seguro de que deseas borrar el contacto?")
        builder.setPositiveButton("Borrar"){ dialog,li ->
            db.collection("users").document(auth.currentUser?.uid.toString())
                .collection("contacts").document(idContacto).delete().addOnCompleteListener {
                    if(it.isSuccessful){
                        requireActivity().toast("El contacto fue eliminado correctamente")
                        findNavController().navigate(R.id.action_editContactFragment_to_contactsFragment3)
                    }else{
                        requireActivity().toast("Hubo un problema al borrar el contacto")
                    }
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
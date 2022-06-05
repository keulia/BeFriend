package com.casoca.befriend

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.casoca.befriend.databinding.FragmentNewContactoBinding
import com.casoca.befriend.utilidades.*
import com.casoca.befriend.utilidades.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*
import java.util.concurrent.TimeUnit


class NewContactoFragment : Fragment(), DatePickerDialog.OnDateSetListener {
    // Declaro el binding
    private var _binding: FragmentNewContactoBinding?=null
    private val binding get() = _binding!!
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth
    // Access to Storage
    private lateinit var storage: FirebaseStorage
    // Save image from contact
    private var imgUrl= ""
    private var selectedImg: Uri?=null
    // abrir para seleccionar la imagen
    private var responseLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode==AppCompatActivity.RESULT_OK){
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

    private var todayDate = ""
    val currentTime = Calendar.getInstance().time


    private var messageNotification = ""

    private var frases = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewContactoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        // Cargar la imagen
        binding.ivFotoContacto.setOnClickListener {
            uploadPhoto()
        }


        cargarListaFrases()


        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent

        alarmMgr = requireActivity().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(HomeActivity.contexto, android.app.Notification::class.java).let { intent ->
            PendingIntent.getBroadcast(HomeActivity.contexto, 0, intent, 0)
        }

        alarmMgr.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10 * 1000,
            alarmIntent
        )


        // Save contact
        binding.btnCrearContacto.setOnClickListener {


            if(binding.etNombre.text.toString().isNotEmpty() &&
                binding.etNumero.text.toString().isNotEmpty()){

                saveContact()
                //enableNotification()
                HomeActivity.contexto.toast("El contacto fue creado correctamente!")
            }else{
                HomeActivity.contexto.toast("Los campos nombre y la frecuencia de notificaciones son campos obligatorios")
            }

        }

        binding.etCumpleanos.setOnClickListener {
            DatePickerDialog(requireContext(),this,2022,0,1).show()
        }

    }

    private fun cargarListaFrases() {
        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                 frases  = it["conversaciones"] as MutableList<String>
            }

    }


    //Save contact to database
    fun saveContact(){
        var idContact = UUID.randomUUID().toString()
        var idContactImg = UUID.randomUUID().toString()
        var folder:StorageReference = FirebaseStorage.getInstance("gs://befriend-d8a37.appspot.com").reference.child(idContact)
        var fileName:StorageReference = folder.child(idContactImg)

        mensajeono()
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
                            "temasConvo" to binding.cbTemasConvo.isChecked,
                            "number" to binding.etNumero.text.toString(),
                            "message" to messageNotification,
                            "img" to imgUrl
                        )
                    ).addOnCompleteListener {

                        registrarNotificacion()
                        findNavController().popBackStack()
                    }
            }
        }
    }
        else
    {
        db.collection("users").document(auth.currentUser?.uid.toString())
            .collection("contacts").document(idContact)
            .set(
                mapOf(
                    "idContact" to idContact,
                    "name" to binding.etNombre.text.toString(),
                    "birthday" to binding.etCumpleanos.text.toString(),
                    "notes" to binding.etNotas.text.toString(),
                    "temasConvo" to binding.cbTemasConvo.isChecked,
                    "number" to binding.etNumero.text.toString(),
                    "fechaCrear" to todayDate,
                    "message" to messageNotification,
                    "img" to ""
                )
            ).addOnCompleteListener {
                registrarNotificacion()
                findNavController().popBackStack()
            }
    }



    }

    // Save picture in cloud storage
    private fun uploadPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        storage = Firebase.storage
        intent.type = "image/*"
        responseLauncher.launch(intent)
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

        todayDate="$dia/$month/$ano"
        binding.etCumpleanos.setText("$day/$month")
    }

    private fun registrarNotificacion(){

        var idNotification = UUID.randomUUID().toString()

        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("notificaciones")
            .document(idNotification)
            .set(
                mapOf(

                    "id" to idNotification,
                    "message" to messageNotification,
                    "name" to binding.etNombre.text.toString(),
                    "dias" to binding.etNumero.text.toString()

                )
            ).addOnSuccessListener {

                val alarmMgr: AlarmManager
                val alarmIntent: PendingIntent

                alarmMgr = HomeActivity.contexto!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(HomeActivity.contexto, Notification::class.java)

                intent.putExtra(titleExtra, "hola!!!! RECUERDA")
                intent.putExtra(messageExtra, messageNotification)

                alarmIntent = PendingIntent.getBroadcast(HomeActivity.contexto, 0, intent, 0)

                // Alarma a las 8:30 a.m.

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                //calendar[Calendar.HOUR_OF_DAY] = 14
                //calendar[Calendar.MINUTE] = 0

                var diasSeleccionados = TimeUnit.HOURS.toMillis(binding.etNumero.text.toString().toLong())


                requireActivity().toast(diasSeleccionados.toString())

                // Repeticiones en intervalos de 20 minutos
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                    diasSeleccionados, alarmIntent
                )
            }

    }



    private fun mensajeono(){
        messageNotification = if (binding.cbTemasConvo.isChecked){
            "Escribe y "+binding.etNombre.text.toString()+" "+frases.random()
        }else{
            "Escribe y "+binding.etNombre.text.toString()
        }
    }

    private fun registrarNotificacion(){

        var idNotification = UUID.randomUUID().toString()

        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .collection("notificaciones")
            .document(idNotification)
            .set(
                mapOf(

                    "id" to idNotification,
                    "message" to messageNotification,
                    "name" to binding.etNombre.text.toString(),
                    "dias" to binding.etNumero.text.toString()

                )
            ).addOnSuccessListener {

                val alarmMgr: AlarmManager
                val alarmIntent: PendingIntent

                alarmMgr = HomeActivity.contexto!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(HomeActivity.contexto, Notification::class.java)

                intent.putExtra(titleExtra, "hola!!!! RECUERDA")
                intent.putExtra(messageExtra, messageNotification)

                alarmIntent = PendingIntent.getBroadcast(HomeActivity.contexto, 0, intent, 0)

                // Alarma a las 8:30 a.m.

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = System.currentTimeMillis()
                //calendar[Calendar.HOUR_OF_DAY] = 14
                //calendar[Calendar.MINUTE] = 0

                var diasSeleccionados = TimeUnit.HOURS.toMillis(binding.etNumero.text.toString().toLong())


                requireActivity().toast(diasSeleccionados.toString())

                // Repeticiones en intervalos de 20 minutos
                alarmMgr.setRepeating(
                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
                    diasSeleccionados, alarmIntent
                )
            }

    }






}
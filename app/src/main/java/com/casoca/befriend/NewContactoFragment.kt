package com.casoca.befriend

import android.app.*
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import java.text.SimpleDateFormat
import java.util.*


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
    private var cumple = ""
    private var frases = mutableListOf<String>()
    private var appContext: Context = HomeActivity.contexto


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

        // Save contact
        binding.btnCrearContacto.setOnClickListener {

            if(binding.etNombre.text.toString().isNotEmpty() &&
                binding.etNumero.text.toString().isNotEmpty()){

                saveContact()
                scheduleNotification(messageNotification, createID())
                HomeActivity.contexto.toast("El contacto fue creado correctamente!")
            }else{
                HomeActivity.contexto.toast("Los campos nombre y la frecuencia de notificaciones son campos obligatorios")
            }

        }

        binding.etCumpleanos.setOnClickListener {
            DatePickerDialog(requireContext(),this,2022,0,1).show()
        }

    }

    // Programar la notificacion
    private fun scheduleNotification(notificationText: String, idContact: Int) {
        val notificationUtils = NotificationUtils(HomeActivity.contexto)
        val etNumeroInDays = binding.etNumero.text.toString().toInt()
        val repeatFrequencyInMilliSec = (60 * 1000 * etNumeroInDays).toLong() //24 * 3600 * 1000 = probar con dias
        notificationUtils.setReminder(notificationText, repeatFrequencyInMilliSec, idContact)


    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)
        val dateformat = android.text.format.DateFormat.getLongDateFormat(appContext)
        val timeformat = android.text.format.DateFormat.getTimeFormat(appContext)

        AlertDialog.Builder(activity)
            .setTitle("Notificatio schedule")
            .setMessage("title $title \n message: $message \n ${dateformat.format(date)}  ${timeformat.format(date)}")
            .setPositiveButton("Okay"){_,_ ->}
            .show()

    }

    // Carga lista de temas de conversaciones
    private fun cargarListaFrases() {
        db.collection("users")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                 frases  = it["conversaciones"] as MutableList<String>
            }

    }

    // Crea una ID pero no se usa luego
    fun createID(): Int {
        val now = Date()
        return SimpleDateFormat("ddHHmmss", Locale.US).format(now).toInt()
    }

    //Guardar contacto a database
    fun saveContact(){
        var idContact = UUID.randomUUID().toString()
        var idContactImg = UUID.randomUUID().toString()
        var folder:StorageReference = FirebaseStorage.getInstance("gs://befriend-d8a37.appspot.com").reference.child(idContact)
        var fileName:StorageReference = folder.child(idContactImg)

        mensajeono()
        birthdayEmpty()

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
                            "birthday" to cumple,
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
                    "birthday" to cumple,
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

    // Guardar img a cloud storage
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

                val intent = Intent(appContext, Notification::class.java)

                val pendingIntent = PendingIntent.getBroadcast(
                    appContext,
                    notificationID,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = appContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val time = getTime()

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    time+ 2*1000,
                    pendingIntent
                )
            }

    }

    private fun getTime(): Long {

        val calendar = Calendar.getInstance()
        return  calendar.timeInMillis
    }



    // En el caso de comboBox activado o desactivado
    private fun mensajeono(){
        messageNotification = if (binding.cbTemasConvo.isChecked){
            "Deberías escribir a "+binding.etNombre.text.toString()+". "+frases.random()
        }else{
            "Deberías escribir a "+binding.etNombre.text.toString()
        }
    }

    // En el caso de que la fecha de cumple este vacia
    private fun birthdayEmpty(){
        cumple = if(binding.etCumpleanos.text.isNotEmpty()){
           binding.etCumpleanos.text.toString()
        }else{
            "??/??"
        }
    }



}
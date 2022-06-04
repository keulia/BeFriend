package com.casoca.befriend

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.casoca.befriend.databinding.FragmentNewContactoBinding
import com.casoca.befriend.utilidades.Notification
import com.casoca.befriend.utilidades.messageExtra
import com.casoca.befriend.utilidades.titleExtra
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
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
        binding.cbTemasConvo.setOnClickListener {
            establecerNotificacion()
        }

        // Save contact
        binding.btnCrearContacto.setOnClickListener {

            establecerNotificacion()
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
    //create notification to text that person
    private fun enableNotification() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("notification_id", name, importance).apply {
            description = descriptionText
        }
        // Register the channel with the system

        val notificationManager: NotificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        var builder = NotificationCompat.Builder(HomeActivity.contexto, channel.id)
            .setSmallIcon(R.drawable.splashscreen_icon)
            .setContentTitle("test title")
            .setContentText("test text")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(HomeActivity.contexto)) {
            // notificationId is a unique int for each notification that you must define
            notify(57, builder.build())
        }

    }


    //Establecer Notificacion

    private fun establecerNotificacion(){
        val title = "Noti de tati"
        val message = "Esto es un apureba"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel =NotificationChannel("45", title, importance)
        channel.description = message

        val notifiactionManager = HomeActivity.contexto.applicationContext.getSystemService(NotificationManager::class.java)

        notifiactionManager.createNotificationChannel(channel)


        var dias = 5//binding.etNumero.text.toString().toLong() //50
        var horas = dias*24
        var minutos = horas*60
        var segundos = minutos*60
        var  tiempoEnMilisegundos = segundos * 1000


        var alarmMgr: AlarmManager? = null

        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var alarmIntent: PendingIntent = Intent(context, Notification::class.java).let { intent ->
            intent.putExtra(titleExtra, title)
            intent.putExtra(messageExtra, message)
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }



        alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ 10 *1000,alarmIntent)

    }


    //Save contact to database
    fun saveContact(){
        var idContact = UUID.randomUUID().toString()
        var idContactImg = UUID.randomUUID().toString()
        var folder:StorageReference = FirebaseStorage.getInstance("gs://befriend-d8a37.appspot.com").reference.child(idContact)
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
                            "temasConvo" to binding.cbTemasConvo.isChecked,
                            "number" to binding.etNumero.text.toString(),
                            "img" to imgUrl
                        )
                    ).addOnCompleteListener {
                        //Aqui va la notificacion para que funcione


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
                    "img" to ""
                )
            ).addOnCompleteListener {
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

}
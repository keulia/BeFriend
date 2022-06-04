package com.casoca.befriend

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.casoca.befriend.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class SignUpFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentSignUpBinding?=null
    private val binding get() = _binding!!
    // Access a Cloud Firestore instance from your Activity
    private var db = Firebase.firestore
    // acceeder al servidor a traves de esta variable
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.btnSignUp.setOnClickListener {
            // Comprobación de que los campos no esten vacios
            if (binding.etEmail.text.toString().isEmpty() && binding.etPassword.text.toString().isEmpty()){
                requireActivity().toast("No pueden haber campos vacíos")
            } else {
                // Mando email y password a funcion
                createAccount(binding.etEmail.text.toString(), binding.etPassword.text.toString())
            }
        }

        // Al pulsar sobre el campo muestra la contraseña
        /*
        binding.etPassword.setOnClickListener {
            if(binding.etPassword.transformationMethod == PasswordTransformationMethod.getInstance()){
                binding.etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else{
                binding.etPassword.transformationMethod = PasswordTransformationMethod.getInstance()

            }
        }
         */

    }

    // Funcion para recibir email y password
    private fun createAccount(email: String, pass: String) {
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
            if (it.isSuccessful){
                requireActivity().toast("Cuenta creada correctamente")
                var idDoc = auth.currentUser?.uid.toString()
                db.collection("users")
                    .document(idDoc).set(
                        mapOf(
                            "idDoc" to idDoc,
                            "nombre" to binding.etName.text.toString(),
                            "email" to binding.etEmail.text.toString(),
                            "frases" to mutableListOf<String>(
                                "Tienes el control de tu vida, nadie puede decirte lo contrario.",
                                "Crea tiempo para hacer las cosas que no quieres hacer, te lo agradecerás después.",
                                "Sanar toma tiempo, y pedir ayuda es un paso que requiere mucho valor.",
                                "Decir que no está bien. Si un amigo se enfada contigo por poner límites, él necesita replantearse lo que significa ser un amigo.",
                                "Todo lo que haces es increíble, todos somos humanos y todos somos diferentes. No tienes que tener miedo al cambio.",
                                "No necesitas a alguien que te complete. Sólo necesitas aceptarte completamente.",
                                "No puedes cambiar tu destino de la noche a la mañana, pero sí puedes cambiar tu dirección de la noche a la mañana.",
                                "Puede que te sientas ansioso al establecer y reforzar tus límites, pero esto no significa que no deba tenerlos.",
                                "El fracaso no tiene que detenerte de lograr tus sueños, sólo significa que no va a ser tan fácil como pensabas.",
                                "Esto no es un recordatorio, es una demanda; Deja de menospreciarte, deja de compararte con los demás y date un maldito descanso por una vez.",
                                "Los hábitos pequeños dan paso a grandes oportunidades.",
                                "Todo por lo que estás trabajando en este momento valdrá la pena. Sigue con ello!",
                                "No existe el momento \"adecuado\", solo el tiempo, y lo que haces con él.",
                                "No pierdas este momento buscando otro.",
                                "deja de llevar viejos sentimientos a nuevas experiencias.",
                                "Respira, solo es un día malo, no una mala vida.",
                                "Realmente no estás confundido acerca de qué hacer, solo necesitas el valor para hacerlo."
                            ),
                            "conversaciones" to mutableListOf<String>(
                                "Habla de la última película que vistes.",
                                "Habla de la última serie que has visto",
                                "Cuentale a la persona lo mejor de tu semana",
                                "Comparte una noticia graciosa",
                                "Pregunta qué ha sido lo mejor de su semana",
                                "Pregunta como le va el día",
                                "Pregunta como le va la semana",
                                "Pregunta que han hecho últimamente",
                                "Cuenta que has hecho últimamente",
                                "Comparte un vídeo gracioso",
                                "Comparte una imagen graciosa",
                                "Haz una pregunta de \" ¿Qué preferirías? \" ",
                                "Pregunta sobre algo de lo que solíais hablar",
                                "Pregunta que tal les va la vida",
                                "Si han subido algo a una red social, comenta",
                                "Cuenta un chiste",
                                "Menciona una historia del pasado",
                                "Pregunta como se sienten",
                                "Pregunta como se han sentido últimamente",
                                "Simplemente saluda :)"
                            )
                        )
                    ).addOnCompleteListener {
                        // Lleva a la home activity
                        startActivity(Intent (requireActivity(),HomeActivity::class.java))
                        // Finalizar el Main activity
                        requireActivity().finish()
                    }
            }else{
                requireActivity().toast("Hubo un problema creando la cuenta")
            }
        }
    }

}
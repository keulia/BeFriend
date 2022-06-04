package com.casoca.befriend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.casoca.befriend.databinding.FragmentHelpBinding
import com.casoca.befriend.databinding.FragmentSignUpBinding
import com.casoca.befriend.utilidades.QuestionAnswer
import com.casoca.befriend.utilidades.RecyclerContacts
import com.casoca.befriend.utilidades.RecyclerHelp

class HelpFragment : Fragment() {
    // Declaro el binding
    private var _binding: FragmentHelpBinding?=null
    private val binding get() = _binding!!
    private var questionAnswer = mutableListOf<QuestionAnswer>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHelpBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionAnswer.add(QuestionAnswer("Añadir un contacto nuevo","" +
                "Para añadir un contacto nuevo debes de ir a la lista de tus contactos y en la " +
                "parte superior pulsar sobre el botón para añadir personas."))

        questionAnswer.add(QuestionAnswer("Eliminar un contacto","" +
                "Para eliminar un contacto debes dirigirte a la opción de editar el contacto deseado " +
                "y pulsar sobre la papelera que se encuentra en la esquina superior derecha."))

        questionAnswer.add(QuestionAnswer("Editar información de un contacto","" +
                "Para editar la información de un contacto deberemos ir a la lista de contactos, " +
                "pulsar sobre el contacto que queramos editar y una vez dentro pulsaremos" +
                "sobre el botón de settings."))

        questionAnswer.add(QuestionAnswer("Notas de un contacto","" +
                "En las notas podremos escribir cualquier cosa que queramos sobre el contacto. Un " +
                "ejemplo de información que podemos añadir son los gustos de la persona, su nombre " +
                "completo, etc."))

        questionAnswer.add(QuestionAnswer("Temas de conversación","Esta opción " +
                "puede ser marcada a la hora de crear o editar un contacto. Con esta opción " +
                "marcada, te llegarán distintos temas para iniciar conversaciones con tus contactos." +
                " Además, si vas a settings podrás administrar los temas de conversación."))

        questionAnswer.add(QuestionAnswer("Frases motivadoras","Podremos administrar " +
                "las frases que aparecen en el menú principal de la app si nos dirigimos a " +
                "settings. Ahí encontraremos una categoría la cual se especializará en esta parte y donde" +
                " podremos administrar las frases."))

        questionAnswer.add(QuestionAnswer("Notificaciones","Cuando creamos un contacto" +
                " nuevo, añadimos una fecha, esta fecha es utilizada para las notificaciones " +
                "las cuales nos recordarán de escribir a nuestros contactos. Para editar la frecuencia " +
                "con la que nos llegan estas notificaciones tendremos que ir a editar el contacto en cuestión."))

        questionAnswer.add(QuestionAnswer("Eventos","Los eventos también son notificados " +
                "y aparecen en el mismo apartado donde aparecen todas las notificaciones. En los eventos " +
                "podremos añadir fechas importantes, quedadas, citas, etc. Estos eventos te serán notificados!"))

        questionAnswer.add(QuestionAnswer("Cumpleaños","Cuando creamos un contacto " +
                "añadimos la fecha de cumpleaños de este contacto. A partir de esta fecha se programa una " +
                "notificación la cual te llegará cuando sea el cumpleaños de algún contacto."))
        //  questionAnswer.add(QuestionAnswer("",""))

        binding.rvQuestion.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = RecyclerHelp(requireContext(), questionAnswer)
        }
    }

}
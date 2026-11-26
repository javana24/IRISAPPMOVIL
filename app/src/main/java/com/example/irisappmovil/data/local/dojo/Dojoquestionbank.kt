package com.example.irisappmovil.data.local.dojo

// Banco de preguntas del Dojo de Defensa.
// GENERADO a partir de la web (javana24/Iris: src/app/data/dojo-questions.ts): si cambias uno, cambia el otro.

data class DojoBankOption(val text: String, val correct: Boolean, val feedback: String)

data class DojoBankQuestion(val text: String, val options: List<DojoBankOption>)

object DojoQuestionBank {
    const val QUESTIONS_PER_ATTEMPT = 5
    const val PASS_SCORE = 70

    /** Preguntas para un intento: QUESTIONS_PER_ATTEMPT al azar, con las opciones barajadas. */
    fun pickForModule(moduleId: String): List<DojoBankQuestion> =
        (questionsByModule[moduleId] ?: questionsByModule.getValue("mod_01"))
            .shuffled()
            .take(QUESTIONS_PER_ATTEMPT)
            .map { it.copy(options = it.options.shuffled()) }

    private val questionsByModule: Map<String, List<DojoBankQuestion>> = mapOf(
        "mod_01" to listOf(
            DojoBankQuestion(
                "Tu pareja te escribe: \"Si de verdad me quisieras, no saldrías hoy con tus amigos\". ¿Qué está haciendo?",
                listOf(
                    DojoBankOption("Demostrar que le importas mucho", false, "Querer a alguien no implica decidir con quién puede estar."),
                    DojoBankOption("Chantaje emocional para aislarte", true, "Usa tu cariño como moneda de cambio para alejarte de tu entorno."),
                    DojoBankOption("Pedirte más tiempo juntos de forma sana", false, "Pedir tiempo juntos es sano; condicionar el amor a que no veas a nadie, no.")
                )
            ),
            DojoBankQuestion(
                "Le cuentas algo que te dolió y responde: \"Eso nunca pasó, te lo estás inventando, estás loca/o\". Si ocurre a menudo, se llama…",
                listOf(
                    DojoBankOption("Luz de gas (gaslighting)", true, "Te hace dudar de tu memoria y tu percepción para que dependas de su versión."),
                    DojoBankOption("Una discusión normal", false, "Discrepar es normal; negar sistemáticamente lo que vives para que dudes de ti, no."),
                    DojoBankOption("Sinceridad", false, "La sinceridad no busca que dudes de tu propia cabeza.")
                )
            ),
            DojoBankQuestion(
                "¿Cuál de estas situaciones es una señal de alerta?",
                listOf(
                    DojoBankOption("Te pregunta qué tal te ha ido el día", false, "Interesarse por ti es una muestra de cariño sana."),
                    DojoBankOption("Se enfada si no le contestas en cinco minutos", true, "Exigir disponibilidad inmediata es una forma de control."),
                    DojoBankOption("Te dice que echa de menos verte", false, "Expresar lo que siente sin exigirte nada es sano.")
                )
            ),
            DojoBankQuestion(
                "Al principio de la relación te llenaba de regalos y mensajes a todas horas; ahora alterna cariño intenso con frialdad y enfados. ¿Qué patrón es?",
                listOf(
                    DojoBankOption("Así es el amor de verdad", false, "El amor sano es estable; no te tiene en una montaña rusa."),
                    DojoBankOption("Refuerzo intermitente: te engancha con cariño y luego lo retira", true, "Ese vaivén crea dependencia y hace que intentes \"recuperar\" la parte buena."),
                    DojoBankOption("Una etapa normal que se pasa sola", false, "No suele pasarse solo; conviene hablarlo y pedir apoyo.")
                )
            ),
            DojoBankQuestion(
                "\"Los celos son una prueba de amor\". ¿Qué opinas?",
                listOf(
                    DojoBankOption("Es verdad: si no tiene celos, no te quiere", false, "Es un mito del amor romántico que justifica el control."),
                    DojoBankOption("Es falso: los celos hablan de inseguridad y posesión, no de amor", true, "Sentir celos puntuales es humano, pero usarlos para controlar no es amor."),
                    DojoBankOption("Depende de lo guapa o guapo que seas", false, "Nada justifica el control sobre otra persona.")
                )
            ),
            DojoBankQuestion(
                "Tu pareja critica a tus amistades a todas horas (\"son mala influencia\", \"te quieren separar de mí\"). Con el tiempo esto puede provocar…",
                listOf(
                    DojoBankOption("Que tengas un grupo de amigos más sano", false, "Quien decide tus amistades eres tú."),
                    DojoBankOption("Que te quedes sin red de apoyo y dependas solo de esa persona", true, "El aislamiento es una de las señales más claras de una relación de control."),
                    DojoBankOption("Que la relación sea más fuerte", false, "Una relación sana suma a tu vida social, no la reduce.")
                )
            ),
            DojoBankQuestion(
                "\"Te miro el móvil porque no tengo nada que ocultar y tú tampoco deberías\". ¿Es razonable?",
                listOf(
                    DojoBankOption("Sí, en pareja no debe haber secretos", false, "Confianza no es vigilancia. Tienes derecho a tu intimidad."),
                    DojoBankOption("No: revisar el móvil de otra persona es control y vulnera su intimidad", true, "Además, acceder sin permiso a los mensajes de otra persona puede ser delito."),
                    DojoBankOption("Solo si lo hacéis los dos", false, "Que sea recíproco no lo convierte en sano.")
                )
            ),
            DojoBankQuestion(
                "Hace bromas en público sobre tu cuerpo o tu forma de hablar y dice \"es que no aguantas nada\". ¿Qué es?",
                listOf(
                    DojoBankOption("Humor sin importancia", false, "Si te hace sentir mal y se repite, no es humor."),
                    DojoBankOption("Menosprecio disfrazado de broma", true, "Ridiculizarte va minando tu autoestima, y culparte por molestarte es otra forma de control."),
                    DojoBankOption("Confianza", false, "La confianza no se demuestra humillando.")
                )
            )
        ),
        "mod_02" to listOf(
            DojoBankQuestion(
                "Tu pareja te pide la contraseña de Instagram \"para estar más unidos\". ¿Qué opción protege mejor tus límites?",
                listOf(
                    DojoBankOption("Dársela para evitar una discusión", false, "Ceder para evitar conflicto suele traer más peticiones de control."),
                    DojoBankOption("Decir que no con calma: tus cuentas son personales", true, "Puedes querer a alguien y mantener tu privacidad digital."),
                    DojoBankOption("Darle una contraseña falsa", false, "Engañar no resuelve el problema de fondo: que intenta controlarte.")
                )
            ),
            DojoBankQuestion(
                "¿Qué es un límite personal?",
                listOf(
                    DojoBankOption("Una forma de castigar a la otra persona", false, "Un límite no busca castigar, sino cuidarte."),
                    DojoBankOption("Lo que aceptas y lo que no en tu relación, para cuidar tu bienestar", true, "Los límites te protegen y hacen la relación más sana."),
                    DojoBankOption("Algo que solo hay que poner si la relación va mal", false, "Los límites son necesarios en todas las relaciones, también en las que van bien.")
                )
            ),
            DojoBankQuestion(
                "Ya has dicho que no quieres compartir tu ubicación en tiempo real, pero tu pareja insiste cada día. ¿Qué haces?",
                listOf(
                    DojoBankOption("Acabar compartiéndola para que deje de insistir", false, "Cuando un \"no\" se convierte en \"sí\" a base de insistir, el límite deja de existir."),
                    DojoBankOption("Mantener el no y decir que la insistencia te hace sentir controlada/o", true, "Repetir el límite con calma es una buena estrategia."),
                    DojoBankOption("Dejar de hablarle sin explicar nada", false, "Puedes hacerlo si lo necesitas, pero expresar el límite te ayuda a ver cómo reacciona.")
                )
            ),
            DojoBankQuestion(
                "En una relación sana, si un día no te apetece hacer algo sexual que habíais hecho antes…",
                listOf(
                    DojoBankOption("Deberías hacerlo igualmente porque ya lo hicisteis", false, "El consentimiento se da cada vez y se puede retirar en cualquier momento."),
                    DojoBankOption("Puedes decir que no y tu pareja debe respetarlo sin enfadarse", true, "Solo sí es sí: sin un sí libre y claro no hay consentimiento."),
                    DojoBankOption("Tienes que darle una buena razón", false, "No necesitas justificarte para decir que no.")
                )
            ),
            DojoBankQuestion(
                "Tu pareja te pide una foto íntima y te dice que \"si me quieres, me la mandas\". ¿Qué es lo más acertado?",
                listOf(
                    DojoBankOption("Mandarla, es una prueba de confianza", false, "Presionar para conseguir imágenes íntimas es una forma de violencia."),
                    DojoBankOption("No mandarla: nadie que te respete te presiona con algo así", true, "Puedes decir que no. Y difundir imágenes íntimas sin permiso es delito."),
                    DojoBankOption("Mandarla pero sin que se te vea la cara", false, "El problema es la presión, no el tipo de foto.")
                )
            ),
            DojoBankQuestion(
                "\"No me gusta que vayas con esa ropa, mejor ponte otra cosa\". ¿Cómo lo valoras?",
                listOf(
                    DojoBankOption("Es su opinión, y además te está cuidando", false, "Dar una opinión es distinto de decidir cómo vistes."),
                    DojoBankOption("Es una forma de control sobre tu cuerpo y tus decisiones", true, "Tu ropa la eliges tú. Si se convierte en norma o en enfado, es control."),
                    DojoBankOption("Es normal en las parejas", false, "Que sea frecuente no lo convierte en sano.")
                )
            ),
            DojoBankQuestion(
                "Pones un límite y tu pareja se enfada muchísimo. ¿Qué te dice eso?",
                listOf(
                    DojoBankOption("Que el límite estaba mal y debes quitarlo", false, "Que alguien se enfade no hace injusto tu límite."),
                    DojoBankOption("Que conviene fijarse en cómo respeta (o no) tus decisiones", true, "Cómo reacciona alguien ante un \"no\" dice mucho de la relación."),
                    DojoBankOption("Que hay que pedir perdón enseguida", false, "No tienes que pedir perdón por cuidarte.")
                )
            ),
            DojoBankQuestion(
                "¿Cuál de estas frases es un límite claro?",
                listOf(
                    DojoBankOption("\"Bueno, haz lo que quieras…\"", false, "Es una respuesta pasiva: no dice qué necesitas."),
                    DojoBankOption("\"No quiero que leas mis mensajes. Si lo vuelves a hacer, me lo voy a replantear.\"", true, "Dice lo que no aceptas y qué harás si se repite."),
                    DojoBankOption("\"Eres un pesado/a, siempre igual\"", false, "Atacar no comunica tu límite y suele empeorar la discusión.")
                )
            )
        ),
        "mod_03" to listOf(
            DojoBankQuestion(
                "¿Qué significa comunicarse de forma asertiva?",
                listOf(
                    DojoBankOption("Decir lo que piensas y sientes con respeto, sin agredir ni callarte", true, "La asertividad está entre la pasividad y la agresividad."),
                    DojoBankOption("Ganar siempre la discusión", false, "No se trata de ganar, sino de expresarte y respetar."),
                    DojoBankOption("Evitar los conflictos a toda costa", false, "Evitar siempre el conflicto suele significar callar lo que necesitas.")
                )
            ),
            DojoBankQuestion(
                "Tu pareja te reprocha: \"Siempre estás con tus amigas, pasas de mí\". ¿Qué respuesta es asertiva?",
                listOf(
                    DojoBankOption("\"Vale, perdona, no volveré a quedar con ellas\"", false, "Respuesta pasiva: renuncias a algo importante para ti."),
                    DojoBankOption("\"Me importa estar contigo y también ver a mis amigas. Podemos buscar planes juntos, pero no voy a dejar de verlas.\"", true, "Validas lo que siente sin renunciar a tu vida."),
                    DojoBankOption("\"Pues si no te gusta, ahí tienes la puerta\"", false, "Respuesta agresiva: escala el conflicto.")
                )
            ),
            DojoBankQuestion(
                "¿Cuál es un mensaje \"yo\" (que habla de cómo te sientes sin atacar)?",
                listOf(
                    DojoBankOption("\"Tú siempre me agobias\"", false, "Empieza culpando a la otra persona."),
                    DojoBankOption("\"Me siento agobiada/o cuando me escribes tantas veces seguidas; necesito mi espacio\"", true, "Describe la situación, cómo te sientes y lo que necesitas."),
                    DojoBankOption("\"Nadie aguanta lo pesado que eres\"", false, "Es un ataque, no comunica lo que necesitas.")
                )
            ),
            DojoBankQuestion(
                "Ante un \"Si me dejas, no sé lo que hago\", ¿qué es lo más recomendable?",
                listOf(
                    DojoBankOption("Quedarte en la relación para que no le pase nada", false, "No eres responsable de las decisiones de otra persona. Es chantaje emocional."),
                    DojoBankOption("Mantener tu decisión y, si teme por su vida, avisar a alguien adulto o al 112", true, "Tomarlo en serio no significa quedarte. Pedir ayuda protege a ambos."),
                    DojoBankOption("Ignorarlo por completo", false, "Si hay riesgo real, conviene avisar a una persona adulta o al 112.")
                )
            ),
            DojoBankQuestion(
                "Una amiga te cuenta que su novio le revisa el móvil y le prohíbe salir. ¿Qué es lo mejor que puedes hacer primero?",
                listOf(
                    DojoBankOption("Decirle que lo deje ya o dejas de hablarle", false, "Presionar puede hacer que se aleje justo de quien la apoya."),
                    DojoBankOption("Escucharla sin juzgar, decirle que no es culpa suya y que estás ahí", true, "Sentirse escuchada y no juzgada es el primer paso para que pida ayuda."),
                    DojoBankOption("Hablar tú con su novio para que pare", false, "Puede ponerla en más riesgo. Mejor acompañarla y buscar ayuda adulta.")
                )
            ),
            DojoBankQuestion(
                "Durante una discusión empiezas a sentirte muy nerviosa/o. ¿Qué es asertivo?",
                listOf(
                    DojoBankOption("Seguir hasta que alguien ceda", false, "Discutir en caliente rara vez resuelve nada."),
                    DojoBankOption("Decir \"Ahora mismo estoy muy alterada/o, prefiero hablarlo más tarde\"", true, "Pedir una pausa es una forma sana de cuidarte y cuidar la conversación."),
                    DojoBankOption("Bloquearle en todas las redes sin decir nada", false, "Puedes hacerlo si te sientes en peligro, pero en una discusión normal es mejor pedir una pausa.")
                )
            ),
            DojoBankQuestion(
                "¿Cuál de estas respuestas es PASIVA?",
                listOf(
                    DojoBankOption("\"No me parece bien y no lo voy a hacer\"", false, "Es asertiva: clara y respetuosa."),
                    DojoBankOption("\"Bueno… como tú digas, no pasa nada\"", true, "Callas lo que piensas para evitar el conflicto."),
                    DojoBankOption("\"Estás loco si crees que voy a hacer eso\"", false, "Es agresiva: ataca a la otra persona.")
                )
            ),
            DojoBankQuestion(
                "Tu pareja te hace sentir culpable: \"Después de todo lo que hago por ti…\". ¿Cómo responderías de forma asertiva?",
                listOf(
                    DojoBankOption("\"Tienes razón, soy lo peor\"", false, "Aceptas una culpa que no te corresponde."),
                    DojoBankOption("\"Valoro lo que haces, pero eso no significa que tenga que hacer lo que tú quieras\"", true, "Reconoces lo bueno sin aceptar el chantaje."),
                    DojoBankOption("\"Pues yo hago más que tú\"", false, "Entrar en competición alimenta el conflicto.")
                )
            )
        ),
        "mod_04" to listOf(
            DojoBankQuestion(
                "¿Qué caracteriza al teléfono 016?",
                listOf(
                    DojoBankOption("Es gratuito, funciona 24 h y no aparece en la factura", true, "Conviene borrarlo también del registro de llamadas del móvil. Hay WhatsApp: 600 000 016."),
                    DojoBankOption("Solo atiende a mujeres mayores de edad", false, "También atiende a menores, familiares y personas del entorno."),
                    DojoBankOption("Solo sirve para poner denuncias", false, "Informa y asesora; no hace falta querer denunciar para llamar.")
                )
            ),
            DojoBankQuestion(
                "Si estás en peligro inmediato, ¿a qué número debes llamar?",
                listOf(
                    DojoBankOption("016", false, "El 016 informa y asesora. En peligro inmediato: 112."),
                    DojoBankOption("112", true, "El 112 es el número de emergencias. También el 091 (Policía Nacional) o el 062 (Guardia Civil)."),
                    DojoBankOption("024", false, "El 024 es la línea de atención a la conducta suicida.")
                )
            ),
            DojoBankQuestion(
                "Alguien amenaza con difundir una foto íntima tuya si no le mandas más o le pagas (sextorsión). ¿Qué es lo correcto?",
                listOf(
                    DojoBankOption("Hacer lo que pide para que no la publique", false, "Ceder casi nunca para el chantaje; suele aumentar las exigencias."),
                    DojoBankOption("No ceder, guardar capturas como prueba, contárselo a un adulto y denunciar", true, "También puedes llamar al 017 (INCIBE, ciberseguridad) o al 016. No es culpa tuya."),
                    DojoBankOption("Borrar todo y no contárselo a nadie", false, "Las pruebas son importantes y no tienes que pasar por esto sola/o.")
                )
            ),
            DojoBankQuestion(
                "Sospechas que tu ex controla tu ubicación o tus cuentas. ¿Qué medida ayuda?",
                listOf(
                    DojoBankOption("Cambiar contraseñas, activar la verificación en dos pasos y revisar qué apps comparten tu ubicación", true, "Revisa también las sesiones abiertas y los dispositivos vinculados."),
                    DojoBankOption("Publicar en redes que lo sabes", false, "Puede ponerte en más riesgo. Mejor protegerte y pedir ayuda."),
                    DojoBankOption("No hacer nada, no se puede evitar", false, "Sí se puede: hay medidas sencillas de seguridad digital.")
                )
            ),
            DojoBankQuestion(
                "¿Quién puede ser una persona de confianza a la que acudir en el instituto?",
                listOf(
                    DojoBankOption("Nadie, es mejor resolverlo sola/o", false, "Pedir ayuda no es debilidad: es una forma de protegerte."),
                    DojoBankOption("El departamento de orientación, tu tutor/a o un profesor/a de confianza", true, "Saben cómo activar los protocolos y recursos adecuados."),
                    DojoBankOption("Solo la policía", false, "La policía es un recurso, pero en el instituto también hay personas preparadas para ayudarte.")
                )
            ),
            DojoBankQuestion(
                "Una amiga está viviendo violencia en su relación y te pide que no se lo cuentes a nadie. Pero cada vez está peor y tienes miedo por ella. ¿Qué haces?",
                listOf(
                    DojoBankOption("Guardar el secreto pase lo que pase", false, "Cuando hay riesgo, su seguridad está por delante del secreto."),
                    DojoBankOption("Decirle que te preocupa y buscar juntas/os ayuda adulta (orientación, familia, 016)", true, "Puedes llamar tú al 016 para pedir consejo sobre cómo ayudarla."),
                    DojoBankOption("Publicarlo en redes para que todo el mundo lo sepa", false, "Exponerla puede hacerle daño y ponerla en más peligro.")
                )
            ),
            DojoBankQuestion(
                "Si alguien sufre violencia de género, ¿necesita denunciar para recibir ayuda?",
                listOf(
                    DojoBankOption("Sí, sin denuncia no hay ninguna ayuda", false, "Hay atención, asesoramiento y apoyo psicológico sin necesidad de denunciar."),
                    DojoBankOption("No: puede recibir información, apoyo y atención aunque no denuncie", true, "En Andalucía también puede acudir al Instituto Andaluz de la Mujer (900 200 999)."),
                    DojoBankOption("Solo si tiene pruebas físicas", false, "La violencia psicológica y digital también es violencia.")
                )
            ),
            DojoBankQuestion(
                "¿Qué es un \"plan de seguridad\"?",
                listOf(
                    DojoBankOption("Una lista de pasos para protegerte si te sientes en peligro (a quién llamar, dónde ir, qué llevar)", true, "Prepararlo con ayuda profesional (016, orientación) te da más control en un momento difícil."),
                    DojoBankOption("Una app para espiar a tu pareja", false, "Espiar no es protegerse y puede ser ilegal."),
                    DojoBankOption("Algo que solo hace la policía", false, "Puedes prepararlo tú con apoyo de profesionales.")
                )
            )
        )
    )
}

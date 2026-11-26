// Genera Dojoquestionbank.kt (app Android) a partir de dojo-questions.ts (web).
// Uso (desde la carpeta que contiene Iris/ e IRISAPPMOVIL/):
//   node IRISAPPMOVIL/scripts/gen-dojo-kt.cjs Iris/src/app/data/dojo-questions.ts IRISAPPMOVIL/app/src/main/java/com/example/irisappmovil/data/local/dojo/Dojoquestionbank.kt
const fs = require('fs');
const [src, dest] = process.argv.slice(2);

const ts = fs.readFileSync(src, 'utf8');
const start = ts.indexOf('export const DOJO_QUESTIONS');
const objStart = ts.indexOf('{', ts.indexOf('=', start));
const objEnd = ts.indexOf('\n};', objStart);
const data = eval('(' + ts.slice(objStart, objEnd + 2) + ')');

const BS = String.fromCharCode(92);
const esc = (s) => '"' + s.split(BS).join(BS + BS).split('"').join(BS + '"').split('$').join(BS + '$') + '"';
const modules = { white: 'mod_01', yellow: 'mod_02', purple: 'mod_03', black: 'mod_04' };

let out = `package com.example.irisappmovil.data.local.dojo

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
`;

const blocks = Object.entries(data).map(([belt, questions]) => {
  const items = questions
    .map(
      (q) => `            DojoBankQuestion(
                ${esc(q.text)},
                listOf(
${q.options.map((o) => `                    DojoBankOption(${esc(o.text)}, ${o.correct}, ${esc(o.feedback)})`).join(',\n')}
                )
            )`
    )
    .join(',\n');
  return `        ${esc(modules[belt])} to listOf(\n${items}\n        )`;
});

out += blocks.join(',\n') + '\n    )\n}\n';
fs.writeFileSync(dest, out);
console.log(Object.entries(data).map(([b, q]) => `${b}: ${q.length}`).join(', '));

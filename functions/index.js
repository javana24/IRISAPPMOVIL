const functions = require("firebase-functions");
const admin = require("firebase-admin");
const crypto = require("crypto");
const nodemailer = require("nodemailer");

admin.initializeApp();

function normalizarCorreo(correo) {
  return String(correo || "").trim().toLowerCase();
}

function idDocumentoCodigo(correo) {
  return crypto.createHash("sha256").update(normalizarCorreo(correo)).digest("hex");
}

/**
 * Región: europe-west1 (debe coincidir con FirebaseModule en la app).
 *
 * SMTP (Gmail: usar contraseña de aplicación, no la contraseña normal):
 *   firebase functions:config:set smtp.user="tu@gmail.com" smtp.pass="xxxx"
 *   firebase deploy --only functions
 *
 * Si el correo no llega: revisa logs de la función en consola Firebase,
 * carpeta spam, y que el proyecto tenga facturación si aplica.
 */
exports.enviarCodigoVerificacionCorreo = functions
  .region("europe-west1")
  .https.onCall(async (data) => {
    const correo = normalizarCorreo(data.correo_electronico);
    const tipo = data.tipo;
    if (!correo || !tipo) {
      throw new functions.https.HttpsError("invalid-argument", "Datos incompletos.");
    }
    if (tipo === "inicio_sesion") {
      try {
        await admin.auth().getUserByEmail(correo);
      } catch (e) {
        throw new functions.https.HttpsError("not-found", "No hay cuenta con ese correo.");
      }
    }
    const cfg = functions.config().smtp;
    if (!cfg || !cfg.user || !cfg.pass) {
      throw new functions.https.HttpsError(
        "failed-precondition",
        "SMTP no configurado. Ejecuta: firebase functions:config:set smtp.user=\"...\" smtp.pass=\"...\""
      );
    }
    const codigo = String(Math.floor(100000 + Math.random() * 900000));
    const hash = crypto.createHash("sha256").update(codigo).digest("hex");
    const ref = admin.firestore().collection("codigos_verificacion_correo").doc(idDocumentoCodigo(correo));
    const transporter = nodemailer.createTransport({
      service: "gmail",
      auth: { user: cfg.user, pass: cfg.pass },
    });
    try {
      await transporter.sendMail({
        from: `"IRIS" <${cfg.user}>`,
        to: correo,
        subject: "Código de verificación IRIS",
        text: `Tu código de verificación es: ${codigo}\n\nCaduca en 15 minutos.`,
      });
    } catch (err) {
      console.error("enviarCodigoVerificacionCorreo sendMail error", err);
      throw new functions.https.HttpsError(
        "internal",
        `No se pudo enviar el correo (${err && err.message ? err.message : "SMTP"}). ` +
          "Comprueba contraseña de aplicación de Gmail y límites de envío."
      );
    }
    await ref.set({
      hash_codigo: hash,
      expira_en: admin.firestore.Timestamp.fromMillis(Date.now() + 15 * 60 * 1000),
      correo_electronico: correo,
      tipo: tipo,
      creado_en: admin.firestore.FieldValue.serverTimestamp(),
    });
    return { ok: true };
  });

exports.verificarCodigoVerificacionCorreo = functions
  .region("europe-west1")
  .https.onCall(async (data) => {
    const correo = normalizarCorreo(data.correo_electronico);
    const codigo = String(data.codigo || "").trim();
    if (!correo || codigo.length !== 6) {
      throw new functions.https.HttpsError("invalid-argument", "Código inválido.");
    }
    const ref = admin.firestore().collection("codigos_verificacion_correo").doc(idDocumentoCodigo(correo));
    const snap = await ref.get();
    if (!snap.exists) {
      throw new functions.https.HttpsError("not-found", "Sin código activo.");
    }
    const d = snap.data();
    const exp = d.expira_en.toMillis();
    if (Date.now() > exp) {
      await ref.delete();
      throw new functions.https.HttpsError("deadline-exceeded", "Código caducado.");
    }
    const hash = crypto.createHash("sha256").update(codigo).digest("hex");
    if (hash !== d.hash_codigo) {
      throw new functions.https.HttpsError("permission-denied", "Código incorrecto.");
    }
    await ref.delete();
    await admin.firestore().collection("verificaciones_correo_exitosas").doc(idDocumentoCodigo(correo)).set({
      verificado_en: admin.firestore.FieldValue.serverTimestamp(),
      correo_electronico: correo,
      tipo: d.tipo,
    });
    return { ok: true };
  });

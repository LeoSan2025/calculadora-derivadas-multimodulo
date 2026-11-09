// ========== VARIABLES GLOBALES ==========
let historial = [];
const historialKey = "historialDerivadas";

// ========== FUNCIONES DE ESCRITURA ==========
const escribir = (caracter) => {
  const textarea = document.getElementById("calculadora__IO--textarea");
  textarea.value += caracter;
};

const delate = () => {
  document.getElementById("calculadora__IO--textarea").value = "";
};

// ========== FUNCIONES ARITMÉTICAS ==========
const suma = (cadena) => {
  const sumandos = cadena.split("+").map((s) => s.trim());
  let resultado = 0;
  for (let s of sumandos) {
    resultado += parseFloat(s);
  }
  document.getElementById("calculadora__IO--textarea").value = resultado;
};

const resta = (cadena) => {
  const partes = cadena.split("-").map((s) => s.trim());
  if (partes.length === 0) return;
  let resultado = parseFloat(partes[0]);
  for (let i = 1; i < partes.length; i++) {
    resultado -= parseFloat(partes[i]);
  }
  document.getElementById("calculadora__IO--textarea").value = resultado;
};

const multiplicacion = (cadena) => {
  const factores = cadena.split("×").map((s) => s.trim());
  let resultado = 1;
  for (let f of factores) {
    resultado *= parseFloat(f);
  }
  document.getElementById("calculadora__IO--textarea").value = resultado;
};

const division = (cadena) => {
  const partes = cadena.split("÷").map((s) => s.trim());
  if (partes.length < 2) {
    alert("Se necesitan al menos dos números para dividir");
    return;
  }
  let resultado = parseFloat(partes[0]);
  for (let i = 1; i < partes.length; i++) {
    const divisor = parseFloat(partes[i]);
    if (divisor === 0) {
      alert("No se puede dividir por cero");
      return;
    }
    resultado /= divisor;
  }
  document.getElementById("calculadora__IO--textarea").value = resultado;
};

// ========== FUNCIÓN PRINCIPAL DE CÁLCULO ==========
function calcular() {
  const textarea = document.getElementById("calculadora__IO--textarea");
  const cadena = textarea.value.trim();

  if (!cadena) return;

  if (cadena.includes("+")) {
    suma(cadena);
  } else if (cadena.includes("×")) {
    multiplicacion(cadena);
  } else if (cadena.includes("÷")) {
    division(cadena);
  } else if (cadena.includes("-")) {
    resta(cadena);
  }
}

// ========== CONEXIÓN CON BACKEND JAVA ==========
async function enviarADerivar() {
  const textarea = document.getElementById("calculadora__IO--textarea");
  const funcion = textarea.value.trim();

  if (!funcion) {
    textarea.value = "❌ Escribe una función para derivar";
    return;
  }

  textarea.value = "⏳ Derivando...";

  try {
    const response = await fetch("http://localhost:8080/derivar", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ funcion: funcion }),
    });

    const data = await response.json();

    if (data.exito) {
      textarea.value = `f(x) = ${funcion}\nf'(x) = ${data.derivada}`;
      agregarAlHistorial(funcion, data.derivada);
    } else {
      textarea.value = `❌ Error: ${data.error}`;
    }
  } catch (error) {
    textarea.value = `❌ Error de conexión: Asegúrate que el servidor Java corre en el puerto 8080`;
    console.error("Error:", error);
  }
}

// ========== HISTORIAL ==========
function agregarAlHistorial(funcion, derivada) {
  historial.push({
    funcion: funcion,
    derivada: derivada,
    fecha: new Date().toLocaleTimeString(),
  });

  guardarHistorial();
  actualizarHistorialUI();
}

function actualizarHistorialUI() {
  const lista = document.querySelector(".aside__historial--ul");
  if (lista) {
    const items = historial.slice(-10).reverse();
    lista.innerHTML = items
      .map(
        (item) =>
          `<li class="aside__historial--item">
                <strong>${item.funcion}</strong><br>
                → ${item.derivada}
                <br><small>${item.fecha}</small>
            </li>`,
      )
      .join("");
  }
}

function guardarHistorial() {
  try {
    localStorage.setItem(historialKey, JSON.stringify(historial));
  } catch (e) {
    console.warn("No se pudo guardar historial:", e);
  }
}

function cargarHistorial() {
  try {
    const guardado = localStorage.getItem(historialKey);
    if (guardado) {
      historial = JSON.parse(guardado);
      actualizarHistorialUI();
    }
  } catch (e) {
    console.warn("No se pudo cargar historial:", e);
  }
}

// ========== INICIALIZACIÓN ==========
document.addEventListener("DOMContentLoaded", () => {
  cargarHistorial();

  // Detectar Ctrl+Enter para derivar
  const textarea = document.getElementById("calculadora__IO--textarea");
  textarea.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && e.ctrlKey) {
      e.preventDefault();
      enviarADerivar();
    }
  });
});

// ========== FUNCIONES DE UTILIDAD ==========
const abecedario = () => {
  // Para futura implementación de funciones trigonométricas
  console.log("Abecedario - Función trigonométrica");
};

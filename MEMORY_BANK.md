# Memory Bank — Calculadora de Derivadas Multimodal

> Documento de contexto persistente del proyecto. Actualizar tras cada bloque de cambios.

## Alcance
- **ACTUAL:** `core` y `fx`.
- **EXCLUIDO TEMPORALMENTE:** `web`.

## Metas por fase
- **Fase 1 (Feria):** App estable.
- **Fase 2:** Refactorización de AST y Parser.
- **Fase 3:** Módulo Web.

## Requerimientos
- Derivar polinomios.
- Regla del producto, cociente y cadena.
- Renderizar LaTeX.

## Arquitectura
- AST con patrón Composite en el core.
- Maven multimódulo.
- UI de `calculadora-fx` con FXML + Scene Builder + patrón Controlador (MVC), en migración desde la UI programática.

## Estado actual
- La app compila y corre con `javafx:run`.
- UI todavía programática; migración a FXML decidida, aún sin implementar (no hay `calculadora.fxml` ni controlador en el repo).
- El motor de derivación tiene bugs de simplificación.
- El parser falla con paréntesis.

## Problemas conocidos (residuales)
- **2.1** Fat-jar frágil → **Decisión:** usar `mvn javafx:run` para la feria.
- **2.2** Classifier `win` hardcodeado → **Decisión:** aceptar por ahora; anotar como deuda técnica.
- **2.3** Inconsistencia de versiones en `fx/pom.xml`.
- **2.4** Código de depuración (`System.out.println` y métodos `main` en nodos).
- Bugs de derivación: parser no consume todo el input; simplificación mezclada con derivación.

## Riesgos identificados
- El fat-jar falla en entornos ajenos.
- La regla de la cadena y de la potencia no están generalizadas.

## Decisiones técnicas
- Usar classpath en vez de module-path.
- Usar `Main.java` como wrapper.
- Migrar a JavaFX 24.
- UI de `calculadora-fx` con FXML + Scene Builder + patrón Controlador (abandona la UI programática).
- Fachada `ServicioDerivadas` en el core como única puerta de entrada para la vista (la vista no toca nodos ni parser).

## Compatibilidad y accesibilidad
- Solo Windows por el momento (classifier `win` hardcodeado).

## Distribución
- **Fase 1:** Ejecución vía IntelliJ / Maven.
- **Fase 2:** JLink / JPackage para crear ejecutable nativo.

## Deuda técnica
- Acoplamiento core → JavaFX en `LaTeXRendererFX.java`.
- Falta de inmutabilidad en los nodos.
- Lógica de simplificación acoplada.

## Reglas de trabajo
- La IA se encarga de tareas repetitivas y boilerplate.
- El humano aprueba las decisiones matemáticas.
- Hacer commits frecuentes.

## Historial de cambios
- Migración a JDK 24 y JavaFX 24 con classifier `win`.
- Corrección de `mainClass`.
- Decisión: migrar la UI de `calculadora-fx` a FXML + Scene Builder + patrón Controlador (MVC).

# 📐 Calculadora de Derivadas Multimodo

> Un proyecto personal y académico que fusiona el cálculo matemático con la arquitectura de software, construyendo un motor de derivación simbólica desde cero.

## 📖 Descripción

Este proyecto nació con el objetivo de aplicar los conocimientos adquiridos en la universidad (tanto de programación como de matemáticas) en una herramienta real. La **Calculadora de Derivadas Multimodo** no solo evalúa expresiones, sino que interpreta funciones, construye un Árbol de Sintaxis Abstracta (AST) y aplica las reglas de derivación de forma simbólica, mostrando el resultado renderizado en LaTeX.

## ✨ Características principales

- **Parser Robusto:** Soporte para multiplicación implícita (`3x`), paréntesis anidados (`((x^2))`), y signos negativos unarios (`-x^2`).
- **Manejo de Errores:** Validación de sintaxis que evita cierres inesperados de la aplicación ante entradas inválidas.
- **Motor Simbólico:** Aplicación de reglas de derivación (suma, resta, producto, cociente y potencia) mediante el patrón de diseño *Composite*.
- **Renderizado Visual:** Integración con `JLaTeXMath` para mostrar las funciones y sus derivadas con formato matemático de alta calidad.

## 🏗️ Arquitectura del Proyecto

El proyecto utiliza una arquitectura **Maven Multimódulo** para garantizar la separación de responsabilidades:

- 📦 **`calculadora-core`**: El cerebro. Contiene el AST, el parser recursivo y el motor de derivación. Es completamente independiente de la interfaz gráfica.
- 🖥️ **`calculadora-fx`**: Interfaz de escritorio desarrollada con **JavaFX**, **FXML** y **Scene Builder**, siguiendo el patrón de diseño Controlador (MVC).
- 🌐 **`calculadora-web`**: Módulo de servidor HTTP *(temporalmente excluido para priorizar la estabilidad de la versión de escritorio)*.

## 🛠️ Tecnologías utilizadas

- **Java 24** (JDK 24)
- **JavaFX 24.0.2** (con *classifiers* nativos para Windows)
- **Maven** (Gestión de dependencias y construcción)
- **JLaTeXMath** (Renderizado de fórmulas matemáticas)
- **IntelliJ IDEA** + **Scene Builder** (Entorno de desarrollo)

## 🚀 Cómo ejecutar el proyecto

### Requisitos previos
- Tener instalado el JDK 24.
- Tener Maven configurado (o usar el integrado en tu IDE).

### Pasos
1. Clona el repositorio:
   ```bash
   git clone https://github.com/LeoSan2025/calculadora-derivadas-multimodulo.git

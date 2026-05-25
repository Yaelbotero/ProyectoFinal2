# 🎨 ColorBlind Helper — Simulador de Daltonismo

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)
![SQLite](https://img.shields.io/badge/SQLite-Embedded-lightblue?style=for-the-badge&logo=sqlite)
![JUnit](https://img.shields.io/badge/JUnit5-Tests-green?style=for-the-badge&logo=junit5)

Aplicación JavaFX que simula los tres tipos principales de daltonismo 
aplicando transformaciones matemáticas de color sobre imágenes.

## 📋 Descripción
ColorBlind Helper permite visualizar cómo percibe el mundo una persona 
con Protanopía, Deuteranopía o Tritanopía, usando las matrices de 
simulación de Machado et al. (2009).

## ✨ Funcionalidades
- 🖼️ Cargar imágenes desde disco (PNG, JPG, BMP)
- 🌐 Cargar imágenes aleatorias desde la API de Picsum Photos
- 👁️ Simular los 3 tipos de daltonismo en tiempo real
- 💾 Guardar la imagen procesada en disco
- 📋 Historial de simulaciones almacenado en SQLite

## 🏗️ Arquitectura
- **Patrón:** MVC (Modelo–Vista–Controlador)
- **Jerarquía:** `ImageFilter` → `BaseFilter` → `ImageModel`
- **UI:** JavaFX con diseño oscuro

## 🛠️ Tecnologías
- Java 17 + JavaFX 21
- Maven
- SQLite (base de datos embebida)
- JUnit 5 + TestFX (pruebas)

## 🚀 Cómo ejecutar
1. Clonar el repositorio
2. Abrir con NetBeans 25 o cualquier IDE con soporte Maven
3. Ejecutar `Launcher.java`

> ⚠️ No requiere instalación de base de datos — SQLite se crea automáticamente en `~/.colorblind_helper/historial.db`

## 👥 Autores
- Yaelbotero
- SofiaHurtado
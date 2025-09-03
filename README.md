# Compilador Java - Prototipo con Interfaz Gráfica

## Descripción

Este proyecto es un **prototipo de compilador** desarrollado en Java con interfaz gráfica utilizando **Swing**. Está diseñado como un mini-IDE, inspirado en Eclipse, para facilitar la edición, análisis léxico y sintáctico, y ejecución de código.

Características principales:  

- Editor de código con **pestañas múltiples**, al estilo de un IDE moderno.  
- **Barra de menús** con opciones: Archivo, Editar, Análisis, Source, Buscar, Run y Help.  
- **Barra de herramientas** con botones de acceso rápido a las acciones más frecuentes.  
- Panel derecho dividido en:
  - Resultados del **Analizador Léxico**.
  - Estado de la pila del **Analizador Sintáctico**.  
- Manejo de archivos: crear, abrir, guardar y advertencias si se cierra un archivo sin guardar.  
- Funcionalidades de edición: copiar, pegar, deshacer y rehacer.  
- Posibilidad de ejecutar o depurar el código desde la interfaz.  

---

## Estructura del proyecto

```
Compilador/
│
├─ src/
│  ├─ Interfaz/           # Clases de la interfaz gráfica (Prototipo_Interfaz.java)
│  ├─ Controlador/        # Funciones de compilador, análisis léxico/sintáctico
│  └─ Iconos/             # Iconos usados en la barra de herramientas
│
├─ README.md
└─ .gitignore
```

---

## Requisitos

- Java 17 o superior.  
- IDE recomendado: Eclipse, IntelliJ IDEA o Visual Studio Code.  
- Las librerías utilizadas son **Swing**, incluidas en Java SE.  

---

## Cómo ejecutar

1. Clona el repositorio:

```bash
git clone https://github.com/tuusuario/Compilador.git
cd Compilador
```

2. Compila y ejecuta desde la terminal:

```bash
javac -d bin src/Interfaz/Prototipo_Interfaz.java
java -cp bin Interfaz.Prototipo_Interfaz
```

3. O abre el proyecto en tu IDE y ejecuta la clase `Prototipo_Interfaz.java`.

---

## Funcionalidades principales

- **Archivo**
  - Nuevo archivo (abrir en nueva pestaña)
  - Abrir archivo existente
  - Guardar archivo
  - Salir de la aplicación
- **Editar**
  - Copiar, pegar
  - Deshacer, rehacer
- **Análisis**
  - Ejecutar analizador léxico
  - Ejecutar analizador sintáctico
- **Source**
  - Formatear código
  - Comentar/Descomentar
- **Buscar**
  - Buscar y reemplazar en el código
- **Run**
  - Ejecutar programa
  - Depurar programa
- **Help**
  - Documentación
  - Acerca de

---

## Uso

- Las pestañas de código tienen un botón "x" para cerrarlas, con advertencia si el archivo no se ha guardado.  
- La barra de herramientas tiene accesos rápidos para **Nuevo, Abrir, Guardar, Ejecutar y Depurar**.  
- La barra de menús funciona de forma idéntica a los botones de la barra de herramientas.  

---

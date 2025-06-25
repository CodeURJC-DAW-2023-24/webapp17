# EL RINCÓN DEL SOFTWARE

## ÍNDICE

- [Tipos de Usuario](#-tipos-de-usuario)
- [Entidades](#-entidades)
- [Imágenes](#-imágenes)
- [Gráficos](#-gráficos)
- [Tecnologías complementarias](#-tecnologías-complementarias-y-algoritmos-avanzados)
- [Guía de despliegue](#-requisitos-previos)
- [Colaboradores](#-colaboradores)

### Descripción general

El rincón del software es una aplicación web que trata de servir como un espacio colaborativo donde los usuarios puedan compartir sus experencias, preguntar dudas o debatir sobre temas relacionados con el software. Es un blog con al propio uso. (Demo privada en youtube)
### 👤 Tipos de Usuario

La aplicación tendrá diferentes tipos de usuarios: no registrados, registrados y administradores.

Los usuarios no registrados podrán acceder al foro , ver los posts y enviar mensajes directamente a los administradores (desde la pestaña de contacto).

Los usuarios registrados, además de acceder al foro, podrán publicar contenido, comentar y gestionar sus propios post.

Los administradores podrán  banear usuarios, eliminar posts y emplear LLM para la autogeneración de post.
![Diagrama de navegación](https://raw.githubusercontent.com/CodeURJC-DAW-2023-24/webapp17/main/webapp17/src/main/resources/static/diagrams/navigation_diagram.png)


### 🔑 Entidades 

La aplicación tendrá diferentes tipos de entidades: usuarios, posts, comentarios y mensajes directos a administradores

![Diagrama de entidades](https://raw.githubusercontent.com/CodeURJC-DAW-2023-24/webapp17/main/webapp17/src/main/resources/static/diagrams/ER-BBDD-DIAGRAM.png)

### 🌄 Imágenes

La aplicación tendrá la opción de subir imágenes en los posts como parte del contenido. 

### 📊 Gráficos

La aplicación tendrá gráficos de tendencias sobre las temáticas con más publicaciones en un periódo de tiempo.

### 🔮 Tecnologías complementarias y algoritmos avanzados

La aplicación  dispondrá de un LLM local para que los administradores puedan emplearlo en la  autogeneración de posts. Además de un chatbot disponible spara todos los usuarios, el chatbot dispone de memoria conversacional.

### 🔧 Diagrama de clases
![Diagrama de clases](https://raw.githubusercontent.com/CodeURJC-DAW-2023-24/webapp17/main/webapp17/src/main/resources/static/diagrams/class_diagram.png)

### 🔧 Diagrama de clases APIRest
![Diagrama de clases APIRest](https://raw.githubusercontent.com/CodeURJC-DAW-2023-24/webapp17/main/webapp17/src/main/resources/static/diagrams/rest.png)

### 🔧 Diagrama de clases Angular
![Diagrama de clases Angular](https://raw.githubusercontent.com/CodeURJC-DAW-2023-24/webapp17/main/webapp17/src/main/resources/static/diagrams/angular.png)
## 🚀 Guía de Despliegue

### ✅ Requisitos Previos

- Docker & Docker Compose
- Git
- [Ollama](https://ollama.com)

---

### 🧠 Instalación y Configuración de Ollama

#### 1. Descargar e instalar Ollama

```bash
curl -fsSL https://ollama.com/install.sh | sh
```

> Si usas macOS con Homebrew:
> ```bash
> brew install ollama
> ```


#### 2. Descargar el modelo Qwen 0.6B

```bash
ollama pull qwen:0.6b
```

---

#### 3. Servir Ollama en red local (escuchar en todas las interfaces)

```bash
OLLAMA_HOST=0.0.0.0 ollama serve
```

---

### 🐳 Levantar la Aplicación con Docker Compose

```bash
docker compose up --build
```

Esto lanzará:

- 🌐 El backend de Spring Boot
- 💾 Una base de datos MySQL con volúmenes persistentes
- 🔍 El cliente que se conecta a Ollama vía HTTP (por ejemplo, para generación de texto o clasificación de publicaciones)
- 🖼 Almacenamiento local de imágenes y datos de usuario

---
## 📥 Descargar la imagen desde Docker Hub

Ejecuta el siguiente comando para descargar la imagen:

```bash
docker pull ggronda/webapp17
```
---

### 📦 Persistencia de Datos

La configuración de `docker-compose.yml` asegura que los siguientes datos se conserven:

- Base de datos MySQL → volumen en `/var/lib/mysql`

### 🌐 Demos en Youtube
- [Demo Fase 1 - Springboot App](https://youtu.be/cso8NQsyryM)
- [Demo Angular](https://youtu.be/KoPP3sRImKQ)

## 🛠️ Guía de Clonación de Repositorio y Despliegue posterior

### 📋 Requisitos previos

Antes de desplegar la aplicación, asegúrate de tener instalado en tu máquina:

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)

### 🚀 Pasos para el despliegue en servidor 

Una vez dentro de la máquina virtual asociada

1. **Clona el repositorio:**

   ```bash
   git clone git@github.com:CodeURJC-DAW-2023-24/webapp17.git
   ```

2. **Accede a la carpeta del proyecto:**

   ```bash
   cd webapp17/
   ```

3. **Construye y levanta los contenedores (solo la primera vez):**

   ```bash
   docker compose up --build
   ```

   > Este comando construirá las imágenes necesarias a partir de los `Dockerfile` y levantará los servicios definidos en `docker-compose.yml`.

4. **En posteriores ejecuciones, simplemente levanta los servicios:**

   ```bash
   docker compose up
   ```

   También puedes usar variantes como:

   ```bash
   docker compose up -d   # para ejecutarlo en segundo plano
   docker compose down    # para detener y eliminar los contenedores
   ```
- La aplicación estará disponible en `https://localhost:8443`
  

### 👥 Colaboradores

| Nombre | Email | Github |
| Jesús González Gironda | j.gironda.2019@alumnos.urjc.es | [ggronda](https://github.com/ggronda) |

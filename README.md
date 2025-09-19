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
En caso de usar Mac instalar Mac y no tener instalado Docker Desktop usamos colima como sustituto:
```bash
brew install colima
colima start -f
```
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

## 🛠️ Guía de Despliegue de la aplicación en una máquina virtual 

### ✅ Requisitos previos

- Tener descargada la clave privada proporcionada por los docentes (`appWeb22.key`)
- Conexión a la VPN de la Universidad (GlobalProtect) o uso del Escritorio de Desarrollo
- Tener Docker y Docker Compose instalados en la máquina virtual (ver más abajo)

---

### 🔐 Conexión a la máquina virtual

Conéctate a la máquina virtual mediante SSH con:

```bash
ssh -i ssh-keys/appWeb22.key vmuser@10.100.139.147
```

o también:

```bash
ssh -i ssh-keys/appWeb22.key vmuser@appWeb22.dawgis.etsii.urjc.es
```

> ⚠️ Si usas Windows, asegúrate de cambiar los permisos de la clave `.key`:
> ```bash
> chmod 400 ssh-keys/appWeb22.key
> ```

---

### 🐳 Instalación de Docker y Docker Compose en la VM

Ejecuta los siguientes comandos en la máquina virtual:

```bash
# Instalar dependencias
sudo apt-get update
sudo apt-get install -y ca-certificates curl gnupg

# Añadir clave GPG oficial de Docker
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo tee /etc/apt/keyrings/docker.asc > /dev/null
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Añadir repositorio de Docker
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "$VERSION_CODENAME") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Instalar Docker y Docker Compose
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

> 📝 Fuente oficial: [Docker Docs](https://docs.docker.com/engine/install/ubuntu/)

---

### 📥 Clonar el repositorio

Una vez dentro de la VM:

```bash
git clone https://github.com/CodeURJC-DAW-2023-24/webapp17.git
cd webapp17/
```

---

### 🚀 Despliegue con Docker Compose

Ejecuta:

```bash
sudo docker compose up --build -d
```

- El parámetro `-d` lo lanza en segundo plano.
- Si quieres ver los logs en tiempo real, puedes omitir `-d`.

---

### 🌐 Acceso a la aplicación

Una vez lanzada la aplicación, accede desde tu navegador a:

```
https://10.100.139.147:8443
```

---

### 🛑 Detener la aplicación

Para detener los contenedores, ejecuta:

```bash
sudo docker compose stop
```

---

### 🔁 Volver a arrancar la aplicación

Para volver a iniciar la aplicación tras haberla detenido:

```bash
sudo docker compose up -d
```
Para eliminar los docker por si hay conflictos por usar la misma BBDD
```bash
docker stop $(docker ps -a -q) && docker rm $(docker ps -a -q)
```
---

### ✅ Capacidades de IA generativa (opcional)

Revisar apartado de configuración de ollama anterior. 

```

  

### 👥 Colaboradores

| Nombre | Email | Github |
| Jesús González Gironda | j.gironda.2019@alumnos.urjc.es | [ggronda](https://github.com/ggronda) |

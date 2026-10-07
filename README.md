# HabitFlow

Aplicación móvil multiplataforma para gestionar hábitos, desarrollada como parte de un challenge técnico para una posición de Software Engineer Mobile.

HabitFlow permite crear, editar, completar y eliminar hábitos, además de filtrarlos según su estado.

El proyecto fue desarrollado utilizando **Kotlin Multiplatform y Compose Multiplatform**, compartiendo la UI y la lógica de negocio entre Android e iOS.

---

## Tecnologías utilizadas

#### Desarrollo multiplataforma
- Kotlin Multiplatform
- Compose Multiplatform
- Kotlin Coroutines
- Kotlin Serialization

#### Arquitectura
- MVVM
- Repository Pattern

#### Backend y datos
- Supabase (PostgreSQL)
- supabase-kt
- Ktor

#### Testing y CI/CD
- Kotlin Test
- GitHub Actions

#### Build y configuración
- Gradle
- BuildKonfig

---

## Funcionalidades

* Crear hábitos.
* Mostrar la lista de hábitos.
* Editar hábitos existentes.
* Eliminar hábitos.
* Marcar y desmarcar hábitos como completados.
* Filtrar hábitos:
  * Todos
  * Pendientes
  * Completados
  
* Validación de datos del formulario.
* Manejo de estados de carga y error.
* Persistencia de datos mediante Supabase.
* Interfaz responsive para diferentes tamaños de pantalla.
* Tema visual oscuro.
* Ícono personalizado de HabitFlow

---

## Arquitectura

Se eligió una arquitectura basada en **MVVM + Repository Pattern**.

La estructura principal es:

```text
UI
 ↓
HabitsViewModel
 ↓
HabitRepository
 ↓
SupabaseHabitRepository
 ↓
Supabase
```

Se eligió esta arquitectura para mantener separadas la interfaz, la lógica de presentación y el acceso a datos y de este modo mantener una solución simple, clara y fácil de mantener.

El `Repository` también permite utilizar un repositorio falso en los tests, evitando depender de Supabase durante las pruebas.

---

## Kotlin Multiplatform

El proyecto utiliza **Kotlin Multiplatform** para compartir código entre Android e iOS.

La mayor parte de la aplicación se encuentra en `commonMain`:

```text
shared/
└── src/
    └── commonMain/
        └── kotlin/
            └── com/example/habitflow/
                ├── App.kt
                ├── data/
                ├── model/
                └── ui/
```

La configuración incluye targets para:

* Android
* iOS físico (`iosArm64`)
* iOS Simulator (`iosSimulatorArm64`)

Para iOS se genera un framework compartido llamado `Shared`, que es consumido desde el proyecto iOS.

---

## Compose Multiplatform

La interfaz está desarrollada utilizando **Compose Multiplatform**, permitiendo compartir la UI declarativa entre Android e iOS.

Las principales pantallas se encuentran dentro de `commonMain`:

```text
ui/
├── HabitsViewModel.kt
├── HomeScreen.kt
└── CreateHabitScreen.kt
```

De esta forma, Android e iOS utilizan la misma implementación de UI y lógica compartida.

---

## Tests

Los tests del proyecto se pueden ejecutar mediante:

```powershell
.\gradlew.bat test
```

Las pruebas se enfocan principalmente en la lógica del `HabitsViewModel`, utilizando un `FakeHabitRepository` para evitar depender directamente de Supabase durante los tests.

---

## Uso de IA

Durante el desarrollo utilicé herramientas de Inteligencia Artificial como apoyo para acelerar el proceso.

Principalmente me ayudaron a:

* Analizar errores y problemas de compilación.
* Proponer y revisar implementaciones.
* Generar y revisar tests.
* Explorar alternativas de arquitectura.
* Acelerar tareas repetitivas de código y documentación.

La IA se utilizó como herramienta de asistencia. Las decisiones finales sobre arquitectura, comportamiento y alcance fueron revisadas y tomadas en función de los requisitos del challenge.

---

## Decisiones técnicas

### Kotlin Multiplatform

Se eligió Kotlin Multiplatform para compartir código entre Android e iOS, de acuerdo con el objetivo multiplataforma del proyecto.

### Compose Multiplatform

Se utilizó para construir la interfaz de usuario de forma declarativa y compartirla entre plataformas.

### MVVM + Repository Pattern

Se utilizó esta combinación para separar la UI de la lógica de presentación y del acceso a datos, manteniendo una estructura sencilla para el alcance del proyecto.

### Supabase

Se eligió como backend por su integración con PostgreSQL y porque permite resolver la persistencia requerida por el proyecto sin necesidad de desarrollar un backend propio. Además, cuenta con integración con Kotlin mediante supabase-kt.

---

## Estructura del proyecto

```text
HabitFlow
│
├── androidApp/
│   └── Aplicación Android
│
├── iosApp/
│   └── Aplicación iOS
│
├── shared/
│   └── src/
│       ├── commonMain/
│       │   └── Código compartido
│       └── commonTest/
│           └── Tests
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── APK/
│   └── HabitFlow.apk
│
└── screenshots/
```

---

## Base de datos

HabitFlow utiliza una base de datos PostgreSQL mediante Supabase.

La tabla principal es `habits`:

| Campo         | Tipo        | Descripción              |
| ------------- | ----------- | ------------------------ |
| `id`          | bigint      | Identificador del hábito |
| `title`       | text        | Título                   |
| `description` | text        | Descripción opcional     |
| `frequency`   | text        | Frecuencia               |
| `completed`   | boolean     | Estado del hábito        |
| `created_at`  | timestamptz | Fecha de creación        |

Las credenciales de Supabase se mantienen fuera del código fuente utilizando `local.properties` y variables de entorno.

> **Nota:** `local.properties` no se incluye en el repositorio ni en la entrega, ya que contiene configuración local y credenciales.

---

## Cómo ejecutar el proyecto

### Requisitos

* Android Studio o IntelliJ IDEA
* JDK 17
* Android SDK
* Proyecto de Supabase

Para desarrollar y ejecutar la versión iOS se requiere macOS con Xcode.

### Configuración

Crear un archivo `local.properties` en la raíz del proyecto:

```properties
SUPABASE_URL=tu_url_de_supabase
SUPABASE_PUBLISHABLE_KEY=tu_publishable_key
```

Estas propiedades son utilizadas por BuildKonfig para generar la configuración necesaria durante la compilación.

### Ejecutar tests

En Windows:

```powershell
.\gradlew.bat test
```

### Compilar APK

```powershell
.\gradlew.bat :androidApp:assembleDebug
```

El APK generado se encuentra en:

```text
androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

Para instalarlo directamente en un dispositivo Android conectado:

```powershell
.\gradlew.bat :androidApp:installDebug
```

---

## Integración continua

El proyecto cuenta con un workflow de GitHub Actions ubicado en:

```text
.github/workflows/ci.yml
```

El workflow ejecuta los tests automáticamente ante cambios en el repositorio y pull requests.

---

## Capturas

* Launcher Icon

* Pantalla principal con todos los hábitos.

* Creación de hábitos.

* Edición de hábitos.
* Eliminación de hábitos.
* Filtros.

---

## APK

El APK instalable incluido en la entrega se encuentra en:

```text
APK/HabitFlow.apk
```

---

## Conclusiones 

Este challenge fue una experiencia muy linda, ya que me permitió trabajar con tecnologías y conceptos relacionados con el desarrollo mobile multiplataforma.

Mi perfil está principalmente orientado al desarrollo web y backend, con algo de sazón en ciberseguridad, por lo que trabajar con **Kotlin Multiplatform** representó un desafío y, al mismo tiempo, una oportunidad para ampliar mis conocimientos.

Me gustó aprender nuevas herramientas, resolver problemas durante el desarrollo y llevar el proyecto desde su planificación hasta una aplicación funcional.

Considero que el challenge también me permitió fortalecer mi capacidad para investigar, tomar decisiones técnicas y adaptarme a nuevas tecnologías en un tiempo acotado.

---

**Jesica Salva Guitián**

Proyecto desarrollado como parte de un challenge técnico.

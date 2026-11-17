# Automatización de pruebas web con Selenium y Playwright (Java)

Proyecto de automatización de pruebas funcionales web con **Selenium WebDriver** y **Playwright**, escrito en **Java** con **JUnit 5** y **Maven**. Incluye **Page Object Model**, esperas explícitas, pruebas parametrizadas y validación de datos en una base de datos con **JDBC**.

## Tecnologías

| Herramienta | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje |
| Selenium WebDriver | 4.27 | Automatización del navegador |
| Playwright para Java | 1.64 | Alternativa moderna a Selenium (mismas pruebas migradas) |
| JUnit 5 | 5.11 | Ejecución de pruebas, aserciones y pruebas parametrizadas |
| Maven | 3 | Dependencias y ejecución |
| H2 + JDBC | 2.5 | Base de datos en memoria para validar datos con SQL |

## Qué cubre el proyecto

| Área | Qué se prueba | Técnicas |
|---|---|---|
| **Formularios** | Los 14 tipos de campo de un formulario HTML y los datos que realmente llegan al servidor | `By` (id, name, CSS, XPath), `Select`, `Keys`, subida de archivos, `JavascriptExecutor`, `assertAll` |
| **Esperas** | Elementos que aparecen con retraso, spinners, calculadora lenta, imágenes que cargan poco a poco | `WebDriverWait`, `ExpectedConditions`, condiciones propias con lambdas, `FluentWait` |
| **Diálogos** | `alert`, `confirm`, `prompt` y ventanas modales | `switchTo().alert()`, `alertIsPresent()` |
| **Login** | Inicio de sesión correcto e incorrecto con varias combinaciones de datos | **Page Object Model**, `@ParameterizedTest` + `@CsvSource` |
| **Base de datos** | Consultas, totales, actualizaciones y reglas de integridad de datos | JDBC, `PreparedStatement`, `JOIN`, `SUM`, `UPDATE` |
| **Login + BD** | Pruebas de interfaz alimentadas con usuarios leídos desde la base de datos | `@MethodSource` |
| **Migración** | Las mismas pruebas reescritas en Playwright para comparar ambas herramientas | `Locator`, `assertThat`, auto-espera |

## Estructura

```
src/test/
├── resources/bd/tienda.sql            ← datos de prueba (usuarios y pedidos)
└── java/cl/aprender/
    ├── BaseTest.java                  ← abre y cierra Chrome en cada prueba (Selenium)
    ├── formulario/                    ← formularios
    ├── esperas/                       ← esperas explícitas, implícitas y fluidas
    ├── dialogos/                      ← alert, confirm, prompt, modal
    ├── login/
    │   ├── selenium/                  ← Page Object + pruebas (Selenium)
    │   └── playwright/                ← el mismo Page Object en Playwright
    ├── playwright/                    ← pruebas migradas de Selenium a Playwright
    └── bd/                            ← validación de datos con JDBC
```

## Buenas prácticas aplicadas

- **Page Object Model:** los localizadores viven solo en las clases *Page*, y estas no contienen aserciones.
- **Sin `Thread.sleep` para sincronizar:** se usan esperas explícitas, para que las pruebas sean rápidas y estables.
- **Una prueba, un caso:** cada `@Test` verifica una sola cosa y es independiente de las demás (navegador nuevo en cada prueba).
- **Clases base** (`BaseTest`, `BasePlaywrightTest`) para no repetir la configuración del navegador.
- **Validación más allá de la pantalla:** se revisan los datos enviados al servidor y el estado de la base de datos.

## Hallazgos documentados

- El formulario de login de práctica envía la **contraseña en la URL** (método GET), un riesgo de seguridad porque queda en el historial y en los registros del servidor.
- Los campos deshabilitados de un formulario **no se envían** al servidor.

## Cómo ejecutarlo

Requisitos: **Java 17 o superior**, **Maven** y **Google Chrome**. Selenium Manager descarga ChromeDriver automáticamente y Playwright descarga sus propios navegadores la primera vez.

```bash
mvn test                                    # todas las pruebas (con 1 s de pausa para observar)
mvn test -Dpausa=0                          # a velocidad real
mvn test -Dtest=LoginTest                   # una clase
mvn test -Dtest="cl.aprender.playwright.*Test"   # solo las de Playwright
```

La propiedad `pausa` (en milisegundos) solo sirve para poder **observar** la ejecución; no se usa para esperar elementos.

## Sitios de práctica

Las pruebas usan páginas públicas creadas para practicar automatización:

- [Formulario de Selenium](https://www.selenium.dev/selenium/web/web-form.html)
- [Elementos dinámicos de Selenium](https://www.selenium.dev/selenium/web/dynamic.html)
- [Sitio de práctica de Boni García](https://bonigarcia.dev/selenium-webdriver-java/)

## Próximos pasos

- [ ] iframes, pestañas y acciones de ratón (`Actions`)
- [ ] Capturas de pantalla automáticas al fallar y ejecución headless
- [ ] Integración continua con GitHub Actions
- [ ] Pruebas de API con RestAssured
- [ ] Escenarios BDD con Cucumber

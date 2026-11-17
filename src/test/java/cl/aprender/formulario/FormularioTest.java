package cl.aprender.formulario;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.aprender.BaseTest;

// Pruebas del formulario de ejemplo de Selenium.
// Hereda de BaseTest: el navegador ya se abre y se cierra solo.
class FormularioTest extends BaseTest {

    private static final String URL = "https://www.selenium.dev/selenium/web/web-form.html";

    @Test
    void tituloDeLaPagina() {
        driver.get(URL);
        pausa();
        assertEquals("Web form", driver.getTitle());
    }

    @Test
    void enviarFormulario() {
        driver.get(URL);
        pausa();

        driver.findElement(By.name("my-text")).sendKeys("Hola Selenium");
        pausa();

        driver.findElement(By.cssSelector("button")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement mensaje = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Received!", mensaje.getText());
    }

    // Recorre TODOS los campos: revisa su estado inicial, interactúa con ellos,
    // comprueba el cambio y al final verifica qué datos se enviaron realmente.
    @Test
    void validarTodosLosCampos() throws IOException {
        driver.get(URL);
        pausa();

        // ---------- 1. Text input ----------
        WebElement texto = driver.findElement(By.id("my-text-id"));
        texto.sendKeys("yennis");
        texto.clear();
        assertEquals("", texto.getDomProperty("value"), "El texto debe empezar vacío");
        texto.sendKeys("Hola Selenium");
        assertEquals("Hola Selenium", texto.getDomProperty("value"));
        pausa();

        // ---------- 2. Password ----------
        WebElement password = driver.findElement(By.name("my-password"));
        assertEquals("password", password.getDomAttribute("type"), "Debe ocultar lo que se escribe");
        password.sendKeys("secreto123");
        assertEquals("secreto123", password.getDomProperty("value"));
        pausa();

        // ---------- 3. Textarea (varias líneas) ----------
        WebElement textarea = driver.findElement(By.name("my-textarea"));
        textarea.sendKeys("Línea 1" + Keys.ENTER + "Línea 2");
        assertEquals("Línea 1\nLínea 2", textarea.getDomProperty("value"));
        pausa();

        // ---------- 4. Disabled input: no se puede usar ----------
        WebElement deshabilitado = driver.findElement(By.name("my-disabled"));
        assertFalse(deshabilitado.isEnabled(), "El campo debe estar deshabilitado");
        assertEquals("Disabled input", deshabilitado.getDomAttribute("placeholder"));

        // ---------- 5. Readonly input: se ve pero no se edita ----------
        WebElement soloLectura = driver.findElement(By.name("my-readonly"));
        assertEquals("true", soloLectura.getDomProperty("readOnly"), "Debe ser de solo lectura");
        assertEquals("Readonly input", soloLectura.getDomProperty("value"));
        pausa();

        // ---------- 6. Dropdown (select) ----------
        Select desplegable = new Select(driver.findElement(By.name("my-select")));
        assertEquals("Open this select menu", desplegable.getFirstSelectedOption().getText());
        assertEquals(4, desplegable.getOptions().size(), "Debe tener 4 opciones");
        desplegable.selectByVisibleText("Three");
        assertEquals("3", desplegable.getFirstSelectedOption().getDomAttribute("value"));
        pausa();

        // ---------- 7. Dropdown (datalist): se escribe y sugiere opciones ----------
        WebElement datalist = driver.findElement(By.name("my-datalist"));
        assertEquals(5, driver.findElements(By.cssSelector("#my-options option")).size());
        datalist.sendKeys("Seattle");
        assertEquals("Seattle", datalist.getDomProperty("value"));
        pausa();

        // ---------- 8. File input: subir un archivo ----------
        // A un input de archivo se le "escribe" la ruta completa del archivo.
        Path archivo = Files.createTempFile("prueba-selenium", ".txt");
        Files.writeString(archivo, "contenido de prueba");
        archivo.toFile().deleteOnExit();
        WebElement inputArchivo = driver.findElement(By.name("my-file"));
        inputArchivo.sendKeys(archivo.toAbsolutePath().toString());
        assertTrue(inputArchivo.getDomProperty("value").endsWith(archivo.getFileName().toString()));
        pausa();

        // ---------- 9. Checkboxes: invertimos los dos ----------
        WebElement check1 = driver.findElement(By.id("my-check-1"));
        WebElement check2 = driver.findElement(By.id("my-check-2"));
        assertTrue(check1.isSelected(), "check 1 empieza marcado");
        assertFalse(check2.isSelected(), "check 2 empieza desmarcado");
        check1.click();
        check2.click();
        assertFalse(check1.isSelected());
        assertTrue(check2.isSelected());
        pausa();

        // ---------- 10. Radios: solo uno puede estar marcado ----------
        WebElement radio1 = driver.findElement(By.id("my-radio-1"));
        WebElement radio2 = driver.findElement(By.id("my-radio-2"));
        assertTrue(radio1.isSelected());
        radio2.click();
        assertTrue(radio2.isSelected());
        assertFalse(radio1.isSelected(), "Al marcar el radio 2, el 1 se desmarca solo");
        pausa();

        // ---------- 11. Color picker ----------
        // El selector de color abre una ventana nativa que Selenium no controla,
        // así que cambiamos el valor con JavaScript.
        WebElement color = driver.findElement(By.name("my-colors"));
        assertEquals("#563d7c", color.getDomProperty("value"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1];"
                        + "arguments[0].dispatchEvent(new Event('change'));",
                color, "#ff0000");
        assertEquals("#ff0000", color.getDomProperty("value"));
        pausa();

        // ---------- 12. Date picker ----------
        WebElement fecha = driver.findElement(By.name("my-date"));
        fecha.sendKeys("11/04/2026");
        fecha.sendKeys(Keys.TAB); // cierra el calendario emergente
        assertEquals("11/04/2026", fecha.getDomProperty("value"));
        pausa();

        // ---------- 13. Range (deslizador) ----------
        WebElement rango = driver.findElement(By.name("my-range"));
        assertEquals("5", rango.getDomProperty("value"));
        rango.sendKeys(Keys.ARROW_RIGHT, Keys.ARROW_RIGHT, Keys.ARROW_RIGHT);
        assertEquals("8", rango.getDomProperty("value"));
        pausa();

        // ---------- 14. Hidden input: existe pero no se ve ----------
        WebElement oculto = driver.findElement(By.name("my-hidden"));
        assertFalse(oculto.isDisplayed(), "El campo oculto no debe verse");

        // ---------- Enviar ----------
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement mensaje = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        assertEquals("Received!", mensaje.getText());
        pausa();

        // ---------- Verificar lo que llegó al servidor ----------
        // El formulario usa method="get", así que los datos viajan en la URL:
        //   submitted-form.html?my-text=Hola+Selenium&my-password=...
        Map<String, String> enviados = parametrosDeLaUrl(driver.getCurrentUrl());

        // assertAll revisa TODO y reporta todos los fallos juntos,
        // en vez de detenerse en el primero.
        assertAll("Datos enviados",
                () -> assertEquals("Hola Selenium", enviados.get("my-text")),
                () -> assertEquals("secreto123", enviados.get("my-password")),
                () -> assertEquals("Línea 1\r\nLínea 2", enviados.get("my-textarea")),
                () -> assertFalse(enviados.containsKey("my-disabled"),
                        "Los campos deshabilitados NO se envían"),
                () -> assertEquals("Readonly input", enviados.get("my-readonly")),
                () -> assertEquals("3", enviados.get("my-select")),
                () -> assertEquals("Seattle", enviados.get("my-datalist")),
                () -> assertEquals(archivo.getFileName().toString(), enviados.get("my-file")),
                () -> assertEquals("on", enviados.get("my-check")),
                () -> assertEquals("on", enviados.get("my-radio")),
                () -> assertEquals("#ff0000", enviados.get("my-colors")),
                () -> assertEquals("11/04/2026", enviados.get("my-date")),
                () -> assertEquals("8", enviados.get("my-range")),
                () -> assertEquals("", enviados.get("my-hidden")));
    }

    // Convierte "?a=1&b=Hola+Mundo" en un mapa {a=1, b=Hola Mundo}
    private static Map<String, String> parametrosDeLaUrl(String url) {
        Map<String, String> parametros = new HashMap<>();
        String query = URI.create(url).getRawQuery();
        if (query == null) {
            return parametros;
        }
        for (String par : query.split("&")) {
            String[] partes = par.split("=", 2);
            String clave = URLDecoder.decode(partes[0], StandardCharsets.UTF_8);
            String valor = partes.length > 1
                    ? URLDecoder.decode(partes[1], StandardCharsets.UTF_8) : "";
            parametros.put(clave, valor);
        }
        return parametros;
    }
}

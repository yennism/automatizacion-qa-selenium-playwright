package cl.aprender.dialogos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.aprender.BaseTest;

// Lección 3 (parte 1): ventanas de diálogo.
//
// Hay dos tipos MUY distintos:
//  - alert / confirm / prompt: ventanas del NAVEGADOR (no son HTML). No se pueden
//    buscar con findElement: se controlan con driver.switchTo().alert().
//  - modal: es HTML normal dibujado encima de la página. Se usa con findElement
//    como cualquier otro elemento.
class DialogosTest extends BaseTest {

    private static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/dialog-boxes.html";

    private WebDriverWait wait;

    // Se ejecuta antes de CADA prueba (después del @BeforeEach de BaseTest,
    // que abre el navegador). Así no repetimos estas dos líneas en todas.
    @BeforeEach
    void abrirPagina() {
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.get(URL);
        pausa();
    }

    // 1) ALERT: solo muestra un mensaje y tiene un botón "Aceptar".
    @Test
    void alerta() {
        driver.findElement(By.id("my-alert")).click();

        // Esperar a que aparezca y "cambiarse" a ella en un solo paso
        Alert alerta = wait.until(ExpectedConditions.alertIsPresent());
        pausa();

        assertEquals("Hello world!", alerta.getText());
        alerta.accept();   // pulsar "Aceptar"
    }

    // 2) CONFIRM aceptado: tiene "Aceptar" y "Cancelar". La página muestra qué elegiste.
    @Test
    void confirmacionAceptada() {
        driver.findElement(By.id("my-confirm")).click();

        Alert confirmacion = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Is this correct?", confirmacion.getText());
        pausa();
        confirmacion.accept();   // "Aceptar"

        assertEquals("You chose: true", driver.findElement(By.id("confirm-text")).getText());
        pausa();
    }

    // 3) CONFIRM cancelado: dismiss() pulsa "Cancelar".
    @Test
    void confirmacionCancelada() {
        driver.findElement(By.id("my-confirm")).click();

        Alert confirmacion = wait.until(ExpectedConditions.alertIsPresent());
        pausa();
        confirmacion.dismiss();  // "Cancelar"

        assertEquals("You chose: false", driver.findElement(By.id("confirm-text")).getText());
        pausa();
    }

    // 4) PROMPT: pide escribir un texto. sendKeys escribe dentro de la ventana.
    @Test
    void promptConTexto() {
        driver.findElement(By.id("my-prompt")).click();

        Alert prompt = wait.until(ExpectedConditions.alertIsPresent());
        assertEquals("Please enter your name", prompt.getText());
        prompt.sendKeys("Selenium");
        pausa();
        prompt.accept();

        assertEquals("You typed: Selenium", driver.findElement(By.id("prompt-text")).getText());
        pausa();
    }

    // 5) PROMPT cancelado: en JavaScript, cancelar un prompt devuelve null,
    //    y la página lo muestra tal cual. Un buen tester prueba también este caso.
    @Test
    void promptCancelado() {
        driver.findElement(By.id("my-prompt")).click();

        Alert prompt = wait.until(ExpectedConditions.alertIsPresent());
        pausa();
        prompt.dismiss();

        assertEquals("You typed: null", driver.findElement(By.id("prompt-text")).getText());
        pausa();
    }

    // 6) MODAL: es HTML, así que NO se usa switchTo().alert().
    //    Aparece con una animación: hay que esperar a que el botón sea clicable.
    //
    //    OJO: en el HTML el texto del botón está partido en dos líneas:
    //        <button ...>Save
    //                    changes</button>
    //    Por eso //button[text()='Save changes'] NO lo encuentra (tiene un salto
    //    de línea y espacios en medio). normalize-space() quita los espacios de
    //    sobra y deja "Save changes".
    @Test
    void modal() {
        driver.findElement(By.id("my-modal")).click();

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[normalize-space()='Save changes']"))).click();

        // También desaparece con animación: esperar a que se oculte
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("example-modal")));
        pausa();

        assertEquals("You chose: Save changes", driver.findElement(By.id("modal-text")).getText());
    }
}

package cl.aprender.esperas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.aprender.BaseTest;

// Lección 2 (parte 2): esperas en situaciones más reales.
class EsperasAvanzadasTest extends BaseTest {

    private static final String CALCULADORA =
            "https://bonigarcia.dev/selenium-webdriver-java/slow-calculator.html";
    private static final String IMAGENES =
            "https://bonigarcia.dev/selenium-webdriver-java/loading-images.html";

    // Pulsa una tecla de la calculadora por su texto (las teclas son <span>).
    private void pulsar(String tecla) {
        driver.findElement(By.xpath("//div[@class='keys']/span[text()='" + tecla + "']")).click();
        pausa();
    }

    // Pone cuántos segundos tarda la calculadora en dar el resultado.
    private void demoraCalculadora(int segundos) {
        WebElement demora = driver.findElement(By.id("delay"));
        demora.clear();
        demora.sendKeys(String.valueOf(segundos));
    }

    // 1) ESPERAR UN TEXTO: la calculadora tarda 3 s en mostrar el resultado.
    //    textToBe espera hasta que el elemento tenga EXACTAMENTE ese texto.
    @Test
    void esperarResultadoDeLaCalculadora() {
        driver.get(CALCULADORA);
        demoraCalculadora(3);
        pausa();

        pulsar("1");
        pulsar("+");
        pulsar("3");
        pulsar("=");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBe(By.className("screen"), "4"));

        assertEquals("4", driver.findElement(By.className("screen")).getText());
    }

    // 2) ESPERAR A QUE ALGO DESAPAREZCA: el "spinner" (ruedita de carga)
    //    se ve mientras calcula. Cuando desaparece, el resultado está listo.
    @Test
    void esperarQueDesaparezcaElSpinner() {
        driver.get(CALCULADORA);
        demoraCalculadora(3);

        pulsar("7");
        pulsar("x");
        pulsar("6");
        pulsar("=");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("spinner")));
        pausa();

        assertEquals("42", driver.findElement(By.className("screen")).getText());
    }

    // 3) TIEMPO AGOTADO: si la espera máxima es menor que lo que tarda la página,
    //    WebDriverWait lanza TimeoutException. Así se ve un fallo por espera corta.
    @Test
    void esperaDemasiadoCortaFalla() {
        driver.get(CALCULADORA);
        demoraCalculadora(3);

        pulsar("2");
        pulsar("+");
        pulsar("2");
        pulsar("=");

        WebDriverWait waitCorto = new WebDriverWait(driver, Duration.ofSeconds(1));
        assertThrows(TimeoutException.class,
                () -> waitCorto.until(ExpectedConditions.textToBe(By.className("screen"), "4")));
    }

    // EJERCICIO 3: 9 - 5 con demora de 2 s.
    // textToBePresentInElementLocated espera a que el texto del elemento CONTENGA
    // el texto indicado (textToBe exige que sea EXACTAMENTE igual).
    // Ojo: "contiene" puede engañar: si la pantalla mostrara "14", también contendría "4".
    // Por eso al final se comprueba el valor exacto con assertEquals.
    @Test
    void ejercicio3Resta() {
        driver.get(CALCULADORA);
        demoraCalculadora(2);
        pausa();

        pulsar("9");
        pulsar("-");
        pulsar("5");
        pulsar("=");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("screen"), "4"));
        pausa();

        assertEquals("4", driver.findElement(By.className("screen")).getText());
    }

    // 4) VARIOS ELEMENTOS: las 4 imágenes aparecen una cada 2 s (8 s en total).
    //    numberOfElementsToBe espera hasta que haya exactamente 4.
    @Test
    void esperarTodasLasImagenes() {
        driver.get(IMAGENES);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        List<WebElement> imagenes = wait.until(ExpectedConditions.numberOfElementsToBe(
                By.cssSelector("#image-container img"), 4));
        wait.until(ExpectedConditions.textToBe(By.id("text"), "Done!"));
        pausa();

        assertEquals("compass", imagenes.get(0).getDomAttribute("id"));
        assertEquals("landscape", imagenes.get(3).getDomAttribute("id"));
    }

    // 5) FLUENTWAIT: la versión configurable de WebDriverWait.
    //    - withTimeout:  espera máxima
    //    - pollingEvery: cada cuánto vuelve a comprobar (WebDriverWait usa 500 ms)
    //    - ignoring:     excepciones que NO cortan la espera (sigue reintentando)
    //    - withMessage:  texto que aparece si se agota el tiempo
    @Test
    void fluentWait() {
        driver.get(IMAGENES);

        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(15))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class)
                .withMessage("La imagen 'landscape' no apareció");

        // findElement lanza NoSuchElementException mientras no existe;
        // como está en ignoring, FluentWait simplemente vuelve a intentar.
        WebElement paisaje = wait.until(d -> d.findElement(By.id("landscape")));
        pausa();

        assertEquals("landscape", paisaje.getDomAttribute("alt"));
    }
}

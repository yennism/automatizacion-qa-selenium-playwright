package cl.aprender.esperas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import cl.aprender.BaseTest;

// Lección 2: esperas.
// En esta página los elementos aparecen 1 segundo DESPUÉS de pulsar el botón,
// como pasa en las webs reales (cargas, animaciones, peticiones al servidor).
class EsperasTest extends BaseTest {

    private static final String URL = "https://www.selenium.dev/selenium/web/dynamic.html";

    // 1) EL PROBLEMA: sin esperar, Selenium busca el elemento al instante,
    //    todavía no existe y la prueba revienta con NoSuchElementException.
    @Test
    void sinEsperaFalla() {
        driver.get(URL);
        pausa();

        driver.findElement(By.id("adder")).click();
        // Aquí NO hay pausa(): buscamos inmediatamente, como haría una prueba real.
        assertThrows(NoSuchElementException.class,
                () -> driver.findElement(By.id("box0")));
    }

    // 2) ESPERA EXPLÍCITA (la recomendada): espera HASTA que se cumpla una
    //    condición, como máximo 5 s. Si aparece en 1 s, sigue en 1 s.
    @Test
    void esperaExplicitaPresencia() {
        driver.get(URL);
        pausa();

        driver.findElement(By.id("adder")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement caja = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("box0")));
        pausa();

        assertEquals("redbox", caja.getDomAttribute("class"));
    }

    // 3) EXISTIR no es lo mismo que SER VISIBLE.
    //    El campo "revealed" está en el HTML desde el principio, pero oculto:
    //    findElement lo encuentra, pero escribir en él falla.
    @Test
    void presenteNoEsVisible() {
        driver.get(URL);
        pausa();

        WebElement campo = driver.findElement(By.id("revealed")); // existe...
        assertFalse(campo.isDisplayed());                          // ...pero no se ve
        assertThrows(ElementNotInteractableException.class,
                () -> campo.sendKeys("no puedo escribir"));

        driver.findElement(By.id("reveal")).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(campo));       // esperar a que se VEA
        campo.sendKeys("Ahora sí");
        pausa();

        assertEquals("Ahora sí", campo.getDomProperty("value"));
    }

    // 4) ESPERA IMPLÍCITA: se configura una vez y afecta a TODOS los findElement.
    //    Parece cómoda, pero tiene problemas (ver lección) y no se debe mezclar
    //    con las explícitas.
    @Test
    void esperaImplicita() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        driver.get(URL);
        pausa();

        driver.findElement(By.id("adder")).click();
        WebElement caja = driver.findElement(By.id("box0")); // reintenta hasta 5 s
        pausa();

        assertTrue(caja.isDisplayed());
    }

    // 5) CONDICIÓN PROPIA: cuando ninguna ExpectedConditions sirve, se escribe
    //    una lambda. Aquí: esperar a que haya exactamente 3 cajas.
    @Test
    void esperaConCondicionPropia() {
        driver.get(URL);
        pausa();

        WebElement boton = driver.findElement(By.id("adder"));
        boton.click();
        boton.click();
        boton.click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        // Ninguna ExpectedConditions sirve para "exactamente 3", así que se escribe
        // una lambda: se repite cada 0,5 s hasta que devuelva true (máximo 5 s).
        wait.until(d -> d.findElements(By.className("redbox")).size() == 3);
        pausa();

        assertEquals(3, driver.findElements(By.className("redbox")).size());
    }
}

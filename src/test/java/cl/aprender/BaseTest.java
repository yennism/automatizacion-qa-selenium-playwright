package cl.aprender;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

// Clase base: todas las clases de prueba heredan de aquí ("extends BaseTest")
// para no repetir la apertura/cierre del navegador en cada una.
public abstract class BaseTest {

    // Milisegundos de pausa entre pasos, para poder ver lo que hace el navegador.
    // Se cambia al ejecutar:  mvn test -Dpausa=0   (rápido)   o   -Dpausa=2000 (más lento)
    private static final long PAUSA_MS = Long.getLong("pausa", 1000);

    protected WebDriver driver;

    @BeforeEach
    void abrirNavegador() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterEach
    void cerrarNavegador() {
        pausa(); // deja ver el resultado final antes de cerrar
        driver.quit();
    }

    // Pausa SOLO para observar la ejecución mientras aprendes.
    // En pruebas reales no se usa: para esperar algo se usa WebDriverWait.
    protected void pausa() {
        if (PAUSA_MS <= 0) {
            return;
        }
        try {
            Thread.sleep(PAUSA_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

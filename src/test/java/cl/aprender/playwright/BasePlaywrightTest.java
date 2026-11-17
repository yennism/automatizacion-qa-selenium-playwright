package cl.aprender.playwright;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

// Equivalente de BaseTest (Selenium) para Playwright.
//
// SELENIUM                          PLAYWRIGHT
// new ChromeDriver() por prueba     Playwright + Browser: UNA vez para toda la clase (@BeforeAll)
//                                   BrowserContext: uno por prueba (@BeforeEach), es como
//                                   una ventana de incógnito nueva: sin cookies ni sesión
//                                   de la prueba anterior. Es mucho más rápido que abrir
//                                   un navegador entero cada vez.
// WebDriver driver                  Page page (la pestaña con la que trabajamos)
// driver.quit()                     context.close() / playwright.close()
public abstract class BasePlaywrightTest {

    // Misma propiedad que en Selenium:  mvn test -Dpausa=0  /  -Dpausa=2000
    private static final long PAUSA_MS = Long.getLong("pausa", 1000);

    private static Playwright playwright;
    private static Browser browser;

    private BrowserContext context;
    protected Page page;

    @BeforeAll
    static void lanzarNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                // Playwright va SIN ventana (headless) por defecto; aquí la mostramos.
                .setHeadless(false)
                // slowMo: espera PAUSA_MS antes de CADA acción. Reemplaza a todos
                // los pausa() que poníamos a mano en las pruebas de Selenium.
                .setSlowMo(PAUSA_MS));
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close(); // cierra también el navegador
    }

    @BeforeEach
    void nuevaPestana() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void cerrarPestana() {
        context.close();
    }
}

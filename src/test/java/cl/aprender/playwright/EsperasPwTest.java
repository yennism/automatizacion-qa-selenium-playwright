package cl.aprender.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;

// Migración de esperas/EsperasTest.java y EsperasAvanzadasTest.java.
//
// LA GRAN DIFERENCIA: Playwright espera SOLO (auto-waiting).
//  - Antes de click/fill/check espera a que el elemento exista, se vea,
//    esté habilitado y quieto (sin animación).
//  - assertThat(...) reintenta la comprobación hasta 5 s.
// Por eso casi todos los WebDriverWait / ExpectedConditions DESAPARECEN.
class EsperasPwTest extends BasePlaywrightTest {

    private static final String DINAMICA = "https://www.selenium.dev/selenium/web/dynamic.html";
    private static final String CALCULADORA =
            "https://bonigarcia.dev/selenium-webdriver-java/slow-calculator.html";
    private static final String IMAGENES =
            "https://bonigarcia.dev/selenium-webdriver-java/loading-images.html";

    // Selenium: click + WebDriverWait(presenceOfElementLocated(By.id("box0")))
    @Test
    void cajaQueApareceDespues() {
        page.navigate(DINAMICA);
        page.locator("#adder").click();

        assertThat(page.locator("#box0")).isVisible(); // espera sola hasta que aparezca
    }

    // Selenium: había que esperar visibilityOf(campo) antes de sendKeys,
    // si no: ElementNotInteractableException.
    @Test
    void campoOcultoQueSeMuestra() {
        page.navigate(DINAMICA);
        page.locator("#reveal").click();

        Locator campo = page.locator("#revealed");
        campo.fill("Ahora sí");          // fill espera solo a que se vea
        assertThat(campo).hasValue("Ahora sí");
    }

    // Selenium: wait.until(d -> d.findElements(By.className("redbox")).size() == 3)
    @Test
    void tresCajas() {
        page.navigate(DINAMICA);
        Locator boton = page.locator("#adder");
        boton.click();
        boton.click();
        boton.click();

        assertThat(page.locator(".redbox")).hasCount(3);
    }

    // Selenium: WebDriverWait(10 s) + textToBe(By.className("screen"), "4")
    // La calculadora tarda 3 s y slowMo suma más: subimos el tiempo máximo a 10 s.
    @Test
    void calculadoraLenta() {
        page.navigate(CALCULADORA);
        page.locator("#delay").fill("3");

        page.locator("//div[@class='keys']/span[text()='1']").click();
        page.locator("//div[@class='keys']/span[text()='+']").click();
        page.locator("//div[@class='keys']/span[text()='3']").click();
        page.locator("//div[@class='keys']/span[text()='=']").click();

        assertThat(page.locator(".screen")).hasText("4",
                new com.microsoft.playwright.assertions.LocatorAssertions.HasTextOptions()
                        .setTimeout(10_000));
    }

    // Selenium: numberOfElementsToBe(By.cssSelector("#image-container img"), 4)
    @Test
    void imagenesQueCarganPocoAPoco() {
        page.navigate(IMAGENES);

        Locator imagenes = page.locator("#image-container img");
        assertThat(imagenes).hasCount(4,
                new com.microsoft.playwright.assertions.LocatorAssertions.HasCountOptions()
                        .setTimeout(15_000));
        assertThat(page.locator("#text")).hasText("Done!");
        assertThat(imagenes.first()).hasId("compass");
    }
}

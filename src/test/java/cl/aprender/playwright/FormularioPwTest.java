package cl.aprender.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.SelectOption;

// Migración de formulario/FormularioTest.java (Selenium) a Playwright.
// Encima de cada línea está, comentado, el código Selenium original.
class FormularioPwTest extends BasePlaywrightTest {

    private static final String URL = "https://www.selenium.dev/selenium/web/web-form.html";

    @Test
    void tituloDeLaPagina() {
        // driver.get(URL);
        page.navigate(URL);

        // assertEquals("Web form", driver.getTitle());
        assertThat(page).hasTitle("Web form");
    }

    @Test
    void enviarFormulario() {
        page.navigate(URL);

        // driver.findElement(By.name("my-text")).sendKeys("Hola Selenium");
        page.locator("[name='my-text']").fill("Hola Selenium");

        // driver.findElement(By.cssSelector("button")).click();
        // Playwright recomienda buscar como lo haría un USUARIO: "el botón que dice Submit".
        page.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Page.GetByRoleOptions()
                .setName("Submit")).click();

        // WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        // WebElement mensaje = wait.until(
        //         ExpectedConditions.visibilityOfElementLocated(By.id("message")));
        // assertEquals("Received!", mensaje.getText());
        //
        // 4 líneas -> 1. assertThat de Playwright REINTENTA solo hasta 5 s:
        // no hace falta WebDriverWait.
        assertThat(page.locator("#message")).hasText("Received!");
    }

    // Unos cuantos campos de "validarTodosLosCampos", para ver las equivalencias.
    @Test
    void camposVarios() {
        page.navigate(URL);

        // Select: new Select(driver.findElement(By.name("my-select"))).selectByVisibleText("Two");
        Locator desplegable = page.locator("[name='my-select']");
        desplegable.selectOption(new SelectOption().setLabel("Two"));
        // assertEquals("2", ...getFirstSelectedOption().getDomAttribute("value"));
        assertThat(desplegable).hasValue("2");

        // Checkbox: if (!check.isSelected()) check.click();
        // check() solo marca si hace falta (nunca lo desmarca por error).
        Locator check2 = page.locator("#my-check-2");
        check2.check();
        assertThat(check2).isChecked();

        // Deshabilitado: assertFalse(campo.isEnabled());
        assertThat(page.locator("[name='my-disabled']")).isDisabled();

        // Oculto: assertFalse(oculto.isDisplayed());
        assertThat(page.locator("[name='my-hidden']")).isHidden();

        // Color: en Selenium necesitábamos JavaScript (executeScript).
        // fill() de Playwright sabe rellenar un input de color directamente.
        Locator color = page.locator("[name='my-colors']");
        color.fill("#ff0000");
        assertThat(color).hasValue("#ff0000");
    }
}

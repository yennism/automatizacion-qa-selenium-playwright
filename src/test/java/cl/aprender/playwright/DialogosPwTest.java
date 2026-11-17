package cl.aprender.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

// Migración de dialogos/DialogosTest.java.
//
// CAMBIO DE MENTALIDAD con alert/confirm/prompt:
//  Selenium:   1) click   2) esperar la alerta   3) switchTo().alert().accept()
//  Playwright: 1) DECIR ANTES qué hacer con la alerta (onceDialog)   2) click
//
// ¡Cuidado! Si no registras nada, Playwright CANCELA todas las alertas solo
// (dismiss). Es un error típico al migrar: la prueba "pasa" pero hizo otra cosa.
class DialogosPwTest extends BasePlaywrightTest {

    private static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/dialog-boxes.html";

    @BeforeEach
    void abrirPagina() {
        page.navigate(URL);
    }

    @Test
    void alerta() {
        // Guardamos el mensaje para comprobarlo después
        AtomicReference<String> mensaje = new AtomicReference<>();
        page.onceDialog(dialogo -> {
            mensaje.set(dialogo.message());   // alerta.getText()
            dialogo.accept();                 // alerta.accept()
        });

        page.locator("#my-alert").click();

        assertEquals("Hello world!", mensaje.get());
    }

    @Test
    void confirmacionAceptada() {
        page.onceDialog(dialogo -> dialogo.accept());
        page.locator("#my-confirm").click();

        assertThat(page.locator("#confirm-text")).hasText("You chose: true");
    }

    @Test
    void confirmacionCancelada() {
        page.onceDialog(dialogo -> dialogo.dismiss());
        page.locator("#my-confirm").click();

        assertThat(page.locator("#confirm-text")).hasText("You chose: false");
    }

    @Test
    void promptConTexto() {
        // Selenium: prompt.sendKeys("Selenium"); prompt.accept();
        page.onceDialog(dialogo -> dialogo.accept("Selenium"));
        page.locator("#my-prompt").click();

        assertThat(page.locator("#prompt-text")).hasText("You typed: Selenium");
    }

    // El modal es HTML: igual que en Selenium, NO usa onceDialog.
    // getByRole compara el texto ignorando saltos de línea y espacios de sobra,
    // así que aquí no existe el problema que tuvimos con text()='Save changes'.
    // Tampoco hace falta esperar la animación: click() espera solo.
    @Test
    void modal() {
        page.locator("#my-modal").click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save changes")).click();

        assertThat(page.locator("#modal-text")).hasText("You chose: Save changes");
    }
}

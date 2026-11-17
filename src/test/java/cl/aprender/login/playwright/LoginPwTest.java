package cl.aprender.login.playwright;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import cl.aprender.playwright.BasePlaywrightTest;

// Las MISMAS pruebas que login/selenium/LoginTest, ahora con Playwright.
// Ponlas lado a lado: la estructura es idéntica. Solo cambian las comprobaciones
// (assertThat de Playwright en vez de assertEquals). Esa es la ventaja del POM
// al migrar: el trabajo grande se hace en LoginPage, no en cada prueba.
class LoginPwTest extends BasePlaywrightTest {

    private LoginPage login;

    @BeforeEach
    void abrirLogin() {
        login = new LoginPage(page);
        login.abrir();
    }

    @Test
    void errorNoSeVeAlEntrar() {
        assertThat(login.mensajeError()).isHidden();
    }

    @Test
    void loginCorrecto() {
        login.iniciarSesion("user", "user");

        assertThat(login.mensajeExito()).hasText("Login successful");
    }

    @ParameterizedTest(name = "usuario=''{0}'' clave=''{1}''")
    @CsvSource({
            "user, mala",
            "malo, user",
            "'', ''",
            "USER, user",
    })
    void loginIncorrecto(String usuario, String clave) {
        login.iniciarSesion(usuario, clave);

        assertThat(login.mensajeError()).hasText("Invalid credentials");
        assertTrue(login.url().contains("login-form"), "Debe quedarse en el login");
    }
}

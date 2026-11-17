package cl.aprender.login.selenium;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import cl.aprender.BaseTest;

// Pruebas de login con Page Object Model (Selenium).
// Fíjate: aquí NO aparece ningún By, findElement ni WebDriverWait.
// La prueba solo dice QUÉ se prueba; LoginPage sabe CÓMO hacerlo.
class LoginTest extends BaseTest {

    private LoginPage login;

    @BeforeEach
    void abrirLogin() {
        login = new LoginPage(driver);
        login.abrir();
        pausa();
    }

    @Test
    void errorNoSeVeAlEntrar() {
        assertFalse(login.errorVisible());
    }

    @Test
    void loginCorrecto() {
        login.iniciarSesion("user", "user");
        pausa();

        assertEquals("Login successful", login.mensajeExito());
    }

    // PRUEBA PARAMETRIZADA (adelanto de la lección 4):
    // UN método que se ejecuta una vez por cada fila de @CsvSource.
    // '' significa texto vacío.
    @ParameterizedTest(name = "usuario=''{0}'' clave=''{1}''")
    @CsvSource({
            "user, mala",      // contraseña incorrecta
            "malo, user",      // usuario incorrecto
            "'', ''",          // los dos vacíos
            "USER, user",      // mayúsculas: ¿distingue? (sí)
    })
    void loginIncorrecto(String usuario, String clave) {
        login.iniciarSesion(usuario, clave);
        pausa();

        assertEquals("Invalid credentials", login.mensajeError());
        assertTrue(driver.getCurrentUrl().contains("login-form"), "Debe quedarse en el login");
    }
}

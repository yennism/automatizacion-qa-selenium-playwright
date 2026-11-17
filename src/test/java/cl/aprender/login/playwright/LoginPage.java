package cl.aprender.login.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

// PAGE OBJECT (versión Playwright) de la misma página de login.
// Compárala con cl.aprender.login.selenium.LoginPage:
//  - recibe Page en vez de WebDriver
//  - guarda Locator en vez de By
//  - no hay WebDriverWait: Playwright espera solo
//  - devuelve Locator (no String) para que la prueba use assertThat,
//    que reintenta hasta que se cumple.
public class LoginPage {

    private static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/login-form.html";

    private final Page page;
    private final Locator usuario;
    private final Locator clave;
    private final Locator botonEnviar;
    private final Locator mensajeExito;
    private final Locator mensajeError;

    public LoginPage(Page page) {
        this.page = page;
        // Crear un Locator NO busca nada todavía: busca recién cuando se usa.
        // Por eso se pueden definir todos aquí, antes de abrir la página.
        this.usuario = page.locator("#username");
        this.clave = page.locator("#password");
        this.botonEnviar = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        this.mensajeExito = page.locator("#success");
        this.mensajeError = page.locator("#invalid");
    }

    public void abrir() {
        page.navigate(URL);
    }

    public void iniciarSesion(String user, String pass) {
        usuario.fill(user);   // fill borra lo que hubiera: no hace falta clear()
        clave.fill(pass);
        botonEnviar.click();
    }

    public Locator mensajeExito() {
        return mensajeExito;
    }

    public Locator mensajeError() {
        return mensajeError;
    }

    public String url() {
        return page.url();
    }
}

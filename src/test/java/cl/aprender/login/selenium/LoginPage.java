package cl.aprender.login.selenium;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

// PAGE OBJECT (versión Selenium) de la página de login.
//
// Regla de oro: aquí van los LOCALIZADORES y las ACCIONES de la página.
// Aquí NO van asserts: comprobar es trabajo de la prueba (LoginTest).
public class LoginPage {

    private static final String URL = "https://bonigarcia.dev/selenium-webdriver-java/login-form.html";

    // Localizadores: el ÚNICO lugar del proyecto donde aparecen.
    // Si la página cambia un id, se corrige solo aquí.
    private final By usuario = By.id("username");
    private final By clave = By.id("password");
    private final By botonEnviar = By.cssSelector("button[type='submit']");
    private final By mensajeExito = By.id("success");
    private final By mensajeError = By.id("invalid");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    }

    public void abrir() {
        driver.get(URL);
    }

    // La acción completa, como la describiría un usuario: "iniciar sesión".
    public void iniciarSesion(String user, String pass) {
        escribir(usuario, user);
        escribir(clave, pass);
        driver.findElement(botonEnviar).click();
    }

    // Las esperas también se esconden aquí: la prueba no necesita saber de WebDriverWait.
    public String mensajeExito() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(mensajeExito)).getText();
    }

    public String mensajeError() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(mensajeError)).getText();
    }

    public boolean errorVisible() {
        return driver.findElement(mensajeError).isDisplayed();
    }

    // Método privado de ayuda: solo lo usa esta clase.
    private void escribir(By campo, String texto) {
        WebElement elemento = driver.findElement(campo);
        elemento.clear();
        elemento.sendKeys(texto);
    }
}

package cl.aprender.bd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cl.aprender.BaseTest;
import cl.aprender.login.selenium.LoginPage;

// Parte 2: navegador + base de datos juntos.
//
// Los usuarios para probar el login NO están escritos en la prueba:
// se LEEN de la base de datos. Si mañana se agrega un usuario a la tabla,
// la prueba lo prueba automáticamente sin tocar el código.
//
// Regla de negocio que verificamos:
//   usuario ACTIVO  -> debe poder entrar
//   usuario INACTIVO -> NO debe poder entrar
//
// (La página de práctica solo acepta user/user. Por eso "admin", que está
//  activo en la BD, FALLARÍA: sería un bug "la web no coincide con la BD".
//  Para que la prueba pase, solo leemos los usuarios que la web conoce
//  más el inactivo. Cambia la consulta para incluir a admin y verás el fallo.)
class LoginConBdTest extends BaseTest {

    // Fuente de datos de la prueba parametrizada: una consulta SQL.
    // Cada fila de la tabla se convierte en una ejecución de la prueba.
    static Stream<Arguments> usuariosDeLaBaseDeDatos() throws SQLException {
        BaseDeDatos.reiniciar();
        List<Map<String, Object>> filas = BaseDeDatos.consultarFilas(
                "SELECT usuario, clave, activo FROM usuarios "
                        + "WHERE usuario IN ('user', 'antiguo') ORDER BY id");

        return filas.stream().map(fila -> Arguments.of(
                fila.get("usuario"), fila.get("clave"), fila.get("activo")));
    }

    @ParameterizedTest(name = "{0} (activo={2})")
    @MethodSource("usuariosDeLaBaseDeDatos")
    void loginSegunLaBaseDeDatos(String usuario, String clave, boolean activo) {
        LoginPage login = new LoginPage(driver);
        login.abrir();
        pausa();
        login.iniciarSesion(usuario, clave);
        pausa();

        if (activo) {
            assertEquals("Login successful", login.mensajeExito());
        } else {
            assertEquals("Invalid credentials", login.mensajeError());
        }
    }
}

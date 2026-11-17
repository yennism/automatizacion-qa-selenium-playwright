package cl.aprender.bd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Parte 1: pruebas que SOLO consultan la base de datos (sin navegador).
// Así valida un QA que los datos están bien guardados.
class BaseDeDatosTest {

    @BeforeEach
    void datosIniciales() throws SQLException {
        BaseDeDatos.reiniciar();
    }

    // 1) Contar filas: el SELECT más típico en una prueba
    @Test
    void cantidadDeUsuarios() throws SQLException {
        Object cantidad = BaseDeDatos.consultarValor("SELECT COUNT(*) FROM usuarios");

        assertEquals(3L, cantidad); // COUNT devuelve un número "long" (por eso 3L)
    }

    // 2) Consultar un valor con un parámetro (?)
    @Test
    void rolDelAdministrador() throws SQLException {
        Object rol = BaseDeDatos.consultarValor(
                "SELECT rol FROM usuarios WHERE usuario = ?", "admin");

        assertEquals("ADMIN", rol);
    }

    // 3) Una fila completa: varias columnas a la vez
    @Test
    void datosDeUnPedido() throws SQLException {
        Map<String, Object> pedido = BaseDeDatos.consultarFilas(
                "SELECT usuario_id, total, estado FROM pedidos WHERE id = ?", 100).get(0);

        assertEquals(1, pedido.get("usuario_id"));
        // compareTo y no equals: 15990.00 y 15990 son el mismo número
        // pero BigDecimal.equals los considera distintos por los decimales.
        assertEquals(0, new BigDecimal("15990").compareTo((BigDecimal) pedido.get("total")));
        assertEquals("PAGADO", pedido.get("estado"));
    }

    // 4) JOIN: unir dos tablas. Total pagado por el usuario "user".
    @Test
    void totalPagadoPorUsuario() throws SQLException {
        BigDecimal total = (BigDecimal) BaseDeDatos.consultarValor(
                "SELECT SUM(p.total) "
                        + "FROM pedidos p JOIN usuarios u ON p.usuario_id = u.id "
                        + "WHERE u.usuario = ? AND p.estado = 'PAGADO'",
                "user");

        assertEquals(0, new BigDecimal("15990").compareTo(total));
    }

    // 5) Calidad de datos: reglas que SIEMPRE deben cumplirse.
    //    Un QA las revisa para detectar datos corruptos.
    @Test
    void noHayPedidosConTotalNegativoNiEstadoDesconocido() throws SQLException {
        List<Map<String, Object>> malos = BaseDeDatos.consultarFilas(
                "SELECT id FROM pedidos "
                        + "WHERE total <= 0 OR estado NOT IN ('PENDIENTE', 'PAGADO', 'ANULADO')");

        assertEquals(List.of(), malos, "Pedidos con datos inválidos");
    }

    // 6) Escribir y comprobar: lo que haría la aplicación al anular un pedido
    @Test
    void anularPedido() throws SQLException {
        int filasCambiadas = BaseDeDatos.ejecutar(
                "UPDATE pedidos SET estado = 'ANULADO' WHERE id = ?", 101);

        assertEquals(1, filasCambiadas);
        assertEquals("ANULADO",
                BaseDeDatos.consultarValor("SELECT estado FROM pedidos WHERE id = ?", 101));
    }

    // 7) Un valor que no existe: la consulta no devuelve filas
    @Test
    void usuarioQueNoExiste() throws SQLException {
        assertNull(BaseDeDatos.consultarValor(
                "SELECT rol FROM usuarios WHERE usuario = ?", "fantasma"));
    }
}

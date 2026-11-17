package cl.aprender.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Ayudante para consultar la base de datos con JDBC (la librería estándar de Java
// para bases de datos: viene en java.sql, no hay que instalar nada).
//
// Es la misma idea que un Page Object: las pruebas no saben CÓMO se conecta,
// solo piden "dame este valor".
public final class BaseDeDatos {

    // CADENA DE CONEXIÓN: lo ÚNICO que cambia según la base de datos.
    //   H2 (memoria):  jdbc:h2:mem:tienda
    //   MySQL:         jdbc:mysql://servidor:3306/tienda
    //   PostgreSQL:    jdbc:postgresql://servidor:5432/tienda
    //   SQL Server:    jdbc:sqlserver://servidor:1433;databaseName=tienda
    //   Oracle:        jdbc:oracle:thin:@servidor:1521/tienda
    // DB_CLOSE_DELAY=-1 mantiene la base viva mientras dure la ejecución.
    private static final String URL = "jdbc:h2:mem:tienda;DB_CLOSE_DELAY=-1";
    private static final String USUARIO = "sa";
    private static final String CLAVE = "";
    // En un proyecto real el usuario y la clave NUNCA se escriben en el código:
    // se leen de variables de entorno, p. ej. System.getenv("BD_CLAVE").

    private BaseDeDatos() {
    }

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    // Borra todo y vuelve a cargar los datos de bd/tienda.sql.
    // Las pruebas lo llaman al empezar: así cada una parte de datos conocidos
    // y no depende de lo que haya cambiado otra prueba.
    // (En una base real compartida esto no se hace: ahí se crean datos propios
    // de la prueba y se borran al final.)
    public static void reiniciar() throws SQLException {
        ejecutar("RUNSCRIPT FROM 'classpath:bd/tienda.sql'");
    }

    // Devuelve UN valor (la primera columna de la primera fila), o null si no hay filas.
    // Ej.: consultarValor("SELECT COUNT(*) FROM usuarios")
    //      consultarValor("SELECT rol FROM usuarios WHERE usuario = ?", "admin")
    public static Object consultarValor(String sql, Object... parametros) throws SQLException {
        List<Map<String, Object>> filas = consultarFilas(sql, parametros);
        if (filas.isEmpty()) {
            return null;
        }
        return filas.get(0).values().iterator().next();
    }

    // Devuelve todas las filas. Cada fila es un mapa  columna -> valor.
    public static List<Map<String, Object>> consultarFilas(String sql, Object... parametros)
            throws SQLException {
        // try-with-resources: cierra conexión, consulta y resultados aunque haya error
        try (Connection conexion = conectar();
                PreparedStatement consulta = conexion.prepareStatement(sql)) {

            // Cada ? del SQL se reemplaza por un parámetro (el primero es el 1, no el 0)
            for (int i = 0; i < parametros.length; i++) {
                consulta.setObject(i + 1, parametros[i]);
            }

            try (ResultSet resultado = consulta.executeQuery()) {
                ResultSetMetaData columnas = resultado.getMetaData();
                List<Map<String, Object>> filas = new ArrayList<>();
                while (resultado.next()) {               // avanza fila por fila
                    Map<String, Object> fila = new LinkedHashMap<>();
                    for (int c = 1; c <= columnas.getColumnCount(); c++) {
                        fila.put(columnas.getColumnLabel(c).toLowerCase(), resultado.getObject(c));
                    }
                    filas.add(fila);
                }
                return filas;
            }
        }
    }

    // Para INSERT, UPDATE y DELETE. Devuelve cuántas filas cambiaron.
    public static int ejecutar(String sql, Object... parametros) throws SQLException {
        try (Connection conexion = conectar();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.length; i++) {
                sentencia.setObject(i + 1, parametros[i]);
            }
            return sentencia.executeUpdate();
        }
    }
}

-- Base de datos de ejemplo: una pequeña tienda.
-- Se crea en memoria cada vez que se ejecutan las pruebas.

DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS usuarios;

CREATE TABLE usuarios (
    id      INT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    clave   VARCHAR(50) NOT NULL,
    activo  BOOLEAN     NOT NULL,
    rol     VARCHAR(20) NOT NULL
);

CREATE TABLE pedidos (
    id         INT PRIMARY KEY,
    usuario_id INT           NOT NULL REFERENCES usuarios(id),
    total      DECIMAL(10,2) NOT NULL,
    estado     VARCHAR(20)   NOT NULL
);

INSERT INTO usuarios VALUES (1, 'user',    'user',   TRUE,  'CLIENTE');
INSERT INTO usuarios VALUES (2, 'admin',   'admin1', TRUE,  'ADMIN');
INSERT INTO usuarios VALUES (3, 'antiguo', 'viejo',  FALSE, 'CLIENTE');

INSERT INTO pedidos VALUES (100, 1, 15990.00, 'PAGADO');
INSERT INTO pedidos VALUES (101, 1,  4990.00, 'PENDIENTE');
INSERT INTO pedidos VALUES (102, 2, 89990.00, 'PAGADO');

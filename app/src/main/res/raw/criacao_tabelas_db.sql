DROP TABLE IF EXISTS notas;

CREATE TABLE notas (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo VARCHAR(50) NOT NULL,
    conteudo TEXT NOT NULL
);
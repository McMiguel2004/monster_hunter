BEGIN WORK;
SET TRANSACTION READ WRITE;

SET datestyle = YMD;

-- Esborra taules si existien
DROP TABLE monstruos;
DROP TABLE location;
DROP TABLE Species;
DROP TABLE Weakness;
DROP TABLE Resistances;

-- Creació de taules
CREATE TABLE monstrus (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    imagen VARCHAR(255)
);

CREATE TABLE location (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT
);

CREATE TABLE species (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);

CREATE TABLE weakness (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE resistances (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);


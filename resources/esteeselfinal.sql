BEGIN WORK;
SET TRANSACTION READ WRITE;

DROP TABLE IF EXISTS Monstruos;
DROP TABLE IF EXISTS Elements;
DROP TABLE IF EXISTS Location;

-- Tabla Monstruos
CREATE TABLE Monstruos (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Nombre VARCHAR(255),
    Imagen VARCHAR(255),
    Descripcion TEXT,
    SpeciesName VARCHAR(255), -- Nueva columna para el nombre de la especie
    SpeciesDescripcion TEXT -- Nueva columna para la descripción de la especie
);

-- Tabla Elements
CREATE TABLE Elements (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Monstruo_ID INT,
    Element1 VARCHAR(255),
    Element2 VARCHAR(255),
    Element3 VARCHAR(255),
    Element4 VARCHAR(255),
    Element5 VARCHAR(255),
    Element6 VARCHAR(255),
    Element7 VARCHAR(255),
    Element8 VARCHAR(255),
    FOREIGN KEY (Monstruo_ID) REFERENCES Monstruos(ID)
);

-- Tabla Location
CREATE TABLE Location (
    ID INT PRIMARY KEY AUTO_INCREMENT,
    Monstruo_ID INT,
    Name VARCHAR(255),
    Descripcion TEXT,
    FOREIGN KEY (Monstruo_ID) REFERENCES Monstruos(ID)
);

COMMIT;

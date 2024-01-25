BEGIN WORK;
SET TRANSACTION READ WRITE;

SET datestyle = YMD;

-- Esborra taules si existien
DROP TABLE Monstruos;
DROP TABLE SubDescripcion;

CREATE TABLE Monstruos (
    MonstruoID INT PRIMARY KEY AUTO_INCREMENT,
    Nombre VARCHAR(255) NOT NULL,
    Imagen VARCHAR(255) NOT NULL,
    Descripcion TEXT NOT NULL
);

CREATE TABLE SubDescripcion (
    SubDescripcionID INT PRIMARY KEY AUTO_INCREMENT,
    MonstruoID INT,
    Descripcion TEXT NOT NULL,
    FOREIGN KEY (MonstruoID) REFERENCES Monstruos(MonstruoID)
);

COMMIT;
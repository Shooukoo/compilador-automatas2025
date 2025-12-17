package Analizador;

public enum TokenType {
    // 1. Tipos de Dato
    INT, FLOAT, BOOLEAN, STRING, 
    // Alias para coincidir con tu lógica (opcional, pero útil)
    ENTERO, REAL, CADENA, BOOL, CAR,

    // 2. Palabras Reservadas
    PROGRAMA, FIN_PROGRAMA, VAR, IF, ELSE, ENDIF, LEER, ESCRIBIR,
    TRUE, FALSE,

    // 3. Identificadores y Literales
    ID, NUM, CADENA_LITERAL, CARACTER_LITERAL,

    // 4. Operadores Aritméticos
    SUMA, RESTA, MULTIPLICACION, DIVISION, POTENCIA, MODULO,
    INCREMENTO, DECREMENTO,

    // 5. Operadores Relacionales
    IGUAL, DIFERENTE, MENOR, MAYOR, MENOR_IGUAL, MAYOR_IGUAL,

    // 6. Operadores Lógicos
    AND, OR, NOT,

    // 7. Asignación y Delimitadores
    ASIGNACION, 
    PUNTOYCOMA, COMA, DOS_PUNTOS, 
    PAREN_ABRE, PAREN_CIERRA, LLAVE_ABRE, LLAVE_CIERRA,

    // 8. Control
    EOF, ERROR
}
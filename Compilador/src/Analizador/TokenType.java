package Analizador;

/**
 * Enumeración de todos los tipos de tokens posibles que el AnalizadorLexico puede reconocer.
 */
public enum TokenType {
    // Tipos de datos y literales booleanos
    INT, FLOAT, BOOLEAN, STRING, TRUE, FALSE,

    // Operadores aritméticos
    SUMA, RESTA, MULTIPLICACION, DIVISION, POTENCIA, MODULO,

    // Operadores relacionales
    MAYOR, MENOR, IGUAL, DIFERENTE, MENOR_IGUAL, MAYOR_IGUAL,

    // Operadores lógicos
    AND, OR, NOT,

    // Otros símbolos y delimitadores
    ASIGNACION, PUNTOYCOMA, PAREN_ABRE, PAREN_CIERRA, LLAVE_ABRE, LLAVE_CIERRA,

    // Valores e identificadores
    IDENTIFICADOR, NUMERO, CADENA,

    // Tipo especial para errores léxicos
    ERROR 
}
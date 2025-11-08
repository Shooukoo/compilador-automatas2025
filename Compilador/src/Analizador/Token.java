package Analizador;

/**
 * Representa un token léxico identificado por el analizador.
 * Contiene el tipo de token, su valor (lexema), y su posición (línea y columna).
 */
public class Token {
    public TokenType tipo;
    public String valor;
    public int linea;
    public int columna;

    /**
     * Constructor para un nuevo token.
     * @param tipo El tipo de token (de TokenType).
     * @param valor El lexema exacto encontrado en el código.
     * @param linea La línea donde comienza el token (empezando en 1).
     * @param columna La columna donde comienza el token (empezando en 1).
     */
    public Token(TokenType tipo, String valor, int linea, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
        this.columna = columna;
    }

    // --- Getters ---

    public TokenType getTipo() {
        return tipo;
    }

    public String getValor() {
        return valor;
    }

    public int getLinea() {
        return linea;
    }

    public int getColumna() {
        return columna;
    }

    /**
     * Devuelve una representación en String del token, incluyendo su posición.
     * @return String formateado del token.
     */
    @Override
    public String toString() {
        // Incluir posición para depuración
        return String.format("<%s, \"%s\"> @ (L:%d, C:%d)", tipo, valor, linea, columna);
    }
}
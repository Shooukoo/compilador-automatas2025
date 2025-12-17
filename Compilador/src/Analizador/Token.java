package Analizador;

public class Token {
    private TokenType tipo;
    private String valor;
    private int linea;
    private int columna;

    public Token(TokenType tipo, String valor, int linea, int columna) {
        this.tipo = tipo;
        this.valor = valor;
        this.linea = linea;
        this.columna = columna;
    }

    public TokenType getTipo() { return tipo; }
    public String getValor() { return valor; }
    public int getLinea() { return linea; }
    public int getColumna() { return columna; }

    /**
     * Devuelve el nombre EXACTO que espera el TASP.csv (el Excel).
     */
    public String getComponenteSintactico() {
        switch (this.tipo) {
            // --- PALABRAS RESERVADAS (Según tu CSV) ---
            case PROGRAMA: return "programa";
            case FIN_PROGRAMA: return "finprograma";
            case VAR: return "var";
            case IF: return "if";
            case ELSE: return "else";
            case ENDIF: return "endif";
            case LEER: return "leer";
            case ESCRIBIR: return "escribir";
            
            // --- TIPOS DE DATOS ---
            case INT: 
            case ENTERO: return "entero"; // Tu CSV dice "entero"
            
            case FLOAT: 
            case REAL: return "real";   // Tu CSV dice "real"
            
            case BOOLEAN: 
            case BOOL: return "bool";   // Tu CSV dice "bool"
            
            case STRING: 
            case CADENA: return "cadena"; // Tu CSV dice "cadena"
            
            case CAR: return "car";

            case TRUE: return "true";
            case FALSE: return "false";

            // --- LITERALES ---
            case ID: return "id";
            case NUM: return "num";
            case CADENA_LITERAL: return "litcad";
            case CARACTER_LITERAL: return "litcar";

            // --- OPERADORES Y SIGNOS ---
            case SUMA: return "+";
            case RESTA: return "-";
            case MULTIPLICACION: return "*";
            case DIVISION: return "/";
            case MODULO: return "%";
            case POTENCIA: return "^"; // Verificar si tu CSV usa ^
            
            case IGUAL: return "==";
            case DIFERENTE: return "!=";
            case MENOR: return "<";
            case MAYOR: return ">";
            case MENOR_IGUAL: return "<=";
            case MAYOR_IGUAL: return ">=";
            
            case AND: return "&&";
            case OR: return "||";
            case NOT: return "!";
            
            case ASIGNACION: return "="; // Ojo: en tu código usas "=" para asignar
            
            // --- DELIMITADORES (Aquí tenías el error) ---
            case COMA: return ",";
            case DOS_PUNTOS: return ":";
            case PUNTOYCOMA: return ";";
            case PAREN_ABRE: return "(";
            case PAREN_CIERRA: return ")";

            // --- FIN DE ARCHIVO ---
            case EOF: return "$";

            default:
                return this.valor;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s (L:%d, C:%d)", getComponenteSintactico(), linea, columna);
    }
}
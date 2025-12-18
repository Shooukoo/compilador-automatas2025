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

    public String getComponenteSintactico() {
        switch (this.tipo) {
            case PROGRAMA: return "programa";
            case FIN_PROGRAMA: return "finprograma";
            case VAR: return "var";
            case IF: return "if";
            case ELSE: return "else";
            case ENDIF: return "endif";
            case LEER: return "leer";
            case ESCRIBIR: return "escribir";
            
            case INT: 
            case ENTERO: return "entero"; 
            
            case FLOAT: 
            case REAL: return "real";  
            
            case BOOLEAN: 
            case BOOL: return "bool";   
            
            case STRING: 
            case CADENA: return "cadena"; 
            
            case CAR: return "car";

            case TRUE: return "true";
            case FALSE: return "false";

            case ID: return "id";
            case NUM: return "num";
            case CADENA_LITERAL: return "litcad";
            case CARACTER_LITERAL: return "litcar";

            case SUMA: return "+";
            case RESTA: return "-";
            case MULTIPLICACION: return "*";
            case DIVISION: return "/";
            case MODULO: return "%";
            case POTENCIA: return "^"; 
            
            case IGUAL: return "==";
            case DIFERENTE: return "!=";
            case MENOR: return "<";
            case MAYOR: return ">";
            case MENOR_IGUAL: return "<=";
            case MAYOR_IGUAL: return ">=";
            
            case AND: return "&&";
            case OR: return "||";
            case NOT: return "!";
            
            case ASIGNACION: return "=";
            
            case COMA: return ",";
            case DOS_PUNTOS: return ":";
            case PUNTOYCOMA: return ";";
            case PAREN_ABRE: return "(";
            case PAREN_CIERRA: return ")";

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
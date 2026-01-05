package Analizador;

import Controlador.FuncionesCompilador;

import java.util.*;

public class AnalizadorSintactico {

    public static class PasoAnalisis {
        public String pilaPrincipal;
        public String pilaAux; 
        public String entrada;
        public String accion;

        public PasoAnalisis(String pila, String pilaAux, String entrada, String accion) {
            this.pilaPrincipal = pila;
            this.pilaAux = pilaAux;
            this.entrada = entrada;
            this.accion = accion;
        }
    }

    private List<PasoAnalisis> bitacora;
    private Stack<String> pila;
    private Stack<String> pilaAux; 

 // -------------------------------------------------------------------------
    // INDICES (Reemplaza los vectores antiguos)
    // -------------------------------------------------------------------------
    private static final String[] V_VARS = {
        "Prog", "Bloque", "Dec", "Sigid", "Tipo", 
        "Sentencia", "Sigif", "Lista_par", "Sigpar", 
        "EL", "EL'", "ER", "R'", "E", "E'", "T", "T'", "F"
    };

    private static final String[] V_TERMS = {
        "id", "num", "litcad", "litcar", "true", "false", 
        "leer", "escribir", 
        "(", ")", "+", "-", "*", "/", "!", 
        "<", ">", ">=", "<=", "==", "!=", "||", "&&", 
        ",", ":", ";", "=", 
        "programa", "finprograma", "var", 
        "if", "else", "endif", 
        "entero", "real", "cadena", "car", "bool", 
        "$" 
    };

    // -------------------------------------------------------------------------
    // MATRIZ TASP (Reemplaza la matriz antigua y el método inicializarMatriz)
    // -------------------------------------------------------------------------
    private static final String[][][] TABLA = {
        // [0] Prog
        {
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"programa", "id", ";", "Bloque", "finprograma"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"sacar"}
        },
        // [1] Bloque
        {
            {"Sentencia", "Bloque"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"Sentencia", "Bloque"}, {"Sentencia", "Bloque"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"e"}, {"Dec", "Bloque"}, 
            {"Sentencia", "Bloque"}, {"e"}, {"e"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"sacar"}
        },
        // [2] Dec
        {
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"var", "id", "Sigid", ":", "Tipo", ";"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"sacar"}
        },
        // [3] Sigid
        {
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, {"saltar"}, 
            {",", "id", "Sigid"}, {"e"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"saltar"}, 
            {"saltar"}, {"saltar"}, {"cadena"}, {"saltar"}, {"bool"}, 
            {"sacar"}
        },
        // [4] Tipo
        {
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"entero"}, {"real"}, {"cadena"}, {"car"}, {"bool"}, 
            {"sacar"}
        },
        // [5] Sentencia
        {
            {"id", "=", "EL", ";"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"leer", "(", "Lista_par", ")", ";"}, {"escribir", "(", "Lista_par", ")", ";"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"if", "EL", "Bloque", "Sigif"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [6] Sigif
        {
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"else", "Bloque", "endif"}, {"endif"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [7] Lista_par
        {
            {"EL", "Sigpar"}, {"EL", "Sigpar"}, {"EL", "Sigpar"}, {"EL", "Sigpar"}, {"EL", "Sigpar"}, {"EL", "Sigpar"}, 
            {"sacar"}, {"sacar"}, 
            {"EL", "Sigpar"}, {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [8] Sigpar
        {
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, 
            {"sacar"}, {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {",", "EL", "Sigpar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [9] EL
        {
            {"ER", "EL'"}, {"ER", "EL'"}, {"ER", "EL'"}, {"ER", "EL'"}, {"ER", "EL'"}, {"ER", "EL'"}, 
            {"sacar"}, {"sacar"}, 
            {"ER", "EL'"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"!", "EL"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [10] EL'
        {
            {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"false"}, 
            {"e"}, {"e"}, 
            {"sacar"}, {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"||", "ER", "EL'"}, {"&&", "ER", "EL'"}, 
            {"e"}, {"sacar"}, {"e"}, {"sacar"}, 
            {"sacar"}, {"e"}, {"e"}, 
            {"e"}, {"e"}, {"e"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [11] ER
        {
            {"E", "R'"}, {"E", "R'"}, {"litcad", "R'"}, {"litcar", "R'"}, {"true", "R'"}, {"false", "R'"}, 
            {"sacar"}, {"sacar"}, 
            {"E", "R'"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [12] R'
        {
            {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"e"}, {"e"}, 
            {"sacar"}, {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"<", "E"}, {">", "E"}, {">=", "E"}, {"<=", "E"}, {"==", "E"}, {"!=", "E"}, {"e"}, {"e"}, 
            {"e"}, {"sacar"}, {"e"}, {"sacar"}, 
            {"sacar"}, {"e"}, {"e"}, 
            {"e"}, {"e"}, {"e"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [13] E
        {
            {"T", "E'"}, {"T", "E'"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, 
            {"T", "E'"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [14] E'
        {
            {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"e"}, {"e"}, 
            {"sacar"}, {"e"}, {"+", "T", "E'"}, {"-", "T", "E'"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, 
            {"e"}, {"sacar"}, {"e"}, {"sacar"}, 
            {"sacar"}, {"e"}, {"e"}, 
            {"e"}, {"e"}, {"e"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [15] T
        {
            {"F", "T'"}, {"F", "T'"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, 
            {"F", "T'"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [16] T'
        {
            {"e"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"e"}, {"e"}, 
            {"sacar"}, {"e"}, {"e"}, {"e"}, {"*", "F", "T'"}, {"/", "F", "T'"}, {"sacar"}, 
            {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, {"e"}, 
            {"e"}, {"sacar"}, {"e"}, {"sacar"}, 
            {"sacar"}, {"e"}, {"e"}, 
            {"e"}, {"e"}, {"e"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        },
        // [17] F
        {
            {"id"}, {"num"}, {"litcad"}, {"litcar"}, {"true"}, {"false"}, 
            {"sacar"}, {"sacar"}, 
            {"(", "EL", ")"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, {"sacar"}, 
            {"sacar"}
        }
    };

    public AnalizadorSintactico() {
        this.bitacora = new ArrayList<>();
        this.pila = new Stack<>();
        this.pilaAux = new Stack<>();
    }

    private int buscarIndice(String[] vector, String busqueda) {
        for (int i = 0; i < vector.length; i++) {
            if (vector[i].equals(busqueda)) return i;
        }
        return -1;
    }

    private boolean esVariable(String simbolo) {
        return buscarIndice(V_VARS, simbolo) != -1;
    }

    private boolean esTerminal(String simbolo) {
        return buscarIndice(V_TERMS, simbolo) != -1;
    }

    public List<PasoAnalisis> analizar(List<Token> tokens) {
        bitacora.clear();
        pila.clear();
        pilaAux.clear();

        pila.push("$");
        pila.push("Prog"); 

        int iToken = 0;
        Token tokenActual = (tokens.isEmpty()) ? new Token(null, "$", 0, 0) : tokens.get(0);

        while (!pila.isEmpty()) {
            String X = pila.peek(); 
            String a = tokenActual.getComponenteSintactico();
            String pilaStr = pila.toString();
            String pilaAuxStr = pilaAux.toString();

            if (esVariable(X)) {
                int f = buscarIndice(V_VARS, X);
                int c = buscarIndice(V_TERMS, a);

                if (c == -1) {
                     bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error: Token desconocido [" + a + "]"));
                     FuncionesCompilador.agregarErrorSintactico("Token inválido: " + a, tokenActual.getLinea(), tokenActual.getColumna());
                     iToken++;
                     if (iToken < tokens.size()) tokenActual = tokens.get(iToken);
                     continue;
                }

                String[] produccion = TABLA[f][c];
                String accionTexto = produccion[0]; 

                if (accionTexto.equals("sacar")) {
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error (Sacar): " + X));
                    FuncionesCompilador.agregarErrorSintactico("Error Sintáctico: Se esperaba estructura de '" + X + "' pero se encontró '" + a + "'", tokenActual.getLinea(), tokenActual.getColumna());
                    pila.pop(); 
                } else if (accionTexto.equals("saltar")) {
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error (Saltar): " + a));
                    FuncionesCompilador.agregarErrorSintactico("Token inesperado: " + a, tokenActual.getLinea(), tokenActual.getColumna());
                    iToken++;
                    if (iToken < tokens.size()) tokenActual = tokens.get(iToken);
                } else if (accionTexto.equals("e")) {
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, X + " -> epsilon"));
                    pila.pop();
                } else {
                    for (String s : produccion) if (!s.isEmpty()) pilaAux.push(s);
                    
                    String prodStr = String.join(" ", produccion);
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAux.toString(), a, X + " -> " + prodStr));
                    
                    pila.pop(); 
                    while(!pilaAux.isEmpty()) pila.push(pilaAux.pop());
                }
            } 
            else if (esTerminal(X) || X.equals("$")) {
                if (X.equals(a)) {
                    if (X.equals("$")) {
                        bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "ACEPTACIÓN"));
                        break;
                    }
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Concuerda: " + a));
                    pila.pop();
                    iToken++;
                    if (iToken < tokens.size()) tokenActual = tokens.get(iToken);
                } else {
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error: Se esperaba " + X));
                    FuncionesCompilador.agregarErrorSintactico("Se esperaba '" + X + "' pero vino '" + a + "'", tokenActual.getLinea(), tokenActual.getColumna());
                    pila.pop(); 
                }
            } 
            else {
                bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error Fatal: " + X));
                pila.pop();
            }
        }
        return bitacora;
    }
}
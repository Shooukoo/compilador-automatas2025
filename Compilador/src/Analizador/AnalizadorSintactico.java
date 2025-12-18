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
    
    private String[] vVariables;   
    private String[] vTerminales;  
    private String[][] matrizTASP; 

    public AnalizadorSintactico() {
        this.bitacora = new ArrayList<>();
        this.pila = new Stack<>();
        this.pilaAux = new Stack<>();
        
        inicializarVectores();
        inicializarMatriz();
    }

    private void inicializarVectores() {
        vVariables = new String[] {
            "Prog", "Bloque", "Dec", "Sigid", "Tipo", 
            "Sentencia", "Sigif", "Lista_par", "Sigpar", 
            "EL", "EL'", "ER", "R'", "E", "E'", "T", "T'", "F"
        };

        vTerminales = new String[] {
            "id", "num", "litcad", "true", "false", 
            "programa", "finprograma", "var", 
            "if", "else", "endif", "leer", "escribir",
            "entero", "real", "cadena", "bool",
            ",", ":", ";", "(", ")", "=", 
            "&&", "||", "!", 
            "<", ">", ">=", "<=", "==", "!=",
            "+", "-", "*", "/", "--", "++",
            "$" 
        };
        
        matrizTASP = new String[vVariables.length][vTerminales.length];
        for (int i = 0; i < vVariables.length; i++) {
            for (int j = 0; j < vTerminales.length; j++) {
                matrizTASP[i][j] = "sacar"; 
            }
        }
    }

    private void inicializarMatriz() {
        ag("Prog", "programa", "programa id ; Bloque finprograma");

        ag("Bloque", "var", "Dec Bloque");
        ag("Bloque", "id", "Sentencia Bloque");
        ag("Bloque", "if", "Sentencia Bloque");
        ag("Bloque", "leer", "Sentencia Bloque");
        ag("Bloque", "escribir", "Sentencia Bloque");
        ag("Bloque", "finprograma", "e");
        ag("Bloque", "else", "e");
        ag("Bloque", "endif", "e");


        ag("Dec", "var", "var id Sigid : Tipo ;");

        ag("Sigid", ",", ", id Sigid");
        ag("Sigid", ":", "e");

        ag("Tipo", "entero", "entero");
        ag("Tipo", "real", "real");
        ag("Tipo", "cadena", "cadena");
        ag("Tipo", "bool", "bool");

        ag("Sentencia", "id", "id = EL ;");
        ag("Sentencia", "if", "if EL Bloque Sigif"); 
        ag("Sentencia", "leer", "leer ( Lista_par ) ;");
        ag("Sentencia", "escribir", "escribir ( Lista_par ) ;");


        ag("Sigif", "else", "else Bloque endif");
        ag("Sigif", "endif", "endif");

        String inicioLista = "EL Sigpar";
        ag("Lista_par", "id", inicioLista); ag("Lista_par", "num", inicioLista);
        ag("Lista_par", "litcad", inicioLista); ag("Lista_par", "(", inicioLista);
        ag("Lista_par", ")", "e");


        ag("Sigpar", ",", ", EL Sigpar");
        ag("Sigpar", ")", "e");

        String inicioEL = "ER EL'";
        ag("EL", "id", inicioEL); ag("EL", "num", inicioEL);
        ag("EL", "litcad", inicioEL); ag("EL", "true", inicioEL);
        ag("EL", "false", inicioEL); ag("EL", "(", inicioEL);
        ag("EL", "!", "! EL"); ag("EL", "--", inicioEL); ag("EL", "++", inicioEL);

        ag("EL'", "&&", "&& ER EL'"); ag("EL'", "||", "|| ER EL'");
        agFollows("EL'", ")", ";", ",", "else", "endif", "finprograma", "id", "if", "var", "leer", "escribir");

        String inicioER = "E R'";
        ag("ER", "id", inicioER); ag("ER", "num", inicioER);
        ag("ER", "(", inicioER); ag("ER", "--", inicioER); ag("ER", "++", inicioER);
        ag("ER", "true", "true R'"); ag("ER", "false", "false R'"); ag("ER", "litcad", "litcad R'");

        ag("R'", "<", "< E"); ag("R'", ">", "> E");
        ag("R'", ">=", ">= E"); ag("R'", "<=", "<= E");
        ag("R'", "==", "== E"); ag("R'", "!=", "!= E");
        agFollows("R'", "&&", "||", ")", ";", ",", "else", "endif", "finprograma", "id", "if", "var", "leer", "escribir");

        String inicioE = "T E'";
        ag("E", "id", inicioE); ag("E", "num", inicioE);
        ag("E", "(", inicioE); ag("E", "--", inicioE); ag("E", "++", inicioE);

        ag("E'", "+", "+ T E'"); ag("E'", "-", "- T E'");
        agFollows("E'", "<", ">", ">=", "<=", "==", "!=", "&&", "||", ")", ";", ",", "else", 
        		"endif", "finprograma", "id", "if", "var", "leer", "escribir");

        String inicioT = "F T'";
        ag("T", "id", inicioT); ag("T", "num", inicioT);
        ag("T", "(", inicioT); ag("T", "--", inicioT); ag("T", "++", inicioT);

        ag("T'", "*", "* F T'"); ag("T'", "/", "/ F T'");
        agFollows("T'", "+", "-", "<", ">", ">=", "<=", "==", "!=", "&&", "||", ")", ";", ",", "else",
        		"endif", "finprograma", "id", "if", "var", "leer", "escribir");

        ag("F", "id", "id"); ag("F", "num", "num");
        ag("F", "litcad", "litcad"); ag("F", "true", "true");
        ag("F", "false", "false"); ag("F", "(", "( EL )");
        ag("F", "--", "-- F"); ag("F", "++", "++ F");
    }

    private int buscarIndice(String[] vector, String busqueda) {
        for (int i = 0; i < vector.length; i++) {
            if (vector[i].equals(busqueda)) return i;
        }
        return -1;
    }

    private void ag(String noTerminal, String terminal, String produccion) {
        int fila = buscarIndice(vVariables, noTerminal);
        int col = buscarIndice(vTerminales, terminal);
        if (fila != -1 && col != -1) {
            matrizTASP[fila][col] = produccion;
        }
    }

    private void agFollows(String noTerminal, String... terminales) {
        for (String t : terminales) ag(noTerminal, t, "e");
    }

    private boolean esVariable(String simbolo) {
        return buscarIndice(vVariables, simbolo) != -1;
    }

    private boolean esTerminal(String simbolo) {
        return buscarIndice(vTerminales, simbolo) != -1;
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
                int fila = buscarIndice(vVariables, X);
                int col = buscarIndice(vTerminales, a);

                if (col == -1) {
                     bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error: Token desconocido " + a));
                     FuncionesCompilador.agregarErrorSintactico("Token inválido: " + a, tokenActual.getLinea(), tokenActual.getColumna());
                     iToken++;
                     if (iToken < tokens.size()) tokenActual = tokens.get(iToken);
                     continue;
                }

                String produccion = matrizTASP[fila][col];

                if (produccion.equals("sacar")) {
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, "Error (Sacar): " + X));
                    pila.pop(); 
                } else if (produccion.equals("e")) {
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAuxStr, a, X + " -> epsilon"));
                    pila.pop();
                } else {
                    String[] simbolos = produccion.split("\\s+");
                    for (String s : simbolos) if (!s.isEmpty()) pilaAux.push(s);
                    bitacora.add(new PasoAnalisis(pilaStr, pilaAux.toString(), a, X + " -> " + produccion));
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
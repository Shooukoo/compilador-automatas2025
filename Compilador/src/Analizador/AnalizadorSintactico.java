package Analizador;

import Controlador.FuncionesCompilador;

import java.util.*;

public class AnalizadorSintactico {
    
    public static class PasoAnalisis {
        public String pila;
        public String entrada;
        public String accion;

        public PasoAnalisis(String pila, String entrada, String accion) {
            this.pila = pila;
            this.entrada = entrada;
            this.accion = accion;
        }
    }

    private Map<String, Map<String, String>> tasp; 
    private List<PasoAnalisis> bitacora;           
    private Stack<String> pila;                   

    public AnalizadorSintactico() {
        this.tasp = new HashMap<>();
        this.bitacora = new ArrayList<>();
        this.pila = new Stack<>();
        inicializarTabla();
    }

    private void inicializarTabla() {
        // --- 1. Prog ---
        agregarRegla("Prog", "programa", "programa id ; Bloque finprograma");
        agregarRegla("Prog", "$", "sacar");

        // --- 2. Bloque ---
        agregarRegla("Bloque", "id", "Sentencia Bloque");
        agregarRegla("Bloque", "if", "Sentencia Bloque");
        agregarRegla("Bloque", "leer", "Sentencia Bloque");
        agregarRegla("Bloque", "escribir", "Sentencia Bloque");
        
        // Si viene una declaración (Ahora permitida donde sea):
        agregarRegla("Bloque", "var", "Dec Bloque");
        agregarRegla("Bloque", "finprograma", "e");
        agregarRegla("Bloque", "else", "e");
        agregarRegla("Bloque", "endif", "e");

        // --- 3. Dec (Declaraciones) ---
        agregarRegla("Dec", "var", "var id Sigid : Tipo ;");
        
        // --- 4. Sigid ---
        agregarRegla("Sigid", ",", ", id Sigid");
        agregarRegla("Sigid", ":", "e"); 

        // --- 5. Tipo ---
        agregarRegla("Tipo", "entero", "entero");
        agregarRegla("Tipo", "real", "real");
        agregarRegla("Tipo", "cadena", "cadena");
        agregarRegla("Tipo", "bool", "bool");

        // --- 6. Sentencia ---
        agregarRegla("Sentencia", "id", "id = EL ;");
        agregarRegla("Sentencia", "if", "if EL Bloque Sigif"); 
        
        agregarRegla("Sentencia", "leer", "leer ( lista_par ) ;");
        agregarRegla("Sentencia", "escribir", "escribir ( lista_par ) ;");

        // 7. Sigif
        agregarRegla("Sigif", "else", "else Bloque endif");
        agregarRegla("Sigif", "endif", "endif");

        // --- 8. Lista_par ---
        agregarRegla("Lista_par", "id", "EL Sigpar");
        agregarRegla("Lista_par", "num", "EL Sigpar");
        agregarRegla("Lista_par", "litcad", "EL Sigpar");
        agregarRegla("Lista_par", "(", "EL Sigpar");
        agregarRegla("Lista_par", ")", "e");

        // --- 9. Sigpar ---
        agregarRegla("Sigpar", ",", ", EL Sigpar");
        agregarRegla("Sigpar", ")", "e");

     // --- 10. Expresiones Lógicas (EL) ---
        String inicioEL = "ER EL'";
        agregarRegla("EL", "id", inicioEL);
        agregarRegla("EL", "num", inicioEL);
        agregarRegla("EL", "litcad", inicioEL);
        agregarRegla("EL", "true", inicioEL);
        agregarRegla("EL", "false", inicioEL);
        agregarRegla("EL", "(", inicioEL);
        agregarRegla("EL", "!", "! EL");
        agregarRegla("EL", "--", inicioEL);
        agregarRegla("EL", "++", inicioEL);

        // --- 11. EL' ---
        agregarRegla("EL'", "&&", "&& ER EL'");
        agregarRegla("EL'", "||", "|| ER EL'");
        agregarRegla("EL'", ")", "e");
        agregarRegla("EL'", ";", "e");
        agregarRegla("EL'", ",", "e");
        agregarRegla("EL'", "id", "e");
        agregarRegla("EL'", "if", "e");
        agregarRegla("EL'", "else", "e");
        agregarRegla("EL'", "endif", "e");
        agregarRegla("EL'", "finprograma", "e");
        agregarRegla("EL'", "var", "e"); 

     // --- 12. ER (Expresiones Relacionales) ---
        String inicioER = "E R'";
        agregarRegla("ER", "id", inicioER);
        agregarRegla("ER", "num", inicioER);
        agregarRegla("ER", "(", inicioER);
        agregarRegla("ER", "true", "true R'");
        agregarRegla("ER", "false", "false R'");
        agregarRegla("ER", "litcad", "litcad R'");
        agregarRegla("ER", "--", inicioER);
        agregarRegla("ER", "++", inicioER);
        
        // --- 13. R' ---
        agregarRegla("R'", "<", "< E");
        agregarRegla("R'", ">", "> E");
        agregarRegla("R'", ">=", ">= E");
        agregarRegla("R'", "<=", "<= E");
        agregarRegla("R'", "==", "== E");
        agregarRegla("R'", "!=", "!= E");
        
        agregarRegla("R'", "&&", "e");
        agregarRegla("R'", "||", "e");
        agregarRegla("R'", ")", "e");
        agregarRegla("R'", ";", "e");
        agregarRegla("R'", ",", "e");
        agregarRegla("R'", "id", "e");
        agregarRegla("R'", "if", "e");
        agregarRegla("R'", "else", "e");
        agregarRegla("R'", "endif", "e");
        agregarRegla("R'", "finprograma", "e");
        agregarRegla("R'", "var", "e");

     // --- 14. E (Expresiones Aritméticas) ---
        String inicioE = "T E'";
        agregarRegla("E", "id", inicioE);
        agregarRegla("E", "num", inicioE);
        agregarRegla("E", "(", inicioE);
        agregarRegla("E", "--", inicioE);
        agregarRegla("E", "++", inicioE);

        // --- 15. E' (Suma/Resta) ---
        agregarRegla("E'", "+", "+ T E'");
        agregarRegla("E'", "-", "- T E'");
        
        agregarRegla("E'", "<", "e");
        agregarRegla("E'", ">", "e");
        agregarRegla("E'", ">=", "e");
        agregarRegla("E'", "<=", "e");
        agregarRegla("E'", "==", "e");
        agregarRegla("E'", "!=", "e");
        agregarRegla("E'", "&&", "e");
        agregarRegla("E'", "||", "e");
        agregarRegla("E'", ")", "e");
        agregarRegla("E'", ";", "e");
        agregarRegla("E'", ",", "e");
        agregarRegla("E'", "id", "e");
        agregarRegla("E'", "if", "e");
        agregarRegla("E'", "else", "e");
        agregarRegla("E'", "endif", "e");
        agregarRegla("E'", "finprograma", "e");
        agregarRegla("E'", "var", "e");

     // --- 16. T (Término) ---
        String inicioT = "F T'";
        agregarRegla("T", "id", inicioT);
        agregarRegla("T", "num", inicioT);
        agregarRegla("T", "(", inicioT);
        agregarRegla("T", "--", inicioT);
        agregarRegla("T", "++", inicioT);

        // --- 17. T' (Mult/Div) ---
        agregarRegla("T'", "*", "* F T'");
        agregarRegla("T'", "/", "/ F T'");
        
        agregarRegla("T'", "+", "e");
        agregarRegla("T'", "-", "e");
        agregarRegla("T'", "<", "e");
        agregarRegla("T'", ">", "e");
        agregarRegla("T'", ">=", "e");
        agregarRegla("T'", "<=", "e");
        agregarRegla("T'", "==", "e");
        agregarRegla("T'", "!=", "e");
        agregarRegla("T'", "&&", "e");
        agregarRegla("T'", "||", "e");
        agregarRegla("T'", ")", "e");
        agregarRegla("T'", ";", "e");
        agregarRegla("T'", ",", "e");
        agregarRegla("T'", "id", "e");
        agregarRegla("T'", "if", "e");
        agregarRegla("T'", "else", "e");
        agregarRegla("T'", "endif", "e");
        agregarRegla("T'", "finprograma", "e");
        agregarRegla("T'", "var", "e");
        
        // F 
        agregarRegla("F", "id", "id");
        agregarRegla("F", "num", "num");
        agregarRegla("F", "litcad", "litcad");
        agregarRegla("F", "true", "true");
        agregarRegla("F", "false", "false");
        agregarRegla("F", "(", "( EL )");
        agregarRegla("F", "--", "-- F");
        agregarRegla("F", "++", "++ F");
    }

    private void agregarRegla(String noTerminal, String terminal, String produccion) {
        tasp.computeIfAbsent(noTerminal, k -> new HashMap<>());
        tasp.get(noTerminal).put(terminal, produccion);
    }

    // --- ALGORITMO DE ANÁLISIS ---
    public List<PasoAnalisis> analizar(List<Token> tokens) {
        bitacora.clear();
        pila.clear();
        
        pila.push("$"); 
        pila.push("Prog"); 

        int indiceToken = 0;
        Token tokenActual = tokens.get(indiceToken);

        while (!pila.isEmpty()) {
            String tope = pila.peek();
            String entrada = tokenActual.getComponenteSintactico();
            String pilaString = pila.toString(); 
            
            if (tope.equals("$") && entrada.equals("$")) {
                bitacora.add(new PasoAnalisis("[$]", "$", "ACEPTACIÓN - CÓDIGO CORRECTO"));
                break;
            }

            if (tope.equals(entrada)) {
                bitacora.add(new PasoAnalisis(pilaString, entrada, "Match: " + tope));
                pila.pop(); 
                if (!tope.equals("$")) {
                    indiceToken++;
                    if (indiceToken < tokens.size()) tokenActual = tokens.get(indiceToken);
                }
            }
            else if (esTerminal(tope)) {
                bitacora.add(new PasoAnalisis(pilaString, entrada, "Error: Se esperaba " + tope));
                FuncionesCompilador.agregarErrorSintactico("Se esperaba '" + tope + "' pero vino '" + entrada + "'", tokenActual.getLinea(), tokenActual.getColumna());
                pila.pop(); 
            }
            else {
                Map<String, String> fila = tasp.get(tope);
                
                if (fila != null && fila.containsKey(entrada)) {
                    String produccion = fila.get(entrada);

                    if (produccion.equals("saltar")) { 
                        bitacora.add(new PasoAnalisis(pilaString, entrada, "Error (Saltar): Ignorando token " + entrada));
                        FuncionesCompilador.agregarErrorSintactico("Token inesperado: " + entrada, tokenActual.getLinea(), tokenActual.getColumna());
                        indiceToken++; 
                        if (indiceToken < tokens.size()) tokenActual = tokens.get(indiceToken);
                    } else if (produccion.equals("sacar")) { 
                        bitacora.add(new PasoAnalisis(pilaString, entrada, "Error (Sacar): Extrayendo estructura " + tope));
                        FuncionesCompilador.agregarErrorSintactico("Estructura incompleta para: " + tope, tokenActual.getLinea(), tokenActual.getColumna());
                        pila.pop();
                    } else if (produccion.equals("e")) { 
                        bitacora.add(new PasoAnalisis(pilaString, entrada, tope + " -> epsilon"));
                        pila.pop(); 
                    } else { 
                        bitacora.add(new PasoAnalisis(pilaString, entrada, tope + " -> " + produccion));
                        pila.pop(); 
                        String[] simbolos = produccion.split("\\s+");
                        for (int i = simbolos.length - 1; i >= 0; i--) {
                            if (!simbolos[i].isEmpty()) pila.push(simbolos[i]);
                        }
                    }
                } else {
                    bitacora.add(new PasoAnalisis(pilaString, entrada, "Error: No hay regla para [" + tope + ", " + entrada + "]"));
                    FuncionesCompilador.agregarErrorSintactico("Sintaxis inválida cerca de: " + entrada, tokenActual.getLinea(), tokenActual.getColumna());
                    indiceToken++; 
                    if (indiceToken < tokens.size()) tokenActual = tokens.get(indiceToken);
                }
            }
        }
        return bitacora;
    }

    private boolean esTerminal(String simbolo) {
        return !tasp.containsKey(simbolo);
    }
}
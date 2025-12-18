package Controlador;

import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;
import java.nio.charset.StandardCharsets; 
import java.util.ArrayList;
import java.util.List;

import Analizador.Token;
import Analizador.AnalizadorLexico;
import Analizador.TokenType;
import Analizador.ErrorCompilador;
import Analizador.AnalizadorSintactico; // Importamos la nueva clase

public class FuncionesCompilador {

    private static UndoManager undoManager = new UndoManager();
    private static File archivoActual = null;
    private static List<ErrorCompilador> listaErrores = new ArrayList<>(); 

    public static File getArchivoActual() {
        return archivoActual;
    }

    public static String abrirArchivo(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        if (archivoActual != null) {
            chooser.setCurrentDirectory(archivoActual.getParentFile());
        } else {
            chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        }

        int resultado = chooser.showOpenDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivoActual), StandardCharsets.UTF_8))) {
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                 if (contenido.length() > 0 && contenido.charAt(contenido.length() - 1) == '\n') {
                    contenido.deleteCharAt(contenido.length() - 1);
                 }
                undoManager.discardAllEdits(); 
                return contenido.toString();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Error de E/S al abrir archivo:\n" + e.getMessage(), "Error al Abrir", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                 JOptionPane.showMessageDialog(parent, "Error inesperado al abrir archivo:\n" + e.getMessage(), "Error al Abrir", JOptionPane.ERROR_MESSAGE);
                 e.printStackTrace();
            }
        }
        return null;
    }

    public static File guardarArchivo(JFrame parent, File archivo, String contenido) {
        File archivoParaGuardar = archivo;
        try {
            if (archivoParaGuardar == null) { 
                JFileChooser chooser = new JFileChooser();
                if (archivoActual != null) {
                    chooser.setCurrentDirectory(archivoActual.getParentFile());
                } else {
                     chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                }

                int resultado = chooser.showSaveDialog(parent);
                if (resultado != JFileChooser.APPROVE_OPTION) {
                    return null; 
                }
                archivoParaGuardar = chooser.getSelectedFile();

                String nombreArchivo = archivoParaGuardar.getName();
                if (!nombreArchivo.contains(".")) {
                    archivoParaGuardar = new File(archivoParaGuardar.getAbsolutePath() + ".txt");
                }

                if (archivoParaGuardar.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(parent,
                            "El archivo '" + archivoParaGuardar.getName() + "' ya existe.\n¿Deseas sobrescribirlo?",
                            "Confirmar Sobrescritura",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return null; 
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoParaGuardar), StandardCharsets.UTF_8))) {
                writer.write(contenido);
                archivoActual = archivoParaGuardar; 
                return archivoParaGuardar; 
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Error de E/S al guardar archivo:\n" + e.getMessage(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error inesperado al guardar archivo:\n" + e.getMessage(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null; 
    }

    public static void agregarErrorLexico(String lexema, int linea, int columna) {
        String descripcion = "Símbolo no reconocido: '" + lexema + "'";
        listaErrores.add(new ErrorCompilador(ErrorCompilador.TipoError.LEXICO, linea, columna, descripcion));
    }

    public static void agregarErrorSintactico(String descripcion, int linea, int columna) {
        listaErrores.add(new ErrorCompilador(ErrorCompilador.TipoError.SINTACTICO, linea, columna, descripcion));
    }

    public static List<ErrorCompilador> getErrores() {
        return listaErrores;
    }


    public static List<Token> obtenerTokens(String codigoFuente) {
        listaErrores.clear(); 
        List<Token> listaTokensValidos = new ArrayList<>();
        
        if (codigoFuente == null || codigoFuente.isEmpty()) {
            return listaTokensValidos;
        }
        
        try {
            Reader reader = new StringReader(codigoFuente);
            AnalizadorLexico lexer = new AnalizadorLexico(reader);
            Token token;
            
            while ((token = lexer.yylex()) != null) {
                if (token.getTipo() != TokenType.ERROR) {
                    listaTokensValidos.add(token);
                }
            }

            // $ al final para la Pila
            listaTokensValidos.add(new Token(TokenType.EOF, "$", 0, 0));

        } catch (IOException e) { 
             agregarErrorLexico("Error de lectura: " + e.getMessage(), 0, 0);
        } catch (Exception e) {
             agregarErrorLexico("Error interno del lexer: " + e.getMessage(), 0, 0);
        }
        return listaTokensValidos;
    }
   
    public static String ejecutarAnalisisSintactico(List<Token> tokens) {
        AnalizadorSintactico parser = new AnalizadorSintactico();
        List<AnalizadorSintactico.PasoAnalisis> pasos = parser.analizar(tokens);
        
        // Formato visual para el JTextArea
        StringBuilder sb = new StringBuilder();
        sb.append("REPORTE DE LA PILA SINTÁCTICA\n");
        sb.append("========================================================================================================================\n");
        // Ajustamos las cabeceras para incluir la Pila Auxiliar
        sb.append(String.format("%-50s | %-30s | %-15s | %-30s\n", "PILA PRINCIPAL", "PILA AUX", "ENTRADA", "ACCIÓN"));
        sb.append("------------------------------------------------------------------------------------------------------------------------\n");
        
        for (AnalizadorSintactico.PasoAnalisis p : pasos) {
            // Recortar Pila Principal si es muy larga
            String pilaMain = p.pilaPrincipal;
            if (pilaMain.length() > 50) pilaMain = "..." + pilaMain.substring(pilaMain.length() - 47);

            // Recortar Pila Auxiliar si es muy larga
            String pilaAux = p.pilaAux;
            if (pilaAux.length() > 30) pilaAux = "..." + pilaAux.substring(pilaAux.length() - 27);
            
            // Usamos p.pilaPrincipal en lugar de p.pila
            sb.append(String.format("%-50s | %-30s | %-15s | %-30s\n", pilaMain, pilaAux, p.entrada, p.accion));
        }
        sb.append("========================================================================================================================\n");

        if (listaErrores.isEmpty()) {
            sb.append("\n>>> CÓDIGO SINTÁCTICAMENTE CORRECTO <<<");
        } else {
            sb.append("\n>>> SE DETECTARON " + listaErrores.size() + " ERRORES (Ver pestaña de Errores) <<<");
        }
        
        return sb.toString();
    }
    public static String obtenerComponente(TokenType tipo) {
         if (tipo == null) return "Desconocido";
        switch (tipo) {
            case INT ,FLOAT, BOOLEAN, STRING: return "Tipo de Dato";
            case TRUE, FALSE: return "Booleano";
            case ID: return "Identificador";
            case NUM: return "Número";
            case CADENA_LITERAL: return "Cadena";
            case INCREMENTO, DECREMENTO: return "Operador Unario";
            case SUMA, RESTA, MULTIPLICACION, DIVISION, POTENCIA, MODULO: return "Aritmético";
            case IGUAL, DIFERENTE, MENOR, MAYOR, MENOR_IGUAL, MAYOR_IGUAL: return "Relacional";
            case AND, OR ,NOT: return "Lógico";
            case ASIGNACION: return "Asignación";
            case PUNTOYCOMA, PAREN_ABRE, PAREN_CIERRA, LLAVE_ABRE, LLAVE_CIERRA: return "Delimitador";
            case EOF: return "Fin de Archivo";
            case ERROR: return "Error Léxico";
            default: return "Símbolo";
        }
    }
    
    public static void deshacer() {
        if (undoManager.canUndo()) {
             try { undoManager.undo(); } catch (CannotUndoException e) { e.printStackTrace(); }
        }
    }

    public static void rehacer() {
        if (undoManager.canRedo()) {
             try { undoManager.redo(); } catch (CannotRedoException e) { e.printStackTrace(); }
        }
    }

    public static UndoManager getUndoManager() {
        return undoManager;
    }
}
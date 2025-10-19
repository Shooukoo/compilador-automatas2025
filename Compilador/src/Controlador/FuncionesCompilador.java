package Controlador;

import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Analizador.Token;
import Analizador.AnalizadorLexico;
import Analizador.TokenType;

public class FuncionesCompilador {

    private static UndoManager undoManager = new UndoManager();
    private static File archivoActual = null;

    public static File getArchivoActual() {
        return archivoActual;
    }

    public static String abrirArchivo(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            archivoActual = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(archivoActual))) {
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                return contenido.toString();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error al abrir archivo:\n" + e.getMessage());
            }
        }
        return null;
    }

    public static File guardarArchivo(JFrame parent, File archivo, String contenido) {
        try {
            if (archivo == null) {
                JFileChooser chooser = new JFileChooser();
                int resultado = chooser.showSaveDialog(parent);
                if (resultado != JFileChooser.APPROVE_OPTION) return null;
                archivo = chooser.getSelectedFile();

                if (!archivo.getName().toLowerCase().endsWith(".txt")) {
                    archivo = new File(archivo.getAbsolutePath() + ".txt");
                }
            }

            try (FileWriter writer = new FileWriter(archivo)) {
                writer.write(contenido);
                archivoActual = archivo;
                JOptionPane.showMessageDialog(parent, "Archivo guardado correctamente");
                return archivo;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error al guardar archivo:\n" + e.getMessage());
        }
        return null;
    }

    // ANALIZADORES
    public static List<Token> obtenerTokens(String codigoFuente) {
        List<Token> lista = new ArrayList<>();
        try {
            Reader reader = new StringReader(codigoFuente);
            AnalizadorLexico lexer = new AnalizadorLexico(reader);
            Token token;
            while ((token = lexer.yylex()) != null) {
                lista.add(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static String obtenerComponente(TokenType tipo) {
        switch (tipo) {
            case INT ,FLOAT, BOOLEAN, STRING:
                return "Tipo de dato";
            case TRUE, FALSE:
                return "Literal booleano";
            case SUMA, RESTA, MULTIPLICACION, DIVISION:
                return "Operador aritmético";
            case IGUAL, DIFERENTE, MENOR, MAYOR, MENOR_IGUAL, MAYOR_IGUAL:
                return "Operador relacional";
            case AND, OR ,NOT:
                return "Operador lógico";
            case ASIGNACION, PUNTOYCOMA, PAREN_ABRE, PAREN_CIERRA, LLAVE_ABRE, LLAVE_CIERRA:
                return "Símbolo";
            case IDENTIFICADOR:
                return "Identificador";
            case NUMERO:
                return "Número";
            case CADENA:
                return "Cadena";
            default:
                return "Desconocido";
        }
    }
    
    public static void deshacer() {
        if (undoManager.canUndo()) undoManager.undo();
    }

    public static void rehacer() {
        if (undoManager.canRedo()) undoManager.redo();
    }

    public static UndoManager getUndoManager() {
        return undoManager;
    }
}

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

    public static String abrirArchivo(JFrame parent) {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showOpenDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileReader reader = new FileReader(file)) {
                char[] buffer = new char[(int) file.length()];
                reader.read(buffer);
                return new String(buffer);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error al abrir archivo:\n" + e.getMessage());
            }
        }
        return null;
    }

    public static void guardarArchivo(JFrame parent, String contenido) {
        JFileChooser chooser = new JFileChooser();
        int resultado = chooser.showSaveDialog(parent);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(contenido);
                JOptionPane.showMessageDialog(parent, "Archivo guardado correctamente");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error al guardar archivo:\n" + e.getMessage());
            }
        }
    }

    public static void nuevoArchivo(JTextArea editor, JFrame frame) {
        editor.setText("");
        frame.setTitle("Prototipo Compilador - Nuevo archivo");
    }

    public static String ejecutarAnalizadorLexico(String codigoFuente) {
        StringBuilder resultado = new StringBuilder();

        // Encabezado de la tabla
        resultado.append(String.format("%-15s | %-15s | %-20s\n", "Lexema", "Patron", "Componente"));
        resultado.append("--------------------------------------------------------------\n");

        try {
            Reader reader = new StringReader(codigoFuente);
            AnalizadorLexico lexer = new AnalizadorLexico(reader);
            Token token;

            while ((token = lexer.yylex()) != null) {
                String lexema = token.valor;                       // acceso directo al campo
                String patron = token.tipo.name();
                String componente = obtenerComponente(token.tipo);

                resultado.append(String.format("%-15s | %-15s | %-20s\n",
                        lexema, patron, componente));
            }
        } catch (Exception e) {
            resultado.append("Error en análisis léxico: ").append(e.getMessage());
        }

        return resultado.toString();
    }

    // === Método auxiliar para obtener el nombre del componente según TokenType ===
    public static String obtenerComponente(TokenType tipo) {
        switch (tipo) {
            case INT: case FLOAT: case BOOLEAN: case STRING:
                return "Tipo de dato";
            case TRUE: case FALSE:
                return "Literal booleano";
            case SUMA: case RESTA: case MULTIPLICACION: case DIVISION: case POTENCIA: case MODULO:
                return "Operador aritmético";
            case IGUAL: case DIFERENTE: case MENOR: case MAYOR: case MENOR_IGUAL: case MAYOR_IGUAL:
                return "Operador relacional";
            case AND: case OR: case NOT:
                return "Operador lógico";
            case ASIGNACION: case PUNTOYCOMA: case PAREN_ABRE: case PAREN_CIERRA: case LLAVE_ABRE: case LLAVE_CIERRA:
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


    public static String ejecutarAnalizadorSintactico(String codigo) {
        return "Estado de pila sintáctica simulado para " + codigo.length() + " caracteres";
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

    public static String formatearCodigo(String codigo) {
        return codigo.replaceAll(";", ";\n");
    }

    public static String comentarDescomentar(String codigo) {
        if (codigo.startsWith("//")) {
            return codigo.replaceFirst("//", "");
        } else {
            return "//" + codigo;
        }
    }

    public static void buscar(JTextArea editor, JFrame parent) {
        String busqueda = JOptionPane.showInputDialog(parent, "Buscar texto:");
        if (busqueda != null && !busqueda.isEmpty()) {
            int index = editor.getText().indexOf(busqueda);
            if (index >= 0) {
                editor.select(index, index + busqueda.length());
                editor.requestFocus();
            } else {
                JOptionPane.showMessageDialog(parent, "Texto no encontrado");
            }
        }
    }

    public static void reemplazar(JTextArea editor, JFrame parent) {
        String busqueda = JOptionPane.showInputDialog(parent, "Texto a buscar:");
        String reemplazo = JOptionPane.showInputDialog(parent, "Texto de reemplazo:");
        if (busqueda != null && reemplazo != null) {
            editor.setText(editor.getText().replace(busqueda, reemplazo));
        }
    }

    public static void ejecutar(String codigo, JTextArea areaSalida) {
        areaSalida.setText("Ejecutando programa...\nCódigo tiene " + codigo.length() + " caracteres");
    }

    public static void depurar(String codigo, JTextArea areaSalida) {
        areaSalida.setText("Depurando programa...\nCódigo tiene " + codigo.length() + " caracteres");
    }

    public static void abrirDocumentacion(JFrame parent) {
        JOptionPane.showMessageDialog(parent, "Documentación del compilador v1.0");
    }

    public static void acercaDe(JFrame parent) {
        JOptionPane.showMessageDialog(parent, "Compilador v1.0\nDesarrollado para la materia de Automatas");
    }
}

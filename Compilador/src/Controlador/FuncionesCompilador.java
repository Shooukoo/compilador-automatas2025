package Controlador;

import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;

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

    public static String ejecutarAnalizadorLexico(String codigo) {
        return "Resultado análisis léxico de " + codigo.length() + " caracteres";
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

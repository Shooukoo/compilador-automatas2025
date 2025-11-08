package Controlador;

import javax.swing.*;
import javax.swing.undo.*;
import java.io.*;
import java.nio.charset.StandardCharsets; // Importar para UTF-8
import java.util.ArrayList;
import java.util.List;

import Analizador.Token;
import Analizador.AnalizadorLexico;
import Analizador.TokenType;
import Analizador.ErrorCompilador; // Importar la nueva clase

/**
 * Clase controladora que maneja la lógica principal del compilador,
 * incluyendo manejo de archivos, análisis léxico, gestión de errores y undo/redo.
 */
public class FuncionesCompilador {

    private static UndoManager undoManager = new UndoManager();
    private static File archivoActual = null;
    private static List<ErrorCompilador> listaErrores = new ArrayList<>(); // Lista para guardar errores

    /**
     * Devuelve el archivo actualmente abierto o guardado.
     * @return El archivo actual (puede ser null).
     */
    public static File getArchivoActual() {
        return archivoActual;
    }

    /**
     * Abre un archivo seleccionado por el usuario.
     * @param parent El JFrame padre para mostrar el diálogo.
     * @return El contenido del archivo como String, o null si ocurre un error o se cancela.
     */
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
            // Usar try-with-resources para asegurar que el lector se cierre
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivoActual), StandardCharsets.UTF_8))) {
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                 // Quitar el último \n si se añadió
                 if (contenido.length() > 0 && contenido.charAt(contenido.length() - 1) == '\n') {
                    contenido.deleteCharAt(contenido.length() - 1);
                 }
                undoManager = new UndoManager(); // Resetear historial de undo al abrir
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

    /**
     * Guarda el contenido en un archivo. Pregunta al usuario si el archivo es nuevo.
     * @param parent El JFrame padre para mostrar el diálogo.
     * @param archivo El archivo destino (puede ser null para archivos nuevos).
     * @param contenido El String con el contenido a guardar.
     * @return El archivo donde se guardó, o null si ocurre un error o se cancela.
     */
    public static File guardarArchivo(JFrame parent, File archivo, String contenido) {
        File archivoParaGuardar = archivo;
        try {
            if (archivoParaGuardar == null) { // Si es "Guardar Como" o archivo nuevo
                JFileChooser chooser = new JFileChooser();
                if (archivoActual != null) {
                    chooser.setCurrentDirectory(archivoActual.getParentFile());
                } else {
                     chooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                }

                int resultado = chooser.showSaveDialog(parent);
                if (resultado != JFileChooser.APPROVE_OPTION) {
                    return null; // El usuario canceló
                }
                archivoParaGuardar = chooser.getSelectedFile();

                // Asegurar la extensión .txt si no tiene una
                String nombreArchivo = archivoParaGuardar.getName();
                if (!nombreArchivo.contains(".")) {
                    archivoParaGuardar = new File(archivoParaGuardar.getAbsolutePath() + ".txt");
                }

                // Confirmar si el archivo ya existe
                if (archivoParaGuardar.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(parent,
                            "El archivo '" + archivoParaGuardar.getName() + "' ya existe.\n¿Deseas sobrescribirlo?",
                            "Confirmar Sobrescritura",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (confirm != JOptionPane.YES_OPTION) {
                        return null; // El usuario no quiso sobrescribir
                    }
                }
            }

            // Escribir en el archivo usando try-with-resources y UTF-8
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivoParaGuardar), StandardCharsets.UTF_8))) {
                writer.write(contenido);
                archivoActual = archivoParaGuardar; // Actualizar la referencia global
                return archivoParaGuardar; // Devolver el archivo guardado
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(parent, "Error de E/S al guardar archivo:\n" + e.getMessage(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error inesperado al guardar archivo:\n" + e.getMessage(), "Error al Guardar", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return null; // Indicar que hubo un error
    }


    // --- MANEJO DE ERRORES ---

    /**
     * Añade un error léxico a la lista de errores. Es llamado estáticamente desde el Lexer.
     * @param lexema El texto que causó el error.
     * @param linea La línea del error (empezando en 1).
     * @param columna La columna del error (empezando en 1).
     */
    public static void agregarErrorLexico(String lexema, int linea, int columna) {
        String descripcion = "Símbolo no reconocido: '" + lexema + "'";
        listaErrores.add(new ErrorCompilador(ErrorCompilador.TipoError.LEXICO, linea, columna, descripcion));
    }

     /**
     * Añade un error sintáctico a la lista de errores.
     * (Este método se usará cuando implementes el parser)
     * @param descripcion Descripción específica del error sintáctico.
     * @param linea La línea del token que causó el error.
     * @param columna La columna del token.
     */
    public static void agregarErrorSintactico(String descripcion, int linea, int columna) {
        listaErrores.add(new ErrorCompilador(ErrorCompilador.TipoError.SINTACTICO, linea, columna, descripcion));
    }


    /**
     * Devuelve la lista actual de errores detectados.
     * @return Lista de objetos ErrorCompilador.
     */
    public static List<ErrorCompilador> getErrores() {
        return listaErrores;
    }

    // --- ANALIZADORES ---

    /**
     * Realiza el análisis léxico del código fuente proporcionado.
     * Limpia la lista de errores antes de empezar.
     * @param codigoFuente El código a analizar.
     * @return Una lista de Tokens VÁLIDOS. Los errores se registran en listaErrores.
     */
    public static List<Token> obtenerTokens(String codigoFuente) {
        listaErrores.clear(); // Limpiar errores de análisis anteriores
        List<Token> listaTokensValidos = new ArrayList<>();
        
        if (codigoFuente == null || codigoFuente.isEmpty()) {
            return listaTokensValidos; // No analizar si no hay código
        }
        
        try {
            Reader reader = new StringReader(codigoFuente);
            AnalizadorLexico lexer = new AnalizadorLexico(reader);
            Token token;
            
            // Recorrer todos los tokens generados por JFlex
            while ((token = lexer.yylex()) != null) {
                // Si el token NO es de tipo ERROR, se añade a la lista de tokens válidos
                // Si ES de tipo ERROR, ya fue registrado en la lista de errores por el
                // método errorToken() del lexer, así que simplemente lo ignoramos aquí.
                if (token.getTipo() != TokenType.ERROR) {
                    listaTokensValidos.add(token);
                }
            }
        } catch (IOException e) { 
             agregarErrorLexico("Error de lectura durante el análisis: " + e.getMessage(), 0, 0);
             e.printStackTrace();
        } catch (Exception e) {
             agregarErrorLexico("Error interno inesperado del analizador: " + e.getMessage(), 0, 0);
             e.printStackTrace();
        }
        return listaTokensValidos; // Devolver solo los tokens válidos
    }


    /**
     * Devuelve una descripción textual del componente léxico basado en su tipo.
     * @param tipo El TokenType del token.
     * @return Una cadena descriptiva.
     */
    public static String obtenerComponente(TokenType tipo) {
         if (tipo == null) return "Desconocido";
        switch (tipo) {
            case INT ,FLOAT, BOOLEAN, STRING:
                return "Tipo de dato";
            case TRUE, FALSE:
                return "Literal booleano";
            case SUMA, RESTA, MULTIPLICACION, DIVISION, POTENCIA, MODULO:
                return "Operador aritmético";
            case IGUAL, DIFERENTE, MENOR, MAYOR, MENOR_IGUAL, MAYOR_IGUAL:
                return "Operador relacional";
            case AND, OR ,NOT:
                return "Operador lógico";
            case ASIGNACION:
                return "Operador de asignación";
            case PUNTOYCOMA:
                return "Delimitador (Punto y coma)";
            case PAREN_ABRE, PAREN_CIERRA:
                return "Delimitador (Paréntesis)";
            case LLAVE_ABRE, LLAVE_CIERRA:
                return "Delimitador (Llave)";
            case IDENTIFICADOR:
                return "Identificador";
            case NUMERO:
                return "Literal numérico";
            case CADENA:
                return "Literal de cadena";
            case ERROR: // Añadido
                return "Error Léxico";
            default:
                return "Desconocido";
        }
    }

    // --- Deshacer/Rehacer ---

    public static void deshacer() {
        if (undoManager.canUndo()) {
             try {
                undoManager.undo();
             } catch (CannotUndoException e) {
                System.err.println("Error al deshacer: " + e.getMessage());
             }
        }
    }

    public static void rehacer() {
        if (undoManager.canRedo()) {
             try {
                 undoManager.redo();
             } catch (CannotRedoException e) {
                 System.err.println("Error al rehacer: " + e.getMessage());
             }
        }
    }

    public static UndoManager getUndoManager() {
        return undoManager;
    }
}
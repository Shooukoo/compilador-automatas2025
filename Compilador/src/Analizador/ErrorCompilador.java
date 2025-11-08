package Analizador;

/**
 * Representa un error detectado durante la compilación (léxico, sintáctico o semántico).
 */
public class ErrorCompilador {

    /**
     * Define el tipo de error según la fase de compilación.
     */
    public enum TipoError {
        LEXICO, SINTACTICO, SEMANTICO
    }

    private TipoError tipo;
    private int linea;
    private int columna;
    private String descripcion;

    /**
     * Constructor para un nuevo error.
     * @param tipo El tipo de error (LEXICO, SINTACTICO, etc.).
     * @param linea La línea donde ocurrió el error (empezando en 1).
     * @param columna La columna donde ocurrió el error (empezando en 1).
     * @param descripcion Una descripción del error.
     */
    public ErrorCompilador(TipoError tipo, int linea, int columna, String descripcion) {
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.descripcion = descripcion;
    }

    // --- Getters ---

    public TipoError getTipo() { 
        return tipo; 
    }
    
    public int getLinea() { 
        return linea; 
    }
    
    public int getColumna() { 
        return columna; 
    }
    
    public String getDescripcion() { 
        return descripcion; 
    }

    /**
     * Devuelve una representación en String del error, formateada para la consola.
     * @return String formateado del error.
     */
    @Override
    public String toString() {
        return String.format("[%s] Línea %d, Columna %d: %s", tipo, linea, columna, descripcion);
    }
}
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
     */
    public ErrorCompilador(TipoError tipo, int linea, int columna, String descripcion) {
        this.tipo = tipo;
        this.linea = linea;
        this.columna = columna;
        this.descripcion = descripcion;
    }


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

    @Override
    public String toString() {
        return String.format("[%s] Línea %d, Columna %d: %s", tipo, linea, columna, descripcion);
    }
}
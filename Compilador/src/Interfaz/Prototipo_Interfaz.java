package Interfaz;

import Controlador.FuncionesCompilador;
import Analizador.Token;
import Analizador.ErrorCompilador; // Importar ErrorCompilador
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Clase principal de la interfaz gráfica del prototipo de compilador.
 * Incluye un editor de texto con pestañas, paneles para análisis y una consola de errores.
 */
public class Prototipo_Interfaz extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;         // Contenedor de pestañas para editores
    private JTable tablaLexico;             // Tabla para mostrar tokens léxicos
    private JTable tablaSintactico;         // Tabla para análisis sintáctico (futuro)
    private JTextArea consolaErrores;       // Área de texto para mostrar errores
    private UndoManager undoManagerGlobal;  // Gestor de undo/redo

    private final String TITULO_BASE = "Prototipo Compilador";

    /**
     * Punto de entrada principal de la aplicación.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                // Configurar Look and Feel del sistema
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("No se pudo establecer el Look and Feel del sistema.");
                }

                Prototipo_Interfaz frame = new Prototipo_Interfaz();
                frame.setLocationRelativeTo(null); // Centrar la ventana
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al iniciar la aplicación:\n" + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    public Prototipo_Interfaz() {
        
        undoManagerGlobal = FuncionesCompilador.getUndoManager();

        setTitle(TITULO_BASE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);
        setMinimumSize(new Dimension(800, 600));

        // --- Panel Principal ---
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // --- Barra de Menú ---
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(crearMenu("Archivo", "Nuevo", "Abrir", "Guardar", "Salir"));
        menuBar.add(crearMenu("Editar", "Copiar", "Pegar", "Deshacer", "Rehacer"));
        menuBar.add(crearMenu("Análisis", "Analizador Léxico", "Analizador Sintáctico"));

     // --- BARRA DE ACCESOS RÁPIDOS CON ÍCONOS ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

     // BIEN: Rutas de recursos (Classpath)
        toolBar.add(crearBotonConIcono("Nuevo", "Archivo>Nuevo", "/Interfaz/Iconos/nuevo.png"));
        toolBar.add(crearBotonConIcono("Abrir", "Archivo>Abrir", "/Interfaz/Iconos/abrir.png"));
        toolBar.add(crearBotonConIcono("Guardar", "Archivo>Guardar", "/Interfaz/Iconos/guardar.png"));
        toolBar.add(crearSeparadorPuntos());
        toolBar.add(crearBotonConIcono("Léxico", "Análisis>Analizador Léxico", "/Interfaz/Iconos/lexico.png")); 
        toolBar.add(crearBotonConIcono("Sintáctico", "Análisis>Analizador Sintáctico", "/Interfaz/Iconos/sintactico.png"));
        contentPane.add(toolBar, BorderLayout.NORTH);


        // --- Área Central: Editor y Paneles de Análisis ---
        tabbedPane = new JTabbedPane();

        // --- Paneles Laterales (Derecha) ---
        JSplitPane panelDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        panelDerecho.setResizeWeight(0.5); // Distribuir 50/50

        // Panel para Tabla Léxica
        JPanel panelLexico = new JPanel(new BorderLayout());
        tablaLexico = new JTable();
        tablaLexico.setFillsViewportHeight(true);
        tablaLexico.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Para que funcione el scroll horizontal
        JScrollPane scrollLexico = new JScrollPane(tablaLexico,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelLexico.add(scrollLexico, BorderLayout.CENTER);
        panelLexico.setBorder(BorderFactory.createTitledBorder("Análisis Léxico"));

        // Panel para Tabla Sintáctica
        JPanel panelSintactico = new JPanel(new BorderLayout());
        tablaSintactico = new JTable();
        tablaSintactico.setFillsViewportHeight(true);
        tablaSintactico.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollSintactico = new JScrollPane(tablaSintactico,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelSintactico.add(scrollSintactico, BorderLayout.CENTER);
        panelSintactico.setBorder(BorderFactory.createTitledBorder("Análisis Sintáctico"));
        
        panelDerecho.setTopComponent(panelLexico);
        panelDerecho.setBottomComponent(panelSintactico);
        panelDerecho.setDividerLocation(300);

        // --- Divisor Principal Horizontal (Editor a la Izq, Paneles a la Der) ---
        JSplitPane mainSplitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, panelDerecho);
        mainSplitHorizontal.setResizeWeight(0.7); // 70% para el editor
        mainSplitHorizontal.setDividerLocation(750);

        // --- Panel Inferior para la Consola de Errores ---
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Consola"));
        consolaErrores = new JTextArea();
        consolaErrores.setEditable(false);
        consolaErrores.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollConsola = new JScrollPane(consolaErrores);
        panelInferior.add(scrollConsola, BorderLayout.CENTER);
        panelInferior.setPreferredSize(new Dimension(0, 150)); // Altura preferida

        // --- Divisor Vertical Final (Área Central Arriba, Consola Abajo) ---
        JSplitPane mainSplitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitHorizontal, panelInferior);
        mainSplitVertical.setResizeWeight(0.8); // 80% para el área superior
        mainSplitVertical.setDividerLocation(550); // Ajustar esta altura

        contentPane.add(mainSplitVertical, BorderLayout.CENTER);

        // Añadir listener para actualizar el título de la ventana cuando cambia la pestaña
        tabbedPane.addChangeListener(e -> actualizarTituloVentana());
    }

    /**
     * Crea un menú con sus opciones.
     */
    private JMenu crearMenu(String nombre, String... opciones) {
        JMenu menu = new JMenu(nombre);
        for (String opcion : opciones) {
            if (opcion.equals("-")) {
                menu.addSeparator();
            } else {
                JMenuItem item = new JMenuItem(opcion);
                item.setActionCommand(nombre + ">" + opcion);
                item.addActionListener(this);
                menu.add(item);
            }
        }
        return menu;
    }

    /**
     * Crea un nuevo componente JTextArea configurado como editor de código.
     */
    private JTextArea crearNuevoEditor() {
        JTextArea editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.setTabSize(4);
        
        // Conectar el editor al UndoManager global
        editor.getDocument().addUndoableEditListener(undoManagerGlobal);

        // Listener para marcar la pestaña como modificada
        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void marcarModificado() {
                Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, editor);
                if (scrollPane == null) return;

                int index = tabbedPane.indexOfComponent((Component) scrollPane);
                if (index != -1) {
                    // Usar la propiedad 'modificado' del JScrollPane
                    ((JComponent) scrollPane).putClientProperty("modificado", Boolean.TRUE);
                    
                    Component tabComponent = tabbedPane.getTabComponentAt(index);
                    if (tabComponent instanceof JPanel) {
                        for (Component comp : ((JPanel) tabComponent).getComponents()) {
                            if (comp instanceof JLabel) {
                                String textoActual = ((JLabel) comp).getText().trim(); // Quitar espacio extra
                                if (!textoActual.endsWith("*")) {
                                    ((JLabel) comp).setText(textoActual + "* "); // Añadir * y espacio
                                    actualizarTituloVentana(); // Actualizar título de la ventana también
                                }
                                break;
                            }
                        }
                    }
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { marcarModificado(); }
            @Override public void removeUpdate(DocumentEvent e) { marcarModificado(); }
            @Override public void changedUpdate(DocumentEvent e) { /* Cambios de estilo, usualmente no marcan como modificado */ }
        });

        return editor;
    }

    /**
     * Añade una nueva pestaña al JTabbedPane con un editor.
     */
    private void agregarNuevaPestana(String nombre, JTextArea editor, File archivo) {
        JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.putClientProperty("archivo", archivo);
        scrollPane.putClientProperty("modificado", Boolean.FALSE); // Inicia como no modificado
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Añadir Numeración de Líneas
        NumeroLinea numeroLinea = new NumeroLinea(editor);
        scrollPane.setRowHeaderView(numeroLinea);

        editor.setMargin(new Insets(5, 5, 5, 5));

        // Añadir la Pestaña al TabbedPane
        tabbedPane.addTab(nombre, scrollPane); // El título aquí es solo una referencia
        int index = tabbedPane.indexOfComponent(scrollPane);

        // Crear Componente Personalizado para la Pestaña (Título + Botón 'x')
        tabbedPane.setTabComponentAt(index, crearTabConBoton(nombre, scrollPane));

        // Seleccionar la nueva pestaña creada
        tabbedPane.setSelectedComponent(scrollPane);
        editor.requestFocusInWindow(); // Dar foco al editor
    }


    /**
     * Crea el componente visual para una pestaña (un JPanel con JLabel y JButton).
     */
    private JPanel crearTabConBoton(String nombre, JScrollPane scrollPane) {
        JPanel pnlTab = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTab.setOpaque(false);

        JLabel lblTitulo = new JLabel(nombre + " ");
        JButton btnCerrar = new JButton("x");

        // Estilo del Botón de Cierre
        btnCerrar.setPreferredSize(new Dimension(17, 17));
        btnCerrar.setMargin(new Insets(0, 0, 0, 0));
        btnCerrar.setToolTipText("Cerrar " + nombre);
        btnCerrar.setBorder(BorderFactory.createEmptyBorder());
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusable(false);
        btnCerrar.setRolloverEnabled(true);
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCerrar.setForeground(Color.RED);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCerrar.setForeground(Color.BLACK);
            }
        });

        pnlTab.add(lblTitulo);
        pnlTab.add(btnCerrar);

        // Acción del Botón de Cierre
        btnCerrar.addActionListener(e -> cerrarPestana(scrollPane));

        return pnlTab;
    }

    /**
     * Lógica para cerrar una pestaña, verificando cambios no guardados.
     * @param scrollPane El JScrollPane de la pestaña a cerrar.
     */
    private void cerrarPestana(JScrollPane scrollPane) {
        int idx = tabbedPane.indexOfComponent(scrollPane);
        if (idx == -1) return;

        boolean modificado = (Boolean) scrollPane.getClientProperty("modificado");
        String tituloPestana = tabbedPane.getTitleAt(idx); // Nombre base guardado

        if (modificado) {
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo '" + tituloPestana + "' tiene cambios no guardados.\n¿Deseas guardarlos antes de cerrar?",
                    "Guardar Cambios",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (opcion == JOptionPane.YES_OPTION) {
                // Seleccionar la pestaña antes de intentar guardarla
                tabbedPane.setSelectedIndex(idx); 
                if (guardarArchivoActual()) {
                    tabbedPane.remove(idx); // Cerrar si se guardó
                }
                // Si guardarArchivoActual() devuelve false no se cierra
            } else if (opcion == JOptionPane.NO_OPTION) {
                tabbedPane.remove(idx); // Cerrar sin guardar
            }
            // Si es CANCEL_OPTION, no hacer nada
        } else {
            tabbedPane.remove(idx); // No modificado, cerrar directamente
        }
    }


    /**
     * Actualiza el título de la ventana principal para reflejar el archivo activo.
     */
    private void actualizarTituloVentana() {
        JScrollPane scrollActivo = getScrollActivo();
        if (scrollActivo != null) {
            File archivo = (File) scrollActivo.getClientProperty("archivo");
            int index = tabbedPane.getSelectedIndex();
            String tituloPestana = tabbedPane.getTitleAt(index);

            String tituloCompleto;
            if (archivo != null) {
                tituloCompleto = TITULO_BASE + " - " + archivo.getAbsolutePath();
            } else {
                 tituloCompleto = TITULO_BASE + " - " + tituloPestana;
            }
             
             boolean modificado = (Boolean)scrollActivo.getClientProperty("modificado");
             if (modificado) {
                tituloCompleto += "*";
             }
            setTitle(tituloCompleto);
        } else {
            setTitle(TITULO_BASE); // Sin pestañas
        }
    }


    /**
     * Obtiene el componente JTextArea del editor actualmente activo.
     */
    private JTextArea getEditorActivo() {
        Component seleccionado = tabbedPane.getSelectedComponent();
        if (seleccionado instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) seleccionado;
            if (scrollPane.getViewport().getView() instanceof JTextArea) {
                return (JTextArea) scrollPane.getViewport().getView();
            }
        }
        return null;
    }

    /**
     * Obtiene el componente JScrollPane de la pestaña actualmente activa.
     */
    private JScrollPane getScrollActivo() {
        Component seleccionado = tabbedPane.getSelectedComponent();
        if (seleccionado instanceof JScrollPane) {
            return (JScrollPane) seleccionado;
        }
        return null;
    }

    /**
     * Intenta guardar el archivo de la pestaña activa.
     * @return true si el archivo se guardó, false si se canceló o hubo error.
     */
    private boolean guardarArchivoActual() {
        JScrollPane scroll = getScrollActivo();
        JTextArea editor = getEditorActivo();

        if (scroll == null || editor == null) {
            JOptionPane.showMessageDialog(this, "No hay archivo activo para guardar.", "Guardar Archivo", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        File archivoActual = (File) scroll.getClientProperty("archivo");
        String contenido = editor.getText();

        File archivoGuardado = FuncionesCompilador.guardarArchivo(this, archivoActual, contenido);

        if (archivoGuardado != null) {
            // Actualizar la información en la pestaña
            scroll.putClientProperty("archivo", archivoGuardado);
            scroll.putClientProperty("modificado", Boolean.FALSE);

            int idx = tabbedPane.getSelectedIndex();
            String nuevoNombre = archivoGuardado.getName();
            tabbedPane.setTitleAt(idx, nuevoNombre); // Actualizar título base (referencia interna)

            // Actualizar el componente visual de la pestaña
            Component tabComponent = tabbedPane.getTabComponentAt(idx);
            if (tabComponent instanceof JPanel) {
               for(Component comp : ((JPanel)tabComponent).getComponents()){
                   if(comp instanceof JLabel){
                       ((JLabel)comp).setText(nuevoNombre + " "); // Quitar el '*'
                   }
                   if(comp instanceof JButton){
                       ((JButton)comp).setToolTipText("Cerrar " + nuevoNombre);
                   }
               }
            }
            actualizarTituloVentana();
            return true; // Guardado exitoso
        } else {
            return false; // Guardado cancelado o fallido
        }
    }


    /**
     * Manejador central de eventos para los menús.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        JTextArea editor = getEditorActivo();

        switch (comando) {
            // --- Archivo ---
            case "Archivo>Nuevo":
                agregarNuevaPestana("Sin título " + (tabbedPane.getTabCount() + 1), crearNuevoEditor(), null);
                break;

            case "Archivo>Abrir":
                String contenido = FuncionesCompilador.abrirArchivo(this);
                if (contenido != null) {
                    File archivoAbierto = FuncionesCompilador.getArchivoActual();
                    
                    // Verificar si ya está abierto
                    for(int i=0; i < tabbedPane.getTabCount(); i++){
                        JScrollPane sp = (JScrollPane)tabbedPane.getComponentAt(i);
                        File f = (File)sp.getClientProperty("archivo");
                        if(f != null && f.equals(archivoAbierto)){
                            tabbedPane.setSelectedIndex(i); // Seleccionar pestaña existente
                            return; // No abrir de nuevo
                        }
                    }
                    
                    JTextArea nuevoEditor = crearNuevoEditor();
                    nuevoEditor.setText(contenido);
                    agregarNuevaPestana(archivoAbierto.getName(), nuevoEditor, archivoAbierto);
                    nuevoEditor.setCaretPosition(0); // Cursor al inicio
                    FuncionesCompilador.getUndoManager().discardAllEdits(); // Limpiar historial
                }
                break;

            case "Archivo>Guardar":
                guardarArchivoActual();
                break;

            case "Archivo>Salir":
                System.exit(0);
                break;

            case "Editar>Copiar":
                if (editor != null) editor.copy();
                break;
            case "Editar>Pegar":
                if (editor != null) editor.paste();
                break;
            case "Editar>Deshacer":
                 FuncionesCompilador.deshacer();
                break;
            case "Editar>Rehacer":
                 FuncionesCompilador.rehacer();
                break;

            case "Análisis>Analizador Léxico":
                if (editor != null) {
                    mostrarResultadoAnalisisLexico(editor.getText());
                } else {
                     limpiarConsola();
                     consolaErrores.setForeground(Color.ORANGE.darker());
                     consolaErrores.setText("No hay un editor activo para analizar.");
                     tablaLexico.setModel(new DefaultTableModel());
                     tablaSintactico.setModel(new DefaultTableModel());
                }
                break;

            case "Análisis>Analizador Sintáctico":
                 limpiarConsola();
                 consolaErrores.setForeground(Color.BLUE.darker());
                 consolaErrores.append("Análisis sintáctico aún no implementado.");
                 tablaSintactico.setModel(new DefaultTableModel());
                 // Opcional: limpiar tabla léxica
                 // tablaLexico.setModel(new DefaultTableModel());
                break;

             default:
                System.out.println("Comando no reconocido: " + comando);
                break;
        }
    }

    /**
     * Limpia el contenido de la consola de errores.
     */
     private void limpiarConsola() {
        consolaErrores.setText("");
     }


    /**
     * Ejecuta el análisis léxico y muestra los resultados (tokens en tabla, errores en consola).
     * @param codigoFuente El código fuente a analizar.
     */
    private void mostrarResultadoAnalisisLexico(String codigoFuente) {
        // 1. Ejecutar el análisis
        List<Token> tokensValidos = FuncionesCompilador.obtenerTokens(codigoFuente);
        List<ErrorCompilador> errores = FuncionesCompilador.getErrores();

        // 2. Limpiar consola y mostrar Errores o Mensaje de Éxito
        limpiarConsola();
        if (errores.isEmpty()) {
            consolaErrores.setForeground(new Color(0, 100, 0)); // Verde oscuro
            consolaErrores.append("Análisis Léxico completado. No se encontraron errores.\n");
            consolaErrores.append("Total de tokens generados: " + tokensValidos.size() + "\n");
        } else {
            consolaErrores.setForeground(Color.RED); // Rojo
            consolaErrores.append("Análisis Léxico completado con errores:\n");
            for (ErrorCompilador error : errores) {
                consolaErrores.append("  " + error.toString() + "\n");
            }
             consolaErrores.append("\nTotal de errores léxicos: " + errores.size());
             consolaErrores.append("\nTotal de tokens válidos generados: " + tokensValidos.size());
        }
        consolaErrores.setCaretPosition(0); // Scroll al inicio

        // 3. Preparar y Mostrar la Tabla de Tokens VÁLIDOS
        String[] columnas = {"Lexema", "Componente", "Tipo (Patrón)", "Línea", "Columna"};
        DefaultTableModel modelTabla = new DefaultTableModel(columnas, 0) {
             private static final long serialVersionUID = 1L; // Evitar warning
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; // No editable
             }
        };

        for (Token t : tokensValidos) { // Iterar solo sobre tokens válidos
            Object[] fila = {
                t.getValor(),
                FuncionesCompilador.obtenerComponente(t.getTipo()),
                t.getTipo().name(),
                t.getLinea(),
                t.getColumna()
            };
            modelTabla.addRow(fila);
        }

        tablaLexico.setModel(modelTabla);

         // Limpiar tabla sintáctica
         tablaSintactico.setModel(new DefaultTableModel());
    }
     
     private JButton crearBotonConIcono(String tooltip, String comando, String rutaIcono) {
    	    JButton boton = new JButton();
    	    boton.setFocusable(false);
    	    boton.setActionCommand(comando);
    	    boton.addActionListener(this);
    	    boton.setToolTipText(tooltip);

    	    try {
    	        // Cargar el ícono desde el paquete
    	        ImageIcon icon = new ImageIcon(getClass().getResource(rutaIcono));
    	        // Redimensionar el ícono a un tamaño uniforme (24x24 px)
    	        Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
    	        boton.setIcon(new ImageIcon(img));
    	    } catch (Exception e) {
    	        System.err.println("No se pudo cargar el ícono: " + rutaIcono);
    	    }

    	    return boton;
    	}

     private Component crearSeparadorPuntos() {
    	    JLabel sep = new JLabel("⋮");
    	    sep.setFont(new Font("SansSerif", Font.BOLD, 18));
    	    sep.setForeground(new Color(120, 120, 120)); // gris suave
    	    sep.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
    	    return sep;
    	}


}
package Interfaz;

import Controlador.FuncionesCompilador;
import Analizador.AnalizadorSintactico; 
import Analizador.Token;
import Analizador.ErrorCompilador;

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

public class Prototipo_Interfaz extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private JTabbedPane tabbedPane;
    private JTable tablaLexico;
    private JTable tablaSintactico;
    private JTextArea consolaErrores;
    private UndoManager undoManagerGlobal;

    private final String TITULO_BASE = "Prototipo Compilador";

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Prototipo_Interfaz frame = new Prototipo_Interfaz();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public Prototipo_Interfaz() {
        undoManagerGlobal = FuncionesCompilador.getUndoManager();

        setTitle(TITULO_BASE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);
        setMinimumSize(new Dimension(800, 600));

        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // --- Barra de Menú ---
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(crearMenu("Archivo", "Nuevo", "Abrir", "Guardar", "Salir"));
        menuBar.add(crearMenu("Editar", "Copiar", "Pegar", "Deshacer", "Rehacer"));
        menuBar.add(crearMenu("Ejecutar", "Compilar")); 

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        toolBar.add(crearBotonConIcono("Nuevo", "Archivo>Nuevo", "/Interfaz/Iconos/nuevo.png"));
        toolBar.add(crearBotonConIcono("Abrir", "Archivo>Abrir", "/Interfaz/Iconos/abrir.png"));
        toolBar.add(crearBotonConIcono("Guardar", "Archivo>Guardar", "/Interfaz/Iconos/guardar.png"));
        toolBar.add(crearSeparadorPuntos());
        toolBar.add(crearBotonConIcono("Compilar Código", "Ejecutar>Compilar", "/Interfaz/Iconos/jugar.png")); 
        
        contentPane.add(toolBar, BorderLayout.NORTH);

        // --- Área Central ---
        tabbedPane = new JTabbedPane();

        // --- Paneles Laterales ---
        JSplitPane panelDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        panelDerecho.setResizeWeight(0.5);

        // Panel Léxico
        JPanel panelLexico = new JPanel(new BorderLayout());
        tablaLexico = new JTable();
        tablaLexico.setFillsViewportHeight(true);
        tablaLexico.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane scrollLexico = new JScrollPane(tablaLexico);
        panelLexico.add(scrollLexico, BorderLayout.CENTER);
        panelLexico.setBorder(BorderFactory.createTitledBorder("Léxico"));

        // Panel Sintáctico
        JPanel panelSintactico = new JPanel(new BorderLayout());
        tablaSintactico = new JTable();
        tablaSintactico.setFillsViewportHeight(true);
        tablaSintactico.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        JScrollPane scrollSintactico = new JScrollPane(tablaSintactico);
        panelSintactico.add(scrollSintactico, BorderLayout.CENTER);
        panelSintactico.setBorder(BorderFactory.createTitledBorder("Sintáctico"));
        
        panelDerecho.setTopComponent(panelLexico);
        panelDerecho.setBottomComponent(panelSintactico);
        panelDerecho.setDividerLocation(300);

        // --- Divisores ---
        JSplitPane mainSplitHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, panelDerecho);
        mainSplitHorizontal.setResizeWeight(0.7);
        mainSplitHorizontal.setDividerLocation(750);

        // --- Consola ---
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBorder(BorderFactory.createTitledBorder("Consola de Salida"));
        consolaErrores = new JTextArea();
        consolaErrores.setEditable(false);
        consolaErrores.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollConsola = new JScrollPane(consolaErrores);
        panelInferior.add(scrollConsola, BorderLayout.CENTER);
        panelInferior.setPreferredSize(new Dimension(0, 150));

        JSplitPane mainSplitVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainSplitHorizontal, panelInferior);
        mainSplitVertical.setResizeWeight(0.8);
        mainSplitVertical.setDividerLocation(550);

        contentPane.add(mainSplitVertical, BorderLayout.CENTER);

        tabbedPane.addChangeListener(e -> actualizarTituloVentana());
    }

    private void compilarTodo(String codigoFuente) {
        limpiarConsola();
        tablaLexico.setModel(new DefaultTableModel());
        tablaSintactico.setModel(new DefaultTableModel());
        
        consolaErrores.append("Iniciando compilación...\n");

        List<Token> tokens = FuncionesCompilador.obtenerTokens(codigoFuente);
        llenarTablaLexica(tokens);
        consolaErrores.append("Análisis Léxico completado.\n");

        AnalizadorSintactico parser = new AnalizadorSintactico();
        List<AnalizadorSintactico.PasoAnalisis> pasos = parser.analizar(tokens);
        llenarTablaSintactica(pasos);
        consolaErrores.append("Análisis Sintáctico completado.\n");

        List<ErrorCompilador> errores = FuncionesCompilador.getErrores();
        
        consolaErrores.append("--------------------------------------------------\n");
        if (errores.isEmpty()) {
            consolaErrores.setForeground(new Color(0, 100, 0));
            consolaErrores.append("RESULTADO: ¡COMPILACIÓN EXITOSA! 0 ERRORES.\n");
        } else {
            consolaErrores.setForeground(Color.RED);
            consolaErrores.append("RESULTADO: FALLÓ LA COMPILACIÓN.\n");
            consolaErrores.append("Se encontraron " + errores.size() + " errores:\n\n");
            for (ErrorCompilador err : errores) {
                consolaErrores.append(" > " + err.toString() + "\n");
            }
        }
        consolaErrores.append("--------------------------------------------------\n");
    }

    private void llenarTablaLexica(List<Token> tokens) {
        String[] columnas = {"Token"};
        
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
             @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (Token t : tokens) {
            if (!t.getComponenteSintactico().equals("$")) {
                Object[] fila = {
                    t.getComponenteSintactico()
                };
                model.addRow(fila);
            }
        }
        tablaLexico.setModel(model);
    }

    private void llenarTablaSintactica(List<AnalizadorSintactico.PasoAnalisis> pasos) {
        String[] columnas = {"Pila Principal", "Pila Aux", "Entrada", "Acción"};
        
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
             @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        for (AnalizadorSintactico.PasoAnalisis p : pasos) {
            model.addRow(new Object[]{p.pilaPrincipal, p.pilaAux, p.entrada, p.accion});
        }
        
        tablaSintactico.setModel(model);
        tablaSintactico.getColumnModel().getColumn(0).setPreferredWidth(300); 
        tablaSintactico.getColumnModel().getColumn(1).setPreferredWidth(150); 
        tablaSintactico.getColumnModel().getColumn(2).setPreferredWidth(100); 
        tablaSintactico.getColumnModel().getColumn(3).setPreferredWidth(200); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        JTextArea editor = getEditorActivo();

        switch (comando) {
            case "Archivo>Nuevo":
                agregarNuevaPestana("Sin título " + (tabbedPane.getTabCount() + 1), crearNuevoEditor(), null);
                break;
            case "Archivo>Abrir":
                String contenido = FuncionesCompilador.abrirArchivo(this);
                if (contenido != null) {
                    File f = FuncionesCompilador.getArchivoActual();
                    for(int i=0; i < tabbedPane.getTabCount(); i++){
                        File openF = (File)((JScrollPane)tabbedPane.getComponentAt(i)).getClientProperty("archivo");
                        if(openF != null && openF.equals(f)){ tabbedPane.setSelectedIndex(i); return; }
                    }
                    JTextArea newEd = crearNuevoEditor();
                    newEd.setText(contenido);
                    agregarNuevaPestana(f.getName(), newEd, f);
                    newEd.setCaretPosition(0);
                    FuncionesCompilador.getUndoManager().discardAllEdits();
                }
                break;
            case "Archivo>Guardar": guardarArchivoActual(); break;
            case "Archivo>Salir": System.exit(0); break;
            case "Editar>Copiar": if (editor != null) editor.copy(); break;
            case "Editar>Pegar": if (editor != null) editor.paste(); break;
            case "Editar>Deshacer": FuncionesCompilador.deshacer(); break;
            case "Editar>Rehacer": FuncionesCompilador.rehacer(); break;
            
            case "Ejecutar>Compilar":
                if (editor != null) {
                    compilarTodo(editor.getText());
                } else {
                    limpiarConsola();
                    consolaErrores.setText("No hay código para compilar.");
                }
                break;
        }
    }

    private void limpiarConsola() { consolaErrores.setText(""); }

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

    private JTextArea crearNuevoEditor() {
        JTextArea editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.setTabSize(4);
        editor.getDocument().addUndoableEditListener(undoManagerGlobal);
        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void marcarModificado() {
                Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, editor);
                if (scrollPane == null) return;
                int index = tabbedPane.indexOfComponent((Component) scrollPane);
                if (index != -1) {
                    ((JComponent) scrollPane).putClientProperty("modificado", Boolean.TRUE);
                    Component tabComponent = tabbedPane.getTabComponentAt(index);
                    if (tabComponent instanceof JPanel) {
                        for (Component comp : ((JPanel) tabComponent).getComponents()) {
                            if (comp instanceof JLabel) {
                                String textoActual = ((JLabel) comp).getText().trim();
                                if (!textoActual.endsWith("*")) {
                                    ((JLabel) comp).setText(textoActual + "* ");
                                    actualizarTituloVentana();
                                }
                                break;
                            }
                        }
                    }
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { marcarModificado(); }
            @Override public void removeUpdate(DocumentEvent e) { marcarModificado(); }
            @Override public void changedUpdate(DocumentEvent e) { }
        });
        return editor;
    }

    private void agregarNuevaPestana(String nombre, JTextArea editor, File archivo) {
        JScrollPane scrollPane = new JScrollPane(editor);
        scrollPane.putClientProperty("archivo", archivo);
        scrollPane.putClientProperty("modificado", Boolean.FALSE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        try {
            NumeroLinea numeroLinea = new NumeroLinea(editor);
            scrollPane.setRowHeaderView(numeroLinea);
        } catch (Exception e) {
            // Ignorar si no existe
        }

        editor.setMargin(new Insets(5, 5, 5, 5));
        tabbedPane.addTab(nombre, scrollPane);
        int index = tabbedPane.indexOfComponent(scrollPane);
        tabbedPane.setTabComponentAt(index, crearTabConBoton(nombre, scrollPane));
        tabbedPane.setSelectedComponent(scrollPane);
        editor.requestFocusInWindow();
    }

    private JPanel crearTabConBoton(String nombre, JScrollPane scrollPane) {
        JPanel pnlTab = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTab.setOpaque(false);
        JLabel lblTitulo = new JLabel(nombre + " ");
        JButton btnCerrar = new JButton("x");
        btnCerrar.setPreferredSize(new Dimension(17, 17));
        btnCerrar.setMargin(new Insets(0, 0, 0, 0));
        btnCerrar.setToolTipText("Cerrar " + nombre);
        btnCerrar.setBorder(BorderFactory.createEmptyBorder());
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusable(false);
        btnCerrar.setRolloverEnabled(true);
        btnCerrar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnCerrar.setForeground(Color.RED); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnCerrar.setForeground(Color.BLACK); }
        });
        pnlTab.add(lblTitulo);
        pnlTab.add(btnCerrar);
        btnCerrar.addActionListener(e -> cerrarPestana(scrollPane));
        return pnlTab;
    }

    private void cerrarPestana(JScrollPane scrollPane) {
        int idx = tabbedPane.indexOfComponent(scrollPane);
        if (idx == -1) return;
        boolean modificado = (Boolean) scrollPane.getClientProperty("modificado");
        String tituloPestana = tabbedPane.getTitleAt(idx);

        if (modificado) {
            int opcion = JOptionPane.showConfirmDialog(this,
                    "El archivo '" + tituloPestana + "' tiene cambios no guardados.\n¿Deseas guardarlos?",
                    "Guardar Cambios", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opcion == JOptionPane.YES_OPTION) {
                tabbedPane.setSelectedIndex(idx); 
                if (guardarArchivoActual()) tabbedPane.remove(idx);
            } else if (opcion == JOptionPane.NO_OPTION) {
                tabbedPane.remove(idx);
            }
        } else {
            tabbedPane.remove(idx);
        }
    }

    private void actualizarTituloVentana() {
        JScrollPane scrollActivo = getScrollActivo();
        if (scrollActivo != null) {
            File archivo = (File) scrollActivo.getClientProperty("archivo");
            String titulo = (archivo != null) ? archivo.getAbsolutePath() : tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
            if ((Boolean)scrollActivo.getClientProperty("modificado")) titulo += "*";
            setTitle(TITULO_BASE + " - " + titulo);
        } else {
            setTitle(TITULO_BASE);
        }
    }

    private JTextArea getEditorActivo() {
        Component sel = tabbedPane.getSelectedComponent();
        if (sel instanceof JScrollPane) {
            return (JTextArea) ((JScrollPane) sel).getViewport().getView();
        }
        return null;
    }

    private JScrollPane getScrollActivo() {
        Component sel = tabbedPane.getSelectedComponent();
        return (sel instanceof JScrollPane) ? (JScrollPane) sel : null;
    }

    private boolean guardarArchivoActual() {
        JScrollPane scroll = getScrollActivo();
        JTextArea editor = getEditorActivo();
        if (scroll == null || editor == null) return true;

        File archivoActual = (File) scroll.getClientProperty("archivo");
        File guardado = FuncionesCompilador.guardarArchivo(this, archivoActual, editor.getText());

        if (guardado != null) {
            scroll.putClientProperty("archivo", guardado);
            scroll.putClientProperty("modificado", Boolean.FALSE);
            int idx = tabbedPane.getSelectedIndex();
            tabbedPane.setTitleAt(idx, guardado.getName());
            Component tabComp = tabbedPane.getTabComponentAt(idx);
            if (tabComp instanceof JPanel) {
               for(Component c : ((JPanel)tabComp).getComponents()){
                   if(c instanceof JLabel) ((JLabel)c).setText(guardado.getName() + " ");
                   if(c instanceof JButton) ((JButton)c).setToolTipText("Cerrar " + guardado.getName());
               }
            }
            actualizarTituloVentana();
            return true;
        }
        return false;
    }

    private JButton crearBotonConIcono(String tooltip, String comando, String rutaIcono) {
        JButton boton = new JButton();
        boton.setFocusable(false);
        boton.setActionCommand(comando);
        boton.addActionListener(this);
        boton.setToolTipText(tooltip);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(rutaIcono));
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            boton.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            boton.setText(tooltip.substring(0, 1));
        }
        return boton;
    }

    private Component crearSeparadorPuntos() {
        JLabel sep = new JLabel("⋮");
        sep.setFont(new Font("SansSerif", Font.BOLD, 18));
        sep.setForeground(Color.GRAY);
        sep.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        return sep;
    }
}
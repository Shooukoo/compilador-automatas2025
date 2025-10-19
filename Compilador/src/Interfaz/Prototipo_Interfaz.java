package Interfaz;

import Controlador.FuncionesCompilador;
import Analizador.Token;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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

    private final String TITULO_BASE = "Prototipo Compilador - " + System.getProperty("user.dir");

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Prototipo_Interfaz frame = new Prototipo_Interfaz();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Prototipo_Interfaz() {
        setTitle(TITULO_BASE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);

        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(crearMenu("Archivo", "Nuevo", "Abrir", "Guardar", "Salir"));
        menuBar.add(crearMenu("Editar", "Copiar", "Pegar", "Deshacer", "Rehacer"));
        menuBar.add(crearMenu("Analisis", "Analizador Léxico", "Analizador Sintáctico"));

        tabbedPane = new JTabbedPane();

        JSplitPane panelDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        panelDerecho.setDividerLocation(350);

        JPanel panelLexico = new JPanel(new BorderLayout());
        tablaLexico = new JTable();
        panelLexico.add(new JScrollPane(tablaLexico), BorderLayout.CENTER);
        panelLexico.setBorder(BorderFactory.createTitledBorder("Analizador Léxico"));

        JPanel panelSintactico = new JPanel(new BorderLayout());
        tablaSintactico = new JTable();
        panelSintactico.add(new JScrollPane(tablaSintactico), BorderLayout.CENTER);
        panelSintactico.setBorder(BorderFactory.createTitledBorder("Analizador Sintáctico"));

        panelDerecho.setTopComponent(panelLexico);
        panelDerecho.setBottomComponent(panelSintactico);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, panelDerecho);
        mainSplit.setDividerLocation(700);
        contentPane.add(mainSplit, BorderLayout.CENTER);
    }

    private JMenu crearMenu(String nombre, String... opciones) {
        JMenu menu = new JMenu(nombre);
        for (String opcion : opciones) {
            JMenuItem item = new JMenuItem(opcion);
            item.setActionCommand(nombre + ">" + opcion);
            item.addActionListener(this);
            menu.add(item);
        }
        return menu;
    }

    private JTextArea crearNuevoEditor() {
        JTextArea editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.getDocument().addUndoableEditListener(FuncionesCompilador.getUndoManager());

        editor.getDocument().addDocumentListener(new DocumentListener() {
            private void marcarModificado() {
                JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, editor);
                if (scroll == null) return;
                int index = tabbedPane.indexOfComponent(scroll);
                if (index >= 0 && !tabbedPane.getTitleAt(index).endsWith("*")) {
                    tabbedPane.setTitleAt(index, tabbedPane.getTitleAt(index) + "*");
                    actualizarTituloVentana(index);
                }
            }

            @Override public void insertUpdate(DocumentEvent e) { marcarModificado(); }
            @Override public void removeUpdate(DocumentEvent e) { marcarModificado(); }
            @Override public void changedUpdate(DocumentEvent e) { marcarModificado(); }
        });

        return editor;
    }

    private void agregarNuevaPestana(String nombre, JTextArea editor, File archivo) {
        JScrollPane scroll = new JScrollPane(editor);
        scroll.putClientProperty("archivo", archivo);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        /*
        LineNumberView lineNumberView = new LineNumberView(editor);

        // Ajustar margen después de que la pestaña sea visible
        SwingUtilities.invokeLater(() -> {
            int alturaPestana = 0;
            if (tabbedPane.getTabCount() > 0) {
                Rectangle r = tabbedPane.getUI().getTabBounds(tabbedPane, 0);
                if (r != null) alturaPestana = r.height;
            }
            lineNumberView.setBorder(BorderFactory.createEmptyBorder(alturaPestana, 0, 0, 0));
        });

        scroll.setRowHeaderView(lineNumberView);
		*/

        tabbedPane.addTab(nombre, scroll);
        int index = tabbedPane.indexOfComponent(scroll);
        tabbedPane.setTabComponentAt(index, crearTabConBoton(nombre, scroll, editor));
        tabbedPane.setSelectedComponent(scroll);
    }

    private JPanel crearTabConBoton(String nombre, JScrollPane scroll, JTextArea editor) {
        JPanel pnlTab = new JPanel();
        pnlTab.setOpaque(false);
        pnlTab.setLayout(new BoxLayout(pnlTab, BoxLayout.X_AXIS));

        JLabel lblTitulo = new JLabel(nombre + "  ");
        JButton btnCerrar = new JButton("x");
        btnCerrar.setMargin(new Insets(0, 0, 0, 0));
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusable(false);

        pnlTab.add(lblTitulo);
        pnlTab.add(Box.createRigidArea(new Dimension(5, 0)));
        pnlTab.add(btnCerrar);

        btnCerrar.addActionListener(e -> {
            int idx = tabbedPane.indexOfComponent(scroll);
            String titulo = tabbedPane.getTitleAt(idx);
            if (titulo.endsWith("*")) {
                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo tiene cambios no guardados. ¿Deseas cerrar de todas formas?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION
                );
                if (opcion != JOptionPane.YES_OPTION) return;
            }
            tabbedPane.remove(scroll);
            if (tabbedPane.getTabCount() == 0) setTitle(TITULO_BASE);
        });

        return pnlTab;
    }

    private void actualizarTituloVentana(int index) {
        JScrollPane scroll = (JScrollPane) tabbedPane.getComponentAt(index);
        File archivo = (File) scroll.getClientProperty("archivo");
        if (archivo != null) {
            setTitle("Prototipo Compilador - " + archivo.getAbsolutePath());
        } else {
            setTitle("Prototipo Compilador - Archivo nuevo");
        }
    }

    private JTextArea getEditorActivo() {
        if (tabbedPane.getSelectedComponent() == null) return null;
        return (JTextArea) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
    }

    private JScrollPane getScrollActivo() {
        return (JScrollPane) tabbedPane.getSelectedComponent();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();
        JTextArea editor = getEditorActivo();
        JScrollPane scroll = getScrollActivo();

        switch (comando) {
            case "Archivo>Nuevo":
                JTextArea nuevo = crearNuevoEditor();
                agregarNuevaPestana("Archivo" + (tabbedPane.getTabCount() + 1), nuevo, null);
                break;

            case "Archivo>Abrir":
                String contenido = FuncionesCompilador.abrirArchivo(this);
                if (contenido != null) {
                    JTextArea nuevoEditor = crearNuevoEditor();
                    nuevoEditor.setText(contenido);
                    File archivo = FuncionesCompilador.getArchivoActual();
                    agregarNuevaPestana(archivo.getName(), nuevoEditor, archivo);
                    setTitle("Prototipo Compilador - " + archivo.getAbsolutePath());
                }
                break;

            case "Archivo>Guardar":
                if (editor == null) {
                    JOptionPane.showMessageDialog(this, "No hay archivo activo");
                    return;
                }
                File archivoActual = (File) scroll.getClientProperty("archivo");
                File guardado = FuncionesCompilador.guardarArchivo(this, archivoActual, editor.getText());
                if (guardado != null) {
                    scroll.putClientProperty("archivo", guardado);
                    int idx = tabbedPane.getSelectedIndex();
                    tabbedPane.setTitleAt(idx, guardado.getName());
                    tabbedPane.setTabComponentAt(idx, crearTabConBoton(guardado.getName(), scroll, editor));
                    setTitle("Prototipo Compilador - " + guardado.getAbsolutePath());
                }
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

            case "Analisis>Analizador Léxico":
                if (editor != null)
                    mostrarTablaLexico(editor.getText());
                break;

            case "Analisis>Analizador Sintáctico":
                JOptionPane.showMessageDialog(this, "Análisis sintáctico en desarrollo");
                break;
        }
    }

    private void mostrarTablaLexico(String codigoFuente) {
        try {
            List<Token> tokens = FuncionesCompilador.obtenerTokens(codigoFuente);
            String[] columnas = {"Lexema", "Patrón", "Componente"};
            String[][] datos = new String[tokens.size()][3];

            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                datos[i][0] = t.valor;
                datos[i][1] = t.tipo.name();
                datos[i][2] = FuncionesCompilador.obtenerComponente(t.tipo);
            }

            tablaLexico.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al analizar: " + ex.getMessage());
        }
    }
}

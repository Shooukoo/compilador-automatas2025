package Interfaz;

import Controlador.FuncionesCompilador;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Prototipo_Interfaz extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private JTextArea textLexico;
    private JTextArea textSintactico;

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
        setTitle("Prototipo Compilador - " + System.getProperty("user.dir"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);

        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // ===== BARRA DE MENÚS =====
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        menuBar.add(crearMenu("Archivo", "Nuevo", "Abrir", "Guardar", "Salir"));
        menuBar.add(crearMenu("Editar", "Copiar", "Pegar", "Deshacer", "Rehacer"));
        menuBar.add(crearMenu("Analisis", "Analizador Léxico", "Analizador Sintáctico"));
        menuBar.add(crearMenu("Source", "Formatear", "Comentar/Descomentar"));
        menuBar.add(crearMenu("Buscar", "Buscar", "Reemplazar"));
        menuBar.add(crearMenu("Run", "Ejecutar", "Depurar"));
        menuBar.add(crearMenu("Help", "Documentación", "Acerca de"));

        // ===== BARRA DE HERRAMIENTAS =====
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        contentPane.add(toolBar, BorderLayout.NORTH);

        toolBar.add(crearBoton("Nuevo", "C:\\Users\\shoxd\\git\\repository\\Compilador\\src\\Interfaz\\Iconos\\agregar-archivo.png", "Archivo>Nuevo"));
        toolBar.add(crearBoton("Abrir", "C:\\Users\\shoxd\\git\\repository\\Compilador\\src\\Interfaz\\Iconos\\carpeta-abierta.png", "Archivo>Abrir"));
        toolBar.add(crearBoton("Guardar", "C:\\Users\\shoxd\\git\\repository\\Compilador\\src\\Interfaz\\Iconos\\guardar-el-archivo.png", "Archivo>Guardar"));
        toolBar.addSeparator();
        toolBar.add(crearBoton("Ejecutar", "C:\\Users\\shoxd\\git\\repository\\Compilador\\src\\Interfaz\\Iconos\\jugar.png", "Run>Ejecutar"));
        toolBar.add(crearBoton("Depurar", "C:\\Users\\shoxd\\git\\repository\\Compilador\\src\\Interfaz\\Iconos\\depurar.png", "Run>Depurar"));

        // ===== PANEL CENTRAL =====
        tabbedPane = new JTabbedPane();

        // Panel derecho fijo
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        textLexico = new JTextArea("Resultados Analizador Léxico...");
        textLexico.setEditable(false);
        textSintactico = new JTextArea("Estado de la pila Analizador Sintáctico...");
        textSintactico.setEditable(false);

        rightPanel.add(new JScrollPane(textLexico));
        rightPanel.add(new JScrollPane(textSintactico));

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, rightPanel);
        mainSplit.setDividerLocation(700);
        contentPane.add(mainSplit, BorderLayout.CENTER);
    }

    // ===== MÉTODOS =====
    private JTextArea crearNuevoEditor() {
        JTextArea editor = new JTextArea();
        editor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editor.getDocument().addUndoableEditListener(FuncionesCompilador.getUndoManager());
        return editor;
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

    private JButton crearBoton(String tooltip, String iconPath, String actionCommand) {
        JButton boton = new JButton();
        boton.setToolTipText(tooltip);
        boton.setIcon(new ImageIcon(iconPath));
        boton.setActionCommand(actionCommand);
        boton.addActionListener(this);
        return boton;
    }


    private JTextArea getEditorActivo() {
        if (tabbedPane.getSelectedComponent() == null) {
            return null;
        }
        return (JTextArea) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
    }

    private void agregarNuevaPestana(String nombre, JTextArea editor) {
        JScrollPane scroll = new JScrollPane(editor);
        tabbedPane.addTab(nombre, scroll);

        JPanel pnlTab = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlTab.setOpaque(false);

        JLabel lblTitulo = new JLabel(nombre + "  ");
        JButton btnCerrar = new JButton("x");
        btnCerrar.setMargin(new Insets(0, 0, 0, 0));
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setFocusable(false);

        pnlTab.add(lblTitulo);
        pnlTab.add(btnCerrar);

        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, pnlTab);

        btnCerrar.addActionListener(e -> {
            if (!editor.getText().isEmpty()) {
                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "El archivo no se ha guardado. ¿Deseas cerrar de todas formas?",
                        "Advertencia",
                        JOptionPane.YES_NO_OPTION
                );
                if (opcion != JOptionPane.YES_OPTION) return;
            }
            tabbedPane.remove(scroll);
        });

        tabbedPane.setSelectedComponent(scroll);
    }

    // ===== ACTION PERFORMED =====
    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        // Nuevo archivo no requiere editor activo
        if (comando.equals("Archivo>Nuevo")) {
            JTextArea nuevoEditor = crearNuevoEditor();
            int numero = tabbedPane.getTabCount() + 1;
            agregarNuevaPestana("Archivo" + numero, nuevoEditor);
            return;
        }

        // Obtener editor activo
        JTextArea editor = getEditorActivo();
        if (editor == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún archivo abierto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (comando) {
            case "Archivo>Abrir":
                String contenido = FuncionesCompilador.abrirArchivo(this);
                if (contenido != null) {
                    JTextArea nuevoEditorAbrir = crearNuevoEditor();
                    nuevoEditorAbrir.setText(contenido);
                    int numAbrir = tabbedPane.getTabCount() + 1;
                    agregarNuevaPestana("Archivo" + numAbrir, nuevoEditorAbrir);
                    setTitle("Prototipo Compilador - Archivo abierto");
                }
                break;
            case "Archivo>Guardar":
                FuncionesCompilador.guardarArchivo(this, editor.getText());
                break;
            case "Archivo>Salir":
                System.exit(0);
                break;
            case "Editar>Copiar":
                editor.copy();
                break;
            case "Editar>Pegar":
                editor.paste();
                break;
            case "Editar>Deshacer":
                FuncionesCompilador.deshacer();
                break;
            case "Editar>Rehacer":
                FuncionesCompilador.rehacer();
                break;
            case "Analisis>Analizador Léxico":
                textLexico.setText(FuncionesCompilador.ejecutarAnalizadorLexico(editor.getText()));
                break;
            case "Analisis>Analizador Sintáctico":
                textSintactico.setText(FuncionesCompilador.ejecutarAnalizadorSintactico(editor.getText()));
                break;
            case "Source>Formatear":
                editor.setText(FuncionesCompilador.formatearCodigo(editor.getText()));
                break;
            case "Source>Comentar/Descomentar":
                editor.setText(FuncionesCompilador.comentarDescomentar(editor.getText()));
                break;
            case "Buscar>Buscar":
                FuncionesCompilador.buscar(editor, this);
                break;
            case "Buscar>Reemplazar":
                FuncionesCompilador.reemplazar(editor, this);
                break;
            case "Run>Ejecutar":
                FuncionesCompilador.ejecutar(editor.getText(), textLexico);
                break;
            case "Run>Depurar":
                FuncionesCompilador.depurar(editor.getText(), textSintactico);
                break;
            case "Help>Documentación":
                FuncionesCompilador.abrirDocumentacion(this);
                break;
            case "Help>Acerca de":
                FuncionesCompilador.acercaDe(this);
                break;
        }
    }
}

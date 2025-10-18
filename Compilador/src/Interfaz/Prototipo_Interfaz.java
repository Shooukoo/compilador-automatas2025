package Interfaz;

import Controlador.FuncionesCompilador;
import Analizador.Token;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class Prototipo_Interfaz extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JTabbedPane tabbedPane;
    private JTable tablaLexico;
    private JTable tablaSintactico;

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

        // BARRA DE MENÚS
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(crearMenu("Archivo", "Nuevo", "Abrir", "Guardar", "Salir"));
        menuBar.add(crearMenu("Editar", "Copiar", "Pegar", "Deshacer", "Rehacer"));
        menuBar.add(crearMenu("Analisis", "Analizador Léxico", "Analizador Sintáctico"));

        // PANEL CENTRAL
        tabbedPane = new JTabbedPane();

        // Panel derecho dividido verticalmente para Léxico y Sintáctico
        JSplitPane panelDerecho = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        panelDerecho.setDividerLocation(350);

        // Panel Analizador Léxico
        JPanel panelLexico = new JPanel(new BorderLayout());
        tablaLexico = new JTable();
        panelLexico.add(new JScrollPane(tablaLexico), BorderLayout.CENTER);
        panelLexico.setBorder(BorderFactory.createTitledBorder("Analizador Léxico"));

        // Panel Analizador Sintáctico
        JPanel panelSintactico = new JPanel(new BorderLayout());
        tablaSintactico = new JTable();
        panelSintactico.add(new JScrollPane(tablaSintactico), BorderLayout.CENTER);
        panelSintactico.setBorder(BorderFactory.createTitledBorder("Analizador Sintáctico"));

        panelDerecho.setTopComponent(panelLexico);
        panelDerecho.setBottomComponent(panelSintactico);

        // Split horizontal principal
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, panelDerecho);
        mainSplit.setDividerLocation(700);
        contentPane.add(mainSplit, BorderLayout.CENTER);
    }

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

    @Override
    public void actionPerformed(ActionEvent e) {
        String comando = e.getActionCommand();

        if (comando.equals("Archivo>Nuevo")) {
            JTextArea nuevoEditor = crearNuevoEditor();
            int numero = tabbedPane.getTabCount() + 1;
            agregarNuevaPestana("Archivo" + numero, nuevoEditor);
            return;
        }

        JTextArea editor = getEditorActivo();
        if (editor == null) {
            JOptionPane.showMessageDialog(this, "No hay ningún archivo abierto.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        switch (comando) {
            case "Analisis>Analizador Léxico":
                mostrarTablaLexico(editor.getText());
                break;
            case "Analisis>Analizador Sintáctico":
                // mostrarTablaSintactico(editor.getText());
                break;
            // Otros casos como abrir, guardar, etc. los dejas igual
        }
    }

    // === Métodos para llenar las tablas ===

    private void mostrarTablaLexico(String codigoFuente) {
        try {
            List<Token> tokens = FuncionesCompilador.obtenerTokens(codigoFuente);
            String[] columnas = {"Lexema", "Patron", "Componente"};
            String[][] datos = new String[tokens.size()][3];

            for (int i = 0; i < tokens.size(); i++) {
                Token t = tokens.get(i);
                datos[i][0] = t.valor;
                datos[i][1] = t.tipo.name();
                datos[i][2] = FuncionesCompilador.obtenerComponente(t.tipo);
            }

            tablaLexico.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al analizar el código: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /* private void mostrarTablaSintactico(String codigoFuente) {
        try {
            // Supongamos que tu parser devuelve una lista de String[]: {Estado, Token, Acción}
            List<String[]> estados = FuncionesCompilador.obtenerEstadosSintacticos(codigoFuente);

            String[] columnas = {"Estado", "Token", "Acción"};
            String[][] datos = new String[estados.size()][3];

            for (int i = 0; i < estados.size(); i++) {
                datos[i] = estados.get(i);
            }

            tablaSintactico.setModel(new javax.swing.table.DefaultTableModel(datos, columnas));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al analizar sintácticamente: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }*/
}

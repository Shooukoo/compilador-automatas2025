package Interfaz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Prototipo_Interfaz extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextArea textEditor;
    private JTextArea textLexico;
    private JTextArea textSintactico;
    private JLabel lblRuta;

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
        setTitle("Mi Compilador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 800);

        // ========= PANEL PRINCIPAL =========
        JPanel contentPane = new JPanel(new BorderLayout(5, 5));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        // ========= BARRA DE TITULO PERSONALIZADA =========
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBorder(new EmptyBorder(5, 5, 5, 5));

        // Icono izquierda
        JLabel lblIcon = new JLabel(new ImageIcon("icon.png")); // reemplaza con ruta de tu icono
        titleBar.add(lblIcon, BorderLayout.WEST);

        // Ruta derecha
        lblRuta = new JLabel("Ruta actual: " + new File("").getAbsolutePath());
        lblRuta.setHorizontalAlignment(SwingConstants.RIGHT);
        titleBar.add(lblRuta, BorderLayout.EAST);

        contentPane.add(titleBar, BorderLayout.NORTH);

        // ========= BARRA DE MENÚS =========
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        menuBar.add(crearMenu("Archivo", "Nuevo", "Abrir", "Guardar", "Salir"));
        menuBar.add(crearMenu("Editar", "Copiar", "Pegar", "Deshacer", "Rehacer"));
        menuBar.add(crearMenu("Analisis", "Analizador Léxico", "Analizador Sintáctico"));
        menuBar.add(crearMenu("Source", "Formatear", "Comentar/Descomentar"));
        menuBar.add(crearMenu("Buscar", "Buscar", "Reemplazar"));
        menuBar.add(crearMenu("Run", "Ejecutar", "Depurar"));
        menuBar.add(crearMenu("Help", "Documentación", "Acerca de"));

        // ========= BARRA DE HERRAMIENTAS =========
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        toolBar.add(crearBoton("Nuevo", "C:\\Users\\shoxd\\OneDrive\\Documentos\\Escuela\\Automatas\\Iconos\\agregar-archivo.png")); 
        toolBar.add(crearBoton("Abrir", "C:\\Users\\shoxd\\OneDrive\\Documentos\\Escuela\\Automatas\\Iconos\\carpeta-abierta.png"));
        toolBar.add(crearBoton("Guardar", "C:\\Users\\shoxd\\OneDrive\\Documentos\\Escuela\\Automatas\\Iconos\\guardar-el-archivo.png"));
        toolBar.addSeparator();
        toolBar.add(crearBoton("Ejecutar", "C:\\Users\\shoxd\\OneDrive\\Documentos\\Escuela\\Automatas\\Iconos\\jugar.png"));
        toolBar.add(crearBoton("Depurar", "C:\\Users\\shoxd\\OneDrive\\Documentos\\Escuela\\Automatas\\Iconos\\depurar.png"));

        contentPane.add(toolBar, BorderLayout.PAGE_START);

        // ========= ZONA DE EDICIÓN Y PANELES DERECHOS =========
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        contentPane.add(mainSplit, BorderLayout.CENTER);

        // Editor de código
        textEditor = new JTextArea();
        textEditor.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollEditor = new JScrollPane(textEditor);
        mainSplit.setLeftComponent(scrollEditor);

        // Panel derecho (Resultados léxico y sintáctico)
        JPanel rightPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        textLexico = new JTextArea("Resultados Analizador Léxico...");
        textLexico.setEditable(false);
        JScrollPane scrollLexico = new JScrollPane(textLexico);
        rightPanel.add(scrollLexico);

        textSintactico = new JTextArea("Estado de la pila Analizador Sintáctico...");
        textSintactico.setEditable(false);
        JScrollPane scrollSintactico = new JScrollPane(textSintactico);
        rightPanel.add(scrollSintactico);

        mainSplit.setRightComponent(rightPanel);
        mainSplit.setDividerLocation(700); // Ajusta tamaño inicial
    }

    // Método auxiliar para crear menús
    private JMenu crearMenu(String nombre, String... opciones) {
        JMenu menu = new JMenu(nombre);
        for (String opcion : opciones) {
            JMenuItem item = new JMenuItem(opcion);
            menu.add(item);
        }
        return menu;
    }

    // Método auxiliar para crear botones de toolbar
    private JButton crearBoton(String tooltip, String iconPath) {
        JButton boton = new JButton();
        boton.setToolTipText(tooltip);
        boton.setIcon(new ImageIcon(iconPath)); // Coloca tus iconos en carpeta icons/
        boton.addActionListener((ActionEvent e) -> {
            System.out.println(tooltip + " presionado");
        });
        return boton;
    }
}

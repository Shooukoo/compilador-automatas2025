package Interfaz;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.Element;
import java.awt.*;
import java.beans.PropertyChangeListener;

/**
 * Componente que muestra los números de línea junto a un JTextArea.
 * Se sincroniza automáticamente con el scroll y el texto.
 * Respeta el borde superior para no solaparse con JTabbedPane.
 */
public class LineNumberView extends JComponent implements CaretListener, DocumentListener, PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private static final int MARGIN = 5;

    private final JTextArea textArea;
    private int currentDigits;

    public LineNumberView(JTextArea textArea) {
        this.textArea = textArea;
        this.textArea.getDocument().addDocumentListener(this);
        this.textArea.addCaretListener(this);
        this.textArea.addPropertyChangeListener((PropertyChangeListener) this);
        setFont(textArea.getFont());
        setBackground(new Color(245, 245, 245));
        setForeground(Color.GRAY);
        setOpaque(true);
        updateWidth();
    }

    private void updateWidth() {
        int lineCount = textArea.getLineCount();
        int digits = Math.max(3, String.valueOf(lineCount).length());
        if (digits != currentDigits) {
            currentDigits = digits;
            FontMetrics fm = getFontMetrics(getFont());
            int width = MARGIN * 2 + fm.charWidth('0') * digits;
            setPreferredSize(new Dimension(width, Integer.MAX_VALUE));
            revalidate();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        FontMetrics fm = getFontMetrics(getFont());
        Insets insets = getInsets(); // Respeta los bordes aplicados (ej. altura de pestaña)
        int lineHeight = fm.getHeight();
        int startOffset = textArea.viewToModel2D(new Point(0, 0));
        int startLine = getLineOfOffset(startOffset);
        int y = -textArea.getVisibleRect().y % lineHeight + fm.getAscent() + insets.top;

        int visibleLines = textArea.getVisibleRect().height / lineHeight + 2;
        for (int i = 0; i < visibleLines; i++) {
            int lineNumber = startLine + i + 1;
            if (lineNumber > textArea.getLineCount()) break;
            String text = String.valueOf(lineNumber);
            int x = getWidth() - MARGIN - fm.stringWidth(text);
            g.setColor(getForeground());
            g.drawString(text, x, y + i * lineHeight);
        }
    }

    private int getLineOfOffset(int offset) {
        Element root = textArea.getDocument().getDefaultRootElement();
        return root.getElementIndex(offset);
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        repaint();
    }

    @Override
    public void changedUpdate(DocumentEvent e) { documentChanged(); }

    @Override
    public void insertUpdate(DocumentEvent e) { documentChanged(); }

    @Override
    public void removeUpdate(DocumentEvent e) { documentChanged(); }

    private void documentChanged() {
        updateWidth();
        repaint();
    }

    @Override
    public void propertyChange(java.beans.PropertyChangeEvent evt) {
        if ("font".equals(evt.getPropertyName())) {
            setFont(textArea.getFont());
            updateWidth();
        }
    }
}

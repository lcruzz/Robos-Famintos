package view.components;

import view.theme.Tema;

import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;


public class Botao extends JButton {

    public enum Tipo { PRINCIPAL, SECUNDARIO }

    private final Tipo tipo;
    private boolean sobre = false;

    public Botao(String texto, Tipo tipo) {
        super(texto);
        this.tipo = tipo;
        setFont(Tema.FONTE_NEGRITO);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setRolloverEnabled(true);
        setMargin(new java.awt.Insets(0, 0, 0, 0));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                sobre = true;
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                sobre = false;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(getText()) + 36, Math.max(38, fm.getHeight() + 18));
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        boolean ativo = isEnabled();
        boolean pressionado = getModel().isPressed() && ativo;
        boolean hover = (sobre || getModel().isRollover()) && ativo;

        Color fundo;
        Color texto;
        Color borda;
        if (!ativo) {
            fundo = Tema.SUPERFICIE;
            texto = Tema.TEXTO_DESABILITADO;
            borda = Tema.BORDA;
        } else if (tipo == Tipo.PRINCIPAL) {
            fundo = pressionado ? Tema.ACENTO_PRESSIONADO : hover ? Tema.ACENTO_HOVER : Tema.ACENTO;
            texto = Tema.TEXTO_ESCURO;
            borda = fundo;
        } else {
            fundo = pressionado ? Tema.SUPERFICIE : hover ? Tema.SUPERFICIE_HOVER : Tema.SUPERFICIE_CLARA;
            texto = Tema.TEXTO;
            borda = hover ? Tema.ACENTO : Tema.BORDA;
        }

        int w = getWidth() - 1;
        int h = getHeight() - 1;
        g2.setColor(fundo);
        g2.fillRoundRect(0, 0, w, h, 12, 12);
        g2.setColor(borda);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, w, h, 12, 12);

        if (isFocusOwner() && ativo) {
            g2.setColor(Tema.TEXTO);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(3, 3, w - 6, h - 6, 8, 8);
        }

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int tx = (getWidth() - fm.stringWidth(getText())) / 2;
        int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g2.setColor(texto);
        g2.drawString(getText(), tx, ty);
        g2.dispose();
    }
}

package view.components;

import view.theme.Tema;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Font;

public class Banner extends JPanel {

    private final JLabel titulo = new JLabel("", SwingConstants.CENTER);
    private final JLabel detalhe = new JLabel("", SwingConstants.CENTER);
    private Color fundo = Tema.ACENTO;

    public Banner() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        setPreferredSize(new Dimension(100, 70));
        setMinimumSize(new Dimension(100, 70));

        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        detalhe.setFont(Tema.FONTE_SUBTITULO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        detalhe.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(javax.swing.Box.createVerticalGlue());
        add(titulo);
        add(javax.swing.Box.createVerticalStrut(3));
        add(detalhe);
        add(javax.swing.Box.createVerticalGlue());
    }

    private void definir(Color fundo, Color texto, String t, String d) {
        this.fundo = fundo;
        titulo.setForeground(texto);
        detalhe.setForeground(texto);
        titulo.setText(t);
        detalhe.setText(d);
        repaint();
    }

    /** Pede ao usuário que escolha a posição do alimento. */
    public void instrucao(String detalheTexto) {
        definir(Tema.ACENTO, Tema.TEXTO_ESCURO, "🍎  Escolha a posição do alimento!", detalheTexto);
    }

    public void andamento(String t, String d) {
        definir(Tema.SUPERFICIE_CLARA, Tema.TEXTO, t, d);
    }

    /** Vitória, na cor do robô vencedor. */
    public void vitoria(String t, String d, Color corRobo) {
        definir(corRobo, Tema.TEXTO_ESCURO, "🏆  " + t, d);
    }

    /** Fim de jogo sem vencedor. */
    public void semVencedor(String t, String d) {
        definir(Tema.BOMBA, Tema.TEXTO_ESCURO, t, d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(fundo);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.setColor(fundo.brighter());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
        g2.dispose();
        super.paintComponent(g);
    }
}
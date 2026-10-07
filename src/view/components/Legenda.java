package view.components;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Dimension;

/** Faixa de legenda sob o tabuleiro: bolinha/quadrado colorido + nome de cada elemento. */
public class Legenda extends JPanel {

    public enum Forma { CIRCULO, QUADRADO, ROBO, ROBO_IA, ALIMENTO, BOMBA, ROCHA }

    public static final class Item {
        final String nome;
        final Color cor;
        final Forma forma;

        public Item(String nome, Color cor, Forma forma) {
            this.nome = nome;
            this.cor = cor;
            this.forma = forma;
        }
    }

    public Legenda(Item[] itens) {
        setOpaque(false);
        setLayout(new FlowLayout(FlowLayout.CENTER, 18, 0));
        for (Item i : itens) {
            add(new Entrada(i));
        }
    }

    private static final class Entrada extends JPanel {
        private final Item item;

        Entrada(Item item) {
            this.item = item;
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
            add(javax.swing.Box.createHorizontalStrut(26));
            add(Componentes.subtitulo(item.nome));
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width, Math.max(d.height, 26));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int cy = getHeight() / 2;
            if (item.forma == Forma.ROBO || item.forma == Forma.ROBO_IA) {
                Icones.desenharRobo(g2, item.cor, item.forma == Forma.ROBO_IA, 11, cy, 24);
            } else if (item.forma == Forma.ALIMENTO) {
                Icones.desenharAlimento(g2, 11, cy, 22);
            } else if (item.forma == Forma.BOMBA) {
                Icones.desenharBomba(g2, 11, cy, 22);
            } else if (item.forma == Forma.ROCHA) {
                Icones.desenharRocha(g2, 11, cy, 22);
            } else {
                g2.setColor(item.cor);
                int y = cy - 6;
                if (item.forma == Forma.CIRCULO) {
                    g2.fillOval(5, y, 12, 12);
                } else {
                    g2.fillRoundRect(5, y, 12, 12, 4, 4);
                }
            }
            g2.dispose();
        }
    }
}

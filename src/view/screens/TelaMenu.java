package view.screens;

import view.JanelaPrincipal;
import view.components.Componentes;
import view.components.Icones;
import view.theme.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;

/** Tela inicial: escolha da aventura (modo de jogo) que será executada. */
public class TelaMenu extends JPanel {

    /** Algo que sabe se desenhar centralizado em (cx, cy) dentro de um quadrado de lado s. */
    private interface Desenho {
        void desenhar(Graphics2D g2, int cx, int cy, int s);
    }

    private static final Desenho ROBO_AZUL = (g, x, y, s) -> Icones.desenharRobo(g, Tema.ROBO_AZUL, false, x, y, s);
    private static final Desenho ROBO_VERMELHO = (g, x, y, s) -> Icones.desenharRobo(g, Tema.ROBO_VERMELHO, false, x, y, s);
    private static final Desenho ROBO_IA = (g, x, y, s) -> Icones.desenharRobo(g, Tema.ROBO_VERDE, true, x, y, s);
    private static final Desenho BOMBA = Icones::desenharBomba;
    private static final Desenho ROCHA = Icones::desenharRocha;

    public TelaMenu(JanelaPrincipal janela) {
        setLayout(new BorderLayout(0, 18));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(28, 72, 32, 72));

        add(new Vitrine(), BorderLayout.NORTH);

        JPanel opcoes = new JPanel();
        opcoes.setOpaque(false);
        opcoes.setLayout(new BoxLayout(opcoes, BoxLayout.Y_AXIS));

        opcoes.add(itemMenu("Você no comando",
                "Guie o robô com as setas até a maçã. Cuidado para não bater na parede!",
                Tema.ROBO_AZUL, new Desenho[] {ROBO_AZUL}, e -> janela.mostrar("manual")));
        opcoes.add(Box.createVerticalStrut(12));
        opcoes.add(itemMenu("Corrida maluca",
                "Dois robôs descontrolados na disputa: quem chega primeiro na maçã?",
                Tema.ROBO_VERMELHO, new Desenho[] {ROBO_AZUL, ROBO_VERMELHO}, e -> janela.mostrar("corrida")));
        opcoes.add(Box.createVerticalStrut(12));
        opcoes.add(itemMenu("Normal x Inteligente",
                "O robô esperto leva vantagem sobre o desajeitado? Vem descobrir!",
                Tema.ROBO_VERDE, new Desenho[] {ROBO_AZUL, ROBO_IA}, e -> janela.mostrar("inteligente")));
        opcoes.add(Box.createVerticalStrut(12));
        opcoes.add(itemMenu("Campo minado",
                "Bombas que explodem e rochas que empurram de volta. Boa sorte!",
                Tema.BOMBA, new Desenho[] {BOMBA, ROCHA, ROBO_IA}, e -> janela.mostrar("obstaculos")));

        add(opcoes, BorderLayout.CENTER);
    }

    /** Cartão clicável (o cartão inteiro é o botão), com a cor do modo e destaque ao passar o mouse. */
    private JComponent itemMenu(String titulo, String descricao, Color cor, Desenho[] icones, ActionListener acao) {
        Cartao card = new Cartao(cor, icones);
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(BorderFactory.createEmptyBorder(14, 28 + Cartao.LARGURA_ICONE * icones.length + 16, 14, 24));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        card.setPreferredSize(new Dimension(100, 92));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(Box.createVerticalGlue());
        JLabel t = Componentes.texto(titulo);
        t.setFont(Tema.FONTE_TITULO.deriveFont(19f));
        textos.add(t);
        textos.add(Box.createVerticalStrut(4));
        textos.add(Componentes.subtitulo(descricao));
        textos.add(Box.createVerticalGlue());
        card.add(textos, BorderLayout.CENTER);

        JLabel seta = Componentes.texto("Jogar  ›");
        seta.setFont(Tema.FONTE_TITULO.deriveFont(16f));
        seta.setForeground(cor);
        card.add(seta, BorderLayout.EAST);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                acao.actionPerformed(new java.awt.event.ActionEvent(card, 0, "abrir"));
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setSobre(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setSobre(false);
            }
        });
        return card;
    }

    /** Painel arredondado com faixa colorida, os personagens do modo e brilho ao passar o mouse. */
    private static final class Cartao extends JPanel {
        static final int LARGURA_ICONE = 54;

        private final Color cor;
        private final Desenho[] icones;
        private boolean sobre;

        Cartao(Color cor, Desenho[] icones) {
            this.cor = cor;
            this.icones = icones;
            setOpaque(false);
        }

        void setSobre(boolean sobre) {
            this.sobre = sobre;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth() - 1;
            int h = getHeight() - 1;

            // fundo (com um leve tom da cor do modo ao passar o mouse)
            g2.setColor(sobre ? mistura(Tema.SUPERFICIE_CLARA, cor, 0.14f) : Tema.SUPERFICIE);
            g2.fillRoundRect(0, 0, w, h, 20, 20);

            // faixa colorida à esquerda
            java.awt.Shape original = g2.getClip();
            g2.setClip(new java.awt.geom.RoundRectangle2D.Double(0, 0, w, h, 20, 20));
            g2.setColor(cor);
            g2.fillRect(0, 0, sobre ? 12 : 8, h);
            g2.setClip(original);

            g2.setColor(sobre ? cor : Tema.BORDA);
            g2.setStroke(new BasicStroke(sobre ? 2f : 1f));
            g2.drawRoundRect(0, 0, w, h, 20, 20);

            // personagens do modo (pulam um pouquinho quando o mouse está em cima)
            int s = 46;
            int x = 28 + LARGURA_ICONE / 2;
            int y = h / 2 + (sobre ? -3 : 0);
            for (Desenho d : icones) {
                d.desenhar(g2, x, y, s);
                x += LARGURA_ICONE;
            }
            g2.dispose();
        }

        private static Color mistura(Color a, Color b, float t) {
            return new Color(
                    Math.round(a.getRed() * (1 - t) + b.getRed() * t),
                    Math.round(a.getGreen() * (1 - t) + b.getGreen() * t),
                    Math.round(a.getBlue() * (1 - t) + b.getBlue() * t));
        }
    }

    private static final class Vitrine extends JPanel {
        private static final int ALTURA = 214;
        private static final int PASSO_MS = 30;
        private static final int PAUSA_QUADROS = 50;

        private double progresso = 0;     // 0..1: posição do robô no caminho
        private int quadro = 0;           // contador de quadros (balanço, brilho)
        private int pausa = 0;            // quadros restantes de comemoração

        Vitrine() {
            setOpaque(false);
            setPreferredSize(new Dimension(100, ALTURA));
            Timer timer = new Timer(PASSO_MS, e -> {
                if (!isShowing()) {
                    return; // tela do menu escondida: não gasta processamento
                }
                quadro++;
                if (pausa > 0) {
                    pausa--;
                    if (pausa == 0) {
                        progresso = 0;
                    }
                } else {
                    progresso += 0.0042;
                    if (progresso >= 1) {
                        progresso = 1;
                        pausa = PAUSA_QUADROS;
                    }
                }
                repaint();
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int w = getWidth();

            desenharTitulo(g2, w);
            desenharCena(g2, w);
            g2.dispose();
        }

        private void desenharTitulo(Graphics2D g2, int w) {
            String titulo = "Robôs Famintos";
            Font fonte = new Font("SansSerif", Font.BOLD, 50);
            g2.setFont(fonte);
            FontMetrics fm = g2.getFontMetrics();
            int larguraTexto = fm.stringWidth(titulo);
            int icone = 54;
            int total = icone + 14 + larguraTexto;
            int x0 = (w - total) / 2;
            int base = 54;

            // maçã "balançando" ao lado do título
            double balanco = Math.sin(quadro * 0.12) * 0.12;
            java.awt.geom.AffineTransform t = g2.getTransform();
            g2.rotate(balanco, x0 + icone / 2.0, base - 18);
            Icones.desenharMaca(g2, x0 + icone / 2, base - 18, icone);
            g2.setTransform(t);

            int xt = x0 + icone + 14;
            g2.setColor(new Color(0, 0, 0, 90));
            g2.drawString(titulo, xt + 3, base + 3);
            g2.setPaint(new GradientPaint(xt, 0, Tema.ROBO_AZUL, xt + larguraTexto, 0, Tema.ROBO_VERDE));
            g2.drawString(titulo, xt, base);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 16));
            fm = g2.getFontMetrics();
            String sub = "Escolha uma aventura e ajude os robôs a encontrar o lanche!";
            g2.setColor(Tema.TEXTO_FRACO);
            g2.drawString(sub, (w - fm.stringWidth(sub)) / 2, base + 32);
        }

        private void desenharCena(Graphics2D g2, int w) {
            int chao = ALTURA - 14;
            int margem = 40;
            int inicio = margem + 30;
            int fim = w - margem - 40;
            int s = 54; // tamanho dos personagens

            // chão tracejado
            g2.setColor(Tema.BORDA);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                    1f, new float[] {2f, 9f}, 0f));
            g2.drawLine(margem, chao, w - margem, chao);

            int rochaX = inicio + (int) ((fim - inicio) * 0.36);
            int bombaX = inicio + (int) ((fim - inicio) * 0.66);
            int macaX = fim;
            int roboX = inicio + (int) ((fim - inicio) * progresso);

            Icones.desenharRocha(g2, rochaX, chao - s / 2 + 2, s - 6);
            Icones.desenharBomba(g2, bombaX, chao - s / 2 + 2, s - 6);

            // pulos: arco sobre cada obstáculo; fora deles, um quiquinho de caminhada
            double pulo = 0;
            double alcance = 62;
            for (int ox : new int[] {rochaX, bombaX}) {
                double d = Math.abs(roboX - ox);
                if (d < alcance) {
                    double r = d / alcance;
                    pulo = Math.max(pulo, 58 * (1 - r * r));
                }
            }
            if (pulo == 0) {
                pulo = 4 * Math.abs(Math.sin(quadro * 0.35));
            }

            boolean chegou = pausa > 0;
            if (chegou) {
                pulo = 16 * Math.abs(Math.sin(quadro * 0.45)); // comemorando
            }

            // maçã (quando o robô chega, ela "pulsa")
            int tamMaca = s + (chegou ? (int) (6 * Math.sin(quadro * 0.45)) : 0);
            Icones.desenharMaca(g2, macaX, chao - s / 2 + 2, tamMaca);

            // sombra do robô no chão
            int larguraSombra = (int) (34 - pulo * 0.25);
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillOval(roboX - larguraSombra / 2, chao - 4, larguraSombra, 8);

            Icones.desenharRobo(g2, Tema.ROBO_AZUL, false, roboX, (int) (chao - s / 2 - pulo + 2), s);

            // estrelinhas de comemoração
            if (chegou) {
                g2.setColor(Tema.ALIMENTO);
                for (int i = 0; i < 5; i++) {
                    double ang = quadro * 0.15 + i * (Math.PI * 2 / 5);
                    int ex = (int) (macaX + Math.cos(ang) * 44);
                    int ey = (int) (chao - s / 2 - 8 + Math.sin(ang) * 30);
                    desenharEstrela(g2, ex, ey, 7);
                }
            }
        }

        private void desenharEstrela(Graphics2D g2, int cx, int cy, int r) {
            Path2D p = new Path2D.Double();
            for (int i = 0; i < 8; i++) {
                double ang = Math.PI * i / 4 - Math.PI / 2;
                double raio = (i % 2 == 0) ? r : r * 0.4;
                double px = cx + Math.cos(ang) * raio;
                double py = cy + Math.sin(ang) * raio;
                if (i == 0) {
                    p.moveTo(px, py);
                } else {
                    p.lineTo(px, py);
                }
            }
            p.closePath();
            g2.fill(p);
        }
    }
}

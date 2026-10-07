package view.components;

import model.Bomba;
import model.Obstaculo;
import model.Robo;
import model.RoboInteligente;
import model.Rocha;
import model.Tabuleiro;
import view.theme.Tema;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

public class PainelTabuleiro extends JPanel {

    private static final int MARGEM = 30;

    private List<Robo> robos = new ArrayList<>();
    private List<Obstaculo> obstaculos = new ArrayList<>();
    private int alimentoX;
    private int alimentoY;

    // aviso temporário (aparece sobre o tabuleiro e some sozinho)
    private static final int DURACAO_AVISO_MS = 3200;
    private static final int FADE_AVISO_MS = 500;
    private String aviso;
    private long inicioAviso;
    private final Timer timerAviso = new Timer(40, e -> {
        if (System.currentTimeMillis() - inicioAviso >= DURACAO_AVISO_MS) {
            aviso = null;
            ((Timer) e.getSource()).stop();
        }
        repaint();
    });

    public PainelTabuleiro() {
        setOpaque(false);
        setPreferredSize(new Dimension(820, 820));
        setMinimumSize(new Dimension(560, 560));
    }

    public void atualizar(List<Robo> robos, List<Obstaculo> obstaculos, int alimentoX, int alimentoY) {
        this.robos = robos;
        this.obstaculos = obstaculos;
        this.alimentoX = alimentoX;
        this.alimentoY = alimentoY;
        repaint();
    }

    /** Mostra uma mensagem sobre o tabuleiro por alguns segundos. */
    public void mostrarAviso(String mensagem) {
        this.aviso = mensagem;
        this.inicioAviso = System.currentTimeMillis();
        timerAviso.restart();
        repaint();
    }

    /** Remove na hora qualquer aviso que esteja aparecendo. */
    public void limparAviso() {
        aviso = null;
        timerAviso.stop();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int tamanho = Tabuleiro.TAMANHO;
        int lado = Math.min(getWidth(), getHeight()) - 2 * MARGEM;
        int celula = Math.max(lado / tamanho, 10);
        int origemX = (getWidth() - celula * tamanho) / 2;
        int origemY = (getHeight() - celula * tamanho) / 2;

        desenharGrade(g2, origemX, origemY, celula, tamanho);
        desenharAlimento(g2, origemX, origemY, celula, tamanho);
        for (Obstaculo o : obstaculos) {
            if (o instanceof Bomba && ((Bomba) o).isDetonada()) {
                continue;
            }
            desenharObstaculo(g2, o, origemX, origemY, celula, tamanho);
        }
        desenharRobos(g2, origemX, origemY, celula, tamanho);
        desenharAviso(g2);
        g2.dispose();
    }

    private int colX(int origemX, int celula, int x) {
        return origemX + x * celula;
    }

    // y cresce para cima no jogo, mas para baixo na tela -> inverte
    private int colY(int origemY, int celula, int tamanho, int y) {
        return origemY + (tamanho - 1 - y) * celula;
    }

    private void desenharGrade(Graphics2D g2, int origemX, int origemY, int celula, int tamanho) {
        int total = celula * tamanho;

        // moldura
        g2.setColor(Tema.SUPERFICIE);
        g2.fillRoundRect(origemX - 14, origemY - 14, total + 28, total + 28, 22, 22);
        g2.setColor(Tema.BORDA);
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(origemX - 14, origemY - 14, total + 28, total + 28, 22, 22);

        // células em xadrez sutil
        for (int x = 0; x < tamanho; x++) {
            for (int y = 0; y < tamanho; y++) {
                g2.setColor((x + y) % 2 == 0 ? Tema.CELULA_A : Tema.CELULA_B);
                g2.fillRect(colX(origemX, celula, x), colY(origemY, celula, tamanho, y), celula, celula);
            }
        }

        g2.setColor(Tema.GRADE);
        for (int i = 0; i <= tamanho; i++) {
            g2.drawLine(origemX + i * celula, origemY, origemX + i * celula, origemY + total);
            g2.drawLine(origemX, origemY + i * celula, origemX + total, origemY + i * celula);
        }

        // números dos eixos
        g2.setFont(Tema.FONTE_NEGRITO);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(Tema.TEXTO_FRACO);
        for (int i = 0; i < tamanho; i++) {
            String n = String.valueOf(i);
            int cx = colX(origemX, celula, i) + celula / 2 - fm.stringWidth(n) / 2;
            g2.drawString(n, cx, origemY + total + 14 + fm.getAscent() / 2 + 6);
            int cy = colY(origemY, celula, tamanho, i) + celula / 2 + fm.getAscent() / 2 - 2;
            g2.drawString(n, origemX - 18 - fm.stringWidth(n) / 2 - 2, cy);
        }
    }

    private void desenharAlimento(Graphics2D g2, int origemX, int origemY, int celula, int tamanho) {
        int cx = colX(origemX, celula, alimentoX) + celula / 2;
        int cy = colY(origemY, celula, tamanho, alimentoY) + celula / 2;
        Icones.desenharAlimento(g2, cx, cy, (int) (celula * 0.62));
    }

    private void desenharObstaculo(Graphics2D g2, Obstaculo o, int origemX, int origemY, int celula, int tamanho) {
        int px = colX(origemX, celula, o.getX());
        int py = colY(origemY, celula, tamanho, o.getY());
        int cx = px + celula / 2;
        int cy = py + celula / 2;
        int tam = (int) (celula * 0.62);
        if (o instanceof Rocha) {
            Icones.desenharRocha(g2, cx, cy, tam);
        } else {
            Icones.desenharBomba(g2, cx, cy, tam);
        }

        // identificação (id) numa plaquinha no canto da célula
        g2.setFont(Tema.FONTE_SECAO);
        FontMetrics fm = g2.getFontMetrics();
        int larg = fm.stringWidth(o.getId()) + 10;
        int alt = fm.getHeight() + 2;
        g2.setColor(new Color(18, 18, 22, 200));
        g2.fillRoundRect(px + 4, py + 4, larg, alt, 8, 8);
        g2.setColor(Tema.TEXTO);
        g2.drawString(o.getId(), px + 9, py + 4 + fm.getAscent() + 1);
    }

    /** Desenha os robôs; se vários estão na mesma célula, dividem o espaço lado a lado. */
    private void desenharRobos(Graphics2D g2, int origemX, int origemY, int celula, int tamanho) {
        for (Robo r : robos) {
            if (!r.isAtivo()) {
                continue;
            }
            int total = 0;
            int indice = 0;
            for (Robo outro : robos) {
                if (outro.isAtivo() && outro.getX() == r.getX() && outro.getY() == r.getY()) {
                    if (outro == r) {
                        indice = total;
                    }
                    total++;
                }
            }
            desenharRobo(g2, r, origemX, origemY, celula, tamanho, indice, total);
        }
    }

    private void desenharRobo(Graphics2D g2, Robo r, int origemX, int origemY, int celula, int tamanho, int indice, int total) {
        int px = colX(origemX, celula, r.getX());
        int py = colY(origemY, celula, tamanho, r.getY());

        // sozinho, o robô ocupa metade da célula; se dividem a célula, ficam lado a lado
        int margem = total == 1 ? celula / 5 : celula / 20;
        int larguraCelula = (celula - 2 * margem) / total;
        int lado = Math.min(larguraCelula, (int) (celula * 0.62));
        int cx = px + margem + indice * larguraCelula + larguraCelula / 2;
        int cy = py + celula / 2;

        Icones.desenharRobo(g2, Tema.corPorNome(r.getCor()), r instanceof RoboInteligente, cx, cy, lado);
    }

    /** Faixa vermelha no topo do tabuleiro, com "fade" nos últimos instantes. */
    private void desenharAviso(Graphics2D g2) {
        if (aviso == null) {
            return;
        }
        long restante = DURACAO_AVISO_MS - (System.currentTimeMillis() - inicioAviso);
        float alfa = restante < FADE_AVISO_MS ? Math.max(0f, restante / (float) FADE_AVISO_MS) : 1f;

        g2.setFont(Tema.FONTE_NEGRITO.deriveFont(16f));
        FontMetrics fm = g2.getFontMetrics();
        
        int icone = 26;
        int larg = Math.min(getWidth() - 20, fm.stringWidth(aviso) + icone + 48);
        int alt = 46;
        int x = (getWidth() - larg) / 2;
        int y = 8;

        g2.setComposite(AlphaComposite.SrcOver.derive(alfa));
        g2.setColor(new Color(0, 0, 0, 90));
        g2.fillRoundRect(x + 2, y + 3, larg, alt, 16, 16);
        g2.setColor(Tema.ERRO);
        g2.fillRoundRect(x, y, larg, alt, 16, 16);

        // ícone "!" desenhado (não depende de fonte de emoji)
        int ix = x + 14;
        int iy = y + (alt - icone) / 2;
        g2.setColor(Tema.TEXTO_ESCURO);
        g2.fillOval(ix, iy, icone, icone);
        g2.setColor(Tema.ERRO);
        g2.drawString("!", ix + icone / 2 - fm.stringWidth("!") / 2, iy + (icone + fm.getAscent()) / 2 - 2);

        g2.setColor(Tema.TEXTO_ESCURO);
        g2.drawString(aviso, ix + icone + 12, y + (alt + fm.getAscent()) / 2 - 3);
        g2.setComposite(AlphaComposite.SrcOver);
    }

}

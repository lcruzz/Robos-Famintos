package view.components;

import view.theme.Tema;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;

public final class Icones {

    private Icones() {
    }

    public static void desenharRobo(Graphics2D g2, Color cor, boolean inteligente, int cx, int cy, int s) {
        double u = s;
        double topo = cy - u / 2 + u * 0.07;

        double cabLarg = u * 0.64;
        double cabAlt = u * 0.40;
        double cabX = cx - cabLarg / 2;
        double cabY = topo + u * 0.17;

        float traco = Math.max(1f, (float) (u * 0.035));
        Color escura = cor.darker();

        // antena
        g2.setStroke(new BasicStroke(Math.max(1.5f, (float) (u * 0.04)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(escura);
        g2.draw(new java.awt.geom.Line2D.Double(cx, cabY, cx, topo + u * 0.07));
        g2.setColor(inteligente ? Tema.ALIMENTO : Tema.TEXTO);
        double rb = u * 0.055;
        g2.fill(new Ellipse2D.Double(cx - rb, topo + u * 0.07 - rb, rb * 2, rb * 2));

        // orelhas
        g2.setColor(escura);
        double orelhaL = u * 0.06;
        double orelhaA = u * 0.16;
        g2.fill(new RoundRectangle2D.Double(cabX - orelhaL + 1, cabY + u * 0.12, orelhaL, orelhaA, 4, 4));
        g2.fill(new RoundRectangle2D.Double(cabX + cabLarg - 1, cabY + u * 0.12, orelhaL, orelhaA, 4, 4));

        // braços
        double corpoLarg = u * 0.54;
        double corpoAlt = u * 0.26;
        double corpoX = cx - corpoLarg / 2;
        double corpoY = cabY + cabAlt + u * 0.03;
        double bracoL = u * 0.08;
        g2.fill(new RoundRectangle2D.Double(corpoX - bracoL - u * 0.01, corpoY + u * 0.02, bracoL, u * 0.20, 4, 4));
        g2.fill(new RoundRectangle2D.Double(corpoX + corpoLarg + u * 0.01, corpoY + u * 0.02, bracoL, u * 0.20, 4, 4));

        // cabeça
        double arco = u * 0.16;
        g2.setColor(cor);
        g2.fill(new RoundRectangle2D.Double(cabX, cabY, cabLarg, cabAlt, arco, arco));
        g2.setColor(escura);
        g2.setStroke(new BasicStroke(traco));
        g2.draw(new RoundRectangle2D.Double(cabX, cabY, cabLarg, cabAlt, arco, arco));

        // visor escuro com olhos e boca
        double visorX = cabX + u * 0.07;
        double visorY = cabY + u * 0.07;
        double visorL = cabLarg - u * 0.14;
        double visorA = cabAlt - u * 0.14;
        g2.setColor(Tema.TEXTO_ESCURO);
        g2.fill(new RoundRectangle2D.Double(visorX, visorY, visorL, visorA, arco * 0.6, arco * 0.6));
        g2.setColor(new Color(150, 240, 255));
        double re = u * 0.06;
        double olhoY = visorY + visorA * 0.38;
        g2.fill(new Ellipse2D.Double(cx - u * 0.13 - re, olhoY - re, re * 2, re * 2));
        g2.fill(new Ellipse2D.Double(cx + u * 0.13 - re, olhoY - re, re * 2, re * 2));
        g2.setStroke(new BasicStroke(Math.max(1f, (float) (u * 0.03)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double bocaY = visorY + visorA * 0.80;
        g2.draw(new java.awt.geom.Line2D.Double(cx - u * 0.07, bocaY, cx + u * 0.07, bocaY));

        // corpo
        g2.setColor(cor);
        g2.fill(new RoundRectangle2D.Double(corpoX, corpoY, corpoLarg, corpoAlt, arco * 0.8, arco * 0.8));
        g2.setColor(escura);
        g2.setStroke(new BasicStroke(traco));
        g2.draw(new RoundRectangle2D.Double(corpoX, corpoY, corpoLarg, corpoAlt, arco * 0.8, arco * 0.8));

        // peito
        if (inteligente) {
            Font f = new Font("SansSerif", Font.BOLD, Math.max(7, (int) (u * 0.16)));
            g2.setFont(f);
            g2.setColor(Tema.TEXTO_ESCURO);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString("IA", (float) (cx - fm.stringWidth("IA") / 2.0),
                    (float) (corpoY + corpoAlt / 2 + fm.getAscent() / 2.0 - 1));
        } else {
            double rp = u * 0.055;
            g2.setColor(Tema.TEXTO_ESCURO);
            g2.fill(new Ellipse2D.Double(cx - rp * 1.6, corpoY + corpoAlt / 2 - rp, rp * 2, rp * 2));
            g2.setColor(Tema.TEXTO);
            g2.fill(new Ellipse2D.Double(cx + rp * 0.2, corpoY + corpoAlt / 2 - rp, rp * 2, rp * 2));
        }
    }

    /** Alimento (maçã) centralizado em (cx, cy), ocupando cerca de s pixels de lado. */
    public static void desenharAlimento(Graphics2D g2, int cx, int cy, int s) {
        desenharMaca(g2, cx, cy, s);
    }

    /** Maçã com formato de verdade: duas "bochechas" no topo, base levemente afinada, cabinho e folha. */
    public static void desenharMaca(Graphics2D g2, int cx, int cy, int s) {
        AffineTransform original = g2.getTransform();
        g2.translate(cx, cy + s * 0.04);
        g2.scale(s, s);

        // sombrinha no chão
        g2.setColor(new Color(0, 0, 0, 55));
        g2.fill(new Ellipse2D.Double(-0.30, 0.40, 0.60, 0.09));

        // corpo
        Path2D corpo = new Path2D.Double();
        corpo.moveTo(0.00, -0.26);
        corpo.curveTo(0.08, -0.40, 0.30, -0.42, 0.40, -0.28);
        corpo.curveTo(0.52, -0.10, 0.46, 0.18, 0.32, 0.36);
        corpo.curveTo(0.24, 0.46, 0.12, 0.46, 0.00, 0.40);
        corpo.curveTo(-0.12, 0.46, -0.24, 0.46, -0.32, 0.36);
        corpo.curveTo(-0.46, 0.18, -0.52, -0.10, -0.40, -0.28);
        corpo.curveTo(-0.30, -0.42, -0.08, -0.40, 0.00, -0.26);
        corpo.closePath();

        g2.setPaint(new RadialGradientPaint(-0.14f, -0.12f, 0.70f,
                new float[] {0f, 0.55f, 1f},
                new Color[] {new Color(255, 105, 97), new Color(220, 40, 45), new Color(150, 15, 30)}));
        g2.fill(corpo);
        g2.setColor(new Color(110, 10, 25));
        g2.setStroke(new BasicStroke(0.018f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(corpo);

        // brilho
        g2.setColor(new Color(255, 255, 255, 120));
        g2.fill(new Ellipse2D.Double(-0.32, -0.20, 0.13, 0.24));

        // cabinho
        g2.setColor(new Color(105, 62, 28));
        g2.setStroke(new BasicStroke(0.045f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D cabo = new Path2D.Double();
        cabo.moveTo(0.00, -0.26);
        cabo.curveTo(0.00, -0.36, 0.02, -0.43, 0.06, -0.50);
        g2.draw(cabo);

        // folha
        Path2D folha = new Path2D.Double();
        folha.moveTo(0.04, -0.37);
        folha.curveTo(0.12, -0.54, 0.30, -0.54, 0.38, -0.46);
        folha.curveTo(0.30, -0.34, 0.14, -0.30, 0.04, -0.37);
        folha.closePath();
        g2.setPaint(new GradientPaint(0.04f, -0.5f, new Color(120, 220, 90), 0.38f, -0.36f, new Color(40, 150, 70)));
        g2.fill(folha);
        g2.setColor(new Color(25, 105, 50));
        g2.setStroke(new BasicStroke(0.012f));
        g2.draw(folha);
        g2.draw(new java.awt.geom.Line2D.Double(0.06, -0.38, 0.32, -0.45));

        g2.setTransform(original);
    }

    /** Bomba clássica: esfera preta com brilho, tampa, pavio e uma faísca. */
    public static void desenharBomba(Graphics2D g2, int cx, int cy, int s) {
        AffineTransform original = g2.getTransform();
        g2.translate(cx, cy + s * 0.06);
        g2.scale(s, s);

        // sombrinha
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fill(new Ellipse2D.Double(-0.28, 0.36, 0.56, 0.08));

        // corpo
        double r = 0.34;
        g2.setPaint(new RadialGradientPaint(-0.12f, -0.12f, 0.62f,
                new float[] {0f, 0.5f, 1f},
                new Color[] {new Color(120, 120, 135), new Color(42, 42, 52), new Color(10, 10, 14)}));
        g2.fill(new Ellipse2D.Double(-r, -r + 0.04, r * 2, r * 2));
        g2.setColor(new Color(0, 0, 0));
        g2.setStroke(new BasicStroke(0.018f));
        g2.draw(new Ellipse2D.Double(-r, -r + 0.04, r * 2, r * 2));

        // brilho
        g2.setColor(new Color(255, 255, 255, 110));
        g2.fill(new Ellipse2D.Double(-0.23, -0.20, 0.13, 0.20));

        // tampa (inclinada, no canto superior direito)
        AffineTransform t = g2.getTransform();
        g2.translate(0.17, -0.25);
        g2.rotate(Math.toRadians(40));
        g2.setPaint(new GradientPaint(-0.1f, 0f, new Color(150, 150, 160), 0.1f, 0f, new Color(80, 80, 92)));
        g2.fill(new RoundRectangle2D.Double(-0.10, -0.07, 0.20, 0.14, 0.04, 0.04));
        g2.setColor(new Color(30, 30, 38));
        g2.setStroke(new BasicStroke(0.014f));
        g2.draw(new RoundRectangle2D.Double(-0.10, -0.07, 0.20, 0.14, 0.04, 0.04));
        g2.setTransform(t);

        // pavio
        g2.setColor(new Color(190, 150, 90));
        g2.setStroke(new BasicStroke(0.035f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D pavio = new Path2D.Double();
        pavio.moveTo(0.24, -0.34);
        pavio.curveTo(0.30, -0.44, 0.38, -0.40, 0.40, -0.50);
        g2.draw(pavio);

        // faísca (estrela de 8 pontas)
        double fx = 0.40;
        double fy = -0.52;
        Path2D estrela = new Path2D.Double();
        for (int i = 0; i < 16; i++) {
            double ang = Math.PI * i / 8 - Math.PI / 2;
            double raio = (i % 2 == 0) ? 0.11 : 0.04;
            double px = fx + Math.cos(ang) * raio;
            double py = fy + Math.sin(ang) * raio;
            if (i == 0) {
                estrela.moveTo(px, py);
            } else {
                estrela.lineTo(px, py);
            }
        }
        estrela.closePath();
        g2.setColor(new Color(255, 149, 0));
        g2.fill(estrela);
        g2.setColor(new Color(255, 225, 80));
        g2.fill(new Ellipse2D.Double(fx - 0.045, fy - 0.045, 0.09, 0.09));

        g2.setTransform(original);
    }

    /** Rocha: pedra irregular cinza com faces claras/escuras e uma rachadura. */
    public static void desenharRocha(Graphics2D g2, int cx, int cy, int s) {
        AffineTransform original = g2.getTransform();
        g2.translate(cx, cy + s * 0.04);
        g2.scale(s, s);

        // sombrinha
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fill(new Ellipse2D.Double(-0.38, 0.28, 0.76, 0.10));

        double[][] p = {
            {-0.44, 0.26}, {-0.48, 0.00}, {-0.34, -0.24}, {-0.08, -0.38},
            {0.20, -0.32}, {0.42, -0.12}, {0.48, 0.14}, {0.38, 0.32}, {-0.10, 0.36}
        };
        Path2D corpo = new Path2D.Double();
        corpo.moveTo(p[0][0], p[0][1]);
        for (int i = 1; i < p.length; i++) {
            corpo.lineTo(p[i][0], p[i][1]);
        }
        corpo.closePath();
        g2.setPaint(new GradientPaint(-0.3f, -0.35f, new Color(190, 192, 202), 0.4f, 0.35f, new Color(88, 90, 102)));
        g2.fill(corpo);

        // face clara no topo
        Path2D topo = new Path2D.Double();
        topo.moveTo(-0.34, -0.24);
        topo.lineTo(-0.08, -0.38);
        topo.lineTo(0.20, -0.32);
        topo.lineTo(0.06, -0.10);
        topo.lineTo(-0.24, -0.06);
        topo.closePath();
        g2.setColor(new Color(225, 227, 235, 150));
        g2.fill(topo);

        // face escura na base
        Path2D base = new Path2D.Double();
        base.moveTo(0.06, -0.10);
        base.lineTo(0.42, -0.12);
        base.lineTo(0.48, 0.14);
        base.lineTo(0.38, 0.32);
        base.lineTo(0.10, 0.20);
        base.closePath();
        g2.setColor(new Color(40, 42, 56, 120));
        g2.fill(base);

        // rachadura
        g2.setColor(new Color(45, 47, 60));
        g2.setStroke(new BasicStroke(0.02f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Path2D crack = new Path2D.Double();
        crack.moveTo(-0.24, -0.06);
        crack.lineTo(-0.12, 0.08);
        crack.lineTo(-0.20, 0.18);
        crack.moveTo(-0.12, 0.08);
        crack.lineTo(0.02, 0.14);
        g2.draw(crack);

        // contorno
        g2.setColor(new Color(50, 52, 66));
        g2.setStroke(new BasicStroke(0.025f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(corpo);

        g2.setTransform(original);
    }
}

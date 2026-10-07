package view.components;

import model.Robo;

import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.function.BiPredicate;

public class SeletorPosicao extends JPanel {

    private final Botao up = Componentes.botaoSecundario("▲");
    private final Botao down = Componentes.botaoSecundario("▼");
    private final Botao left = Componentes.botaoSecundario("◀");
    private final Botao right = Componentes.botaoSecundario("▶");

    private final int xInicial;
    private final int yInicial;
    private int posX;
    private int posY;
    private Runnable aoMudar = () -> { };
    private BiPredicate<Integer, Integer> posicaoPermitida = (x, y) -> true;
    private Runnable aoBloquear = () -> { };

    public SeletorPosicao(int xInicial, int yInicial) {
        this.xInicial = xInicial;
        this.yInicial = yInicial;
        this.posX = xInicial;
        this.posY = yInicial;

        setOpaque(false);
        setLayout(new GridLayout(2, 3, 6, 6));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(190, 90));
        add(vazio());
        add(up);
        add(vazio());
        add(left);
        add(down);
        add(right);

        up.addActionListener(e -> mover(0, 1));
        down.addActionListener(e -> mover(0, -1));
        left.addActionListener(e -> mover(-1, 0));
        right.addActionListener(e -> mover(1, 0));
    }

    private void mover(int dx, int dy) {
        int nx = posX + dx;
        int ny = posY + dy;
        boolean pulou = false;
        while (dentro(nx, ny) && !posicaoPermitida.test(nx, ny)) {
            nx += dx;
            ny += dy;
            pulou = true;
        }
        if (!dentro(nx, ny)) {
            if (pulou) {
                aoBloquear.run();
            }
            return;
        }
        posX = nx;
        posY = ny;
        aoMudar.run();
    }

    private static boolean dentro(int x, int y) {
        return x >= 0 && x < Robo.LIMITE && y >= 0 && y < Robo.LIMITE;
    }

    public void setRestricao(BiPredicate<Integer, Integer> permitida, Runnable aoBloquear) {
        this.posicaoPermitida = permitida;
        this.aoBloquear = aoBloquear;
    }

    private JPanel vazio() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    /** Volta à posição inicial e reabilita as setas, sem disparar o callback de mudança. */
    public void reiniciar() {
        posX = xInicial;
        posY = yInicial;
        setHabilitado(true);
    }

    /** Chamado sempre que a posição escolhida muda. */
    public void setAoMudar(Runnable aoMudar) {
        this.aoMudar = aoMudar;
    }

    public void setHabilitado(boolean habilitado) {
        up.setEnabled(habilitado);
        down.setEnabled(habilitado);
        left.setEnabled(habilitado);
        right.setEnabled(habilitado);
    }
}

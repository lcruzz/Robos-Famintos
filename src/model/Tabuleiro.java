package model;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {

    public static final int TAMANHO = Robo.LIMITE;

    private final int alimentoX;
    private final int alimentoY;
    private final List<Obstaculo> obstaculos = new ArrayList<>();

    public Tabuleiro(int alimentoX, int alimentoY) {
        this.alimentoX = alimentoX;
        this.alimentoY = alimentoY;
    }

    public int getAlimentoX() {
        return alimentoX;
    }

    public int getAlimentoY() {
        return alimentoY;
    }

    public void adicionarObstaculo(Obstaculo obstaculo) {
        obstaculos.add(obstaculo);
    }

    public List<Obstaculo> getObstaculos() {
        return obstaculos;
    }

    /** @return o obstáculo ativo na posição atual do robô, ou null se não houver */
    public Obstaculo verificarColisao(Robo robo) {
        for (Obstaculo o : obstaculos) {
            if (o instanceof Bomba && ((Bomba) o).isDetonada()) {
                continue; // bomba já explodiu, sumiu do tabuleiro
            }
            if (o.estaNaPosicao(robo.getX(), robo.getY())) {
                return o;
            }
        }
        return null;
    }

    /** A posição (0, 0) é onde os robôs começam, então alimento/obstáculos não podem ficar nela. */
    public static boolean ehPosicaoInicial(int x, int y) {
        return x == 0 && y == 0;
    }

    /** Valida se uma posição de alimento/obstáculo está dentro do tabuleiro. */
    public static boolean dentroDoTabuleiro(int x, int y) {
        return x >= 0 && x < TAMANHO && y >= 0 && y < TAMANHO;
    }
}

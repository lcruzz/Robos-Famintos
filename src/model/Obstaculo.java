package model;

public abstract class Obstaculo {

    private final String id;
    private final int x;
    private final int y;

    protected Obstaculo(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean estaNaPosicao(int x, int y) {
        return this.x == x && this.y == y;
    }

    /**
     * Efeito que o obstáculo causa no robô que colidiu com ele.
     *
     * @param robo      robô que colidiu
     * @param xAnterior posição x do robô antes do movimento que causou a colisão
     * @param yAnterior posição y do robô antes do movimento que causou a colisão
     */
    public abstract void bater(Robo robo, int xAnterior, int yAnterior);
}

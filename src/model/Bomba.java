package model;

public class Bomba extends Obstaculo {

    private boolean detonada = false;

    public Bomba(String id, int x, int y) {
        super(id, x, y);
    }

    public boolean isDetonada() {
        return detonada;
    }

    /** Recoloca a bomba no tabuleiro (usado ao iniciar uma nova partida). */
    public void reiniciar() {
        detonada = false;
    }

    @Override
    public void bater(Robo robo, int xAnterior, int yAnterior) {
        robo.setAtivo(false);
        this.detonada = true; // a bomba some do tabuleiro após explodir
    }
}

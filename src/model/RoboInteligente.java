package model;

import exceptions.MovimentoInvalidoException;
import java.util.Random;

public class RoboInteligente extends Robo {

    /** Última direção (1..4) que resultou em movimento inválido. */
    private Integer ultimaDirecaoInvalida = null;

    public RoboInteligente(String cor) {
        super(cor);
    }

    @Override
    public boolean moverAleatorio(Random random) {
        while (true) {
            int direcao;
            do {
                direcao = random.nextInt(4) + 1;
            } while (ultimaDirecaoInvalida != null && direcao == ultimaDirecaoInvalida);

            try {
                mover(direcao);
                ultimaDirecaoInvalida = null;
                return true;
            } catch (MovimentoInvalidoException e) {
                ultimaDirecaoInvalida = direcao;
                // continua tentando outras direções (não repete esta)
                // até que um movimento válido seja realizado, como pede o enunciado
            }
        }
    }

    public Integer getUltimaDirecaoInvalida() {
        return ultimaDirecaoInvalida;
    }
}

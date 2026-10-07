package model;

import exceptions.MovimentoInvalidoException;
import java.util.Random;

public class Robo {

    public static final int LIMITE = 6;

    protected int x;
    protected int y;
    protected final String cor;

    /** false quando o robô "explode" ao bater numa bomba. */
    protected boolean ativo = true;

    protected int movimentosValidos = 0;
    protected int movimentosInvalidos = 0;

    public Robo(String cor) {
        this.cor = cor;
        this.x = 0;
        this.y = 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getCor() {
        return cor;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public int getMovimentosValidos() {
        return movimentosValidos;
    }

    public int getMovimentosInvalidos() {
        return movimentosInvalidos;
    }

    /**
     * Move o robô uma casa na direção indicada.
     *
     * @param direcao "up", "down", "right" ou "left"
     * @throws MovimentoInvalidoException se o movimento levar o robô para
     *                                     fora do tabuleiro, ou se a String
     *                                     não representar uma direção válida
     */
    public void mover(String direcao) throws MovimentoInvalidoException {
        if (direcao == null) {
            throw new MovimentoInvalidoException("null");
        }

        int novoX = x;
        int novoY = y;

        switch (direcao) {
            case "up":
                novoY++;
                break;
            case "down":
                novoY--;
                break;
            case "right":
                novoX++;
                break;
            case "left":
                novoX--;
                break;
            default:
                throw new MovimentoInvalidoException(direcao);
        }

        if (novoX < 0 || novoY < 0 || novoX >= LIMITE || novoY >= LIMITE) {
            movimentosInvalidos++;
            throw new MovimentoInvalidoException(direcao);
        }

        this.x = novoX;
        this.y = novoY;
        movimentosValidos++;
    }

    /**
     * Sobrecarga do método mover (item d): 1=up, 2=down, 3=right, 4=left.
     *
     * @throws MovimentoInvalidoException se o código não for 1..4 ou se o
     *                                     movimento resultante for inválido
     */
    public void mover(int direcaoCodigo) throws MovimentoInvalidoException {
        switch (direcaoCodigo) {
            case 1:
                mover("up");
                break;
            case 2:
                mover("down");
                break;
            case 3:
                mover("right");
                break;
            case 4:
                mover("left");
                break;
            default:
                movimentosInvalidos++;
                throw new MovimentoInvalidoException("codigo=" + direcaoCodigo);
        }
    }

    /**
     * Sorteia uma das 4 direções e tenta mover o robô.
     *
     * @return true se o movimento sorteado foi válido, false caso contrário
     */
    public boolean moverAleatorio(Random random) {
        int direcao = random.nextInt(4) + 1;
        try {
            mover(direcao);
            return true;
        } catch (MovimentoInvalidoException e) {
            return false;
        }
    }

    /** @return true se o robô está exatamente sobre a posição do alimento */
    public boolean encontrouAlimento(int alimentoX, int alimentoY) {
        return this.x == alimentoX && this.y == alimentoY;
    }

    /** Usado quando o robô esbarra numa Rocha e deve voltar para trás. */
    public void voltarPara(int xAnterior, int yAnterior) {
        this.x = xAnterior;
        this.y = yAnterior;
    }
}

package exceptions;

public class MovimentoInvalidoException extends Exception {

    private final String movimentoInvalido;

    /**
     * @param movimentoInvalido descrição do movimento que causou o erro
     *                          (ex: "up", "left", "codigo=7")
     */
    public MovimentoInvalidoException(String movimentoInvalido) {
        super("Movimento inválido: \"" + movimentoInvalido
                + "\" faria o robô sair da área permitida do tabuleiro.");
        this.movimentoInvalido = movimentoInvalido;
    }

    public String getMovimentoInvalido() {
        return movimentoInvalido;
    }
}

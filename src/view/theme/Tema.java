package view.theme;

import java.awt.Color;
import java.awt.Font;

public final class Tema {

    private Tema() {
    }

    // Fundo / superfícies
    public static final Color FUNDO = new Color(18, 18, 22);
    public static final Color SUPERFICIE = new Color(30, 30, 36);
    public static final Color SUPERFICIE_CLARA = new Color(48, 48, 58);
    public static final Color SUPERFICIE_HOVER = new Color(62, 62, 76);
    public static final Color BORDA = new Color(66, 66, 78);

    // Destaque (botões principais, títulos de seção)
    public static final Color ACENTO = new Color(167, 139, 250);
    public static final Color ACENTO_HOVER = new Color(190, 168, 255);
    public static final Color ACENTO_PRESSIONADO = new Color(139, 108, 232);

    // Texto
    public static final Color TEXTO = new Color(240, 240, 245);
    public static final Color TEXTO_FRACO = new Color(160, 160, 175);
    public static final Color TEXTO_ESCURO = new Color(18, 18, 22);
    public static final Color TEXTO_DESABILITADO = new Color(120, 120, 134);

    // Cores dos robôs (usadas também para identificá-los na interface)
    public static final Color ROBO_AZUL = new Color(86, 150, 255);
    public static final Color ROBO_VERDE = new Color(72, 199, 116);
    public static final Color ROBO_VERMELHO = new Color(240, 90, 90);

    // Erro / movimento inválido (destaque no histórico e mensagem temporária)
    public static final Color ERRO = new Color(255, 99, 99);
    public static final Color ERRO_FUNDO = new Color(78, 28, 34);

    // Elementos do tabuleiro
    public static final Color GRADE = new Color(70, 70, 82);
    public static final Color CELULA_A = new Color(34, 34, 41);
    public static final Color CELULA_B = new Color(40, 40, 48);
    public static final Color ALIMENTO = new Color(250, 204, 21);
    public static final Color BOMBA = new Color(255, 149, 0);
    public static final Color ROCHA = new Color(140, 140, 150);

    public static final Font FONTE_TITULO = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONTE_SUBTITULO = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONTE_TEXTO = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONTE_NEGRITO = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FONTE_SECAO = new Font("SansSerif", Font.BOLD, 12);
    public static final Font FONTE_MONO = new Font("Monospaced", Font.PLAIN, 13);

    /** Resolve o nome de cor guardado em Robo.getCor() para um java.awt.Color. */
    public static Color corPorNome(String nome) {
        if (nome == null) {
            return TEXTO_FRACO;
        }
        switch (nome.toLowerCase()) {
            case "azul":
                return ROBO_AZUL;
            case "verde":
                return ROBO_VERDE;
            case "vermelho":
                return ROBO_VERMELHO;
            default:
                return TEXTO_FRACO;
        }
    }
}

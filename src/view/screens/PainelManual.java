package view.screens;

import exceptions.MovimentoInvalidoException;
import model.Robo;
import model.Tabuleiro;
import view.JanelaPrincipal;
import view.components.Botao;
import view.components.Componentes;
import view.components.SeletorPosicao;
import view.theme.Tema;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Modo manual: instancia um robô, o usuário escolhe a posição do alimento
 * (por setas) e move o robô (setas) até achar a comida, tratando a
 * MovimentoInvalidoException a cada tentativa.
 */
public class PainelManual extends PainelBase {

    private static final String DICA_ESCOLHA =
            "Use as setas ao lado e clique em Iniciar";

    private final SeletorPosicao seletorAlimento = new SeletorPosicao(Robo.LIMITE - 1, Robo.LIMITE - 1);
    private final Botao botaoIniciar = Componentes.botao("Iniciar");
    private final Botao botaoNovoJogo = Componentes.botaoSecundario("Novo jogo");
    private final Botao up = Componentes.botaoSecundario("▲");
    private final Botao down = Componentes.botaoSecundario("▼");
    private final Botao left = Componentes.botaoSecundario("◀");
    private final Botao right = Componentes.botaoSecundario("▶");

    private Robo robo;
    private Tabuleiro tabuleiroJogo;
    private boolean emJogo = false;

    public PainelManual(JanelaPrincipal janela) {
        super(janela, "Movimento manual",
                "Mova o robô com as setas até ele encontrar o alimento");
        montarCorpo();
        habilitarSetas(false);
        botaoNovoJogo.setEnabled(false);
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();

        botaoIniciar.addActionListener(e -> iniciar());
        botaoNovoJogo.addActionListener(e -> reiniciar());
        // o alimento não pode ficar na posição inicial dos robôs
        seletorAlimento.setRestricao((x, y) -> !Tabuleiro.ehPosicaoInicial(x, y),
                () -> erroDeEntrada(msgPosicaoInicial("o alimento")));
        seletorAlimento.setAoMudar(() -> {
            if (!emJogo) {
                robo = null;
                tabuleiroJogo = null;
                banner.instrucao(DICA_ESCOLHA);
            }
            atualizarTela();
        });
        up.addActionListener(e -> mover("up"));
        down.addActionListener(e -> mover("down"));
        left.addActionListener(e -> mover("left"));
        right.addActionListener(e -> mover("right"));
    }

    private void iniciar() {
        int ax = seletorAlimento.getPosX();
        int ay = seletorAlimento.getPosY();
        tabuleiroJogo = new Tabuleiro(ax, ay);
        robo = new Robo("azul");
        emJogo = true;
        log.limpar();
        log.log("Alimento definido em (" + ax + ", " + ay + ").");
        log.log("Robô azul criado na posição (0, 0).");
        seletorAlimento.setHabilitado(false);
        botaoIniciar.setEnabled(false);
        botaoNovoJogo.setEnabled(true);
        habilitarSetas(true);
        banner.andamento("Jogo em andamento", "Mova o robô azul com as setas até o alimento");
        atualizarTela();
    }

    private void mover(String direcao) {
        if (robo == null) {
            return;
        }
        try {
            robo.mover(direcao);
            log.log("Moveu '" + direcao + "' -> posição (" + robo.getX() + ", " + robo.getY() + ")");
        } catch (MovimentoInvalidoException e) {
            movimentoInvalido("Exceção capturada: " + e.getMessage());
        }
        atualizarTela();

        if (robo.encontrouAlimento(tabuleiroJogo.getAlimentoX(), tabuleiroJogo.getAlimentoY())) {
            log.log("O robô encontrou o alimento em " + robo.getMovimentosValidos() + " movimento(s) válido(s)!");
            habilitarSetas(false);
            banner.vitoria("O robô azul ganhou!",
                    "Encontrou o alimento em " + robo.getMovimentosValidos() + " movimento(s) válido(s)",
                    Tema.ROBO_AZUL);
            // jogo terminou: permite escolher outro alimento
            emJogo = false;
            seletorAlimento.setHabilitado(true);
            botaoIniciar.setEnabled(true);
        }
    }

    @Override
    protected void reiniciar() {
        emJogo = false;
        robo = null;
        tabuleiroJogo = null;
        seletorAlimento.reiniciar();
        botaoIniciar.setEnabled(true);
        botaoNovoJogo.setEnabled(false);
        habilitarSetas(false);
        log.limpar();
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();
    }

    private void atualizarTela() {
        List<Robo> robos = new ArrayList<>();
        if (robo != null) {
            robos.add(robo);
        }
        int fx = tabuleiroJogo == null ? seletorAlimento.getPosX() : tabuleiroJogo.getAlimentoX();
        int fy = tabuleiroJogo == null ? seletorAlimento.getPosY() : tabuleiroJogo.getAlimentoY();
        tabuleiro.atualizar(robos, tabuleiroJogo == null ? new ArrayList<>() : tabuleiroJogo.getObstaculos(), fx, fy);
    }

    private void habilitarSetas(boolean habilitado) {
        for (Botao b : Arrays.asList(up, down, left, right)) {
            b.setEnabled(habilitado);
        }
    }

    @Override
    protected JPanel montarControles() {
        JPanel painel = new JPanel();
        painel.setOpaque(false);
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));

        painel.add(Componentes.secao("Posição do alimento"));
        painel.add(Box.createVerticalStrut(8));
        painel.add(seletorAlimento);
        painel.add(Box.createVerticalStrut(8));
        painel.add(linhaBotoes(botaoIniciar, botaoNovoJogo));

        painel.add(Box.createVerticalStrut(20));
        painel.add(Componentes.secao("Mover robô azul"));
        painel.add(Box.createVerticalStrut(8));
        JPanel setas = new JPanel(new GridLayout(2, 3, 6, 6));
        setas.setOpaque(false);
        setas.setAlignmentX(Component.LEFT_ALIGNMENT);
        setas.setMaximumSize(new Dimension(190, 90));
        setas.add(blank());
        setas.add(up);
        setas.add(blank());
        setas.add(left);
        setas.add(down);
        setas.add(right);
        painel.add(setas);

        return painel;
    }

    private JPanel blank() {
        JPanel p = new JPanel();
        p.setOpaque(false);
        return p;
    }
}

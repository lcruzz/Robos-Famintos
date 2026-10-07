package view.screens;

import model.Robo;
import model.Tabuleiro;
import view.JanelaPrincipal;
import view.components.Botao;
import view.components.Componentes;
import view.components.Legenda;
import view.components.SeletorPosicao;
import view.theme.Tema;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PainelCorrida extends PainelBase {

    private static final String DICA_ESCOLHA =
            "Use as setas ao lado e clique em Iniciar";

    private final SeletorPosicao seletorAlimento = new SeletorPosicao(Robo.LIMITE - 1, Robo.LIMITE - 1);
    private final Botao botaoIniciar = Componentes.botao("Iniciar");
    private final Botao botaoNovoJogo = Componentes.botaoSecundario("Novo jogo");

    private final Random random = new Random();
    private Timer timer;
    private Robo roboAzul;
    private Robo roboVermelho;
    private Tabuleiro tabuleiroJogo;
    private boolean vezDoAzul = true;

    public PainelCorrida(JanelaPrincipal janela) {
        super(janela, "Corrida aleatória",
                "Dois robôs se movem sozinhos, um de cada vez, até um achar o alimento");
        montarCorpo();
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();
        botaoIniciar.addActionListener(e -> iniciar());
        botaoNovoJogo.addActionListener(e -> reiniciar());
        botaoNovoJogo.setEnabled(false);
        // o alimento não pode ficar na posição inicial dos robôs
        seletorAlimento.setRestricao((x, y) -> !Tabuleiro.ehPosicaoInicial(x, y),
                () -> erroDeEntrada(msgPosicaoInicial("o alimento")));
        seletorAlimento.setAoMudar(() -> {
            roboAzul = null;
            roboVermelho = null;
            tabuleiroJogo = null;
            banner.instrucao(DICA_ESCOLHA);
            atualizarTela();
        });
    }

    private void iniciar() {
        if (timer != null) {
            timer.stop();
        }
        int ax = seletorAlimento.getPosX();
        int ay = seletorAlimento.getPosY();
        tabuleiroJogo = new Tabuleiro(ax, ay);
        roboAzul = new Robo("azul");
        roboVermelho = new Robo("vermelho");
        vezDoAzul = true;

        log.limpar();
        log.log("Alimento em (" + ax + ", " + ay + ").");
        log.log("Robô azul e robô vermelho criados em (0, 0).");
        atualizarTela();

        botaoIniciar.setEnabled(false);
        botaoNovoJogo.setEnabled(true);
        seletorAlimento.setHabilitado(false);
        banner.andamento("Corrida em andamento", "Os robôs azul e vermelho estão procurando o alimento...");
        timer = new Timer(200, e -> executarTurno());
        timer.start();
    }

    private void executarTurno() {
        Robo atual = vezDoAzul ? roboAzul : roboVermelho;
        String nomeCor = vezDoAzul ? "Azul" : "Vermelho";

        boolean valido = atual.moverAleatorio(random);
        if (valido) {
            log.log(nomeCor + " -> (" + atual.getX() + ", " + atual.getY() + ")");
        } else {
            movimentoInvalido(nomeCor + " tentou um movimento inválido (exceção tratada).");
        }
        atualizarTela();

        if (atual.encontrouAlimento(tabuleiroJogo.getAlimentoX(), tabuleiroJogo.getAlimentoY())) {
            timer.stop();
            log.log("");
            log.log(nomeCor + " encontrou o alimento primeiro!");
            log.log("Azul     -> válidos: " + roboAzul.getMovimentosValidos()
                    + " | inválidos: " + roboAzul.getMovimentosInvalidos());
            log.log("Vermelho -> válidos: " + roboVermelho.getMovimentosValidos()
                    + " | inválidos: " + roboVermelho.getMovimentosInvalidos());
            botaoIniciar.setEnabled(true);
            seletorAlimento.setHabilitado(true);
            banner.vitoria("O robô " + nomeCor.toLowerCase() + " ganhou!",
                    "Encontrou o alimento primeiro (" + atual.getMovimentosValidos() + " movimento(s) válido(s))",
                    vezDoAzul ? Tema.ROBO_AZUL : Tema.ROBO_VERMELHO);
            return;
        }

        vezDoAzul = !vezDoAzul;
    }

    @Override
    protected void reiniciar() {
        if (timer != null) {
            timer.stop();
        }
        roboAzul = null;
        roboVermelho = null;
        tabuleiroJogo = null;
        vezDoAzul = true;
        seletorAlimento.reiniciar();
        botaoIniciar.setEnabled(true);
        botaoNovoJogo.setEnabled(false);
        log.limpar();
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();
    }

    private void atualizarTela() {
        List<Robo> robos = new ArrayList<>();
        if (roboAzul != null) {
            robos.addAll(Arrays.asList(roboAzul, roboVermelho));
        }
        int fx = tabuleiroJogo == null ? seletorAlimento.getPosX() : tabuleiroJogo.getAlimentoX();
        int fy = tabuleiroJogo == null ? seletorAlimento.getPosY() : tabuleiroJogo.getAlimentoY();
        tabuleiro.atualizar(robos, new ArrayList<>(), fx, fy);
    }

    @Override
    protected Legenda.Item[] legendaItens() {
        return new Legenda.Item[] {
            new Legenda.Item("Robô azul", Tema.ROBO_AZUL, Legenda.Forma.ROBO),
            new Legenda.Item("Robô vermelho", Tema.ROBO_VERMELHO, Legenda.Forma.ROBO),
            new Legenda.Item("Alimento", Tema.ALIMENTO, Legenda.Forma.ALIMENTO)
        };
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

        return painel;
    }
}
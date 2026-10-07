package view.screens;

import model.Robo;
import model.RoboInteligente;
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Normal x Inteligente: um Robo normal (azul) e um RoboInteligente (verde)
 * se movem aleatoriamente, um de cada vez, até que AMBOS encontrem o
 * alimento. Cada um para de jogar assim que encontra; ao final mostra
 * quantos movimentos cada um precisou.
 */
public class PainelInteligente extends PainelBase {

    private static final String DICA_ESCOLHA =
            "Use as setas ao lado e clique em Iniciar";

    private final SeletorPosicao seletorAlimento = new SeletorPosicao(Robo.LIMITE - 1, Robo.LIMITE - 1);
    private final Botao botaoIniciar = Componentes.botao("Iniciar");
    private final Botao botaoNovoJogo = Componentes.botaoSecundario("Novo jogo");

    private final Random random = new Random();
    private Timer timer;
    private Robo roboNormal;
    private RoboInteligente roboInteligente;
    private Tabuleiro tabuleiroJogo;
    private boolean vezDoNormal = true;
    private boolean normalAchou = false;
    private boolean inteligenteAchou = false;
    private String vencedor = null;
    private Color corVencedor = null;

    public PainelInteligente(JanelaPrincipal janela) {
        super(janela, "Normal x Inteligente",
                "Robô comum contra o RoboInteligente, até ambos acharem o alimento");
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
            roboNormal = null;
            roboInteligente = null;
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
        roboNormal = new Robo("azul");
        roboInteligente = new RoboInteligente("verde");
        vezDoNormal = true;
        normalAchou = false;
        inteligenteAchou = false;
        vencedor = null;
        corVencedor = null;

        log.limpar();
        log.log("Alimento em (" + ax + ", " + ay + ").");
        log.log("Robô normal (azul) e RoboInteligente (verde) criados em (0, 0).");
        atualizarTela();

        botaoIniciar.setEnabled(false);
        botaoNovoJogo.setEnabled(true);
        seletorAlimento.setHabilitado(false);
        banner.andamento("Jogo em andamento", "O robô normal (azul) e o RoboInteligente (verde) estão procurando o alimento...");
        timer = new Timer(200, e -> executarTurno());
        timer.start();
    }

    private void executarTurno() {
        if (normalAchou && inteligenteAchou) {
            timer.stop();
            return;
        }

        boolean deveJogarNormal = !normalAchou && (vezDoNormal || inteligenteAchou);
        if (deveJogarNormal) {
            jogarTurno(roboNormal, "Normal (azul)", true);
        } else if (!inteligenteAchou) {
            jogarTurno(roboInteligente, "Inteligente (verde)", false);
        }
        vezDoNormal = !vezDoNormal;
        atualizarTela();

        if (normalAchou && inteligenteAchou) {
            timer.stop();
            log.log("");
            log.log("Os dois robôs encontraram o alimento!");
            log.log("Normal      -> movimentos válidos: " + roboNormal.getMovimentosValidos()
                    + " | inválidos: " + roboNormal.getMovimentosInvalidos());
            log.log("Inteligente -> movimentos válidos: " + roboInteligente.getMovimentosValidos()
                    + " | inválidos: " + roboInteligente.getMovimentosInvalidos());
            botaoIniciar.setEnabled(true);
            seletorAlimento.setHabilitado(true);
            banner.vitoria(vencedor + " ganhou!",
                    "Chegou primeiro ao alimento. O outro robô também chegou depois.", corVencedor);
        }
    }

    private void jogarTurno(Robo robo, String nome, boolean ehNormal) {
        boolean valido = robo.moverAleatorio(random);
        if (valido) {
            log.log(nome + " -> (" + robo.getX() + ", " + robo.getY() + ")");
        } else {
            movimentoInvalido(nome + " tentou um movimento inválido (exceção tratada).");
        }
        if (robo.encontrouAlimento(tabuleiroJogo.getAlimentoX(), tabuleiroJogo.getAlimentoY())) {
            log.log(nome + " encontrou o alimento em " + robo.getMovimentosValidos() + " movimento(s) válido(s)!");
            if (ehNormal) {
                normalAchou = true;
            } else {
                inteligenteAchou = true;
            }
            if (vencedor == null) {
                vencedor = ehNormal ? "O robô normal (azul)" : "O RoboInteligente (verde)";
                corVencedor = ehNormal ? Tema.ROBO_AZUL : Tema.ROBO_VERDE;
                banner.vitoria(vencedor + " ganhou!",
                        "Achou o alimento primeiro; o outro robô ainda está procurando...", corVencedor);
            }
        }
    }

    @Override
    protected void reiniciar() {
        if (timer != null) {
            timer.stop();
        }
        roboNormal = null;
        roboInteligente = null;
        tabuleiroJogo = null;
        vezDoNormal = true;
        normalAchou = false;
        inteligenteAchou = false;
        vencedor = null;
        corVencedor = null;
        seletorAlimento.reiniciar();
        botaoIniciar.setEnabled(true);
        botaoNovoJogo.setEnabled(false);
        log.limpar();
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();
    }

    private void atualizarTela() {
        List<Robo> robos = new ArrayList<>();
        if (roboNormal != null) {
            robos.addAll(Arrays.asList(roboNormal, roboInteligente));
        }
        int fx = tabuleiroJogo == null ? seletorAlimento.getPosX() : tabuleiroJogo.getAlimentoX();
        int fy = tabuleiroJogo == null ? seletorAlimento.getPosY() : tabuleiroJogo.getAlimentoY();
        tabuleiro.atualizar(robos, new ArrayList<>(), fx, fy);
    }

    @Override
    protected Legenda.Item[] legendaItens() {
        return new Legenda.Item[] {
            new Legenda.Item("Robô normal", Tema.ROBO_AZUL, Legenda.Forma.ROBO),
            new Legenda.Item("RoboInteligente", Tema.ROBO_VERDE, Legenda.Forma.ROBO_IA),
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
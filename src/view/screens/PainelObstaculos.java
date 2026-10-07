package view.screens;

import model.Bomba;
import model.Obstaculo;
import model.Robo;
import model.RoboInteligente;
import model.Rocha;
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
import javax.swing.JSpinner;
import javax.swing.Timer;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PainelObstaculos extends PainelBase {

    private static final String DICA_ESCOLHA =
        "Use as setas ao lado, adicione obstáculos se quiser e clique em Iniciar";

    private final SeletorPosicao seletorAlimento = new SeletorPosicao(Robo.LIMITE - 1, Robo.LIMITE - 1);
    private final JSpinner spinnerObstX = Componentes.spinnerCoordenada();
    private final JSpinner spinnerObstY = Componentes.spinnerCoordenada();
    private final Botao botaoBomba = Componentes.botaoSecundario("+ Bomba");
    private final Botao botaoRocha = Componentes.botaoSecundario("+ Rocha");
    private final Botao botaoIniciar = Componentes.botao("Iniciar");
    private final Botao botaoNovoJogo = Componentes.botaoSecundario("Novo jogo");

    private final Random random = new Random();
    private final List<Obstaculo> obstaculosPendentes = new ArrayList<>();
    private int contadorObstaculo = 1;

    private Timer timer;
    private Robo roboNormal;
    private RoboInteligente roboInteligente;
    private Tabuleiro tabuleiroJogo;
    private boolean vezDoNormal = true;

    public PainelObstaculos(JanelaPrincipal janela) {
        super(janela, "Bombas e rochas",
                "Adicione obstáculos e veja o robô normal e o inteligente tentarem chegar ao alimento");
        montarCorpo();
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();

        botaoBomba.addActionListener(e -> adicionarObstaculo(true));
        botaoRocha.addActionListener(e -> adicionarObstaculo(false));
        botaoIniciar.addActionListener(e -> iniciar());
        botaoNovoJogo.addActionListener(e -> reiniciar());
        botaoNovoJogo.setEnabled(false);
        // o alimento nunca pode ficar na posição (0, 0) nem em cima de um obstáculo já colocado
        seletorAlimento.setRestricao(
                (x, y) -> !Tabuleiro.ehPosicaoInicial(x, y) && obstaculoEm(x, y) == null,
                () -> erroDeEntrada("O alimento não pode ficar em (0, 0) nem em cima de uma bomba ou rocha!"));
        seletorAlimento.setAoMudar(() -> {
            roboNormal = null;
            roboInteligente = null;
            tabuleiroJogo = null;
            banner.instrucao(DICA_ESCOLHA);
            atualizarTela();
        });
    }

    private void adicionarObstaculo(boolean bomba) {
        int x = (int) spinnerObstX.getValue();
        int y = (int) spinnerObstY.getValue();
        if (Tabuleiro.ehPosicaoInicial(x, y)) {
            erroDeEntrada(msgPosicaoInicial(bomba ? "uma bomba" : "uma rocha"));
            return;
        }
        if (x == seletorAlimento.getPosX() && y == seletorAlimento.getPosY()) {
            erroDeEntrada("Não dá para colocar " + (bomba ? "uma bomba" : "uma rocha")
                    + " em (" + x + ", " + y + "): o alimento está nessa posição!");
            return;
        }
        String id = (bomba ? "B" : "R") + contadorObstaculo++;
        Obstaculo o = bomba ? new Bomba(id, x, y) : new Rocha(id, x, y);
        obstaculosPendentes.add(o);
        log.log((bomba ? "Bomba " : "Rocha ") + id + " adicionada em (" + x + ", " + y + ").");
        roboNormal = null;
        roboInteligente = null;
        tabuleiroJogo = null;
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();
    }

    /** Obstáculo já colocado na posição (x, y), ou null se a casa estiver livre. */
    private Obstaculo obstaculoEm(int x, int y) {
        for (Obstaculo o : obstaculosPendentes) {
            if (o.estaNaPosicao(x, y)) {
                return o;
            }
        }
        return null;
    }

    private void iniciar() {
        if (timer != null) {
            timer.stop();
        }
        int ax = seletorAlimento.getPosX();
        int ay = seletorAlimento.getPosY();
        tabuleiroJogo = new Tabuleiro(ax, ay);
        for (Obstaculo o : obstaculosPendentes) {
            if (o instanceof Bomba) {
                ((Bomba) o).reiniciar();
            }
            tabuleiroJogo.adicionarObstaculo(o);
        }
        roboNormal = new Robo("azul");
        roboInteligente = new RoboInteligente("verde");
        vezDoNormal = true;

        log.limpar();
        log.log("Alimento em (" + ax + ", " + ay + "), " + obstaculosPendentes.size() + " obstáculo(s) no tabuleiro.");
        atualizarTela();

        botaoIniciar.setEnabled(false);
        botaoNovoJogo.setEnabled(true);
        botaoBomba.setEnabled(false);
        botaoRocha.setEnabled(false);
        seletorAlimento.setHabilitado(false);
        banner.andamento("Jogo em andamento", "Cuidado com as bombas e rochas no caminho dos robôs...");
        timer = new Timer(200, e -> executarTurno());
        timer.start();
    }

    private void executarTurno() {
        Robo atual = vezDoNormal ? roboNormal : roboInteligente;
        String nome = vezDoNormal ? "Normal (azul)" : "Inteligente (verde)";
        vezDoNormal = !vezDoNormal;

        if (!atual.isAtivo()) {
            verificarFim();
            return;
        }

        int xAntes = atual.getX();
        int yAntes = atual.getY();
        boolean valido = atual.moverAleatorio(random);

        if (!valido) {
            movimentoInvalido(nome + " tentou um movimento inválido (exceção tratada).");
        } else {
            log.log(nome + " -> (" + atual.getX() + ", " + atual.getY() + ")");
            Obstaculo colisao = tabuleiroJogo.verificarColisao(atual);
            if (colisao != null) {
                colisao.bater(atual, xAntes, yAntes);
                if (colisao instanceof Bomba) {
                    roboExplodiu(nome, colisao.getId());
                } else {
                    log.log(nome + " bateu na rocha " + colisao.getId() + " e voltou para (" + xAntes + ", " + yAntes + ").");
                }
            }
        }
        atualizarTela();

        if (atual.isAtivo() && atual.encontrouAlimento(tabuleiroJogo.getAlimentoX(), tabuleiroJogo.getAlimentoY())) {
            timer.stop();
            log.log("");
            log.log(nome + " encontrou o alimento!");
            boolean normal = atual == roboNormal;
            banner.vitoria("O robô " + (normal ? "normal (azul)" : "inteligente (verde)") + " ganhou!",
                    "Encontrou o alimento primeiro",
                    normal ? Tema.ROBO_AZUL : Tema.ROBO_VERDE);
            mostrarResultadoFinal();
            return;
        }

        verificarFim();
    }

    private void verificarFim() {
        if (!roboNormal.isAtivo() && !roboInteligente.isAtivo()) {
            timer.stop();
            log.log("");
            log.log("Os dois robôs explodiram. Ninguém encontrou o alimento.");
            banner.semVencedor("💥  Os dois robôs explodiram!", "Nenhum robô ganhou: ninguém encontrou o alimento");
            mostrarResultadoFinal();
        }
    }

    private void mostrarResultadoFinal() {
        log.log("Normal      -> válidos: " + roboNormal.getMovimentosValidos()
                + " | inválidos: " + roboNormal.getMovimentosInvalidos()
                + (roboNormal.isAtivo() ? "" : " | EXPLODIU"));
        log.log("Inteligente -> válidos: " + roboInteligente.getMovimentosValidos()
                + " | inválidos: " + roboInteligente.getMovimentosInvalidos()
                + (roboInteligente.isAtivo() ? "" : " | EXPLODIU"));
        botaoIniciar.setEnabled(true);
        botaoBomba.setEnabled(true);
        botaoRocha.setEnabled(true);
        seletorAlimento.setHabilitado(true);
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
        obstaculosPendentes.clear();
        contadorObstaculo = 1;
        spinnerObstX.setValue(0);
        spinnerObstY.setValue(0);
        seletorAlimento.reiniciar();
        botaoIniciar.setEnabled(true);
        botaoNovoJogo.setEnabled(false);
        botaoBomba.setEnabled(true);
        botaoRocha.setEnabled(true);
        log.limpar();
        tabuleiro.limparAviso();
        banner.instrucao(DICA_ESCOLHA);
        atualizarTela();
    }

    private void atualizarTela() {
        List<Robo> robos = new ArrayList<>();
        if (roboNormal != null) {
            robos.addAll(Arrays.asList(roboNormal, roboInteligente));
        }
        List<Obstaculo> obstaculos = tabuleiroJogo == null ? obstaculosPendentes : tabuleiroJogo.getObstaculos();
        int fx = tabuleiroJogo == null ? seletorAlimento.getPosX() : tabuleiroJogo.getAlimentoX();
        int fy = tabuleiroJogo == null ? seletorAlimento.getPosY() : tabuleiroJogo.getAlimentoY();
        tabuleiro.atualizar(robos, obstaculos, fx, fy);
    }

    @Override
    protected Legenda.Item[] legendaItens() {
        return new Legenda.Item[] {
            new Legenda.Item("Robô normal", Tema.ROBO_AZUL, Legenda.Forma.ROBO),
            new Legenda.Item("RoboInteligente", Tema.ROBO_VERDE, Legenda.Forma.ROBO_IA),
            new Legenda.Item("Alimento", Tema.ALIMENTO, Legenda.Forma.ALIMENTO),
            new Legenda.Item("Bomba", Tema.BOMBA, Legenda.Forma.BOMBA),
            new Legenda.Item("Rocha", Tema.ROCHA, Legenda.Forma.ROCHA)
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

        painel.add(Box.createVerticalStrut(18));
        painel.add(Componentes.secao("Adicionar obstáculo"));
        painel.add(Box.createVerticalStrut(8));
        painel.add(Componentes.linhaCoordenadas(spinnerObstX, spinnerObstY));
        painel.add(Box.createVerticalStrut(10));
        JPanel botoesObst = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        botoesObst.setOpaque(false);
        botoesObst.setAlignmentX(Component.LEFT_ALIGNMENT);
        botoesObst.add(botaoBomba);
        botoesObst.add(Box.createHorizontalStrut(8));
        botoesObst.add(botaoRocha);
        painel.add(botoesObst);

        painel.add(Box.createVerticalStrut(12));
        painel.add(linhaBotoes(botaoIniciar, botaoNovoJogo));

        return painel;
    }
}
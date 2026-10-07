package view.screens;

import view.JanelaPrincipal;
import view.components.Banner;
import view.components.Botao;
import view.components.Componentes;
import view.components.Legenda;
import view.components.PainelLog;
import view.components.PainelTabuleiro;
import view.theme.Tema;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public abstract class PainelBase extends JPanel {

    protected final PainelTabuleiro tabuleiro = new PainelTabuleiro();
    protected final PainelLog log = new PainelLog();
    protected final Banner banner = new Banner();

    protected PainelBase(JanelaPrincipal janela, String titulo, String subtitulo) {
        setLayout(new BorderLayout(0, 10));
        setBackground(Tema.FUNDO);
        setBorder(BorderFactory.createEmptyBorder(14, 24, 16, 24));

        Botao voltar = Componentes.botaoSecundario("‹  Menu");
        voltar.addActionListener(e -> {
            reiniciar();
            janela.mostrar("menu");
        });

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        textos.add(Componentes.titulo(titulo));
        textos.add(Box.createVerticalStrut(4));
        textos.add(Componentes.subtitulo(subtitulo));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);
        cabecalho.add(voltar, BorderLayout.WEST);
        cabecalho.add(textos, BorderLayout.CENTER);
        add(cabecalho, BorderLayout.NORTH);
    }

    /**
     * Monta o corpo da tela (tabuleiro + painel lateral).
     * As subclasses DEVEM chamar este método no final do próprio construtor
     * — nunca é chamado automaticamente pelo construtor da base — porque
     * montarControles() usa campos que só existem depois que os
     * inicializadores de campo da subclasse já rodaram.
     */
    protected void montarCorpo() {
        JPanel ladoTabuleiro = new JPanel(new BorderLayout(0, 8));
        ladoTabuleiro.setOpaque(false);
        ladoTabuleiro.add(banner, BorderLayout.NORTH);
        ladoTabuleiro.add(tabuleiro, BorderLayout.CENTER);
        ladoTabuleiro.add(new Legenda(legendaItens()), BorderLayout.SOUTH);

        JPanel lateral = new Cartao();
        lateral.setLayout(new BorderLayout(0, 16));
        lateral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        lateral.setPreferredSize(new Dimension(330, 0));
        lateral.add(montarControles(), BorderLayout.NORTH);

        JPanel historico = new JPanel(new BorderLayout(0, 8));
        historico.setOpaque(false);
        historico.add(Componentes.secao("Histórico"), BorderLayout.NORTH);
        historico.add(log, BorderLayout.CENTER);
        lateral.add(historico, BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(24, 0));
        centro.setOpaque(false);
        centro.add(ladoTabuleiro, BorderLayout.CENTER);
        centro.add(lateral, BorderLayout.EAST);
        add(centro, BorderLayout.CENTER);
    }

    /** Linha com os botões "Iniciar" e "Novo jogo" lado a lado. */
    protected JPanel linhaBotoes(Botao iniciar, Botao novoJogo) {
        JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha.setOpaque(false);
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);
        linha.add(iniciar);
        linha.add(Box.createHorizontalStrut(8));
        linha.add(novoJogo);
        return linha;
    }

    /**
     * Registra um movimento inválido (robô bateu na parede): a linha vai para
     * o histórico, em destaque.
     */
    /** Mensagem de erro padrão para itens colocados na posição inicial dos robôs. */
    protected static String msgPosicaoInicial(String item) {
        return "Não dá para colocar " + item + " em (0, 0): é a posição inicial dos robôs!";
    }

    /** Ação recusada pelo jogo: linha em destaque no histórico e aviso temporário sobre o tabuleiro. */
    protected void erroDeEntrada(String mensagem) {
        log.logErro(mensagem);
        tabuleiro.mostrarAviso(mensagem);
    }

    protected void movimentoInvalido(String linhaHistorico) {
        log.logErro(linhaHistorico);
    }

    /**
     * Registra a explosão de um robô: linha em destaque no histórico e um
     * aviso temporário sobre o tabuleiro dizendo que ele explodiu e morreu.
     */
    protected void roboExplodiu(String nomeRobo, String idBomba) {
        log.logErro(nomeRobo + " bateu na bomba " + idBomba + " e explodiu!");
        tabuleiro.mostrarAviso("Robô " + nomeRobo + " explodiu e morreu!");
    }

    /**
     * Devolve a tela ao estado inicial (chamado ao voltar para o menu): para
     * qualquer partida em andamento, limpa robôs, obstáculos e histórico e
     * recoloca o alimento e os controles nas posições originais.
     */
    protected abstract void reiniciar();

    /** Controles específicos de cada tela (alimento, botões etc.); o histórico é adicionado pela base. */
    protected abstract JPanel montarControles();

    /** Itens da legenda sob o tabuleiro; as telas com obstáculos sobrescrevem. */
    protected Legenda.Item[] legendaItens() {
        return new Legenda.Item[] {
            new Legenda.Item("Robô", Tema.ROBO_AZUL, Legenda.Forma.ROBO),
            new Legenda.Item("Alimento", Tema.ALIMENTO, Legenda.Forma.ALIMENTO)
        };
    }

    /** Painel com fundo arredondado e borda sutil. */
    static class Cartao extends JPanel {
        Cartao() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Tema.SUPERFICIE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.setColor(Tema.BORDA);
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g2.dispose();
        }
    }
}

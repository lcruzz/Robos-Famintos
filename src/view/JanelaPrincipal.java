package view;

import view.screens.PainelCorrida;
import view.screens.PainelInteligente;
import view.screens.PainelManual;
import view.screens.PainelObstaculos;
import view.screens.TelaMenu;
import view.theme.Tema;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.CardLayout;
import java.awt.Dimension;

/** Janela única do jogo; troca de tela por CardLayout (menu <-> telas do jogo). */
public class JanelaPrincipal extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cartas = new JPanel(cardLayout);

    public JanelaPrincipal() {
        super("Robôs Famintos");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 800));
        getContentPane().setBackground(Tema.FUNDO);

        cartas.setOpaque(true);
        cartas.setBackground(Tema.FUNDO);
        cartas.add(new TelaMenu(this), "menu");
        cartas.add(new PainelManual(this), "manual");
        cartas.add(new PainelCorrida(this), "corrida");
        cartas.add(new PainelInteligente(this), "inteligente");
        cartas.add(new PainelObstaculos(this), "obstaculos");

        setContentPane(cartas);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // abre grande: o plano cartesiano ocupa o espaço disponível
    }

    public void mostrar(String nomeTela) {
        cardLayout.show(cartas, nomeTela);
    }
}

package view.components;

import model.Robo;
import view.theme.Tema;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicSpinnerUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;

/** Métodos utilitários para criar botões/rótulos/spinners com o tema escuro padrão. */
public final class Componentes {

    private Componentes() {
    }

    /** Botão de ação principal (fundo de destaque, texto escuro). */
    public static Botao botao(String texto) {
        return new Botao(texto, Botao.Tipo.PRINCIPAL);
    }

    /** Botão secundário (fundo cinza-escuro, texto claro). */
    public static Botao botaoSecundario(String texto) {
        return new Botao(texto, Botao.Tipo.SECUNDARIO);
    }

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.FONTE_TITULO);
        l.setForeground(Tema.TEXTO);
        return l;
    }

    public static JLabel subtitulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.FONTE_SUBTITULO);
        l.setForeground(Tema.TEXTO_FRACO);
        return l;
    }

    public static JLabel texto(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.FONTE_TEXTO);
        l.setForeground(Tema.TEXTO);
        return l;
    }

    /** Título de seção do painel lateral (pequeno, em caixa alta, cor de destaque). */
    public static JLabel secao(String texto) {
        JLabel l = new JLabel(texto.toUpperCase());
        l.setFont(Tema.FONTE_SECAO);
        l.setForeground(Tema.ACENTO);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    /** Linha "x: [ ]  y: [ ]" para escolher uma coordenada. */
    public static JPanel linhaCoordenadas(JSpinner x, JSpinner y) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(texto("x"));
        p.add(javax.swing.Box.createHorizontalStrut(8));
        p.add(x);
        p.add(javax.swing.Box.createHorizontalStrut(12));
        p.add(texto("y"));
        p.add(javax.swing.Box.createHorizontalStrut(8));
        p.add(y);
        return p;
    }

    /** Spinner numérico limitado a [0, Robo.LIMITE - 1], usado para escolher coordenadas. */
    public static JSpinner spinnerCoordenada() {
        JSpinner s = new JSpinner(new SpinnerNumberModel(0, 0, Robo.LIMITE - 1, 1));
        s.setUI(new SpinnerEscuro());
        s.setFont(Tema.FONTE_NEGRITO);
        s.setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        s.setPreferredSize(new Dimension(70, 34));

        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) s.getEditor();
        editor.setBackground(Tema.SUPERFICIE_CLARA);
        editor.getTextField().setBackground(Tema.SUPERFICIE_CLARA);
        editor.getTextField().setForeground(Tema.TEXTO);
        editor.getTextField().setCaretColor(Tema.TEXTO);
        editor.getTextField().setFont(Tema.FONTE_NEGRITO);
        editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
        editor.getTextField().setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
        return s;
    }

    /** Spinner com setas escuras (o do sistema ficaria branco sobre o tema escuro). */
    private static final class SpinnerEscuro extends BasicSpinnerUI {
        @Override
        protected Component createNextButton() {
            return seta(SwingConstants.NORTH, "Spinner.nextButton");
        }

        @Override
        protected Component createPreviousButton() {
            return seta(SwingConstants.SOUTH, "Spinner.previousButton");
        }

        private Component seta(int direcao, String nome) {
            BasicArrowButton b = new BasicArrowButton(direcao,
                    Tema.SUPERFICIE_HOVER, Tema.SUPERFICIE, Tema.TEXTO, Tema.SUPERFICIE_HOVER);
            b.setBorder(BorderFactory.createEmptyBorder());
            b.setName(nome);
            b.setInheritsPopupMenu(true);
            if (direcao == SwingConstants.NORTH) {
                installNextButtonListeners(b);
            } else {
                installPreviousButtonListeners(b);
            }
            return b;
        }
    }
}

package view.components;

import view.theme.Tema;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.plaf.basic.BasicScrollBarUI;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/** Área de texto (somente leitura) para mostrar o histórico de movimentos e exceções. */
public class PainelLog extends JScrollPane {

    private final JTextPane area = new JTextPane();
    private final SimpleAttributeSet estiloNormal = new SimpleAttributeSet();
    private final SimpleAttributeSet estiloErro = new SimpleAttributeSet();

    public PainelLog() {
        area.setEditable(false);
        area.setBackground(Tema.FUNDO);
        area.setForeground(Tema.TEXTO);
        area.setFont(Tema.FONTE_MONO);
        StyleConstants.setForeground(estiloNormal, Tema.TEXTO);
        StyleConstants.setFontFamily(estiloNormal, Tema.FONTE_MONO.getFamily());
        StyleConstants.setFontSize(estiloNormal, Tema.FONTE_MONO.getSize());

        // movimento inválido: negrito, vermelho e fundo avermelhado para chamar atenção
        estiloErro.addAttributes(estiloNormal);
        StyleConstants.setForeground(estiloErro, Tema.ERRO);
        StyleConstants.setBackground(estiloErro, Tema.ERRO_FUNDO);
        StyleConstants.setBold(estiloErro, true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        setViewportView(area);
        setBorder(BorderFactory.createLineBorder(Tema.BORDA));
        setPreferredSize(new Dimension(200, 200));
        getViewport().setBackground(Tema.FUNDO);
        setBackground(Tema.FUNDO);

        JScrollBar barra = getVerticalScrollBar();
        barra.setUI(new BarraEscura());
        barra.setPreferredSize(new Dimension(10, 0));
        barra.setBackground(Tema.FUNDO);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }

    /** Linha comum do histórico. */
    public void log(String mensagem) {
        adicionar(mensagem, estiloNormal);
    }

    /** Linha de movimento inválido: aparece em destaque (vermelho, negrito e com fundo). */
    public void logErro(String mensagem) {
        adicionar("[!] " + mensagem, estiloErro);
    }

    private void adicionar(String mensagem, SimpleAttributeSet estilo) {
        StyledDocument doc = area.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), mensagem + "\n", estilo);
        } catch (BadLocationException e) {
            throw new IllegalStateException(e);
        }
        area.setCaretPosition(doc.getLength());
    }

    public void limpar() {
        area.setText("");
    }

    /** Barra de rolagem fina e escura (a do sistema é clara e destoa do tema). */
    private static final class BarraEscura extends BasicScrollBarUI {
        @Override
        protected void configureScrollBarColors() {
            thumbColor = Tema.SUPERFICIE_HOVER;
            trackColor = Tema.FUNDO;
        }

        @Override
        protected javax.swing.JButton createDecreaseButton(int orientation) {
            return botaoVazio();
        }

        @Override
        protected javax.swing.JButton createIncreaseButton(int orientation) {
            return botaoVazio();
        }

        private javax.swing.JButton botaoVazio() {
            javax.swing.JButton b = new javax.swing.JButton();
            b.setPreferredSize(new Dimension(0, 0));
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(trackColor);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty()) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 8, 8);
            g2.dispose();
        }
    }
}

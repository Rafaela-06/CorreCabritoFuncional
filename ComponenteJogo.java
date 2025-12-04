import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponenteJogo extends JPanel {
    private final Jogo jogo;
    private int deslocamentoX = 0;
    private int deslocamentoY = 0;

    // debug markers removed

    public ComponenteJogo(Jogo jogo) {
        this.jogo = jogo;
        setOpaque(false);
        setPreferredSize(new Dimension(800, 600));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX() - deslocamentoX;
                int mouseY = e.getY() - deslocamentoY;

                try {
                    Jogo.ResultadoClique resultado = jogo.identificadorClique(mouseX, mouseY);
                    if (resultado.mudou) {
                        repaint();
                    }

                    if (resultado.mensagem != null) {
                        Window janela = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                        JOptionPane.showMessageDialog(janela, resultado.mensagem);
                    }

                } catch (MovimentoInvalidoException ex) {
                    Window janela = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                    JOptionPane.showMessageDialog(janela, ex.getMessage(), "Movimento inválido",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // suaviza as bordas das linha e dos circulos para melhorar a qualidade
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // defina a espessura das linhas
        g2.setStroke(new BasicStroke(4));
        // armazenam as informações dos circulos e suas ligações
        Circulo[] circulos = jogo.getCirculos();
        int[][] ligacoes = jogo.getLigacoes();

        // calcular bounding box dos círculos para centralizar
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < circulos.length; i++) {
            // coordendada X do circulo
            int x = circulos[i].getPosicaoX();
            // coordenada Y do circulo
            int y = circulos[i].getPosicaoY();
            // diametro do circulo
            int diametro = circulos[i].getDiametro();
            // valor minimo do x e y
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            // valor maximo de x e y
            maxX = Math.max(maxX, x + diametro);
            maxY = Math.max(maxY, y + diametro);
        }
        int larguraTotal = maxX - minX;
        int alturaTotal = maxY - minY;
        deslocamentoX = (getWidth() - larguraTotal) / 2 - minX;
        deslocamentoY = (getHeight() - alturaTotal) / 2 - minY;

        // desenhar ligações
        g2.setColor(Color.BLACK);
        for (int[] l : ligacoes) {
            int x1 = circulos[l[0]].getPosicaoX() + circulos[l[0]].getDiametro() / 2 + deslocamentoX;
            int y1 = circulos[l[0]].getPosicaoY() + circulos[l[0]].getDiametro() / 2 + deslocamentoY;
            int x2 = circulos[l[1]].getPosicaoX() + circulos[l[1]].getDiametro() / 2 + deslocamentoX;
            int y2 = circulos[l[1]].getPosicaoY() + circulos[l[1]].getDiametro() / 2 + deslocamentoY;
            g2.drawLine(x1, y1, x2, y2);
        }

        // informações do jogo
        int carcaraPosicao = jogo.getCarcaraPos();
        int cabritoPosicao = jogo.getCabritoPos();

        Personagem selecionado = jogo.getSelecionado();

        // desenhar círculos com regras de cor
        for (int i = 0; i < circulos.length; i++) {
            int x = circulos[i].getPosicaoX() + deslocamentoX;
            int y = circulos[i].getPosicaoY() + deslocamentoY;
            int diametro = circulos[i].getDiametro();

            Color preenchimento = Color.WHITE;

            if (selecionado != null) {
                int posSelecionado = selecionado.getPosicao();
                boolean conectado = false;
                if (i == posSelecionado) {
                    preenchimento = Color.YELLOW;
                }
                // mostra os lugares onde o personagem pode ir
                if (posSelecionado >= 0) {
                    // verifica ligação circuloOrigem->i
                    for (int[] l : ligacoes) {
                        if ((l[0] == posSelecionado && l[1] == i) || (l[1] == posSelecionado && l[0] == i)) {
                            conectado = true;
                            break;
                        }
                    }
                    boolean circuloOcupado = (cabritoPosicao == i) || (carcaraPosicao == i);
                    if (conectado && !circuloOcupado)
                        preenchimento = Color.GREEN;
                    if (conectado && preenchimento != Color.YELLOW) {
                        // Se o Cabrito foi selecionado e o Carcará está na posição adjacente i
                        if (selecionado.getPosicao() == cabritoPosicao && i == carcaraPosicao) {
                            preenchimento = Color.RED;
                        }
                        // Se o Carcará foi selecionado e o Cabrito está na posição adjacente i
                        else if (selecionado.getPosicao() == carcaraPosicao && i == cabritoPosicao) {
                            preenchimento = Color.RED;
                        }
                    }
                }
            }

            // desenha preenchimento e contorno sempre (não apenas quando há seleção)
            g2.setColor(preenchimento);
            g2.fillOval(x, y, diametro, diametro);

            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, diametro, diametro);

            // desenhar personagens por cima do percurso
            Carcara car = jogo.getCarcara();
            // verifica se está aparecendo e se está numa posição válida
            if (car != null && carcaraPosicao >= 0) {
                int cx = circulos[carcaraPosicao].getPosicaoX() + circulos[carcaraPosicao].getDiametro() / 2
                        + deslocamentoX;
                int cy = circulos[carcaraPosicao].getPosicaoY() + circulos[carcaraPosicao].getDiametro() / 2
                        + deslocamentoY;
                car.desenhar((Graphics2D) g2, cx, cy, circulos[carcaraPosicao].getDiametro());
            }
            Cabrito cab = jogo.getCabrito();
            if (cab != null && cabritoPosicao >= 0) {
                int cx = circulos[cabritoPosicao].getPosicaoX() + circulos[cabritoPosicao].getDiametro() / 2
                        + deslocamentoX;
                int cy = circulos[cabritoPosicao].getPosicaoY() + circulos[cabritoPosicao].getDiametro() / 2
                        + deslocamentoY;
                cab.desenhar((Graphics2D) g2, cx, cy, circulos[cabritoPosicao].getDiametro());
            }
            // desenhar Cabrito Assado
            CabritoAssado cabAssado = jogo.getCabritoAssado();
            int cabAssadoPos = jogo.getCabritoAssadoPos();
            if (cabAssado != null && cabAssadoPos >= 0) {
                int cx = circulos[cabAssadoPos].getPosicaoX() + circulos[cabAssadoPos].getDiametro() / 2
                        + deslocamentoX;
                int cy = circulos[cabAssadoPos].getPosicaoY() + circulos[cabAssadoPos].getDiametro() / 2
                        + deslocamentoY;
                cabAssado.desenhar(g2, cx, cy, circulos[cabAssadoPos].getDiametro());
            }

            // desenhar contadores no topo central
            int larguraFundo = 360;
            int alturaFundo = 48;
            int xFundo = (getWidth() - larguraFundo) / 2;
            int yFundo = 8;
            g2.setColor(new Color(0, 0, 0, 160));
            // desenha e preenche um retângulo arredondado
            g2.fillRoundRect(xFundo, yFundo, larguraFundo, alturaFundo, 16, 16);
            // configuração e desenho do texto
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            int xTexto = xFundo + 12;
            g2.drawString("Movimentos Cabrito: " + jogo.getMovimentosCabrito(), xTexto, yFundo + 18);
            g2.drawString("Movimentos Carcará: " + jogo.getMovimentosCarcara(), xTexto, yFundo + 36);
        }
    }
}
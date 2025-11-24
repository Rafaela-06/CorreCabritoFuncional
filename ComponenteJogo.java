import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponenteJogo extends JPanel {
    // variável que armazena a lógica do jogo
    private final Jogo jogo;
    // deslocamentos no X e Y para centralizar
    private int deslocamentoX = 0;
    private int deslocamentoY = 0;

    public ComponenteJogo(Jogo jogo) {
        this.jogo = jogo;
        // deixa o fundo transparante
        setOpaque(false);
        setPreferredSize(new Dimension(800, 600));
        // adiciona o listener para o clique do mouse
        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX() - deslocamentoX;
                int mouseY = e.getY() - deslocamentoY;

                try {
                    Jogo.ResultadoClique resultado = jogo.identificadorClique(mouseX, mouseY);

                    if (resultado.mudou)
                        repaint();

                    if (resultado.mensagem != null) {
                        Window janela = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                        JOptionPane.showMessageDialog(janela, resultado.mensagem);
                    }

                } catch (MovimentoInvalidoException ex) {
                    Window janela = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                    JOptionPane.showMessageDialog(janela, ex.getMessage(),
                            "Movimento inválido", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // método que pinta o percurso do jogo
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // suaviza as bordas das linha e dos circulos para melhorar a qualidade
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // defina a espessura das linhas
        g2.setStroke(new BasicStroke(4));
        // armazenam as informações dos circulos e suas ligações
        int[][] circulos = jogo.getCirculos();
        int[][] ligacoes = jogo.getLigacoes();

        // calcular bounding box dos círculos para centralizar
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < circulos.length; i++) {
            // coordendada X do circulo
            int x = circulos[i][0];
            // coordenada Y do circulo
            int y = circulos[i][1];
            // diametro do circulo
            int diametro = circulos[i][2];
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
            int x1 = circulos[l[0]][0] + circulos[l[0]][2] / 2 + deslocamentoX;
            int y1 = circulos[l[0]][1] + circulos[l[0]][2] / 2 + deslocamentoY;
            int x2 = circulos[l[1]][0] + circulos[l[1]][2] / 2 + deslocamentoX;
            int y2 = circulos[l[1]][1] + circulos[l[1]][2] / 2 + deslocamentoY;
            g2.drawLine(x1, y1, x2, y2);
        }

        // informações do jogo
        int carcaraPosicao = jogo.getCarcaraPos();
        int cabritoPosicao = jogo.getCabritoPos();

        Personagem selecionado = jogo.getSelecionado();

        // desenhar círculos com regras de cor
        for (int i = 0; i < circulos.length; i++) {
            int x = circulos[i][0] + deslocamentoX;
            int y = circulos[i][1] + deslocamentoY;
            int size = circulos[i][2];

            Color preenchimento = Color.WHITE;

            if (selecionado != null) {

                int posSelecionado = selecionado.getPosicao();
                if (i == posSelecionado && cabritoPosicao != -1) {
                    preenchimento = Color.YELLOW;

                } else {
                    boolean conectado = false;
                    for (int[] l : ligacoes) {
                        if ((l[0] == posSelecionado && l[1] == i) || (l[1] == posSelecionado && l[0] == i)) {
                            conectado = true;
                            break;
                        }
                    }

                    if (conectado) {

                        boolean ocupado = (i == cabritoPosicao) || (i == carcaraPosicao);

                        if (ocupado) {
                            preenchimento = Color.RED;
                        } else {
                            preenchimento = Color.GREEN;
                        }

                    } else {
                        preenchimento = Color.WHITE;
                    }

                    if (cabritoPosicao == -1) {
                        preenchimento = Color.white;
                    }

                }
            }

            g2.setColor(preenchimento);
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, size, size);

            // desenhar personagens por cima do percurso
            Carcara car = jogo.getCarcara();
            // verifica se está aparecendo e se está numa posição válida
            if (car != null && carcaraPosicao >= 0) {
                int cx = circulos[carcaraPosicao][0] + circulos[carcaraPosicao][2] / 2 + deslocamentoX;
                int cy = circulos[carcaraPosicao][1] + circulos[carcaraPosicao][2] / 2 + deslocamentoY;
                car.desenhar((Graphics2D) g2, cx, cy, circulos[carcaraPosicao][2]);
            }
            Cabrito cab = jogo.getCabrito();
            if (cab != null && cabritoPosicao >= 0) {
                int cx = circulos[cabritoPosicao][0] + circulos[cabritoPosicao][2] / 2 + deslocamentoX;
                int cy = circulos[cabritoPosicao][1] + circulos[cabritoPosicao][2] / 2 + deslocamentoY;
                cab.desenhar((Graphics2D) g2, cx, cy, circulos[cabritoPosicao][2]);
            }
            // desenhar Cabrito Assado
            CabritoAssado cabAssado = jogo.getCabritoAssado();
            int cabAssadoPos = jogo.getCabritoAssadoPos();
            if (cabAssado != null && cabAssadoPos >= 0) {
                int cx = circulos[cabAssadoPos][0] + circulos[cabAssadoPos][2] / 2 + deslocamentoX;
                int cy = circulos[cabAssadoPos][1] + circulos[cabAssadoPos][2] / 2 + deslocamentoY;
                cabAssado.desenhar(g2, cx, cy, circulos[cabAssadoPos][2]);
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
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponenteJogo extends JPanel {
    private final Jogo jogo;
    private int deslocamentoX = 0;
    private int deslocamentoY = 0;
    private Image imagemFundo;

    // construtor
    public ComponenteJogo(Jogo jogo) {
        this.jogo = jogo;
        this.imagemFundo = new ImageIcon("windowsXP.jpg").getImage();
        // define a área do JPanel como transparente
        setOpaque(false);
        // define o tamanho da janela
        setPreferredSize(new Dimension(800, 600));
        // adiciona um listener para os cliques do mouse
        addMouseListener(new MouseAdapter() {
            // sobrescrita do método que possui o evento de clique do mouse
            @Override
            public void mouseClicked(MouseEvent e) {
                // pega as coordenadas do clique do mouse
                int mouseX = e.getX() - deslocamentoX;
                int mouseY = e.getY() - deslocamentoY;
                // tratamento de exceção para movimentos inválidos
                try {
                    // identifica o clique na tela e verifica se o resultado mudou, 
                    // por fim pinta novamente com as alterações visuais
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
        g2.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);

        // armazenam as informações dos circulos e suas ligações
        Circulo[] circulos = jogo.getCirculos();
        int[][] ligacoes = jogo.getLigacoes();

        // informações do jogo
        int carcaraPosicao = jogo.getCarcaraPos();
        int cabritoPosicao = jogo.getCabritoPos();
        Personagem selecionado = jogo.getSelecionado();
        Carcara car = jogo.getCarcara();
        Cabrito cab = jogo.getCabrito();
        CabritoAssado cabAssado = jogo.getCabritoAssado();
        int cabAssadoPos = jogo.getCabritoAssadoPos();

        // suaviza as bordas das linha e dos circulos para melhorar a qualidade
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // defina a espessura das linhas
        g2.setStroke(new BasicStroke(4));

        // calcular bounding box dos círculos para centralizar
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < circulos.length; i++) {
            // coordendada X do circulo
            int x = circulos[i].getPosicaoX();
            // coordenada Y do circulo
            int y = circulos[i].getPosicaoY();
            // diametro do circulo
            int diametro = Circulo.DIAMETRO;
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
            int x1 = circulos[l[0]].getPosicaoX() + Circulo.DIAMETRO / 2 + deslocamentoX;
            int y1 = circulos[l[0]].getPosicaoY() + Circulo.DIAMETRO / 2 + deslocamentoY;
            int x2 = circulos[l[1]].getPosicaoX() + Circulo.DIAMETRO / 2 + deslocamentoX;
            int y2 = circulos[l[1]].getPosicaoY() + Circulo.DIAMETRO / 2 + deslocamentoY;
            g2.drawLine(x1, y1, x2, y2);
        }

        // desenhar círculos com regras de cor
        for (int i = 0; i < circulos.length; i++) {
            int x = circulos[i].getPosicaoX() + deslocamentoX;
            int y = circulos[i].getPosicaoY() + deslocamentoY;
            int diametro = Circulo.DIAMETRO;
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

            // (desenho de personagens e contadores feito após o laço)
        }

        // --- desenhar personagens por cima do percurso (uma vez) ---
        if (car != null && carcaraPosicao >= 0) {
            int cx = circulos[carcaraPosicao].getPosicaoX() + Circulo.DIAMETRO / 2
                    + deslocamentoX;
            int cy = circulos[carcaraPosicao].getPosicaoY() + Circulo.DIAMETRO / 2
                    + deslocamentoY;
            car.desenhar((Graphics2D) g2, cx, cy, Circulo.DIAMETRO);
        }

        if (cab != null && cabritoPosicao >= 0) {
            int cx = circulos[cabritoPosicao].getPosicaoX() + Circulo.DIAMETRO / 2
                    + deslocamentoX;
            int cy = circulos[cabritoPosicao].getPosicaoY() + Circulo.DIAMETRO / 2
                    + deslocamentoY;
            cab.desenhar((Graphics2D) g2, cx, cy, Circulo.DIAMETRO);
        }

        if (cabAssado != null && cabAssadoPos >= 0) {
            int cx = circulos[cabAssadoPos].getPosicaoX() + Circulo.DIAMETRO / 2
                    + deslocamentoX;
            int cy = circulos[cabAssadoPos].getPosicaoY() + Circulo.DIAMETRO / 2
                    + deslocamentoY;
            cabAssado.desenhar(g2, cx, cy, Circulo.DIAMETRO);
        }

        // desenhar contadores no topo central (fora do laço)
        int larguraFundo = 360;
        int alturaFundo = 48;
        int xFundo = (getWidth() - larguraFundo) / 2;
        int yFundo = 8;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(xFundo, yFundo, larguraFundo, alturaFundo, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        int xTexto = xFundo + 12;
        g2.drawString("Movimentos Cabrito: " + jogo.getMovimentosCabrito(), xTexto, yFundo + 18);
        g2.drawString("Movimentos Carcará: " + jogo.getMovimentosCarcara(), xTexto, yFundo + 36);

        // desenhar legenda de cores no canto inferior direito
        int legendaLarg = 260;
        int legendaAlt = 110;
        int pad = 12;
        int xLegenda = getWidth() - legendaLarg - pad;
        int yLegenda = getHeight() - legendaAlt - pad;

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(xLegenda, yLegenda, legendaLarg, legendaAlt, 12, 12);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int sw = 14; // square width for color sample
        int gapY = 22;
        int textoX = xLegenda + sw + 18;
        int linhaY = yLegenda + 20;

        // Branco: posição livre
        g2.setColor(Color.WHITE);
        g2.fillRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.WHITE);
        g2.drawString("Branco — Posição Livre", textoX, linhaY);

        // Amarelo: selecionado
        linhaY += gapY;
        g2.setColor(Color.YELLOW);
        g2.fillRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.WHITE);
        g2.drawString("Amarelo — Personagem Selecionado", textoX, linhaY);

        // Verde: movimento disponível
        linhaY += gapY;
        g2.setColor(Color.GREEN);
        g2.fillRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.WHITE);
        g2.drawString("Verde — Movimento Disponível", textoX, linhaY);

        // Vermelho: posição de confronto / captura
        linhaY += gapY;
        g2.setColor(Color.RED);
        g2.fillRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - sw + 4, sw, sw);
        g2.setColor(Color.WHITE);
        g2.drawString("Vermelho — Risco de Morrer", textoX, linhaY);

        // legenda do superpulo
        int padRight = 12;
        int larguraSuper = 180;
        int alturaSuper = 32;
        int xSuper = getWidth() - larguraSuper - padRight;
        int ySuper = 12; 

        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(xSuper, ySuper, larguraSuper, alturaSuper, 10, 10);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        boolean superPuloDisponivel2 = false;
        if (jogo.getCabrito() != null) {
            superPuloDisponivel2 = jogo.getCabrito().podeSuperPulo();
        }
        String textoSuper2 = superPuloDisponivel2 ? "Super pulo: Disponível" : "Super pulo: Indisponível";
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(textoSuper2);
        int textX = xSuper + 12;
        int textY = ySuper + (alturaSuper + fm.getAscent()) / 2 - 4;
        g2.drawString(textoSuper2, textX, textY);
    }
}
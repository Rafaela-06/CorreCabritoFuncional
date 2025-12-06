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
                    // verifica se existe alguma mensagem para exibir na tela
                    if (resultado.mensagem != null) {
                        Window janela = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                        JOptionPane.showMessageDialog(janela, resultado.mensagem);
                    }

                } catch (MovimentoInvalidoException ex) {
                    // se lançar a exceção MovimentoInvalidoException aparece uma janela de erro avisando
                    Window janela = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                    JOptionPane.showMessageDialog(janela, ex.getMessage(), "Movimento inválido",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    // método responsável por desenhar o todo o mapa do jogo e seus componentes
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // desenha a imagem de fundo
        g2.drawImage(imagemFundo, 0, 0, getWidth(), getHeight(), this);

        // armazenam as informações dos circulos e suas ligações
        Circulo[] circulos = jogo.getCirculos();
        int[][] ligacoes = jogo.getLigacoes();

        // recebe as posições atuais do Carcará e do Cabrito e
        // qual personagem está selecionado
        int carcaraPosicao = jogo.getCarcaraPos();
        int cabritoPosicao = jogo.getCabritoPos();
        Personagem selecionado = jogo.getSelecionado();
        Carcara car = jogo.getCarcara();
        Cabrito cab = jogo.getCabrito();
        CabritoAssado cabAssado = jogo.getCabritoAssado();
        int cabAssadoPos = jogo.getCabritoAssadoPos();

        // suaviza as bordas das linha e dos circulos para melhorar a qualidade
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // define a espessura das linhas
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
        // calcula o valor de deslocamento para centralizar a área do jogo
        deslocamentoX = (getWidth() - larguraTotal) / 2 - minX;
        deslocamentoY = (getHeight() - alturaTotal) / 2 - minY;

        // desenha as ligações entre os círculos
        g2.setColor(Color.BLACK);
        for (int[] l : ligacoes) {
            int x1 = circulos[l[0]].getPosicaoX() + Circulo.DIAMETRO / 2 + deslocamentoX;
            int y1 = circulos[l[0]].getPosicaoY() + Circulo.DIAMETRO / 2 + deslocamentoY;
            int x2 = circulos[l[1]].getPosicaoX() + Circulo.DIAMETRO / 2 + deslocamentoX;
            int y2 = circulos[l[1]].getPosicaoY() + Circulo.DIAMETRO / 2 + deslocamentoY;
            g2.drawLine(x1, y1, x2, y2);
        }

        // desenha os círculos com as regras de cada cor
        for (int i = 0; i < circulos.length; i++) {
            // cálculo para garantir que os circulos sejam desenhados no local correto
            int x = circulos[i].getPosicaoX() + deslocamentoX;
            int y = circulos[i].getPosicaoY() + deslocamentoY;

            // definição do diametro e da cor de preenchimento de todos os círculos
            int diametro = Circulo.DIAMETRO;
            Color preenchimento = Color.WHITE;

            // verifica se o personagem está selecionado e guarda sua posição
            if (selecionado != null) {
                int posSelecionado = selecionado.getPosicao();
                boolean conectado = false;
                // define a cor de preenchimento para amarelo onde o personagem selecionado está
                if (i == posSelecionado) {
                    preenchimento = Color.YELLOW;
                }
                // mostra os lugares onde o personagem pode ir
                if (posSelecionado >= 0) {
                    // verifica a ligação entre os círculos
                    for (int[] l : ligacoes) {
                        if ((l[0] == posSelecionado && l[1] == i) || (l[1] == posSelecionado && l[0] == i)) {
                            conectado = true;
                            break;
                        }
                    }
                    // verifica se o círculo está ocupado por um dos personagens
                    boolean circuloOcupado = (cabritoPosicao == i) || (carcaraPosicao == i);
                    // verifica se o circulo está conectado, se não está ocupado e preenche de verde 
                    if (conectado && !circuloOcupado)
                        preenchimento = Color.GREEN;
                    if (conectado && preenchimento != Color.YELLOW) {
                        // Se o Cabrito foi selecionado e o 
                        // Carcará está na posição adjacente preenche de vermelho
                        if (selecionado.getPosicao() == cabritoPosicao && i == carcaraPosicao) {
                            preenchimento = Color.RED;
                        }
                        // Se o Carcará foi selecionado e o 
                        // Cabrito está na posição adjacente preenche de vermelho
                        else if (selecionado.getPosicao() == carcaraPosicao && i == cabritoPosicao) {
                            preenchimento = Color.RED;
                        }
                    }
                }
            }

            // desenha o preenchimento e contorno
            g2.setColor(preenchimento);
            g2.fillOval(x, y, diametro, diametro);
            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, diametro, diametro);
        }

        // desenha os personagens por cima do mapa
        // verifica se o personagem foi instanciado e se está em uma posição válida
        if (car != null && carcaraPosicao >= 0) {
            // calcula o centro do círculo para desenhar o personagem centralizado
            int centroX = circulos[carcaraPosicao].getPosicaoX() + Circulo.DIAMETRO / 2
                    + deslocamentoX;
            int centroY = circulos[carcaraPosicao].getPosicaoY() + Circulo.DIAMETRO / 2
                    + deslocamentoY;
            car.desenhar((Graphics2D) g2, centroX, centroY, Circulo.DIAMETRO);
        }
        // verifica se o personagem foi instanciado e se está em uma posição válida
        if (cab != null && cabritoPosicao >= 0) {
            // calcula o centro do círculo para desenhar o personagem centralizado
            int centroX = circulos[cabritoPosicao].getPosicaoX() + Circulo.DIAMETRO / 2
                    + deslocamentoX;
            int centroY = circulos[cabritoPosicao].getPosicaoY() + Circulo.DIAMETRO / 2
                    + deslocamentoY;
            cab.desenhar((Graphics2D) g2, centroX, centroY, Circulo.DIAMETRO);
        }
        // verifica se o personagem foi instanciado e se está em uma posição válida
        if (cabAssado != null && cabAssadoPos >= 0) {
            // calcula o centro do círculo para desenhar o personagem centralizado
            int centroX = circulos[cabAssadoPos].getPosicaoX() + Circulo.DIAMETRO / 2
                    + deslocamentoX;
            int centroY = circulos[cabAssadoPos].getPosicaoY() + Circulo.DIAMETRO / 2
                    + deslocamentoY;
            cabAssado.desenhar(g2, centroX, centroY, Circulo.DIAMETRO);
        }

        // desenha os contadores no centro superior
        int larguraFundo = 360;
        int alturaFundo = 48;
        int xFundo = (getWidth() - larguraFundo) / 2;
        int yFundo = 8;
        // desenha o fundo do contador
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(xFundo, yFundo, larguraFundo, alturaFundo, 16, 16);
        // desenha as letras do contador
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        int xTexto = xFundo + 12;
        g2.drawString("Movimentos Cabrito: " + jogo.getMovimentosCabrito(), xTexto, yFundo + 18);
        g2.drawString("Movimentos Carcará: " + jogo.getMovimentosCarcara(), xTexto, yFundo + 36);

        // desenha a legenda das cores no canto inferior direito
        int legendaLarg = 260;
        int legendaAlt = 110;
        int margemBorda = 10;
        // calulam a posição das legendas
        int xLegenda = getWidth() - legendaLarg - margemBorda;
        int yLegenda = getHeight() - legendaAlt - margemBorda;

        // desenha o fundo da lagenda
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(xLegenda, yLegenda, legendaLarg, legendaAlt, 12, 12);

        // desenha as letras no lugar desejado
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        int tamanhoQuadradoCor = 14; 
        int espacamentoY = 20;
        int textoX = xLegenda + tamanhoQuadradoCor + 15;
        int linhaY = yLegenda + 20;


        // desenha o quadrado onde fica a cor de exemplo -> amarelo
        linhaY += espacamentoY;
        g2.setColor(Color.YELLOW);
        g2.fillRect(xLegenda + 10, linhaY - tamanhoQuadradoCor + 4, tamanhoQuadradoCor, tamanhoQuadradoCor);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - tamanhoQuadradoCor + 4, tamanhoQuadradoCor, tamanhoQuadradoCor);
        g2.setColor(Color.WHITE);
        g2.drawString("Amarelo — Personagem Selecionado", textoX, linhaY);

        // desenha o quadrado onde fica a cor de exemplo -> verde
        linhaY += espacamentoY;
        g2.setColor(Color.GREEN);
        g2.fillRect(xLegenda + 10, linhaY - tamanhoQuadradoCor + 4, tamanhoQuadradoCor, tamanhoQuadradoCor);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - tamanhoQuadradoCor + 4, tamanhoQuadradoCor, tamanhoQuadradoCor);
        g2.setColor(Color.WHITE);
        g2.drawString("Verde — Movimento Disponível", textoX, linhaY);

        // desenha o quadrado onde fica a cor de exemplo -> vermelho
        linhaY += espacamentoY;
        g2.setColor(Color.RED);
        g2.fillRect(xLegenda + 10, linhaY - tamanhoQuadradoCor + 4, tamanhoQuadradoCor, tamanhoQuadradoCor);
        g2.setColor(Color.BLACK);
        g2.drawRect(xLegenda + 10, linhaY - tamanhoQuadradoCor + 4, tamanhoQuadradoCor, tamanhoQuadradoCor);
        g2.setColor(Color.WHITE);
        g2.drawString("Vermelho — Risco de Morrer", textoX, linhaY);

        // cálculo da legenda do superpulo
        int margemDireita = 12;
        int larguraSuper = 180;
        int alturaSuper = 32;
        int xSuper = getWidth() - larguraSuper - margemDireita;
        int ySuper = 12; 
        // desenha o fundo da legenda
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRoundRect(xSuper, ySuper, larguraSuper, alturaSuper, 10, 10);
        // desenha as letras da legenda
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        boolean superPuloDisponivel = false;
        // verifica se o Cabrito existe no jogo e 
        // chama o método para verificar se o super pulo pode ser utilizado
        if (jogo.getCabrito() != null) {
            superPuloDisponivel = jogo.getCabrito().verificarSuperPulo();
        }
        // verifica se super pulo está disponível para definir a legenda
        String textoSuper = superPuloDisponivel ? "Super pulo: Disponível" : "Super pulo: Indisponível";
        FontMetrics metricasFonte = g2.getFontMetrics();
        int textWidth = metricasFonte.stringWidth(textoSuper);
        int textX = xSuper + 12;
        // garante que a legenda esteja desenhada no centro do fundo dela
        int textY = ySuper + (alturaSuper + metricasFonte.getAscent()) / 2 - 4;
        g2.drawString(textoSuper, textX, textY);
    }
}
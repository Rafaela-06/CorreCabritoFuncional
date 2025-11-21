import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ComponenteJogo extends JPanel{

    // Coordenadas dos círculos (x, y, tamanho)
    private int[][] circulos = {
            {200, 40, 70},   // topo
            {80, 140, 70},   // esquerda alta
            {320, 140, 70},  // direita alta
            {140, 250, 70},  // esquerda baixa
            {260, 250, 70},  // direita baixa
            {200, 160, 70}   // centro
    };

    // Ligações (índices dos circulos)
    private int[][] ligacoes = {
            {0, 1}, {0, 2}, {0, 5},
            {1, 3}, 
            {2, 4}, 
            {3, 4}, {3, 5}, {4, 5}
    };

    private Carcara carcara;
    private Cabrito cabrito;
    private Personagem selecionado = null;

    public ComponenteJogo(){
        setOpaque(false); // permite ver o fundo do JFrame
        setPreferredSize(new Dimension(500, 400));

        // listener para cliques: detecta clique em círculo ou personagem
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // descobrir em qual círculo (se houver) o clique ocorreu
                int clickedIndex = -1;
                for (int i = 0; i < circulos.length; i++) {
                    int cx = circulos[i][0] + circulos[i][2] / 2;
                    int cy = circulos[i][1] + circulos[i][2] / 2;
                    int r = circulos[i][2] / 2;
                    int dx = x - cx;
                    int dy = y - cy;
                    if (dx*dx + dy*dy <= r*r) {
                        clickedIndex = i;
                        break;
                    }
                }

                if (clickedIndex == -1) return; // clique fora de qualquer círculo

                // se clicou em cima do cabrito
                if (cabrito != null && cabrito.getPosicao() == clickedIndex) {
                    if (selecionado == null) {
                        selecionado = cabrito;
                        JOptionPane.showMessageDialog(ComponenteJogo.this, "Cabrito selecionado");
                    } else if (selecionado == cabrito) {
                        selecionado = null;
                        JOptionPane.showMessageDialog(ComponenteJogo.this, "Cabrito desmarcado");
                    } else {
                        // mover selecionado para a posição do cabrito (troca)
                        selecionado.setPosicao(clickedIndex);
                        selecionado = null;
                        repaint();
                    }
                    return;
                }

                // se clicou em cima do carcara
                if (carcara != null && carcara.getPosicao() == clickedIndex) {
                    if (selecionado == null) {
                        selecionado = carcara;
                        JOptionPane.showMessageDialog(ComponenteJogo.this, "Carcará selecionado");
                    } else if (selecionado == carcara) {
                        selecionado = null;
                        JOptionPane.showMessageDialog(ComponenteJogo.this, "Carcará desmarcado");
                    } else {
                        selecionado.setPosicao(clickedIndex);
                        selecionado = null;
                        repaint();
                    }
                    return;
                }

                // clique em círculo vazio
                if (selecionado != null) {
                    selecionado.setPosicao(clickedIndex);
                    selecionado = null;
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(ComponenteJogo.this, "Clicou no círculo " + clickedIndex);
                }
            }
        });
    }

    public int getPosicoes(){
        return circulos.length;
    }

    public int[] getCirculos(int indice){
        if(indice < 0 || indice >= circulos.length) return null;
        return circulos[indice];
    }

    public void setCarcara(Carcara c) {

        this.carcara = c; // Armazena a referência do objeto Carcara

        // Verifica se o objeto carcara que esta sendo atribuido não é nulo e Verifica
        // se o carcará ainda não tem uma posição
        if (c != null && c.getPosicao() == -1) {
            int pos;

            // Cria uma nova instância da classe Random para gerar números aleatórios.
            java.util.Random random = new java.util.Random();
            // Um loop que garante que o carcara não vai escolher a mesma posição do cabrito
            do {
                pos = random.nextInt(getPosicoes());
            } while (cabrito != null && cabrito.getPosicao() == pos);
            c.setPosicao(pos);
        }
        // Método que solicita que o carcara seja redesenhado
        repaint();
    }

    public void setCabrito(Cabrito c) {
        this.cabrito = c; // Armazena a referência do objeto Cabrito

        // Verifica se o objeto cabrito que esta sendo atribuido não é nulo e Verifica
        // se o cabrito ainda não tem uma posição
        if (c != null && c.getPosicao() == -1) {
            int pos;
            java.util.Random random = new java.util.Random();
            // Um loop que garante que o cabrito não vai escolher a mesma posição do carcara
            do {
                pos = random.nextInt(getPosicoes());
            } while (carcara != null && carcara.getPosicao() == pos);
            c.setPosicao(pos);
        }
        // Método que solicita que o cabrito seja redesenhado
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Espessura das linhas
        g2.setStroke(new BasicStroke(4));

        // Desenhar linhas (atrás dos círculos)
        g2.setColor(Color.BLACK);
        for (int[] l : ligacoes) {
            // cálculos para encontrar os pontos centrais dos círculos para desenhar as ligações
            int x1 = circulos[l[0]][0] + circulos[l[0]][2] / 2;
            int y1 = circulos[l[0]][1] + circulos[l[0]][2] / 2;
            int x2 = circulos[l[1]][0] + circulos[l[1]][2] / 2;
            int y2 = circulos[l[1]][1] + circulos[l[1]][2] / 2;

            g2.drawLine(x1, y1, x2, y2);
        }

        // Preencher círculos com branco e desenhar contornos pretos
        for (int[] c : circulos) {
            int x = c[0];
            int y = c[1];
            int size = c[2];

            g2.setColor(Color.WHITE);
            g2.fillOval(x, y, size, size); //preenche o interior dos circulos

            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, size, size); //desenha o contorno dos circulos
        }

        // Desenhar personagens (por cima dos círculos)
        if(carcara != null){
            int posicaoAtual = carcara.getPosicao();
            if(posicaoAtual >= 0 && posicaoAtual < circulos.length){ //garante que a posição não é negativa e que está dentro do array dos circulos
                int cx = circulos[posicaoAtual][0] + circulos[posicaoAtual][2] / 2;
                int cy = circulos[posicaoAtual][1] + circulos[posicaoAtual][2] / 2;
                carcara.desenhar(g2, cx, cy, circulos[posicaoAtual][2]);
            }
        }

        if(cabrito != null){
            int posicaoAtual = cabrito.getPosicao();
            if(posicaoAtual >= 0 && posicaoAtual < circulos.length){
                int cx = circulos[posicaoAtual][0] + circulos[posicaoAtual][2] / 2;
                int cy = circulos[posicaoAtual][1] + circulos[posicaoAtual][2] / 2;
                cabrito.desenhar(g2, cx, cy, circulos[posicaoAtual][2]);
            }
        }
    }
}

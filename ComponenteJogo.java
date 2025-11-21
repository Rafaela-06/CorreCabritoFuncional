import javax.swing.*;

import java.awt.*;

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

    public ComponenteJogo(){
        setOpaque(false); // permite ver o fundo do JFrame
        setPreferredSize(new Dimension(500, 400));
    }

    public int getPosicoes(){
        return circulos.length;
    }

    public int[] getCirculos(int indice){
        if(indice < 0 || indice >= circulos.length) return null;
        return circulos[indice];
    }

    public void setCarcara(Carcara c){
        this.carcara = c;
        if(c != null && c.getPosicao() == -1){
            c.posicaoAleatoria(getPosicoes());
        }
        repaint();
    }

    public void setCabrito(Cabrito c){
        this.cabrito = c;
        if(c != null && c.getPosicao() == -1){
            c.posicaoAleatoria(getPosicoes());
        }
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

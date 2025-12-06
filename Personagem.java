import java.awt.*;
import javax.swing.*;

public abstract class Personagem {

    protected Image image;

    // Começa em -1 porque nenhuma posição inicial foi definida.
    protected int posicao = -1;

    public Personagem(String caminhoImagem) {
        if (caminhoImagem != null) { // Só tenta carregar se o caminho não for nulo
            ImageIcon ic = new ImageIcon(caminhoImagem);
            image = ic.getImage(); // Extrai a imagem do ícone
        }
    }

    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int pos) {
        this.posicao = pos;
    }

    // Desenha o personagem na tela
    // g2 = objeto gráfico
    // centroX, centroY = coordenadas onde o personagem deve ser desenhado
    // tamanho = tamanho do espaço onde a imagem deve caber
    public void desenhar(Graphics2D g2, int centroX, int centroY, int tamanho) {

        if (image != null) { // Caso a imagem tenha sido carregada corretamente

            // Define o tamanho da imagem como 80% do tamanho da célula
            int imgL = (int) (tamanho * 0.8); // largura
            int imgA = (int) (tamanho * 0.8); // altura

            // Calcula a posição para centralizar a imagem
            int x = centroX - imgL / 2;
            int y = centroY - imgA / 2;

            // Desenha a imagem redimensionada
            g2.drawImage(image, x, y, imgL, imgA, null);

        } else {
            // Caso não tenha imagem, desenha um círculo amarelo como fallback
            int r = tamanho / 2; // raio do círculo

            g2.setColor(new Color(255, 255, 0));
            g2.fillOval(centroX - r / 2, centroY - r / 2, r, r);

            g2.setColor(Color.BLACK);
            g2.drawOval(centroX - r / 2, centroY - r / 2, r, r);
        }
    }
}

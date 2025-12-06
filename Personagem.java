import java.awt.*;
import javax.swing.*;

public abstract class Personagem {

    protected Image imagem;

    // Começa em -1 porque nenhuma posição inicial foi definida.
    protected int posicao = -1;

    public Personagem(String caminhoImagem) {
        if (caminhoImagem != null) { // Só tenta carregar se o caminho não for nulo
            ImageIcon icone = new ImageIcon(caminhoImagem);
            imagem = icone.getImage(); // Extrai a imagem do ícone
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

        if (imagem != null) { // Caso a imagem tenha sido carregada corretamente

            // Define o tamanho da imagem como 80% do tamanho da célula
            int imagemLargura = (int) (tamanho * 0.8); // largura
            int imagemAltura = (int) (tamanho * 0.8); // altura

            // Calcula a posição para centralizar a imagem
            int x = centroX - imagemLargura / 2;
            int y = centroY - imagemAltura / 2;

            // Desenha a imagem redimensionada
            g2.drawImage(imagem, x, y, imagemLargura, imagemAltura, null);

        } else {
            // Caso não tenha imagem, desenha um círculo amarelo como fallback
            int raio = tamanho / 2; // raio do círculo

            g2.setColor(new Color(255, 255, 0));
            g2.fillOval(centroX - raio / 2, centroY - raio / 2, raio, raio);

            g2.setColor(Color.BLACK);
            g2.drawOval(centroX - raio / 2, centroY - raio / 2, raio, raio);
        }
    }
}

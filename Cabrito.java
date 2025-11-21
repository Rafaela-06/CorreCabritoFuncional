import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Cabrito {
    private Image image;
    private int posicao = -1; //começa com -1 porque não possui uma posição definida

    public Cabrito(String imagePath){
        if(imagePath != null){
            ImageIcon ic = new ImageIcon(imagePath);
            image = ic.getImage();
        }
    }

    public void posicaoAleatoria(int numPosicoes){
        if(numPosicoes <= 0) return;
        Random r = new Random();
        posicao = r.nextInt(numPosicoes);
    }

    public int getPosicao(){
        return posicao;
    }

    public void desenhar(Graphics2D g2, int centroX, int centroY, int tamanho){
        if(image != null){
            int imgL = (int) (tamanho * 0.8); //determina a largura que deve ser a imagem
            int imgA = (int) (tamanho * 0.8); //determina a altura que deve ser a imagem
            int x = centroX - imgL/2;
            int y = centroY - imgA/2;
            g2.drawImage(image, x, y, imgL, imgA, null);
        } else {
            // fallback: desenha um círculo laranja pequeno
            int r = tamanho/2;
            g2.setColor(new Color(255,165,0)); // laranja
            g2.fillOval(centroX - r/2, centroY - r/2, r, r);
            g2.setColor(Color.BLACK);
            g2.drawOval(centroX - r/2, centroY - r/2, r, r);
        }
    }
}


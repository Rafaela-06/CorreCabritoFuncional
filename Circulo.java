public class Circulo {
    private int posicaoX;
    private int posicaoY;
    // define o diâmetro de todos os círculos
    public static final int DIAMETRO = 70;
    // construtor para definir a posição de cada círculo
    public Circulo(int posicaoX, int posicaoY){
        this.posicaoX=posicaoX;
        this.posicaoY=posicaoY;
    }
    public int getPosicaoX(){
        return posicaoX;
    }
    public int getPosicaoY(){
        return posicaoY;
    }
}

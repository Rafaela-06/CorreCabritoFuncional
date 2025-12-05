public class Circulo {
    private int posicaoX;
    private int posicaoY;
    public static final int DIAMETRO = 70;
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

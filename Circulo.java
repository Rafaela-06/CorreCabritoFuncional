public class Circulo {
    private int posicaoX;
    private int posicaoY;
    private static int diametro = 70;
    public Circulo(int posicaoX, int posicaoY){
        this.posicaoX=posicaoX;
        this.posicaoY=posicaoY;
    }
    public int getDiametro(){
        return diametro;
    }
    public int getPosicaoX(){
        return posicaoX;
    }
    public int getPosicaoY(){
        return posicaoY;
    }
}

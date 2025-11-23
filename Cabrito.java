import javax.swing.*;

public class Cabrito extends Personagem{
    public Cabrito(String caminhoImagem){
      super(caminhoImagem);
    }
  
    private boolean superPuloDisponivel = true;

    public boolean podeSuperPulo() {
        return superPuloDisponivel;
    }

    public void superPulo(int destino) {
        setPosicao(destino);
        superPuloDisponivel = false;
    }

    public void reset() {
        superPuloDisponivel = true;
  
    }

}


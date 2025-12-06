public class Cabrito extends Personagem {

    public Cabrito(String caminhoImagem) {
        super(caminhoImagem);
    }

    private boolean superPuloDisponivel = true;

    public boolean verificarSuperPulo() {
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

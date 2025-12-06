public class Cabrito extends Personagem {

    public Cabrito(String caminhoImagem) {
        super(caminhoImagem);
    }

    // Indica se o super pulo ainda pode ser usado
    private boolean superPuloDisponivel = true;

    // Retorna se o super pulo está disponível
    public boolean verificarSuperPulo() {
        return superPuloDisponivel;
    }

    // Executa o super pulo, movendo o personagem e desativando o recurso
    public void superPulo(int destino) {
        setPosicao(destino); // Move o personagem para a posição destino
        superPuloDisponivel = false; // Depois de usar, o super pulo não está mais disponível
    }

    // Reseta o super pulo, permitindo usar novamente
    public void redefinirSuperPulo() {
        superPuloDisponivel = true;
    }
}

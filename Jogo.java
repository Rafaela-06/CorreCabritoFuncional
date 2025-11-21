import javax.swing.*;
import java.util.Random;

public class Jogo {
    // coordenadas e ligações (definidas aqui para uso lógico)
    private int[][] circulos = {
            {200, 40, 70},
            {80, 140, 70},
            {320, 140, 70},
            {140, 250, 70},
            {260, 250, 70},
            {200, 160, 70}
    };

    private int[][] ligacoes = {
            {0, 1}, {0, 2}, {0, 5},
            {1, 3},
            {2, 4},
            {3, 4}, {3, 5}, {4, 5}
    };

    private Carcara carcara;
    private Cabrito cabrito;
    private Personagem selecionado = null;
    private String turno = "cabrito"; // sempre começa com cabrito

    private int movimentosCabrito = 0;
    private int movimentosCarcara = 0;

    public static class ClickResult {
        public final boolean changed;
        public final String message;
        public ClickResult(boolean changed, String message){ this.changed = changed; this.message = message; }
    }

    public int[][] getCirculos(){ return circulos; }
    public int[][] getLigacoes(){ return ligacoes; }

    public int getCarcaraPos(){ return carcara != null ? carcara.getPosicao() : -1; }
    public int getCabritoPos(){ return cabrito != null ? cabrito.getPosicao() : -1; }
    public Carcara getCarcara(){ return carcara; }
    public Cabrito getCabrito(){ return cabrito; }
    public Personagem getSelecionado(){ return selecionado; }
    public String getTurno(){ return turno; }
    public int getMovimentosCabrito(){ return movimentosCabrito; }
    public int getMovimentosCarcara(){ return movimentosCarcara; }

    public void setCarcara(Carcara c){
        this.carcara = c;
        if(c != null && c.getPosicao() == -1){
            // posiciona aleatoriamente sem colidir com cabrito
            int pos;
            Random r = new Random();
            do { pos = r.nextInt(circulos.length); }
            while (cabrito != null && cabrito.getPosicao() == pos);
            c.setPosicao(pos);
        }
    }

    public void setCabrito(Cabrito c){
        this.cabrito = c;
        if(c != null && c.getPosicao() == -1){
            int pos;
            Random r = new Random();
            do { pos = r.nextInt(circulos.length); }
            while (carcara != null && carcara.getPosicao() == pos);
            c.setPosicao(pos);
        }
    }

    private boolean isLigado(int a, int b){
        for (int[] l : ligacoes){ if ((l[0]==a && l[1]==b) || (l[0]==b && l[1]==a)) return true; }
        return false;
    }

    // Processa clique nas coordenadas do componente; devolve resultado com mensagem e se houve mudança
    public ClickResult handleClick(int x, int y){
        // determina índice de círculo clicado
        int clicked = -1;
        for (int i = 0; i < circulos.length; i++){
            int cx = circulos[i][0] + circulos[i][2]/2;
            int cy = circulos[i][1] + circulos[i][2]/2;
            int r = circulos[i][2]/2;
            int dx = x - cx, dy = y - cy;
            if (dx*dx + dy*dy <= r*r){ clicked = i; break; }
        }
        if (clicked == -1) return new ClickResult(false, null);

        // clique no cabrito
        if (cabrito != null && cabrito.getPosicao() == clicked){
            if (!turno.equals("cabrito")) return new ClickResult(false, "Não é a vez do cabrito.");
            if (selecionado == null) { selecionado = cabrito; return new ClickResult(true, "Cabrito selecionado"); }
            if (selecionado == cabrito) { selecionado = null; return new ClickResult(true, "Cabrito desmarcado"); }
            return new ClickResult(false, null);
        }

        // clique no carcará
        if (carcara != null && carcara.getPosicao() == clicked){
            if (!turno.equals("carcara")) return new ClickResult(false, "Não é a vez do carcará.");
            if (selecionado == null) { selecionado = carcara; return new ClickResult(true, "Carcará selecionado"); }
            if (selecionado == carcara) { selecionado = null; return new ClickResult(true, "Carcará desmarcado"); }
            return new ClickResult(false, null);
        }

        // clique em círculo vazio -> tenta mover selecionado
        if (selecionado != null){
            int origem = selecionado.getPosicao();
            if (origem == clicked){ selecionado = null; return new ClickResult(true, null); }
            // não permite mover para círculo ocupado
            if ((cabrito != null && cabrito.getPosicao() == clicked) || (carcara != null && carcara.getPosicao() == clicked)){
                return new ClickResult(false, "Círculo ocupado.");
            }
            if (!isLigado(origem, clicked)) return new ClickResult(false, "Movimento inválido: não há ligação entre os círculos.");

            // executar movimento
            if (selecionado == cabrito) { movimentosCabrito++; }
            else if (selecionado == carcara) { movimentosCarcara++; }

            selecionado.setPosicao(clicked);
            // alterna turno
            if (selecionado == cabrito) turno = "carcara"; else turno = "cabrito";
            selecionado = null;
            return new ClickResult(true, "Movimento realizado. Agora é a vez do " + turno + ".");
        }

        return new ClickResult(false, "Selecione um dos personagens!");
    }

    public void resetGame(){
        movimentosCabrito = 0;
        movimentosCarcara = 0;
        turno = "cabrito";
        selecionado = null;
        if (carcara != null) carcara.setPosicao(-1);
        if (cabrito != null) cabrito.setPosicao(-1);
    }
}

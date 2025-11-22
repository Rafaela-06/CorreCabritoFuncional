import java.util.Random;

public class Jogo {
    // coordenadas e ligações (definidas aqui para uso lógico)
    private int[][] circulos = {
            { 200, 40, 70 },
            { 80, 140, 70 },
            { 320, 140, 70 },
            { 140, 250, 70 },
            { 260, 250, 70 },
            { 200, 160, 70 }
    };

    private int[][] ligacoes = {
            { 0, 1 }, { 0, 2 }, { 0, 5 },
            { 1, 3 },
            { 2, 4 },
            { 3, 4 }, { 3, 5 }, { 4, 5 }
    };

    private Carcara carcara;
    private Cabrito cabrito;
    private Personagem selecionado = null;
    private String turno = "cabrito"; // sempre começa com cabrito

    private int movimentosCabrito = 0;
    private int movimentosCarcara = 0;

    public static class ResultadoClique {
        public final boolean mudou;
        public final String mensagem;

        public ResultadoClique(boolean mudou, String mensagem) {
            this.mudou = mudou;
            this.mensagem = mensagem;
        }
    }

    public int[][] getCirculos() {
        return circulos;
    }

    public int[][] getLigacoes() {
        return ligacoes;
    }

    // tem que verificar esses de posição pra ver se não estão redundantes
    public int getCarcaraPos() {
        return carcara != null ? carcara.getPosicao() : -1;
    }

    public int getCabritoPos() {
        return cabrito != null ? cabrito.getPosicao() : -1;
    }

    public Carcara getCarcara() {
        return carcara;
    }

    public Cabrito getCabrito() {
        return cabrito;
    }

    public String getTurno() {
        return turno;
    }

    public Personagem getSelecionado() {
        return selecionado;
    }

    public int getMovimentosCabrito() {
        return movimentosCabrito;
    }

    public int getMovimentosCarcara() {
        return movimentosCarcara;
    }

    public void setCarcara(Carcara c) {
        this.carcara = c;
        if (c != null && c.getPosicao() == -1) {
            // posiciona aleatoriamente sem colidir com cabrito
            int pos;
            Random r = new Random();
            do {
                pos = r.nextInt(circulos.length);
            } while (cabrito != null && cabrito.getPosicao() == pos);
            c.setPosicao(pos);
        }
    }

    public void setCabrito(Cabrito c) {
        this.cabrito = c;
        if (c != null && c.getPosicao() == -1) {
            int pos;
            Random r = new Random();
            do {
                pos = r.nextInt(circulos.length);
            } while (carcara != null && carcara.getPosicao() == pos);
            c.setPosicao(pos);
        }
    }

    // verifica se dois sirculos estão conectados
    private boolean estaConectado(int a, int b) {
        for (int[] l : ligacoes) {
            if ((l[0] == a && l[1] == b) || (l[0] == b && l[1] == a))
                return true;
        }
        return false;
    }

    // processa clique nas coordenadas do componente; devolve resultado com mensagem
    // e se houve mudança
    public ResultadoClique identificadorClique(int x, int y) {
        // Converter as coordenadas do clique (x, y) em um índice
        int clicado = -1;
        for (int i = 0; i < circulos.length; i++) {
            int centroX = circulos[i][0] + circulos[i][2] / 2;
            int centroY = circulos[i][1] + circulos[i][2] / 2;
            int raio = circulos[i][2] / 2;
            int diferencaX = x - centroX;
            int diferençaY = y - centroY;
            if (diferencaX * diferencaX + diferençaY * diferençaY <= raio * raio) {
                clicado = i;
                break;
            }
        }
        if (clicado == -1)
            return new ResultadoClique(false, null);

        // clique no cabrito
        if (cabrito != null && cabrito.getPosicao() == clicado) {
            if (!turno.equals("cabrito"))
                return new ResultadoClique(false, "Não é a vez do cabrito.");
            if (selecionado == null) {
                selecionado = cabrito;
                return new ResultadoClique(true, "Cabrito selecionado");
            }
            if (selecionado == cabrito) {
                selecionado = null;
                return new ResultadoClique(true, "Cabrito desmarcado");
            }
            return new ResultadoClique(false, null);
        }

        // clique no carcará
        if (carcara != null && carcara.getPosicao() == clicado) {
            if (!turno.equals("carcara"))
                return new ResultadoClique(false, "Não é a vez do carcará.");
            if (selecionado == null) {
                selecionado = carcara;
                return new ResultadoClique(true, "Carcará selecionado");
            }
            if (selecionado == carcara) {
                selecionado = null;
                return new ResultadoClique(true, "Carcará desmarcado");
            }
            return new ResultadoClique(false, null);
        }

        // clique em círculo vazio -> tenta mover selecionado
        if (selecionado != null) {
            int origem = selecionado.getPosicao();
            if (origem == clicado) {
                selecionado = null;
                return new ResultadoClique(true, null);
            }
            // não permite mover para círculo ocupado
            if ((cabrito != null && cabrito.getPosicao() == clicado)
                    || (carcara != null && carcara.getPosicao() == clicado)) {
                return new ResultadoClique(false, "Círculo ocupado.");
            }
            if (!estaConectado(origem, clicado))
                return new ResultadoClique(false, "Movimento inválido: não há ligação entre os círculos.");

            // executar movimento
            if (selecionado == cabrito) {
                movimentosCabrito++;
            } else if (selecionado == carcara) {
                movimentosCarcara++;
            }

            selecionado.setPosicao(clicado);
            // alterna turno
            if (selecionado == cabrito)
                turno = "carcara";
            else
                turno = "cabrito";
            selecionado = null;
            return new ResultadoClique(true, "Movimento realizado. Agora é a vez do " + turno + ".");
        }

        return new ResultadoClique(false, "Selecione um dos personagens!");
    }
    // reinicia o jogo
    public void resetarJogo() {
        movimentosCabrito = 0;
        movimentosCarcara = 0;
        turno = "cabrito";
        selecionado = null;
        if (carcara != null)
            carcara.setPosicao(-1);
        if (cabrito != null)
            cabrito.setPosicao(-1);
    }
}

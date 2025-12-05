public class Jogo {
    // coordenadas e ligações (definidas aqui para uso lógico)
    /*private int[][] circulos = {
            { 200, 40, 70 },
            { 40, 140, 70 },
            { 360, 140, 70 },
            { 90, 330, 70 },
            { 310, 330, 70 },
            { 200, 190, 70 }
    };*/
    private Circulo[] circulos={
        new Circulo(200, 40), 
        new Circulo(40, 140),
        new Circulo(360, 140), 
        new Circulo(90, 330),
        new Circulo(310, 330),
        new Circulo(200, 190)
    };
    
    private int[][] ligacoes = {
            { 0, 1 }, { 0, 2 }, { 0, 5 },
            { 1, 3 },
            { 2, 4 },
            { 3, 4 }, { 3, 5 }, { 4, 5 }
    };
    private static final int POS_INICIAL_CARCARA = 1;
    private static final int POS_INICIAL_CABRITO = 0;

    private Carcara carcara;
    private Cabrito cabrito;

    private Personagem selecionado = null;
    private String turno = "cabrito"; // sempre começa com cabrito

    private int movimentosCabrito = 0;
    private int movimentosCarcara = 0;

    private boolean jogoFinalizado = false;

    private CabritoAssado cabritoAssado; // personagem que aparece quando o cabrito morre

    private int cabritoAssadoPosicao = -1;

    public static class ResultadoClique {
        public final boolean mudou;
        public final String mensagem;

        public ResultadoClique(boolean mudou, String mensagem) {
            this.mudou = mudou;
            this.mensagem = mensagem;
        }
    }

    public CabritoAssado getCabritoAssado() {
        return cabritoAssado;
    }

    public void setCabritoAssado(CabritoAssado cabritoAssado) {
        this.cabritoAssado = cabritoAssado;
    }

    public int getCabritoAssadoPos() {
        return cabritoAssadoPosicao;
    }

    public Circulo[] getCirculos() {
        return circulos;
    }

    public int[][] getLigacoes() {
        return ligacoes;
    }

    // encapsula a posição armazenada e garante que não seja nula
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
    // retorna o número de movimentos de cada personagem
    public int getMovimentosCabrito() {
        return movimentosCabrito;
    }

    public int getMovimentosCarcara() {
        return movimentosCarcara;
    }

    public void setCabrito(Cabrito c) {
        this.cabrito = c;
        //se o cabrito estiver fora do mapa, ele volta para posição inicial
        if (c.getPosicao() == -1)
            c.setPosicao(POS_INICIAL_CABRITO);
    }

    public void setCarcara(Carcara c) {
        this.carcara = c;
        //se o carcara estiver fora do mapa, ele volta para posição inicial
        if (c.getPosicao() == -1)
            c.setPosicao(POS_INICIAL_CARCARA);
    }
    //método para aparecer a imagem do cabrito assado quando ele é capturado
    public void cabritoCapturado(boolean suicidio) {

        if (cabrito == null)
            return;

        int posicaoAssado;

        if (suicidio) {
            //assado aparece no lugar do Carcará
            posicaoAssado = carcara.getPosicao();
        } else {
            // assado no lugar do Cabrito
            posicaoAssado = cabrito.getPosicao();
        }

        // cria o cabrito assado
        cabritoAssado = new CabritoAssado("cabritoAssado.png");
        cabritoAssadoPosicao = posicaoAssado;

        // remove o cabrito (morto)
        cabrito.setPosicao(-1);
        carcara.setPosicao(-1);
    }

    // verifica se dois circulos estão conectados
    private boolean estaConectado(int a, int b) {
        for (int[] l : ligacoes) {
            if ((l[0] == a && l[1] == b) || (l[0] == b && l[1] == a))
                return true;
        }
        return false;
    }
    //esse método vai verificar se é fim de jogo
    private String verificarFimDeJogo() {
        if (carcara != null && cabrito != null &&
                carcara.getPosicao() == cabrito.getPosicao() || cabrito.getPosicao() == -1 && carcara.getPosicao() == -1
                || cabrito == null) {

            jogoFinalizado = true;
            //ele retorna o total de movimentos dos personagens 
            int total_jogadas = movimentosCabrito + movimentosCarcara;
            return "Cabrito capturado!\nTotal de jogadas: " + total_jogadas;
        }
        return null;
    }

    // processa clique nas coordenadas do componente; devolve resultado com mensagem
    // e se houve mudança
    public ResultadoClique identificadorClique(int x, int y){
        if (jogoFinalizado)
            throw new MovimentoInvalidoException(
                    "O jogo já terminou. Reinicie para jogar novamente.");

        // Converter as coordenadas do clique (x, y) em um índice
        int clicado = -1;
        for (int i = 0; i < circulos.length; i++) {
            int centroX= circulos[i].getPosicaoX() + Circulo.DIAMETRO / 2;
            //int centroX = circulos[i][0] + circulos[i][2] / 2;
            int centroY = circulos[i].getPosicaoY() + Circulo.DIAMETRO / 2;
            //int centroY = circulos[i][1] + circulos[i][2] / 2;
            int raio = Circulo.DIAMETRO / 2;
            int diferencaX = x - centroX;
            int diferencaY = y - centroY;
            if (diferencaX * diferencaX + diferencaY * diferencaY <= raio * raio) {
                clicado = i;
                break;
            }
        }
        //garante que os personagens não saiam do mapa
        if (clicado == -1)
            return new ResultadoClique(false, null);

        // Clique no cabrito
        if (cabrito != null && cabrito.getPosicao() == clicado) {
            if (selecionado == carcara && estaConectado(carcara.getPosicao(), cabrito.getPosicao())) {

                carcara.setPosicao(clicado); // carcara se move
                cabritoCapturado(false);// cabrito capturado
                String fim = verificarFimDeJogo();
                return new ResultadoClique(true, fim);
            } else if (selecionado == carcara) {
                throw new MovimentoInvalidoException("Cabrito não está adjacente. Ataque impossível!");
            } else {

                // comportamento normal de seleção do cabrito
                if (!turno.equals("cabrito"))
                    throw new MovimentoInvalidoException("Não é a vez do cabrito.");

                if (selecionado == null) {
                    selecionado = cabrito;
                    return new ResultadoClique(true, null);
                }
                if (selecionado == cabrito) {
                    selecionado = null;
                    return new ResultadoClique(true, null);
                }
                return new ResultadoClique(false, null);
            }
        }

        // clique no carcará
        if (carcara != null && carcara.getPosicao() == clicado) {
            if (selecionado == cabrito && estaConectado(carcara.getPosicao(), cabrito.getPosicao())) {
                cabritoCapturado(true);// cabrito capturado
                String fim = verificarFimDeJogo();
                return new ResultadoClique(true, fim);
            } else {

                if (!turno.equals("carcara")) {
                    throw new MovimentoInvalidoException("Não é a vez do carcará.");
                }
                if (selecionado == null) {
                    selecionado = carcara;
                    return new ResultadoClique(true, null);
                }
                if (selecionado == carcara) {
                    selecionado = null;
                    return new ResultadoClique(true, null);
                }
                return new ResultadoClique(false, null);
            }
        }

        // clique em círculo vazio - tenta mover selecionado
        if (selecionado != null) {
            int origem = selecionado.getPosicao();
            if (origem == clicado) {
                selecionado = null;
                return new ResultadoClique(true, null);
            }

            // Movimento do cabrito
            if (selecionado == cabrito) {

                boolean conectado = estaConectado(origem, clicado);

                if (!conectado) {
                    if (cabrito.podeSuperPulo()) {
                        cabrito.superPulo(clicado);
                        movimentosCabrito++;
                        turno = "carcara"; // passa a vez para o carcara
                        selecionado = null;
                        return new ResultadoClique(true, "Super Pulo do Cabrito usado!");

                    } else {
                        throw new MovimentoInvalidoException(
                                "Movimento inválido: sem ligação e super pulo já usado.");
                    }
                } else {
                    cabrito.setPosicao(clicado);
                }

                movimentosCabrito++;
                turno = "carcara";
                selecionado = null;

                String fim = verificarFimDeJogo();
                if (fim != null)
                    return new ResultadoClique(true, fim);

                return new ResultadoClique(true, null);
            }

            // Movimento do carcará
            if (selecionado == carcara) {

                if (!estaConectado(origem, clicado))
                    throw new MovimentoInvalidoException(
                            "O carcará só pode mover para espaços adjacentes.");

                carcara.setPosicao(clicado);
                movimentosCarcara++;
                turno = "cabrito";
                selecionado = null;

                String fim = verificarFimDeJogo();
                if (fim != null)
                    return new ResultadoClique(true, fim);

                return new ResultadoClique(true, null);
            }
        }

        throw new MovimentoInvalidoException("Selecione um personagem para mover.");
    }

    public void resetarJogo() {
        movimentosCabrito = 0;//reseta o total de movimentos
        movimentosCarcara = 0;
        turno = "cabrito"; //turno começa com o cabrito
        selecionado = null; 
        jogoFinalizado = false;

        //os personagens voltam pra posição inical
        if (carcara != null) {
            carcara.setPosicao(POS_INICIAL_CARCARA);
        }

        if (cabrito != null) {
            cabrito.reset();
            cabrito.setPosicao(POS_INICIAL_CABRITO);
        }
        //remove a imagem do cabrito assado
        cabritoAssado = null;
        cabritoAssadoPosicao = -1;
    }
}
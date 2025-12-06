import javax.swing.*;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceJogo extends JFrame{
    private Jogo jogo = new Jogo();
    private ComponenteJogo percurso = new ComponenteJogo(jogo);
    private String carcaraImage = "carcara.png";
    private String cabritoImage = "cabrito.png";

    public InterfaceJogo(){
        super();
        configurarTela();
    }

    public void configurarTela(){
        // define os elementos para a criação da janela
        getContentPane().setBackground(new Color(0, 128, 0));
        setSize(800, 600);
        setTitle("Corre Cabrito");
        // define que ao fechar a janela do jogo deve parar de executar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints organizadorComponentes = new GridBagConstraints();
        organizadorComponentes.gridx = 0; //define a coluna da grade onde o componente percurso começará
        organizadorComponentes.gridy = 0; //define a linha da grade onde o componente percurso começará
        organizadorComponentes.fill = GridBagConstraints.BOTH; //faz o percurso preencher totalmente o espaço diponivel
        //determinam como o espaço extra deve ser distribuído se o contêiner for redimensionado
        organizadorComponentes.weightx = 1.0;
        organizadorComponentes.weighty = 1.0;
        add(percurso, organizadorComponentes);

        // cria os personagens
        jogo.setCarcara(new Carcara(carcaraImage));
        jogo.setCabrito(new Cabrito(cabritoImage));

        configurarMenu();
        // dimensiona a janela para o tamanho definido ajustando seus componentes
        pack();
        // centraliza a janela na tela do computador
        setLocationRelativeTo(null);
        setVisible(true);
    }
    //configura o menu de reiniciar, sair e de ver os nomes
    private void configurarMenu(){
        //cria a barra de menu
        JMenuBar menuBar = new JMenuBar();
        // define os menus da barra de menu
        JMenu menuJogo = new JMenu("Jogo");
        JMenu menuAutoria = new JMenu("Autoria");
        JMenu menuAjuda = new JMenu("Ajuda");
        // define as opções de cada menu
        JMenuItem botaoReiniciar = new JMenuItem("Reiniciar");
        JMenuItem botaoSair = new JMenuItem("Sair");
        JMenuItem botaoVerNomes = new JMenuItem("Ver Nomes");
        JMenuItem botaoComoJogar = new JMenuItem("Como Jogar?");
        // cria um listener para o botão Reiniciar
        botaoReiniciar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                    // reinicia o estado do jogo, os personagens e o contador de jodagas
                        jogo.resetarJogo();
                        jogo.setCarcara(new Carcara(carcaraImage));
                        jogo.setCabrito(new Cabrito(cabritoImage));
                        percurso.repaint();
            }
        });
        // cria um listener para o botão Sair
        botaoSair.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // encerra o programa totalmente
                System.exit(0);
            }
        });
        // cria um listener para o botão Ver Nomes
        botaoVerNomes.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // cria uma janela de mensagem e exibe a String autores
                String autores = "Autores:\n- Mariana Ferrão Chuquel\n- Rafaela de Menezes";
                JOptionPane.showMessageDialog(InterfaceJogo.this, autores, "Autoria", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // cria um listener para o botão Como Jogar?
        botaoComoJogar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // cria uma janela de mensagem e exibe a String instrucoes
                String instrucoes = "Como Jogar:\n"
                        + "- Este jogo foi criado para 2 jogadores;\n"
                        + "- Cada jogador deve escolher entre os personagens Cabrito e Carcará,\n"
                        + "- Para realizar o movimento de seu personagem selecione ele, avalie as cores do mapa e clique no círculo desejado;\n"
                        + "- O Cabrito possui a habilidade de super pulo, o que o possiblitita pular para qualquer círculo no mapa,\n" 
                        + "porém só poderá ser utilizada 1 vez;\n"
                        + "- O Carcará possui o objetivo de capturar o Cabrito;\n"
                        + "- O jogo termina quando o Cabrito é capturado pelo Carcará.\n";
                JOptionPane.showMessageDialog(InterfaceJogo.this, instrucoes, "Como Jogar", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        // adiciona cada menu a barra de menu
        menuJogo.add(botaoReiniciar);
        menuJogo.add(botaoSair);
        menuAjuda.add(botaoComoJogar);
        menuAutoria.add(botaoVerNomes);
        menuBar.add(menuJogo);
        menuBar.add(menuAutoria);
        menuBar.add(menuAjuda);
        // aenxa a barra de menu
        setJMenuBar(menuBar);
    }
}
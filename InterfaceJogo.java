import javax.swing.*;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// mouse handling moved to `ComponenteJogo`

public class InterfaceJogo extends JFrame {
    private Jogo jogo = new Jogo();
    private ComponenteJogo percurso = new ComponenteJogo(jogo);
    private String carcaraImage = "carcara.png";
    private String cabritoImage = "cabrito.png";

    public InterfaceJogo() {
        super();
        configurarTela();
    }

    public void configurarTela() {
        getContentPane().setBackground(new Color(0, 128, 0));
        setLocation(400, 100);
        setSize(800, 600);
        setTitle("Corre Cabrito");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints organizadorComponentes = new GridBagConstraints();
        organizadorComponentes.gridx = 0; // define a coluna da grade onde o componente percurso começará
        organizadorComponentes.gridy = 0; // define a linha da grade onde o componente percurso começará
        organizadorComponentes.fill = GridBagConstraints.BOTH; // faz o percurso preencher totalmente o espaço diponivel
        // determinam como o espaço extra deve ser distribuído se o contêiner for
        // redimensionado
        organizadorComponentes.weightx = 1.0;
        organizadorComponentes.weighty = 1.0;
        add(percurso, organizadorComponentes);

        // cria personagens e posiciona aleatoriamente via modelo Jogo
        jogo.setCarcara(new Carcara(carcaraImage));
        jogo.setCabrito(new Cabrito(cabritoImage));

        configurarMenu();
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // configura o menu de reiniciar, sair e se ver os nomes
    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuJogo = new JMenu("Jogo");
        JMenuItem botaoReiniciar = new JMenuItem("Reiniciar");
        JMenuItem botaoSair = new JMenuItem("Sair");

        botaoReiniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // reinicia o estado do jogo (personagens e contadores)
                jogo.resetarJogo();
                jogo.setCarcara(new Carcara(carcaraImage));
                jogo.setCabrito(new Cabrito(cabritoImage));
                percurso.repaint();
            }
        });

        botaoSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menuJogo.add(botaoReiniciar);
        menuJogo.add(botaoSair);

        JMenu menuAutoria = new JMenu("Autoria");
        JMenuItem botaoVerNomes = new JMenuItem("Ver nomes");
        botaoVerNomes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String autores = "Autores:\n- Mariana Ferrao Chuquel\n- Rafaela de Menezes";
                JOptionPane.showMessageDialog(InterfaceJogo.this, autores, "Autoria", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        menuAutoria.add(botaoVerNomes);

        menuBar.add(menuJogo);
        menuBar.add(menuAutoria);

        setJMenuBar(menuBar);
    }
}
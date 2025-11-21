import javax.swing.*;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InterfaceJogo extends JFrame{
    private ComponenteJogo percurso = new ComponenteJogo();
    private String carcaraImage = "carcara.png";
    private String cabritoImage = "cabrito.png";

    public InterfaceJogo(){
        super();
        configurarTela();
    }

    public void configurarTela(){
        getContentPane().setBackground(new Color(0, 128, 0));
        setLocation(400, 100);
        setSize(800, 600);
        setTitle("Corre Cabrito");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(percurso, gbc);

        // cria personagens e posiciona aleatoriamente
        Carcara carcara = new Carcara(carcaraImage);
        Cabrito cabrito = new Cabrito(cabritoImage);
        percurso.setCarcara(carcara);
        percurso.setCabrito(cabrito);

        configurarMenu();

        setVisible(true);
    }

    private void configurarMenu(){
        JMenuBar menuBar = new JMenuBar();

        JMenu menuJogo = new JMenu("Jogo");
        JMenuItem botaoReiniciar = new JMenuItem("Reiniciar");
        JMenuItem botaoSair = new JMenuItem("Sair");

        botaoReiniciar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                // recria personagens e posiciona aleatoriamente
                Carcara newCarcara = new Carcara(carcaraImage);
                Cabrito newCabrito = new Cabrito(cabritoImage);
                percurso.setCarcara(newCarcara);
                percurso.setCabrito(newCabrito);
                percurso.repaint();
            }
        });

        botaoSair.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });

        menuJogo.add(botaoReiniciar);
        menuJogo.add(botaoSair);

        JMenu menuAutoria = new JMenu("Autoria");
        JMenuItem botaoVerNomes = new JMenuItem("Ver nomes");
        botaoVerNomes.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
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
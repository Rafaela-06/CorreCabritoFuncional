import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Window;
import javax.swing.SwingUtilities;

public class ComponenteJogo extends JPanel {
    private final Jogo jogo;
    // últimos offsets aplicados para centralizar o desenho
    private int offsetX = 0;
    private int offsetY = 0;

    public ComponenteJogo(Jogo jogo){
        this.jogo = jogo;
        setOpaque(false);
        setPreferredSize(new Dimension(800, 600));
        // trata cliques localmente para aplicar offset antes de delegar ao modelo
        addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int mx = e.getX() - offsetX;
                int my = e.getY() - offsetY;
                Jogo.ClickResult res = jogo.handleClick(mx, my);
                if (res.changed) repaint();
                if (res.message != null){
                    Window w = SwingUtilities.getWindowAncestor(ComponenteJogo.this);
                    JOptionPane.showMessageDialog(w, res.message);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(4));

        int[][] circulos = jogo.getCirculos();
        int[][] ligacoes = jogo.getLigacoes();

        // calcular bounding box dos círculos para centralizar
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
        for (int i = 0; i < circulos.length; i++){
            int x = circulos[i][0];
            int y = circulos[i][1];
            int s = circulos[i][2];
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x + s);
            maxY = Math.max(maxY, y + s);
        }
        int contentW = maxX - minX;
        int contentH = maxY - minY;
        offsetX = (getWidth() - contentW) / 2 - minX;
        offsetY = (getHeight() - contentH) / 2 - minY;

        // desenhar ligações
        g2.setColor(Color.BLACK);
        for (int[] l : ligacoes){
            int x1 = circulos[l[0]][0] + circulos[l[0]][2]/2 + offsetX;
            int y1 = circulos[l[0]][1] + circulos[l[0]][2]/2 + offsetY;
            int x2 = circulos[l[1]][0] + circulos[l[1]][2]/2 + offsetX;
            int y2 = circulos[l[1]][1] + circulos[l[1]][2]/2 + offsetY;
            g2.drawLine(x1, y1, x2, y2);
        }

        // informações do jogo
        int carcaraPos = jogo.getCarcaraPos();
        int cabritoPos = jogo.getCabritoPos();
        Personagem selecionado = jogo.getSelecionado();

        // desenhar círculos com regras de cor
        for (int i = 0; i < circulos.length; i++){
            int x = circulos[i][0] + offsetX;
            int y = circulos[i][1] + offsetY;
            int size = circulos[i][2];

            Color fill = Color.WHITE;

            if (carcaraPos == i) fill = Color.RED;
            else if (cabritoPos == i){
                // se ligado ao carcara -> amarelo
                boolean ligado = false;
                if (carcaraPos >= 0){
                    for (int[] l : ligacoes) if ((l[0]==i && l[1]==carcaraPos) || (l[1]==i && l[0]==carcaraPos)) ligado = true;
                }
                fill = ligado ? Color.YELLOW : Color.WHITE;
            } else if (selecionado != null){
                int origem = selecionado.getPosicao();
                if (origem >= 0){
                    // verifica ligação origem->i
                    boolean ligado = false;
                    for (int[] l : ligacoes) if ((l[0]==origem && l[1]==i) || (l[1]==origem && l[0]==i)) ligado = true;
                    boolean ocupado = (cabritoPos == i) || (carcaraPos == i);
                    if (ligado && !ocupado) fill = Color.GREEN;
                }
            }

            g2.setColor(fill);
            g2.fillOval(x, y, size, size);

            g2.setColor(Color.BLACK);
            g2.drawOval(x, y, size, size);
        }

        // desenhar personagens por cima
        Carcara car = jogo.getCarcara();
        if (car != null && carcaraPos >= 0){
            int cx = circulos[carcaraPos][0] + circulos[carcaraPos][2]/2 + offsetX;
            int cy = circulos[carcaraPos][1] + circulos[carcaraPos][2]/2 + offsetY;
            car.desenhar((Graphics2D) g2, cx, cy, circulos[carcaraPos][2]);
        }
        Cabrito cab = jogo.getCabrito();
        if (cab != null && cabritoPos >= 0){
            int cx = circulos[cabritoPos][0] + circulos[cabritoPos][2]/2 + offsetX;
            int cy = circulos[cabritoPos][1] + circulos[cabritoPos][2]/2 + offsetY;
            cab.desenhar((Graphics2D) g2, cx, cy, circulos[cabritoPos][2]);
        }

        // desenhar contadores no topo central
        int boxW = 360;
        int boxH = 48;
        int bx = (getWidth() - boxW) / 2;
        int by = 8;
        g2.setColor(new Color(0,0,0,160));
        g2.fillRoundRect(bx, by, boxW, boxH, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));
        int textX = bx + 12;
        g2.drawString("Movimentos Cabrito: " + jogo.getMovimentosCabrito(), textX, by + 18);
        g2.drawString("Movimentos Carcará: " + jogo.getMovimentosCarcara(), textX, by + 36);
    }
}

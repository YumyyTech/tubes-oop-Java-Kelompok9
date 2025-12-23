import javax.swing.*;
import java.awt.*;

public class ChartPanel extends JPanel {

    private Poll poll;
    private Color[] colors = {
        Color.BLUE, Color.RED, Color.GREEN, Color.ORANGE,
        Color.MAGENTA, Color.CYAN, Color.PINK,
        Color.YELLOW, Color.GRAY, Color.DARK_GRAY
    };

    public ChartPanel(Poll poll) {
        this.poll = poll;
        setPreferredSize(new Dimension(500, 250));
        setBackground(Color.WHITE);
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int total = poll.getTotalVotes();
        if (total == 0) {
            g.drawString("Belum ada vote", 200, 120);
            return;
        }

        int max = 1;
        for (int i = 0; i < poll.getOptions().length; i++) {
            max = Math.max(max, poll.getVoteCount(i));
        }

        int barWidth = Math.max(40, 500 / poll.getOptions().length - 10);
        int x = 50;

        for (int i = 0; i < poll.getOptions().length; i++) {
            int h = poll.getVoteCount(i) * 150 / max;
            g.setColor(colors[i % colors.length]);
            g.fillRect(x, 200 - h, barWidth, h);
            g.setColor(Color.BLACK);

            String txt = poll.getOptions()[i];
            if (txt.length() > 8) txt = txt.substring(0, 8) + "...";

            g.drawString(txt, x, 220);
            g.drawString("" + poll.getVoteCount(i),
                    x + barWidth / 2 - 5, 190 - h);
            x += barWidth + 20;
        }
    }
}

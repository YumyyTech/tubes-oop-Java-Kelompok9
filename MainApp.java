import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainApp extends JFrame {

    private Poll poll;
    private Voter voter;

    private ChartPanel chartPanel;
    private JTable table;
    private DefaultTableModel tableModel;

    private JPanel optionsButtonPanel;
    private JTextField questionField;
    private JTextField optionField;

    public MainApp() {
        login();
        initPoll();
        initGUI();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    poll.saveToFile(new File("autosave_poll.txt"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void login() {
        String name = JOptionPane.showInputDialog(
                null,
                "Masukkan nama Anda:",
                "Login",
                JOptionPane.QUESTION_MESSAGE
        );

        if (name == null || name.trim().isEmpty()) {
            System.exit(0);
        }
        voter = new Voter(name);
    }

    private void initPoll() {
        File f = new File("autosave_poll.txt");
        if (f.exists()) {
            try {
                poll = Poll.loadFromFile(f);
                return;
            } catch (IOException ignored) {}
        }
        poll = new Poll("Masukkan pertanyaan polling", new String[]{});
    }

    private void initGUI() {
        setTitle("Sistem Voting");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTop(), BorderLayout.NORTH);
        add(createCenter(), BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createTop() {
        JPanel p = new JPanel();
        p.setBackground(new Color(52, 152, 219));
        JLabel l = new JLabel("SISTEM VOTING OOP JAVA", SwingConstants.CENTER);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 22));
        p.add(l);
        return p;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new GridLayout(1, 2));
        center.add(createVotePanel());
        center.add(createResultPanel());
        return center;
    }

    private JPanel createVotePanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createTitledBorder("Voting"));

        questionField = new JTextField(poll.getQuestion(), 25);
        JButton updateQ = new JButton("Update");
        updateQ.addActionListener(e -> updateQuestion());

        JPanel qPanel = new JPanel();
        qPanel.add(new JLabel("Pertanyaan:"));
        qPanel.add(questionField);
        qPanel.add(updateQ);

        optionField = new JTextField(20);
        JButton addOpt = new JButton("Tambah");
        addOpt.addActionListener(e -> addOption());

        JPanel oPanel = new JPanel();
        oPanel.add(new JLabel("Opsi:"));
        oPanel.add(optionField);
        oPanel.add(addOpt);

        optionsButtonPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        refreshOptionsButtons();

        p.add(qPanel);
        p.add(oPanel);
        p.add(new JScrollPane(optionsButtonPanel));
        return p;
    }

    private JPanel createResultPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder("Hasil"));

        chartPanel = new ChartPanel(poll);
        p.add(chartPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"No", "Opsi", "Vote", "Persentase"}, 0
        );
        table = new JTable(tableModel);
        refreshTable();

        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel createBottom() {
        JPanel p = new JPanel();

        JButton save = new JButton("Save");
        JButton load = new JButton("Load");
        JButton reset = new JButton("Reset Vote");

        save.addActionListener(e -> savePoll());
        load.addActionListener(e -> loadPoll());
        reset.addActionListener(e -> voter = new Voter(voter.username));

        p.add(save);
        p.add(load);
        p.add(reset);
        return p;
    }

    private void refreshOptionsButtons() {
        optionsButtonPanel.removeAll();

        String[] options = poll.getOptions();
        for (int i = 0; i < options.length; i++) {
            int idx = i;
            JButton b = new JButton(options[i]);
            b.addActionListener(e -> {
                if (voter.canVote()) {
                    try {
                        poll.addVote(idx);
                        voter.setVoted();
                        refreshTable();
                        chartPanel.repaint();
                    } catch (Exception ignored) {}
                }
            });
            optionsButtonPanel.add(b);
        }

        optionsButtonPanel.revalidate();
        optionsButtonPanel.repaint();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int total = poll.getTotalVotes();

        for (int i = 0; i < poll.getOptions().length; i++) {
            int v = poll.getVoteCount(i);
            double p = total > 0 ? (v * 100.0 / total) : 0;
            tableModel.addRow(new Object[]{
                    i + 1,
                    poll.getOptions()[i],
                    v,
                    String.format("%.1f%%", p)
            });
        }
    }

    private void updateQuestion() {
        poll = new Poll(questionField.getText(), poll.getOptions());
        chartPanel.setPoll(poll);
    }

    private void addOption() {
        String o = optionField.getText().trim();
        if (o.isEmpty()) return;

        String[] old = poll.getOptions();
        String[] n = new String[old.length + 1];
        System.arraycopy(old, 0, n, 0, old.length);
        n[old.length] = o;

        poll.updateOptions(n);
        refreshOptionsButtons();
        refreshTable();
        optionField.setText("");
    }

    private void savePoll() {
        try {
            poll.saveToFile(new File("poll.txt"));
        } catch (IOException ignored) {}
    }

    private void loadPoll() {
        try {
            poll = Poll.loadFromFile(new File("poll.txt"));
            chartPanel.setPoll(poll);
            refreshTable();
            refreshOptionsButtons();
        } catch (IOException ignored) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}

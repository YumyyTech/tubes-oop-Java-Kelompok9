import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MainApp extends JFrame {

    private Poll poll;
    private Voter voter;

    private JTextField txtQuestion;
    private JTextField txtNewOption;

    private JPanel panelOptions;
    private ChartPanel chartPanel;

    private DefaultTableModel tableModel;
    private JTable table;

    private ArrayList<String> options = new ArrayList<>();

    public MainApp() {
        setTitle("Sistem Voting - Input Manual & Tombol");
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        voter = new Voter("user1");
        poll = new Poll("Masukkan pertanyaan polling", new String[]{});

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 152, 219));
        JLabel title = new JLabel("SISTEM VOTING - INPUT MANUAL & TOMBOL PILIHAN");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        header.add(title);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new GridLayout(1, 2, 10, 0));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        main.add(createLeftPanel());
        main.add(createRightPanel());
        return main;
    }

    // ================= LEFT =================
    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(10, 10));

        left.add(createSetupPanel(), BorderLayout.NORTH);
        left.add(createOptionPanel(), BorderLayout.CENTER);

        return left;
    }

    private JPanel createSetupPanel() {
        JPanel setup = new JPanel(new GridLayout(2, 1, 5, 5));
        setup.setBorder(BorderFactory.createTitledBorder("Setup & Voting"));

        JPanel qPanel = new JPanel(new BorderLayout(5, 5));
        qPanel.add(new JLabel("Pertanyaan Polling:"), BorderLayout.WEST);
        txtQuestion = new JTextField("Masukkan pertanyaan polling");
        JButton btnUpdate = new JButton("Update");
        qPanel.add(txtQuestion, BorderLayout.CENTER);
        qPanel.add(btnUpdate, BorderLayout.EAST);

        btnUpdate.addActionListener(e -> {
            poll = new Poll(txtQuestion.getText(), options.toArray(new String[0]));
            chartPanel.setPoll(poll);
            refreshTable();
        });

        JPanel addPanel = new JPanel(new BorderLayout(5, 5));
        addPanel.add(new JLabel("Tambah Opsi Baru:"), BorderLayout.WEST);
        txtNewOption = new JTextField();
        JButton btnAdd = new JButton("Tambah");
        addPanel.add(txtNewOption, BorderLayout.CENTER);
        addPanel.add(btnAdd, BorderLayout.EAST);

        btnAdd.addActionListener(e -> addOption());

        setup.add(qPanel);
        setup.add(addPanel);

        return setup;
    }

    private JPanel createOptionPanel() {
    JPanel optionPanel = new JPanel(new BorderLayout(5, 5));
    optionPanel.setBorder(BorderFactory.createTitledBorder("PILIHAN (Klik untuk vote):"));

    panelOptions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

    JLabel emptyLabel = new JLabel("Belum ada opsi. Tambahkan dulu!");
    emptyLabel.setForeground(Color.GRAY);
    panelOptions.add(emptyLabel);

    JScrollPane scroll = new JScrollPane(panelOptions);
    scroll.setPreferredSize(new Dimension(400, 250));

    JButton btnClear = new JButton("Hapus Semua Opsi");
    btnClear.addActionListener(e -> clearOptions());

    optionPanel.add(scroll, BorderLayout.CENTER);
    optionPanel.add(btnClear, BorderLayout.SOUTH);

    return optionPanel;
}


    // ================= RIGHT =================
    private JPanel createRightPanel() {
    JPanel right = new JPanel(new BorderLayout(5, 5));
    right.setBorder(BorderFactory.createTitledBorder("Hasil"));

    chartPanel = new ChartPanel(poll);
    chartPanel.setPreferredSize(new Dimension(500, 220));

    tableModel = new DefaultTableModel(
            new Object[]{"No", "Opsi", "Jumlah Vote", "Persentase"}, 0);
    table = new JTable(tableModel);

    right.add(chartPanel, BorderLayout.CENTER);
    right.add(new JScrollPane(table), BorderLayout.SOUTH);

    return right;
}


    // ================= BOTTOM =================
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton btnSave = new JButton("Save Polling");
        JButton btnLoad = new JButton("Load Polling");
        JButton btnResetMe = new JButton("Reset Vote Saya");
        JButton btnResetAll = new JButton("Reset Semua Polling");

        btnSave.addActionListener(e -> savePolling());
        btnLoad.addActionListener(e -> loadPolling());
        btnResetMe.addActionListener(e -> voter = new Voter("user1"));
        btnResetAll.addActionListener(e -> resetAll());

        bottom.add(btnSave);
        bottom.add(btnLoad);
        bottom.add(btnResetMe);
        bottom.add(btnResetAll);

        return bottom;
    }

    // ================= LOGIC =================
    private void addOption() {
        String opt = txtNewOption.getText().trim();
        if (opt.isEmpty()) return;

        options.add(opt);
        poll.updateOptions(options.toArray(new String[0]));
        txtNewOption.setText("");

        refreshOptionButtons();
        refreshTable();
    }

private void refreshOptionButtons() {
    panelOptions.removeAll();

    for (int i = 0; i < options.size(); i++) {
        int idx = i;
        JButton btn = new JButton(options.get(i));
        btn.setPreferredSize(new Dimension(120, 30));
        btn.addActionListener(e -> vote(idx));
        panelOptions.add(btn);
    }

    panelOptions.revalidate();
    panelOptions.repaint();
}

    private void vote(int index) {
        if (!voter.canVote()) {
            JOptionPane.showMessageDialog(this, "Anda sudah vote!");
            return;
        }
        try {
            poll.addVote(index);
            voter.setVoted();
            refreshTable();
            chartPanel.repaint();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int total = poll.getTotalVotes();

        for (int i = 0; i < options.size(); i++) {
            int v = poll.getVoteCount(i);
            double p = total == 0 ? 0 : (v * 100.0 / total);
            tableModel.addRow(new Object[]{
                    i + 1, options.get(i), v, String.format("%.1f%%", p)
            });
        }
    }

    private void clearOptions() {
        options.clear();
        poll.updateOptions(new String[]{});
        panelOptions.removeAll();
        panelOptions.add(new JLabel("Belum ada opsi. Tambahkan dulu!"));
        refreshTable();
        chartPanel.repaint();
    }

    private void resetAll() {
        poll.resetAllVotes();
        voter = new Voter("user1");
        refreshTable();
        chartPanel.repaint();
    }

    private void savePolling() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                poll.saveToFile(fc.getSelectedFile());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    private void loadPolling() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                poll = Poll.loadFromFile(fc.getSelectedFile());
                options.clear();
                for (String s : poll.getOptions()) options.add(s);
                chartPanel.setPoll(poll);
                refreshOptionButtons();
                refreshTable();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp().setVisible(true));
    }
}

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
        setTitle("Sistem Voting Kelompok 9");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        voter = new Voter("user1");
        poll = new Poll("Masukkan pertanyaan polling", new String[] {});

        add(createHeader(), BorderLayout.NORTH);
        add(createMainPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setBackground(new Color(52, 73, 94));
        header.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel title = new JLabel("SISTEM VOTING");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        header.add(title);
        return header;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new GridLayout(1, 2, 15, 0));
        main.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        main.setBackground(new Color(240, 240, 240));

        main.add(createLeftPanel());
        main.add(createRightPanel());
        return main;
    }

    // ================= LEFT PANEL =================
    private JPanel createLeftPanel() {
        JPanel left = new JPanel(new BorderLayout(10, 10));
        left.setBackground(new Color(240, 240, 240));

        left.add(createSetupPanel(), BorderLayout.NORTH);
        left.add(createOptionPanel(), BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(240, 240, 240));
        JLabel status = new JLabel("Voting " + (voter.canVote() ? "Satu Kali" : "Tidak Ganda"));
        status.setFont(new Font("Arial", Font.PLAIN, 12));
        statusPanel.add(status);
        left.add(statusPanel, BorderLayout.SOUTH);

        return left;
    }

    private JPanel createSetupPanel() {
        JPanel setup = new JPanel(new GridLayout(3, 1, 8, 8));
        setup.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                "📝 Setup Polling"));
        setup.setBackground(Color.WHITE);

        // Panel Pertanyaan
        JPanel qPanel = new JPanel(new BorderLayout(10, 5));
        qPanel.setBackground(Color.WHITE);
        JLabel qLabel = new JLabel("Pertanyaan:");
        qLabel.setFont(new Font("Arial", Font.BOLD, 12));
        qPanel.add(qLabel, BorderLayout.WEST);

        txtQuestion = new JTextField("Masukkan pertanyaan polling");
        txtQuestion.setFont(new Font("Arial", Font.PLAIN, 12));
        txtQuestion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBackground(new Color(52, 152, 219));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFont(new Font("Arial", Font.BOLD, 12));
        btnUpdate.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        qPanel.add(txtQuestion, BorderLayout.CENTER);
        qPanel.add(btnUpdate, BorderLayout.EAST);

        // Panel Tambah Opsi
        JPanel addPanel = new JPanel(new BorderLayout(10, 5));
        addPanel.setBackground(Color.WHITE);
        JLabel addLabel = new JLabel("Opsi Baru:");
        addLabel.setFont(new Font("Arial", Font.BOLD, 12));
        addPanel.add(addLabel, BorderLayout.WEST);

        txtNewOption = new JTextField();
        txtNewOption.setFont(new Font("Arial", Font.PLAIN, 12));
        txtNewOption.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton btnAdd = new JButton("Tambah");
        btnAdd.setBackground(new Color(46, 204, 113));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 12));
        btnAdd.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        addPanel.add(txtNewOption, BorderLayout.CENTER);
        addPanel.add(btnAdd, BorderLayout.EAST);

        // Panel Info
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(255, 255, 204));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 204, 0)));
        JLabel infoLabel = new JLabel("Klik tombol opsi untuk memberikan vote");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        infoPanel.add(infoLabel);

        // Event Listeners
        btnUpdate.addActionListener(e -> {
            poll = new Poll(txtQuestion.getText(), options.toArray(new String[0]));
            chartPanel.setPoll(poll);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Pertanyaan berhasil diupdate!");
        });

        btnAdd.addActionListener(e -> addOption());

        // Enter key untuk tambah opsi
        txtNewOption.addActionListener(e -> addOption());

        setup.add(qPanel);
        setup.add(addPanel);
        setup.add(infoPanel);

        return setup;
    }

    private JPanel createOptionPanel() {
        JPanel optionPanel = new JPanel(new BorderLayout(5, 5));
        optionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
                "🗳️ PILIHAN (Klik untuk vote)"));
        optionPanel.setBackground(Color.WHITE);

        // Panel untuk tombol-tombol dengan layout VERTIKAL
        panelOptions = new JPanel();
        panelOptions.setLayout(new BoxLayout(panelOptions, BoxLayout.Y_AXIS));
        panelOptions.setBackground(Color.WHITE);

        JLabel emptyLabel = new JLabel("Belum ada opsi. Tambahkan dulu!");
        emptyLabel.setForeground(Color.BLACK);
        emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelOptions.add(emptyLabel);

        JScrollPane scroll = new JScrollPane(panelOptions);
        scroll.setPreferredSize(new Dimension(450, 300));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnClear = new JButton("Hapus Semua Opsi");
        btnClear.setBackground(new Color(231, 76, 60));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFont(new Font("Arial", Font.BOLD, 12));
        btnClear.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnClear.addActionListener(e -> clearOptions());

        buttonPanel.add(btnClear);

        optionPanel.add(scroll, BorderLayout.CENTER);
        optionPanel.add(buttonPanel, BorderLayout.SOUTH);

        return optionPanel;
    }

    // ================= RIGHT PANEL =================
    private JPanel createRightPanel() {
        JPanel right = new JPanel(new BorderLayout(10, 10));
        right.setBackground(new Color(240, 240, 240));

        // Panel Chart
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(241, 196, 15), 2),
                "📊 Grafik Hasil Voting"));
        chartContainer.setBackground(Color.WHITE);

        chartPanel = new ChartPanel(poll);
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        // Panel Tabel
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(26, 188, 156), 2),
                "📋 Data Voting"));
        tableContainer.setBackground(Color.WHITE);

        tableModel = new DefaultTableModel(
                new Object[] { "No", "Opsi", "Jumlah Vote", "Persentase" }, 0);
        table = new JTable(tableModel);

        // Styling tabel
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(500, 200));
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        right.add(chartContainer, BorderLayout.NORTH);
        right.add(tableContainer, BorderLayout.CENTER);

        return right;
    }

    // ================= BOTTOM PANEL =================
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottom.setBackground(new Color(52, 73, 94));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Style untuk semua tombol
        Color buttonColor = new Color(41, 128, 185);
        Color hoverColor = new Color(52, 152, 219);

        JButton btnSave = createStyledButton("Save Polling", buttonColor);
        JButton btnLoad = createStyledButton("Load Polling", new Color(46, 204, 113));
        JButton btnResetMe = createStyledButton("Reset Vote Saya", new Color(241, 196, 15));
        JButton btnResetAll = createStyledButton("️Reset Semua", new Color(231, 76, 60));

        btnSave.addActionListener(e -> savePolling());
        btnLoad.addActionListener(e -> loadPolling());
        btnResetMe.addActionListener(e -> {
            voter = new Voter("user1");
            JOptionPane.showMessageDialog(this, "Status vote Anda telah direset!");
        });
        btnResetAll.addActionListener(e -> resetAll());

        bottom.add(btnSave);
        bottom.add(btnLoad);
        bottom.add(btnResetMe);
        bottom.add(btnResetAll);

        return bottom;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        button.setFocusPainted(false);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });

        return button;
    }

    // ================= LOGIC METHODS =================
    private void addOption() {
        String opt = txtNewOption.getText().trim();
        if (opt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Opsi tidak boleh kosong!");
            return;
        }

        options.add(opt);
        poll.updateOptions(options.toArray(new String[0]));
        txtNewOption.setText("");
        txtNewOption.requestFocus();

        refreshOptionButtons();
        refreshTable();

        JOptionPane.showMessageDialog(this, "Opsi berhasil ditambahkan!");
    }

    private void refreshOptionButtons() {
        panelOptions.removeAll();

        if (options.isEmpty()) {
            JLabel emptyLabel = new JLabel("📋 Belum ada opsi. Tambahkan dulu!");
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelOptions.add(emptyLabel);
        } else {
            for (int i = 0; i < options.size(); i++) {
                int idx = i;
                JButton btn = createOptionButton(options.get(i), i);
                btn.addActionListener(e -> vote(idx));

                // Atur alignment agar tombol berada di tengah horizontal
                btn.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelOptions.add(btn);

                // Tambah spacing antar tombol
                panelOptions.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }

        panelOptions.revalidate();
        panelOptions.repaint();
    }

    private JButton createOptionButton(String text, int index) {
        JButton button = new JButton((index + 1) + ". " + text);
        button.setMaximumSize(new Dimension(400, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(350, 40));

        // Styling tombol
        button.setBackground(new Color(155, 89, 182));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(142, 68, 173));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(125, 60, 152), 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(155, 89, 182));
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(142, 68, 173), 2),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)));
            }
        });

        return button;
    }

    private void vote(int index) {
        if (!voter.canVote()) {
            JOptionPane.showMessageDialog(this, "❌ Anda sudah melakukan vote!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            poll.addVote(index);
            voter.setVoted();
            refreshTable();
            chartPanel.repaint();

            // Tampilkan konfirmasi
            String message = String.format("✅ Vote Anda untuk '%s' telah direkam!", options.get(index));
            JOptionPane.showMessageDialog(this, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int total = poll.getTotalVotes();

        for (int i = 0; i < options.size(); i++) {
            int v = poll.getVoteCount(i);
            double p = total == 0 ? 0 : (v * 100.0 / total);
            tableModel.addRow(new Object[] {
                    i + 1,
                    options.get(i),
                    v,
                    String.format("%.1f%%", p)
            });
        }
    }

    private void clearOptions() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin menghapus semua opsi?",
                "Konfirmasi",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            options.clear();
            poll.updateOptions(new String[] {});
            refreshOptionButtons();
            refreshTable();
            chartPanel.repaint();
            JOptionPane.showMessageDialog(this, "Semua opsi telah dihapus!");
        }
    }

    private void resetAll() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Apakah Anda yakin ingin mereset semua data polling?\n" +
                        "Semua vote akan dihapus!",
                "Konfirmasi Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            poll.resetAllVotes();
            voter = new Voter("user1");
            refreshTable();
            chartPanel.repaint();
            JOptionPane.showMessageDialog(this, "✅ Semua data voting telah direset!");
        }
    }

    private void savePolling() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Simpan Polling");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                poll.saveToFile(fc.getSelectedFile());
                JOptionPane.showMessageDialog(this,
                        "✅ Data polling berhasil disimpan!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "❌ Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadPolling() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Load Polling");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                poll = Poll.loadFromFile(fc.getSelectedFile());
                options.clear();
                for (String s : poll.getOptions())
                    options.add(s);
                chartPanel.setPoll(poll);
                txtQuestion.setText(poll.getQuestion());
                refreshOptionButtons();
                refreshTable();
                JOptionPane.showMessageDialog(this,
                        "✅ Data polling berhasil dimuat!",
                        "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "❌ Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                MainApp app = new MainApp();
                app.setVisible(true);

                // Tambah icon jika tersedia
                // app.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

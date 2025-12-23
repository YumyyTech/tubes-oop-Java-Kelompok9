import java.io.*;
import java.util.*;

public class Poll implements Pollable {

    private String question;
    private String[] options;
    private int[] votes;

    public Poll(String question, String[] options) {
        this.question = question;
        this.options = options;
        this.votes = new int[options.length];
    }

    @Override
    public void addVote(int optionIndex) throws Exception {
        if (optionIndex < 0 || optionIndex >= options.length) {
            throw new Exception("Opsi tidak valid");
        }
        votes[optionIndex]++;
    }

    @Override
    public int getVoteCount(int optionIndex) {
        return votes[optionIndex];
    }

    @Override
    public String getQuestion() {
        return question;
    }

    @Override
    public String[] getOptions() {
        return options;
    }

    public int getTotalVotes() {
        int total = 0;
        for (int v : votes) total += v;
        return total;
    }

    public void updateOptions(String[] newOptions) {
        this.options = newOptions;
        this.votes = new int[newOptions.length];
    }

    public void resetAllVotes() {
        for (int i = 0; i < votes.length; i++) {
            votes[i] = 0;
        }
    }

    public void saveToFile(File file) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        bw.write(question);
        bw.newLine();
        for (int i = 0; i < options.length; i++) {
            bw.write(options[i] + "," + votes[i]);
            bw.newLine();
        }
        bw.close();
    }

    public static Poll loadFromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String q = br.readLine();

        List<String> opts = new ArrayList<>();
        List<Integer> vts = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            String[] p = line.split(",");
            opts.add(p[0]);
            vts.add(Integer.parseInt(p[1]));
        }
        br.close();

        Poll poll = new Poll(q, opts.toArray(new String[0]));
        for (int i = 0; i < vts.size(); i++) {
            poll.votes[i] = vts.get(i);
        }
        return poll;
    }
}
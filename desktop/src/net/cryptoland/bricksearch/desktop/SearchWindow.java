package net.cryptoland.bricksearch.desktop;

import net.cryptoland.bricksearch.Part;
import net.cryptoland.bricksearch.PartDatabase;
import net.cryptoland.bricksearch.SetDatabase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.IOException;
import java.util.List;

public class SearchWindow {
    private JTextField searchText;
    private JList resultList;
    private JPanel rootPanel;

    private PartDatabase partDB = new PartDatabase();
    private SetDatabase setDB = new SetDatabase();

    public SearchWindow() {
        try {
            partDB.loadCSV("pieces.csv");
            setDB.loadCSV("set_pieces.csv");
            partDB.loadSetsStatistics(setDB);
        } catch (IOException e) {
            e.printStackTrace();
        }
        searchText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SearchWindow.this.searchKeyTyped();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SearchWindow.this.searchKeyTyped();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SearchWindow.this.searchKeyTyped();
            }
        });
    }

    private void searchKeyTyped() {
        String query = searchText.getText();
        DefaultListModel<Part> model = new DefaultListModel<Part>();
        if (query.length() > 0) {
            List<Part> parts = partDB.textSearch(query);
            for (Part part: parts) {
                model.addElement(part);
            }
        }
        resultList.setModel(model);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("SearchWindow");
        frame.setContentPane(new SearchWindow().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

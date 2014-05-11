package net.cryptoland.bricksearch.desktop;

import net.cryptoland.bricksearch.Part;
import net.cryptoland.bricksearch.PartDatabase;
import net.cryptoland.bricksearch.SetDatabase;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class SearchWindow implements IImageViewer {
    private JTextField searchText;
    private JTable resultList;
    private JPanel rootPanel;

    private PartDatabase partDB = new PartDatabase();
    private SetDatabase setDB = new SetDatabase();
    private LoadPartsWorker currentWorker;
    private ImageCollection imageCollection;

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
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("mouse pressed twice!");
                    int row = resultList.getSelectedRow();
                    PartTableModel model = (PartTableModel) resultList.getModel();
                    if (row >= 0 && model != null) {
                        String id = model.getID(row);
                        PartWindow partWindow = new PartWindow(id, partDB, setDB, imageCollection);
                        partWindow.show();
                    }
                }
            }
        });
        resultList.setRowHeight(50);
        imageCollection = new ImageCollection(this);
    }

    public JFrame show() {
        JFrame frame = new JFrame("SearchWindow");
        frame.setContentPane(this.rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    private void searchKeyTyped() {
        String query = searchText.getText();
        if (currentWorker != null) {
            try {
                currentWorker.cancel(true);
            } catch (CancellationException e) {
                //e.printStackTrace();
            }
        }
        currentWorker = new LoadPartsWorker(query);
        currentWorker.execute();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new SearchWindow().show();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    private class LoadPartsWorker extends SwingWorker<List<Part>, Void> {
        private String query;

        public LoadPartsWorker(String query) {
            this.query = query;
        }

        @Override
        protected List<Part> doInBackground() throws Exception {
            return partDB.textSearch(query);
        }

        @Override
        protected void done() {
            try {
                List<Part> parts = get();
                TableModel model = new PartTableModel(parts, imageCollection, SearchWindow.this);
                resultList.setModel(model);
                resultList.getColumnModel().getColumn(0).setMaxWidth(100);
                resultList.getColumnModel().getColumn(1).setMaxWidth(60);
                resultList.getColumnModel().getColumn(2).setMaxWidth(60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showIcon(Icon icon, int row) {
        PartTableModel model = (PartTableModel) resultList.getModel();
        if (model != null) {
            model.updateIconCell(row);
        }
    }

    @Override
    public boolean isCellVisible(int rowIndex, int vColIndex) {
        if (!(resultList.getParent() instanceof JViewport)) {
            return false;
        }
        JViewport viewport = (JViewport) resultList.getParent();
        Rectangle rect = resultList.getCellRect(rowIndex, vColIndex, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        return new Rectangle(viewport.getExtentSize()).contains(rect);
    }
}

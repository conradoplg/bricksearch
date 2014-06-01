package net.cryptoland.bricksearch.desktop;

import net.cryptoland.bricksearch.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public class SearchWindow implements IImageViewer {
    private JTextField searchText;
    private JTable resultList;
    private JPanel rootPanel;
    private JCheckBox ownedCheck;
    private JComboBox colorCombo;

    private Database database;
    private LoadPartsWorker currentWorker;
    private ImageCollection imageCollection;

    public SearchWindow() {
        try {
            database = new Database();
        } catch (IOException e) {
            e.printStackTrace();
        }
        colorCombo.addItem("Any");
        for (String id: database.getSortedColorIDs()) {
            colorCombo.addItem(database.getColorDB().getDescription(id));
        }
        resultList.setRowHeight(50);
        imageCollection = new ImageCollection();
        setupListeners();
        HashSet forward = new HashSet(
                resultList.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS));
        forward.add(KeyStroke.getKeyStroke("TAB"));
        resultList.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forward);
        HashSet backward = new HashSet(
                resultList.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS));
        backward.add(KeyStroke.getKeyStroke("shift TAB"));
        resultList.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backward);
    }

    private void setupListeners() {
        searchText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SearchWindow.this.searchPart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SearchWindow.this.searchPart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                SearchWindow.this.searchPart();
            }
        });
        ownedCheck.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                SearchWindow.this.searchPart();
            }
        });
        colorCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchWindow.this.searchPart();
            }
        });
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = resultList.getSelectedRow();
                    PartTableModel model = (PartTableModel) resultList.getModel();
                    if (row >= 0 && model != null) {
                        String id = model.getID(row);
                        PartWindow partWindow = new PartWindow(id, database, imageCollection);
                        partWindow.show();
                    }
                }
            }
        });
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

    private void searchPart() {
        String query = searchText.getText();
        boolean owned = ownedCheck.isSelected();
        int colorSel = colorCombo.getSelectedIndex();
        String colorID = null;
        if (colorSel > 0) {
            colorID = database.getSortedColorIDs()[colorSel - 1];
        }
        if (currentWorker != null) {
            try {
                currentWorker.cancel(true);
            } catch (CancellationException e) {
                //e.printStackTrace();
            }
        }
        imageCollection.stopLoading();
        currentWorker = new LoadPartsWorker(query, owned, colorID);
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
        private boolean owned;
        private String colorID;

        public LoadPartsWorker(String query, boolean owned, String colorID) {
            this.query = query;
            this.owned = owned;
            this.colorID = colorID;
        }

        @Override
        protected List<Part> doInBackground() throws Exception {
            return database.searchPart(query, owned, colorID);
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

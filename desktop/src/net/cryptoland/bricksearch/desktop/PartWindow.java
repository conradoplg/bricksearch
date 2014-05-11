package net.cryptoland.bricksearch.desktop;

import net.cryptoland.bricksearch.Part;
import net.cryptoland.bricksearch.PartDatabase;
import net.cryptoland.bricksearch.SetDatabase;

import javax.swing.*;

public class PartWindow implements IImageViewer {
    private JLabel iconLabel;
    private JLabel nameLabel;
    private JPanel rootPanel;
    private String id;
    private PartDatabase partDB;
    private SetDatabase setDB;
    private ImageCollection imageCollection;

    public PartWindow(String id, PartDatabase partDB, SetDatabase setDB, ImageCollection imageCollection) {
        this.id = id;
        this.partDB = partDB;
        this.setDB = setDB;
        this.imageCollection = imageCollection;
        Part part = partDB.get(id);
        Icon icon = imageCollection.get(id, 0, this);
        if (icon != null) {
            showIcon(icon, 0);
        }
        nameLabel.setText(part.getDescription());
    }

    public JFrame show() {
        JFrame frame = new JFrame("PartWindow");
        frame.setContentPane(this.rootPanel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;
    }

    @Override
    public void showIcon(Icon icon, int row) {
        iconLabel.setIcon(icon);
    }

    @Override
    public boolean isCellVisible(int rowIndex, int vColIndex) {
        return true;
    }
}

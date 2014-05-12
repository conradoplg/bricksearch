package net.cryptoland.bricksearch.desktop;

import net.cryptoland.bricksearch.*;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class PartWindow implements IImageViewer {
    private JLabel iconLabel;
    private JLabel nameLabel;
    private JPanel rootPanel;
    private JTextPane ownershipPane;
    private JScrollPane ownershipPaneScroll;
    private String id;
    private PartDatabase partDB;
    private SetDatabase setDB;
    private ColorDatabase colorDB;
    private UserPartDatabase userPartDB;
    private ImageCollection imageCollection;

    public PartWindow(String id, PartDatabase partDB, SetDatabase setDB, ColorDatabase colorDB, UserPartDatabase userPartDB, ImageCollection imageCollection) {
        this.id = id;
        this.partDB = partDB;
        this.setDB = setDB;
        this.colorDB = colorDB;
        this.userPartDB = userPartDB;
        this.imageCollection = imageCollection;

        ownershipPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        Part part = partDB.get(id);
        Icon icon = imageCollection.get(id, 0, this);
        if (icon != null) {
            showIcon(icon, 0);
        }
        nameLabel.setText(part.getDescription());

        String ownershipText = "<html>";
        Map<String, List<SetPart>> ownershipData = userPartDB.getPartOwnershipData(id);
        for (String colorID: ownershipData.keySet()) {
            int count = 0;
            String colorText = "<ul>";
            for (SetPart setPart: ownershipData.get(colorID)) {
                count += setPart.getQuantity();
                colorText += "<li>" + setPart.getQuantity() + " in <a href=\"http://brickset.com/sets/" + setPart.getSetID() + "\">" + setPart.getSetID() + "</a></li>";
            }
            colorText += "</ul>";
            ownershipText += "<p>" + count + " in " + colorDB.getDescription(colorID) + "</p>";
            ownershipText += colorText;
        }
        ownershipText += "</html>";
        ownershipPane.setText(ownershipText);
        ownershipPane.setCaretPosition(0);
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

package net.cryptoland.bricksearch.desktop;

import javax.swing.*;

public interface IImageViewer {
    public void showIcon(Icon icon, int row);

    public boolean isCellVisible(int rowIndex, int vColIndex);
}

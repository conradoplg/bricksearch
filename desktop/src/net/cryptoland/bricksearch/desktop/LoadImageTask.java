package net.cryptoland.bricksearch.desktop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class LoadImageTask {
    public String id;
    public int row;
    public ImageIcon icon;

    LoadImageTask(String id, int row) {
        this.id = id;
        this.row = row;
    }

    public ImageIcon load() {
        try {
            URL url = new URL("http://rebrickable.com/img/pieces/15/" + id + ".png");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.131 Safari/537.36");
            BufferedImage img = ImageIO.read(connection.getInputStream());
            double factor = img.getHeight() / 50.0;
            BufferedImage small = new BufferedImage((int) (img.getWidth() / factor), 50, BufferedImage.TRANSLUCENT);
            Graphics2D g = small.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(img, 0, 0, small.getWidth(), small.getHeight(), null);
            g.dispose();
            icon = new ImageIcon(small);
            return icon;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

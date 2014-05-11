package net.cryptoland.bricksearch.desktop;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ImageCollection {
    private LoadImageWorker loadWorker;
    private BlockingDeque<LoadImageTask> loadDeque = new LinkedBlockingDeque<LoadImageTask>();
    private HashMap<String, Icon> images = new HashMap<String, Icon>();

    public ImageCollection(IImageViewer viewer) {
        loadWorker = new LoadImageWorker();
        loadWorker.execute();
    }

    public Icon get(String id, int row, IImageViewer viewer) {
        Icon icon = images.get(id);
        if (icon == null) {
            LoadImageTask task = new LoadImageTask(id, row, viewer);
            loadDeque.addFirst(task);
        }
        return icon;
    }

    private class LoadImageWorker extends SwingWorker<Void, LoadImageTask> {
        @Override
        protected Void doInBackground() throws Exception {
            while (!isCancelled()) {
                LoadImageTask task = loadDeque.takeFirst();
                if (task.load() != null) {
                    publish(task);
                }
            }
            return null;
        }

        @Override
        protected void process(List<LoadImageTask> chunks) {
            for (LoadImageTask task: chunks) {
                task.showIcon();
                images.put(task.id, task.icon);
            }
        }
    }
}

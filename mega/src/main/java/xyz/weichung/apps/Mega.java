package xyz.weichung.apps;

import xyz.weichung.libs.mega.*;
import xyz.weichung.libs.mega.Error;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class Mega {
    private static void createDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.exists()) {
            // TODO report failure to create directory
        }
    }

    private static void download(InputStream is, String path) throws Error, IOException {
        Path parent = Paths.get(path).getParent();
        createDirectory(parent.toString());

        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(path))) {
            byte[] buff = new byte[4096];
            while (true) {
                int read = is.read(buff);
                os.write(buff, 0, read);
                if (read < 4096) {
                    break;
                }
            }
        }
    }

    private static String nodePath(Map<String,Node> nodeMap, String outputDir, Node node) {
        String path = "";
        Node iter = node;
        do {
            path = Paths.get(iter.getName(), path).toString();
            iter = nodeMap.get(iter.getParent());
        } while (iter != null);
        path = Paths.get(outputDir, path).toString();
        return path;
    }

    public static void main(String[] args) throws Error {
        if (!(1 <= args.length && args.length <= 2)) {
            System.out.println("Usage: java Mega MEGA_URL [OUTPUT_DIR]");
            return;
        }

        String url = args[0];
        String outputDir;
        if (args.length == 2) {
            outputDir = args[1];
            createDirectory(outputDir);
        } else {
            outputDir = System.getProperty("user.dir");
        }

        Client client = new Client("gvgwwgxnih", "uQYSQToD");
        Node[] nodes = client.getNodesInShare(url);

        Map<String,Node> nodeMap = new HashMap<>();
        for (Node node: nodes) {
            try {
                nodeMap.put(node.getHash(), node);

                if (node.getType() == Type.FILE) {
                    // TODO report download in progress
                    String path = nodePath(nodeMap, outputDir, node);
                    InputStream is = client.download(node);
                    download(is, path);
                    // TODO report download succeeds
                }
            } catch (Error | IOException e) {
                // TODO report failure to download
                e.printStackTrace();
            }
        }
    }
}

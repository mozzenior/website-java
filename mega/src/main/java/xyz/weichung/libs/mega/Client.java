package xyz.weichung.libs.mega;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public final class Client {
    private String id;
    private String key;

    public Client(String id, String key) {
        this.id = id;
        this.key = key;
    }

    String getId() {
        return id;
    }

    String getKey() {
        return key;
    }

    private void updateTemporaryLink(Node node) throws Error {
        try {
            Share share = node.getShare();
            String req = share.request(node);

            HttpURLConnection conn = share.getConnection(this);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(req.length()));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream())));
            writer.print(req);
            writer.close();

            if (conn.getResponseCode() != 200) {
                throw Error.format("MEGA returns code %d for node %s", conn.getResponseCode(), share.getHash());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = reader.readLine();
            reader.close();

            share.updateNode(res, node);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public InputStream download(Node node) throws Error {
        try {
            if (node.getTemporaryLink() == null) {
                updateTemporaryLink(node);
            }

            URL url = new URL(node.getTemporaryLink());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/octet-stream");
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.connect();

            if (conn.getResponseCode() != 200) {
                throw Error.format("MEGA returns code %d for node %s", conn.getResponseCode(), node.getHash());
            }

            return node.getKey().decryptInputStream(conn.getInputStream());
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public Node[] getNodesInShare(String url) throws Error {
        try {
            Share share = Share.create(url);
            String req = share.request();

            HttpURLConnection conn = share.getConnection(this);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(req.length()));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();

            PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(conn.getOutputStream())));
            writer.print(req);
            writer.close();

            if (conn.getResponseCode() != 200) {
                throw Error.format("MEGA returns code %d for node %s", conn.getResponseCode(), share.getHash());
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String res = reader.readLine();
            reader.close();

            return share.getNodes(res);
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}

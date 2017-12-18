package xyz.weichung.libs.mega;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class FileShare extends Share {
    static class Request {
        String a;
        String p;
        Integer g;

        Request(FileShare share) {
            a = "g";
            g = 1;
            p = share.hash;
        }
    }

    FileShare(String hash, String key) throws Error {
        super(hash, key, Type.FILE);
    }

    @Override
    HttpURLConnection getConnection(Client client) throws Error {
        try {
            URL url = new URL(String.format("https://g.api.mega.co.nz/cs?id=%s&ak=%s", client.getId(), client.getKey()));
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    Node[] getNodes(String json) throws Error {
        Gson gson = new Gson();
        NodeResponse[] ress = gson.fromJson(json, NodeResponse[].class);
        if (ress == null || ress.length != 1) {
            throw Error.format("%s is not a valid response", json);
        }

        Node[] nodes = new Node[1];
        nodes[0] = ress[0].toNode(this);
        return nodes;
    }

    @Override
    String request() {
        Gson gson = new Gson();
        return gson.toJson(new Request[] {new Request(this)});
    }

    @Override
    String request(Node node) {
        return request();
    }
}

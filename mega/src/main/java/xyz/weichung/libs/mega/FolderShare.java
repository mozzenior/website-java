package xyz.weichung.libs.mega;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class FolderShare extends Share {
    static class Request {
        String a;

        Request() {
            a = "f";
        }
    }

    static class NodeRequest extends Request {
        Integer g;
        String n;

        NodeRequest() {
            super();
        }

        NodeRequest(Node node) {
            super();
            a = "g";
            g = 1;
            n = node.getHash();
        }
    }

    static class Response {
        NodeResponse[] f;
        String sn;
        Integer noc;

        Response() {
        }

        Node[] getNodes(FolderShare share) throws Error {
            if (f == null || f.length == 0) throw new Error("MEGA returns empty response");
            Node[] nodes = new Node[f.length];
            nodes[0] = f[0].toNode(share);
            for (int i = 1; i < f.length; i++) {
                try {
                    nodes[i] = f[i].toNode(share);
                } catch (Error e) {
                    nodes[i] = Node.NULL;
                }
            }
            return nodes;
        }
    }

    FolderShare(String hash, String key) throws Error {
        super(hash, key, Type.FOLDER);
    }

    @Override
    HttpURLConnection getConnection(Client client) throws Error {
        try {
            URL url = new URL(String.format("https://g.api.mega.co.nz/cs?id=%s&ak=%s&n=%s", client.getId(), client.getKey(), hash));
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Override
    Node[] getNodes(String json) throws Error {
        Gson gson = new Gson();
        Response[] ress = gson.fromJson(json, Response[].class);
        if (ress == null || ress.length != 1) {
            throw Error.format("%s is not a valid response", json);
        }

        return ress[0].getNodes(this);
    }

    @Override
    String request() {
        Gson gson = new Gson();
        return gson.toJson(new Request[] {new Request()});
    }

    @Override
    String request(Node node) {
        Gson gson = new Gson();
        return gson.toJson(new NodeRequest[] {new NodeRequest(node)});
    }
}

package xyz.weichung.libs.mega;

import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

abstract public class Share {
    static class NodeResponse {
        String a;
        String at;
        String c;
        Integer fav;
        String g;
        String h;
        String k;
        Integer msd;
        String n;
        String p;
        String u;
        Long s;
        Integer t;
        Long ts;

        NodeResponse() {
        }

        private void decryptAttribute(Node node, Key key, String a) throws Error {
            String attr = key.decryptAttributes(a);
            if (!attr.startsWith("MEGA")) {
                throw Error.format("%s is not valid attribute", attr);
            }
            String json = attr.substring(4);
            NodeResponse res = (new Gson()).fromJson(json, NodeResponse.class);
            res.toNode(node);
        }

        Key decryptKey(Share share, String str) throws Error {
            int idx = str.indexOf(':');
            if (idx == -1 || idx == str.length()-1) {
                throw Error.format("%s is not a valid key", str);
            }
            str = str.substring(idx+1);
            return share.getKey().decryptKey(str);
        }

        Node toNode(Share share) throws Error {
            Node node = new Node(share);
            node.setHash(share.getHash());
            node.setKey(share.getKey());
            node.setType(share.getType());
            return toNode(node);
        }

        Node toNode(Node node) throws Error {
            if (at != null) decryptAttribute(node, node.getKey() != null ? node.getKey() : node.getShare().getKey(), at);
            if (c != null) node.setFingerprint(c);
            if (g != null) node.setTemporaryLink(g);
            if (h != null) node.setHash(h);
            if (k != null) node.setKey(decryptKey(node.getShare(), k));
            if (n != null) node.setName(n);
            if (p != null) node.setParent(p);
            if (u != null) node.setOwner(u);
            if (s != null) node.setSize(s);
            if (t != null) {
                switch (t) {
                    case 0: node.setType(Type.FILE); break;
                    case 1: node.setType(Type.FOLDER); break;
                    case 2: node.setType(Type.LINK); break;
                }
            }
            if (ts != null) node.setTimestamp(ts);
            if (a != null) decryptAttribute(node, node.getKey(), a);
            return node;
        }
    }

    protected String hash;
    protected Key key;
    protected Type type;

    Share(String hash, String key, Type type) throws Error {
        this.hash = hash;
        this.key = new Key(key);
        this.type = type;
    }

    static Share create(String sUrl) throws Error {
        try {
            URL url = new URL(sUrl);
            if (!url.getProtocol().equals("https")) {
                throw Error.format("%s is not a valid MEGA URL", sUrl);
            }
            if (!url.getHost().equals("mega.nz")) {
                throw Error.format("%s is not a valid MEGA URL", sUrl);
            }

            String ref = url.getRef();
            if (ref == null) {
                throw Error.format("%s is not a valid MEGA URL", sUrl);
            }
            int idx1 = ref.indexOf('!');
            int idx2 = ref.lastIndexOf('!');
            if (idx1 == -1 || idx2 == -1) {
                throw Error.format("%s is not a valid MEGA URL", sUrl);
            }
            if (idx1 == 1 && ref.charAt(0) != 'F') {
                throw Error.format("%s is not a valid MEGA URL", sUrl);
            }

            String hash = ref.substring(ref.indexOf('!')+1, ref.lastIndexOf('!'));
            String key = ref.substring(ref.lastIndexOf('!')+1);
            Type type = (ref.charAt(0) == '!') ? Type.FILE : Type.FOLDER;

            switch (type) {
                case FILE: return new FileShare(hash, key);
                case FOLDER: return new FolderShare(hash, key);
                default: throw new Error("unsupported Node type");
            }
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
    }

    abstract HttpURLConnection getConnection(Client client) throws Error;

    String getHash() {
        return hash;
    }

    Key getKey() {
        return key;
    }

    Type getType() {
        return type;
    }

    abstract Node[] getNodes(String json) throws Error;

    abstract String request();

    abstract String request(Node node);

    void setHash(String hash) {
        this.hash = hash;
    }

    void setKey(Key key) {
        this.key = key;
    }

    void setType(Type type) {
        this.type = type;
    }

    void updateNode(String json, Node node) throws Error {
        Gson gson = new Gson();
        NodeResponse[] ress = gson.fromJson(json, NodeResponse[].class);
        if (ress == null || ress.length != 1) {
            throw Error.format("%s is not a valid response", json);
        }

        ress[0].toNode(node);
    }
}

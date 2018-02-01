package xyz.weichung.libs.mega;

public final class Node {
    private Share share;
    private String fingerprint;
    private String hash;
    private Key key;
    private String name;
    private String owner;
    private String parent;
    private long size;
    private String temporaryLink;
    private long timestamp;
    private Type type;

    static final Node NULL = new Node(null);

    Node(Share share) {
        this.share = share;
    }

    String getFingerprint() {
        return fingerprint;
    }

    public String getHash() {
        return hash;
    }

    Key getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    Share getShare() {
        return share;
    }

    long getSize() {
        return size;
    }

    String getTemporaryLink() {
        return temporaryLink;
    }

    public Type getType() {
        return type;
    }

    void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    void setHash(String hash) {
        this.hash = hash;
    }

    void setKey(Key key) {
        this.key = key;
    }

    void setName(String name) {
        this.name = name;
    }

    void setOwner(String owner) {
        this.owner = owner;
    }

    void setParent(String parent) {
        this.parent = parent;
    }

    void setShare(Share share) {
        this.share = share;
    }

    void setSize(Long size) {
        this.size = size;
    }

    void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    void setTemporaryLink(String temporaryLink) {
        this.temporaryLink = temporaryLink;
    }

    void setType(Type type) {
        this.type = type;
    }
}

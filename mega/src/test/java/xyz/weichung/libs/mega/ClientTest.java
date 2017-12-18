package xyz.weichung.libs.mega;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ClientTest {
    @Test
    void download_file() throws Error {
        Client client = new Client("gvgwwgxnih", "uQYSQToD");
        Node[] nodes = client.getNodesInShare("https://mega.nz/#!nFIlDYqa!UqLcVX1iws_tDpsNa3j_-tLkwEZrC-K37J7_IHxDJvE");

        Node node = nodes[0];
        InputStream is = client.download(node);
        byte[] buff = new byte[16];
        int nRead = is.read(buff);
        Assertions.assertEquals(4, nRead);
        Assertions.assertEquals("123\n", new String(buff, 0, 4));
    }

    @Test
    void download_folder() throws Error {
        Client client = new Client("gvgwwgxnih", "uQYSQToD");
        Node[] nodes = client.getNodesInShare("https://mega.nz/#F!eZRm1TKQ!1n3xClNDXsqeumDF9I9KFg");

        Node node = nodes[1];
        InputStream is = client.download(node);
        byte[] buff = new byte[64];
        int nRead = is.read(buff);
        Assertions.assertEquals(34, nRead);
        Assertions.assertEquals("0123456789ABCDEF=FEDCBA9876543210\n", new String(buff, 0, 34));
    }

    @Test
    void resolve_fileUrl() throws Error {
        Client client = new Client("gvgwwgxnih", "uQYSQToD");
        Node[] nodes = client.getNodesInShare("https://mega.nz/#!nFIlDYqa!UqLcVX1iws_tDpsNa3j_-tLkwEZrC-K37J7_IHxDJvE");
        Assertions.assertEquals(1, nodes.length);

        Assertions.assertEquals("測試.txt", nodes[0].getName());
        Assertions.assertEquals(4, nodes[0].getSize());
        Assertions.assertEquals(Type.FILE, nodes[0].getType());
        Assertions.assertNotNull(nodes[0].getTemporaryLink());
        Assertions.assertNotNull(nodes[0].getKey());
        Assertions.assertEquals("nFIlDYqa", nodes[0].getHash());
    }

    @Test
    void resolve_folderUrl() throws Error {
        Client client = new Client("gvgwwgxnih", "uQYSQToD");
        Node[] nodes = client.getNodesInShare("https://mega.nz/#F!eZRm1TKQ!1n3xClNDXsqeumDF9I9KFg");
        Assertions.assertEquals(6, nodes.length);

        Assertions.assertNotNull(nodes[0].getShare());
        Assertions.assertEquals("HelloWorld", nodes[0].getName());
        Assertions.assertEquals(0, nodes[0].getSize());
        Assertions.assertEquals(Type.FOLDER, nodes[0].getType());
        Assertions.assertNull(nodes[0].getTemporaryLink());
        Assertions.assertNotNull(nodes[0].getKey());
        Assertions.assertEquals("3cAH0CaT", nodes[0].getHash());

        Assertions.assertNotNull(nodes[1].getShare());
        Assertions.assertEquals("test.txt", nodes[1].getName());
        Assertions.assertEquals(34, nodes[1].getSize());
        Assertions.assertEquals(Type.FILE, nodes[1].getType());
        Assertions.assertNull(nodes[1].getTemporaryLink());
        Assertions.assertNotNull(nodes[1].getKey());
        Assertions.assertEquals("TRpAFLID", nodes[1].getHash());
    }

    @Test
    void resolve_invalidUrl() {
        Client client = new Client("gvgwwgxnih", "uQYSQToD");
        Assertions.assertThrows(Error.class, () -> client.getNodesInShare("https://example.com"));
    }
}
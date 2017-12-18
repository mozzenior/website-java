package xyz.weichung.libs.mega;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

class KeyTest {
    @Test
    void constructor_fileKey() throws Error {
        new Key("UqLcVX1iws_tDpsNa3j_-tLkwEZrC-K37J7_IHxDJvE");
    }

    @Test
    void constructor_folderKey() throws Error {
        new Key("A1YeVy1Ga6X9bCYCS_lAyA");
    }

    @Test
    void constructor_invalidKey() {
        Assertions.assertThrows(Error.class, () -> new Key("abcdefg"));
    }

    @Test
    void decryptAttributes() throws Error {
        Key fileKey = new Key("UqLcVX1iws_tDpsNa3j_-tLkwEZrC-K37J7_IHxDJvE");
        String attrs = fileKey.decryptAttributes("tkNfqa7roCsZh8n5HA95F4n6FFkTVtiWNA1q1MArlU8LUT7JXoB7DW3wiM57H22eN6LY_MuisZ2CqxAfKdq8xw");
        Assertions.assertEquals("MEGA{\"n\":\"測試.txt\",\"c\":\"MTIzCgAAAAAAAAAAAAAAAAT6K65W\"}", attrs);
    }

    @Test
    void decryptInputStream() throws Error {
        java.io.InputStream is = new ByteArrayInputStream(new byte[] {(byte) 0x77, (byte) 0x8a, (byte) 0xaa, (byte) 0x80});
        Key fileKey = new Key("UqLcVX1iws_tDpsNa3j_-tLkwEZrC-K37J7_IHxDJvE");
        InputStream nis = fileKey.decryptInputStream(is);
        byte[] plaintext = new byte[16];
        int nRead = nis.read(plaintext);
        Assertions.assertEquals(4, nRead);
        String content = new String(plaintext, 0, nRead);
        Assertions.assertEquals("123\n", content);
    }

    @Test
    void decryptKey_validKey() throws Error {
        Key folderKey = new Key("A1YeVy1Ga6X9bCYCS_lAyA");
        Key fileKey = folderKey.decryptKey("t6f5l0LXFMbA7i_B8cHMKA");
        Assertions.assertNotNull(fileKey);
        Assertions.assertEquals("IYo0a3VRkzGsWHTrUOL1nQ==", fileKey.toString());
    }

    @Test
    void decryptKey_invalidKey() throws Error {
        Key folderKey = new Key("A1YeVy1Ga6X9bCYCS_lAyA");
        Assertions.assertThrows(Error.class, () -> folderKey.decryptKey("abcdefg"));
    }
}
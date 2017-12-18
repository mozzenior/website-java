package xyz.weichung.libs.mega;

import javax.crypto.Cipher;
import java.io.IOException;

public final class InputStream {
    private java.io.InputStream is;
    private Cipher cipher;
    private byte[] ctr;
    private byte[] buff;
    private int available;
    private int offset;

    InputStream(java.io.InputStream is, Cipher cipher, byte[] ctr) {
        if (ctr.length != 16) {
            throw new IllegalArgumentException("Argument ctr's length is not 16");
        }

        this.is = is;
        this.cipher = cipher;
        this.ctr = ctr;
        buff = new byte[16];
        available = 0;
        offset = 0;
    }

    private void decrypt() {
        // decrypt
        byte[] tmp = cipher.update(ctr);
        for (int i = 0; i < buff.length; i++) {
            buff[i] ^= tmp[i];
        }

        // prepare for next time of decryption
        for (int i = 15; 0 <= i; i--) {
            ctr[i]++;
            if (ctr[i] != 0) {
                break;
            }
        }
    }

    public int read(byte[] b) throws Error {
        try {
            int nRead = 0;
            int len = b.length;
            int off = 0;

            while (0 < len) {
                if (available == offset) {
                    available = is.readNBytes(buff, 0, buff.length);
                    offset = 0;
                    decrypt();
                }
                if (available == 0) {
                    break;
                }

                int nCopy = Math.min(available-offset, len);
                System.arraycopy(buff, offset, b, off, nCopy);
                offset += nCopy;
                off += nCopy;
                len -= nCopy;
                nRead += nCopy;
            }

            return nRead;
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}

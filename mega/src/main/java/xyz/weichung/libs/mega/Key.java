package xyz.weichung.libs.mega;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class Key {
    private static final IvParameterSpec ZEROIV = new IvParameterSpec(new byte[16]);

    private byte[] key;
    private byte[] ctriv;
    private byte[] mac;

    Key(String str) throws Error {
        byte[] data;
        try {
            data = Base64.getUrlDecoder().decode(str);
        } catch (IllegalArgumentException e) {
            throw Error.format(e, "%s is not a valid key", str);
        }

        if (data.length == 16) {
            key = data;
        } else if (data.length == 32) {
            key = new byte[16];
            ctriv = new byte[16];
            mac = new byte[8];

            for (int i = 0; i < 16; i++) {
                key[i] = (byte) (data[i] ^ data[16+i]);
            }

            System.arraycopy(data, 16, ctriv, 0, 8);
            System.arraycopy(data, 24, mac, 0, 8);
        } else {
            throw Error.format("%s is not a valid key", str);
        }
    }

    String decryptAttributes(String str) throws Error {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ZEROIV);
            byte[] plaintext = cipher.update(Base64.getUrlDecoder().decode(str));
            String attrs = new String(plaintext);
            int idx = attrs.indexOf('\0');
            return (idx != -1) ? attrs.substring(0, idx) : attrs;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new Error(e);
        } catch (InvalidKeyException e) {
            throw Error.format(e, "%s is not a valid key", str);
        }
    }

    InputStream decryptInputStream(java.io.InputStream is) throws Error {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] ctr = new byte[16];
            System.arraycopy(ctriv, 0, ctr, 0, ctriv.length);
            return new InputStream(is, cipher, ctr);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new Error(e);
        }
    }

    Key decryptKey(String str) throws Error {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] plaintext = cipher.update(Base64.getUrlDecoder().decode(str));
            String plainkey = Base64.getUrlEncoder().encodeToString(plaintext);
            return new Key(plainkey);
        } catch (IllegalArgumentException e) {
            throw Error.format(e, "%s is not a valid key", str);
        } catch (InvalidKeyException e) {
            throw Error.format(e, "%s is not a valid key", toString());
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new Error(e);
        }
    }

    @Override
    public String toString() {
        return Base64.getUrlEncoder().encodeToString(key);
    }
}

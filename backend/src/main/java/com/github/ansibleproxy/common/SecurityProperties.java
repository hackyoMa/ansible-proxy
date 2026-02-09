package com.github.ansibleproxy.common;

import com.github.ansibleproxy.util.EncryptUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

/**
 * SecurityProperties
 *
 * @author hackyo
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties("security")
public class SecurityProperties {

    private String[] allWhitelist;
    private Map<HttpMethod, String[]> whitelist;
    private Secret secret;

    @Data
    public static class Secret {
        public static final String JWS_ALGORITHM = "RS256";
        private static final String ALGORITHM = "RSA";
        private static final int KEY_SIZE = 2048;

        private String keyId;
        private PrivateKey privateKey;
        private PublicKey publicKey;

        public Secret(Path privateKey, Path publicKey) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
            if (!Files.exists(privateKey) || !Files.exists(publicKey)) {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
                keyPairGenerator.initialize(KEY_SIZE);
                KeyPair keyPair = keyPairGenerator.generateKeyPair();
                this.privateKey = keyPair.getPrivate();
                this.publicKey = keyPair.getPublic();
                writeKey(privateKey, this.privateKey.getEncoded(), KeyType.PRIVATE);
                writeKey(publicKey, this.publicKey.getEncoded(), KeyType.PUBLIC);
                if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
                    Files.setPosixFilePermissions(privateKey, PosixFilePermissions.fromString("rw-------"));
                }
            } else {
                KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
                this.privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(loadKey(privateKey)));
                this.publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(loadKey(publicKey)));
            }
            this.keyId = EncryptUtil.blake3(this.publicKey.getEncoded());
        }

        private static byte[] loadKey(Path path) throws IOException {
            return EncryptUtil.base64ToBytes(Files.readString(path)
                    .replaceAll(".*?-----", "")
                    .replaceAll("\\s+", ""));
        }

        private static void writeKey(Path path, byte[] key, KeyType type) throws IOException {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            String keyString = type.getHeader() + "\n"
                    + EncryptUtil.bytesToBase64(key, 64) + "\n"
                    + type.getFooter() + "\n";
            Files.writeString(path, keyString, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        }

        @Getter
        @AllArgsConstructor
        private enum KeyType {
            PRIVATE("-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----"),
            PUBLIC("-----BEGIN PUBLIC KEY-----", "-----END PUBLIC KEY-----");

            private final String header;
            private final String footer;
        }
    }

}

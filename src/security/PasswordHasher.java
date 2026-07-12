package security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordHasher {
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final String IDENTIFIER = "pbkdf2_sha256";
    private static final String PREFIX = IDENTIFIER + "$";
    private static final int ITERATION_COUNT = 600_000;
    private static final int MIN_ACCEPTED_ITERATIONS = 100_000;
    private static final int MAX_ACCEPTED_ITERATIONS = 10_000_000;
    private static final int SALT_LENGTH_BYTES = 16;
    private static final int HASH_LENGTH_BYTES = 32;
    private static final int HASH_LENGTH_BITS = HASH_LENGTH_BYTES * Byte.SIZE;
    private static final int ENCODED_SALT_LENGTH = 24;
    private static final int ENCODED_HASH_LENGTH = 44;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(char[] password) {
        if (password == null || password.length == 0) {
            throw new IllegalArgumentException("비밀번호가 비어 있습니다.");
        }

        byte[] salt = new byte[SALT_LENGTH_BYTES];
        byte[] derivedKey = null;
        SECURE_RANDOM.nextBytes(salt);

        try {
            derivedKey = deriveKey(password, salt, ITERATION_COUNT);
            return IDENTIFIER + "$" + ITERATION_COUNT + "$"
                    + Base64.getEncoder().encodeToString(salt) + "$"
                    + Base64.getEncoder().encodeToString(derivedKey);
        } finally {
            Arrays.fill(salt, (byte) 0);
            if (derivedKey != null) {
                Arrays.fill(derivedKey, (byte) 0);
            }
        }
    }

    public static boolean verify(char[] password, String storedValue) {
        if (password == null || password.length == 0 || !hasRecognizedPrefix(storedValue)) {
            return false;
        }

        ParsedHash parsed = parse(storedValue);
        if (parsed == null) {
            return false;
        }

        byte[] calculatedHash = null;
        try {
            calculatedHash = deriveKey(password, parsed.salt, parsed.iterationCount);
            return MessageDigest.isEqual(parsed.hash, calculatedHash);
        } finally {
            parsed.clear();
            if (calculatedHash != null) {
                Arrays.fill(calculatedHash, (byte) 0);
            }
        }
    }

    public static boolean isEncoded(String storedValue) {
        ParsedHash parsed = parse(storedValue);
        if (parsed == null) {
            return false;
        }
        parsed.clear();
        return true;
    }

    public static boolean hasRecognizedPrefix(String storedValue) {
        return storedValue != null && storedValue.startsWith(PREFIX);
    }

    public static boolean needsUpgrade(String storedValue) {
        ParsedHash parsed = parse(storedValue);
        if (parsed == null) {
            return true;
        }

        try {
            return parsed.iterationCount < ITERATION_COUNT;
        } finally {
            parsed.clear();
        }
    }

    private static byte[] deriveKey(char[] password, byte[] salt, int iterationCount) {
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterationCount, HASH_LENGTH_BITS);
        try {
            return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(keySpec).getEncoded();
        } catch (Exception e) {
            throw new IllegalStateException("비밀번호 해시 처리에 실패했습니다.", e);
        } finally {
            keySpec.clearPassword();
        }
    }

    private static ParsedHash parse(String storedValue) {
        if (!hasRecognizedPrefix(storedValue)) {
            return null;
        }

        String[] fields = storedValue.split("\\$", -1);
        if (fields.length != 4 || !IDENTIFIER.equals(fields[0])) {
            return null;
        }
        if (!fields[1].matches("[0-9]{1,8}")
                || fields[2].length() != ENCODED_SALT_LENGTH
                || fields[3].length() != ENCODED_HASH_LENGTH) {
            return null;
        }

        int iterationCount;
        try {
            iterationCount = Integer.parseInt(fields[1]);
        } catch (NumberFormatException e) {
            return null;
        }
        if (iterationCount < MIN_ACCEPTED_ITERATIONS || iterationCount > MAX_ACCEPTED_ITERATIONS) {
            return null;
        }

        byte[] salt = null;
        byte[] hash = null;
        try {
            salt = Base64.getDecoder().decode(fields[2]);
            hash = Base64.getDecoder().decode(fields[3]);
            if (salt.length != SALT_LENGTH_BYTES || hash.length != HASH_LENGTH_BYTES) {
                Arrays.fill(salt, (byte) 0);
                Arrays.fill(hash, (byte) 0);
                return null;
            }
            return new ParsedHash(iterationCount, salt, hash);
        } catch (IllegalArgumentException e) {
            if (salt != null) {
                Arrays.fill(salt, (byte) 0);
            }
            if (hash != null) {
                Arrays.fill(hash, (byte) 0);
            }
            return null;
        }
    }

    private static final class ParsedHash {
        private final int iterationCount;
        private final byte[] salt;
        private final byte[] hash;

        private ParsedHash(int iterationCount, byte[] salt, byte[] hash) {
            this.iterationCount = iterationCount;
            this.salt = salt;
            this.hash = hash;
        }

        private void clear() {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(hash, (byte) 0);
        }
    }
}

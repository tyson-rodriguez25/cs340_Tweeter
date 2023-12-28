package edu.byu.cs.tweeter.server.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

public class SaltedSHAHashing {

    public String saltedPassword(String nonSecurePassword) {
        // Store this in the database
        String salt = getSalt();
        //System.out.println("Salt: " + salt);

        // Store this in the database
        String securePassword = getSecurePassword(nonSecurePassword, salt);
        //System.out.println("Secured Password: " + securePassword);

        // Given at login
        //String suppliedPassword = "password";
        //String regeneratedPasswordToVerify = getSecurePassword(suppliedPassword, salt);
        //System.out.println("Regenerated Password: " + regeneratedPasswordToVerify);

        //System.out.println("Passwords are the same: " + securePassword.equals(regeneratedPasswordToVerify));
        return securePassword;
    }

    public String getSecurePassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH PASSWORD";
    }

    public String getSalt() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
            byte[] salt = new byte[16];
            sr.nextBytes(salt);
            return Base64.getEncoder().encodeToString(salt);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            e.printStackTrace();
        }
        return "FAILED TO GET SALT";
    }
}

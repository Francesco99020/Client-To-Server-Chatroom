import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureUtils {

    public static String getSecurePassword(String password, byte[] salt) {

        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static byte[] getSalt(){
        String saltString = "[B@35f983a6";
        return saltString.getBytes();
    }

    // private static byte[] getSalt() throws NoSuchAlgorithmException {
    //     SecureRandom random = new SecureRandom();
    //     byte[] salt = new byte[16];
    //     random.nextBytes(salt);
    //     System.out.println("salt -> " + salt);
    //     return salt;
    // }

    public static boolean validatePassword(String password, String passwordAttempt) throws NoSuchAlgorithmException{
        // same salt should be passed
        String password1 = getSecurePassword(password, getSalt());
        String password2 = getSecurePassword(passwordAttempt, getSalt());
        if (password1.equals(password2)) {
            return true;
        }else{
            return false;
        }
    }

    public static boolean VerifyPassword(String password, String passwordAttempt){
        String encryptedPasswordAttempt = getSecurePassword(passwordAttempt, getSalt());
        System.out.println("password Attempt: " + encryptedPasswordAttempt);
        System.out.println("Password: " + password);
        if(password.equals(encryptedPasswordAttempt)) return true;
        else return false;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    public static void main(String[] args) throws NoSuchAlgorithmException {
        /*
         * password = Password (capital P)
         * salt = [B@35f983a6
         */
    }
}
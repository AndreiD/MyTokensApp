package mytoken.mytokenapp.utils;

public class SecurityHolder {
    public static String publicAddress = "";
    public static String lastScanAddress = "";

    // Using a rooted phone is not recommended
    //public static void storeContacts(Context ctx, String contacts) {
    //    try {
    //        PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
    //        AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, DUtils.getUniqueID());
    //        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(contacts, key, "UTF-8");
    //        preferencesHelper.setContacts(cipherTextIvMac.toString());
    //    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
    //        e.printStackTrace();
    //    }
    //}
    //
    //public static String getContacts(Context ctx) {
    //    try {
    //        PreferencesHelper preferencesHelper = new PreferencesHelper(ctx);
    //        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = new AesCbcWithIntegrity.CipherTextIvMac(preferencesHelper.getContacts());
    //        AesCbcWithIntegrity.SecretKeys key = AesCbcWithIntegrity.generateKeyFromPassword(SecurityHolder.pin, DUtils.getUniqueID());
    //        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, key);
    //    } catch (GeneralSecurityException | UnsupportedEncodingException e) {
    //        e.printStackTrace();
    //    }
    //    return null;
    //}
}

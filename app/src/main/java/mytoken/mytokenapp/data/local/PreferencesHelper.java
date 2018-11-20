package mytoken.mytokenapp.data.local;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Almost all values are encrypted
 */
@SuppressLint("ApplySharedPref")
public class PreferencesHelper {

  private static final String PREF_FILE_NAME = "myapp_shared_prefs";
  private static final String KEY_PIN_CREATED = "KEY_PIN_CREATED";
  private static final String KEY_SEED = "KEY_SEED";
  private static final String KEY_ADDRESS = "KEY_ADDRESS";
  private static final String KEY_PASSWORD = "KEY_PASSWORD";
  private static final String KEY_PIN = "KEY_PIN";
  private static final String KEY_CONFIRMED_TOS = "KEY_CONFIRMED_TOS";
  private static final String KEY_INVALID_PINS = "KEY_INVALID_PINS";
  private static final String KEY_DEFAULT_TOKEN = "KEY_DEFAULT_TOKEN";
  private static SharedPreferences mPref;

  public PreferencesHelper(Context context) {
    mPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
  }

  public void clear() {
    mPref.edit().clear().apply();
  }



  public boolean getPinCreated() {
    return mPref.getBoolean(KEY_PIN_CREATED, false);
  }

  public void setPinCreated(boolean pinCreated) {
    mPref.edit().putBoolean(KEY_PIN_CREATED, pinCreated).commit();
  }

  public boolean getConfirmedTos() {
    return mPref.getBoolean(KEY_CONFIRMED_TOS, false);
  }

  public void setConfirmedTos(boolean confirmedTos) {
    mPref.edit().putBoolean(KEY_CONFIRMED_TOS, confirmedTos).commit();
  }

  public int getInvalidPins() {
    return mPref.getInt(KEY_INVALID_PINS, 0);
  }

  public void setInvalidPins(int invalidPins) {
    mPref.edit().putInt(KEY_INVALID_PINS, invalidPins).commit();
  }

  public String getAddress() {
    return mPref.getString(KEY_ADDRESS, null);
  }

  public void setAddress(String addr) {
    mPref.edit().putString(KEY_ADDRESS, addr).commit();
  }

  public String getSeed() {
    return mPref.getString(KEY_SEED, "");
  }

  public void setSeed(String seed) {
    mPref.edit().putString(KEY_SEED, seed).commit();
  }

  public String getPassword() {
    return mPref.getString(KEY_PASSWORD, "");
  }

  public void setPassword(String password) {
    mPref.edit().putString(KEY_PASSWORD, password).commit();
  }

  public String getPIN() {
    return mPref.getString(KEY_PIN, "");
  }

  public void setPIN(String encryptedPin) {
    mPref.edit().putString(KEY_PIN, encryptedPin).commit();
  }

  public String getDefaultToken() {
    return mPref.getString(KEY_DEFAULT_TOKEN, null);
  }

  public void setDefaultToken(String address) {
    mPref.edit().putString(KEY_DEFAULT_TOKEN, address).commit();
  }
}


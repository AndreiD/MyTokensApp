package mytoken.mytokenapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Constants {
  // development address:
  // 0x06205f4394eeadbe3d5046aa570f6cb8e30bccf1 =
  // fuel dune marriage parent fever spike close clog test hip final demise
  public static final String CONTRACT_ADDRESS = "0x3758a452fd5139db395fdb7253c919d26086a378";
  public static final BigInteger DEFAULT_GAS_LIMIT = new BigInteger("200000");
  public static final BigInteger CHEAP_GAS_PRICE = new BigInteger("4100000000");
  public static final BigInteger NORMAL_GAS_PRICE = new BigInteger("8600000000");
  public static final BigInteger FAST_GAS_PRICE = new BigInteger("16300000000");
  private Constants() {
  }

  public static BigInteger getGasPrice(Context ctx) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String feePreference = prefs.getString("fee_preference", "");

    switch (feePreference) {
      case "cheap":
        return CHEAP_GAS_PRICE;
      case "normal":
        return NORMAL_GAS_PRICE;
      case "fast":
        return FAST_GAS_PRICE;

      default:
        return NORMAL_GAS_PRICE;
    }
  }

  public static String getBlockchanPreference(Context ctx) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String networkPreference = prefs.getString("network_preference", "");

    final String INFURA_KEY = "2wIEW5lLyEaefGEPhoxX"; // please don't use it since you can get one free
    switch (networkPreference) {
      case "mainnet":
        return "https://mainnet.infura.io/" + INFURA_KEY;
      case "rinkeby":
        return "https://rinkeby.infura.io/" + INFURA_KEY;
      case "ropsten":
        return "https://ropsten.infura.io/" + INFURA_KEY;
      case "kovan":
        return "https://kovan.infura.io/" + INFURA_KEY;
      default:
        return "https://mainnet.infura.io/" + INFURA_KEY;
    }
  }

  public static String getExplorerUrl(Context ctx) {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    String networkPreference = prefs.getString("network_preference", "");

    switch (networkPreference) {
      case "mainnet":
        return "https://www.etherchain.org/tx/";  //or https://etherscan.io/tx/
      case "rinkeby":
        return "https://rinkeby.etherscan.io/tx/";
      case "ropsten":
        return "https://ropsten.etherscan.io/tx/";
      case "kovan":
        return "https://kovan.etherscan.io/tx/";
      default:
        return "https://www.etherchain.org/tx/";
    }
  }

  public static DecimalFormat getDecimalFormat() {
    DecimalFormat df = new DecimalFormat("#.########");
    df.setRoundingMode(RoundingMode.FLOOR);
    return df;
  }
}

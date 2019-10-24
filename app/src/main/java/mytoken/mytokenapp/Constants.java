package mytoken.mytokenapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class Constants {

  // Testing seed
  // fuel dune marriage parent fever spike close clog test hip final demise

  // Andy token rinkeby: 0x7ec133d17f253bf759d58882bf9ff18fddcf2155
  //OASChain https://etherscan.io/token/0xfd2bdfdce55bad3b4b916e5758d44f0f8bc2d680
  public static final String CONTRACT_ADDRESS = "0x7ec133d17f253bf759d58882bf9ff18fddcf2155";
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

    final String INFURA_KEY = "5d09436c72db420e87ce0c587e48c073"; // please don't use it since you can get one free
    switch (networkPreference) {
      case "mainnet":
        return "https://mainnet.infura.io/v3/" + INFURA_KEY;
      case "rinkeby":
        return "https://rinkeby.infura.io/v3/" + INFURA_KEY;
      case "ropsten":
        return "https://ropsten.infura.io/v3/" + INFURA_KEY;
      case "kovan":
        return "https://kovan.infura.io/v3/" + INFURA_KEY;
      default:
        return "https://mainnet.infura.io/v3/" + INFURA_KEY;
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

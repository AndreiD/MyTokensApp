package mytoken.mytokenapp;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import com.socks.library.KLog;
import java.util.concurrent.TimeUnit;
import mytoken.mytokenapp.data.local.AppDatabase;
import mytoken.mytokenapp.data.local.PreferencesHelper;
import okhttp3.OkHttpClient;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application {

  public static Web3j web3j;
  private static AppDatabase roomDb;
  private static PreferencesHelper preferencesHelper;

  @Override
  public void onCreate() {
    super.onCreate();

    boolean isDebuggable = (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE));

    if (isDebuggable) {
      KLog.init(true);
    } else {
      KLog.init(false);
    }

    //fonts init
    CalligraphyConfig.initDefault(
        new CalligraphyConfig.Builder().setDefaultFontPath("fonts/regular.otf")
            .setFontAttrId(R.attr.fontPath)
            .build());

    //web3
    String blockchainPreference = Constants.getBlockchanPreference(BaseApplication.this);
    web3j = Web3j.build(new HttpService(blockchainPreference, createOkHttpClient(), false));

    //db

  }


  /**
   * Gets the PreferencesHelper
   */
  public static PreferencesHelper getPreferencesHelper(Context ctx) {
    if (preferencesHelper == null) {
      preferencesHelper = new PreferencesHelper(ctx);
    }
    return preferencesHelper;
  }

  public static AppDatabase getAppDatabase(Context ctx) {
    if (roomDb == null) {
      roomDb = AppDatabase.getAppDatabase(ctx);
      return roomDb;
    }
    return roomDb;
  }

  public static Web3j getWeb3(Context context) {

    if (web3j != null) {
      return web3j;
    }

    String blockchainPreference = Constants.getBlockchanPreference(context);
    web3j = Web3j.build(new HttpService(blockchainPreference, createOkHttpClient(), false));

    try {
      Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().sendAsync().get();
      KLog.d("Connected to Ethereum client version: "
          + web3ClientVersion.getWeb3ClientVersion());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return web3j;
  }

  private static OkHttpClient createOkHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();
    builder.readTimeout(25, TimeUnit.SECONDS);
    builder.connectTimeout(20, TimeUnit.SECONDS);
    builder.writeTimeout(20, TimeUnit.SECONDS);
    return builder.build();
  }

  public static BaseApplication get(Context context) {
    return (BaseApplication) context.getApplicationContext();
  }
}

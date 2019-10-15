package mytoken.mytokenapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.socks.library.KLog;

import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import mytoken.mytokenapp.BaseApplication;
import mytoken.mytokenapp.BaseFragment;
import mytoken.mytokenapp.Constants;
import mytoken.mytokenapp.R;
import mytoken.mytokenapp.data.local.AppDatabase;
import mytoken.mytokenapp.data.local.PreferencesHelper;
import mytoken.mytokenapp.utils.Cryptography;
import mytoken.mytokenapp.utils.QueryBlockchain;
import mytoken.mytokenapp.utils.Token;

public class HomeFragment extends BaseFragment {

  @BindView(R.id.textView_fragmentHome_tokenName) TextView textView_fragmentHome_tokenName;
  @BindView(R.id.textView_fragmentHome_balance_eth) TextView textView_fragmentHome_balance_eth;
  @BindView(R.id.textView_fragmentHome_balance_token) TextView textView_fragmentHome_balance_token;
  @BindView(R.id.textView_fragmentHome_status) TextView textView_fragmentHome_status;
  @BindView(R.id.textView_fragmentHome_greeting) TextView textView_fragmentHome_greeting;
  @BindView(R.id.linlayout_fragmenthome_balance_token) LinearLayout linlayout_fragmenthome_balance_token;
  @BindView(R.id.textView_fragmentHome_date) TextView textView_fragmentHome_date;
  private Web3j web3j = null;
  private Disposable disposable;
  private PreferencesHelper preferencesHelper;

  public HomeFragment() {
  }

  public static HomeFragment newInstance(boolean isjustcreated) {
    HomeFragment fragment = new HomeFragment();
    Bundle args = new Bundle();
    args.putBoolean("isjustcreated", isjustcreated);
    fragment.setRetainInstance(true);
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_home, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    preferencesHelper = BaseApplication.getPreferencesHelper(getActivity());
    textView_fragmentHome_balance_eth.setText("");
    textView_fragmentHome_balance_token.setText("");

    textView_fragmentHome_status.setText("Updating...");
    showGreeting();

    boolean isjustcreated = getArguments().getBoolean("isjustcreated", false);

    if (isjustcreated) {
      textView_fragmentHome_status.setText("Thank you for creating a wallet with us.");
    }
  }

  @Override public void onResume() {
    super.onResume();

    if (preferencesHelper.getDefaultToken() == null) {
      linlayout_fragmenthome_balance_token.setVisibility(View.GONE);
    } else {
      linlayout_fragmenthome_balance_token.setVisibility(View.VISIBLE);
    }

    web3j = BaseApplication.getWeb3(getActivity());

    disposable = Observable.interval(100, 15000,
        TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::updateBalances);
  }

  @Override public void onPause() {
    super.onPause();
    disposable.dispose();
  }

  private void updateBalances(Long aLong) {

    // get the coinbase
    String encAddress = preferencesHelper.getAddress();

    String address = "";
    String decodedPassword = "";
    String decodedSeed = "";
    Cryptography cryptography = new Cryptography(getActivity());
    try {
      decodedPassword = cryptography.decryptData(preferencesHelper.getPassword());
      decodedSeed = cryptography.decryptData(preferencesHelper.getSeed());

      address = cryptography.decryptData(encAddress);
    } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | CertificateException | InvalidAlgorithmParameterException | IOException | InvalidKeyException | NoSuchProviderException | IllegalBlockSizeException | BadPaddingException e) {
      e.printStackTrace();
      return;
    }

    // get balance for ETH
    Single<BigInteger> balanceForETH =
        QueryBlockchain.getBalanceForETH(web3j, address);
    balanceForETH.subscribe(new SingleObserver<BigInteger>() {
      @Override
      public void onSubscribe(Disposable d) {
      }

      @Override
      public void onSuccess(BigInteger bigInteger) {
        BigDecimal balanceETH =
            Convert.fromWei(new BigDecimal(bigInteger.toString()), Convert.Unit.ETHER);
        String balanceETHFormatted = Constants.getDecimalFormat().format(balanceETH);
        textView_fragmentHome_balance_eth.setText(balanceETHFormatted);
        textView_fragmentHome_status.setText("all is good.");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String network_preference = prefs.getString("network_preference", "mainnet");
        if (!network_preference.equals("mainnet")){
          textView_fragmentHome_status.setText("you are using a test network");
          textView_fragmentHome_status.setTextColor(getResources().getColor(R.color.appcolor_red_darker));
        }
      }

      @Override
      public void onError(Throwable e) {
        KLog.e(e);
      }
    });


    //get the default token from the db
    AppDatabase db = BaseApplication.getAppDatabase(getActivity());
    db.tokenDao().findByAddress(preferencesHelper.getDefaultToken()).subscribe(
        ethToken -> {
          textView_fragmentHome_tokenName.setText(ethToken.getName());
        });

    // get balance for token
    Credentials credentials = WalletUtils.loadBip39Credentials(decodedPassword, decodedSeed);
    Token mToken;
    try {
      mToken = Token.load(preferencesHelper.getDefaultToken(), web3j, credentials,
          Constants.getGasPrice(getActivity()),
          Constants.DEFAULT_GAS_LIMIT);
    } catch (Exception ex) {
      KLog.e(ex);
      return;
    }

    Single<Uint256> balanceForToken =
        QueryBlockchain.balanceOf(mToken, address);
    balanceForToken.subscribe(new SingleObserver<Uint256>() {
      @Override
      public void onSubscribe(Disposable d) {
      }

      @Override
      public void onSuccess(Uint256 uint256) {

        BigInteger decimals = new BigInteger("18"); //defaults to 18
        try {
          decimals = mToken.decimals().sendAsync().get().getValue();
        } catch (Exception e) {
          e.printStackTrace();
          KLog.e("decimals...", e.getLocalizedMessage());
        }
        BigDecimal balanceBigDecimal = new BigDecimal(uint256.getValue().toString());
        balanceBigDecimal = balanceBigDecimal.divide(
            new BigDecimal(String.valueOf(Math.pow(10, decimals.doubleValue()))));
        String balanceTokenFormatted = Constants.getDecimalFormat().format(balanceBigDecimal);
        textView_fragmentHome_balance_token.setText(balanceTokenFormatted);
      }

      @Override
      public void onError(Throwable e) {
        if (e.getMessage().contains("returned a null value")) {
          textView_fragmentHome_balance_token.setText("0");
        } else {
          KLog.e(e);
        }
      }
    });
  }

  private void showGreeting() {
    Calendar c = Calendar.getInstance();
    int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

    if (timeOfDay >= 2 && timeOfDay < 12) {
      textView_fragmentHome_greeting.setText("Good Morning");
    } else if (timeOfDay >= 12 && timeOfDay < 16) {
      textView_fragmentHome_greeting.setText("Good Afternoon");
    } else if (timeOfDay >= 16 && timeOfDay < 22) {
      textView_fragmentHome_greeting.setText("Good Evening");
    } else if (timeOfDay >= 22 && timeOfDay < 2) {
      textView_fragmentHome_greeting.setText("Good Night");
    }

    DateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
    textView_fragmentHome_date.setText(formatter.format(c.getTime()));
  }
}
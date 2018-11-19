package mytoken.mytokenapp.dialogs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.socks.library.KLog;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
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
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import mytoken.mytokenapp.BaseApplication;
import mytoken.mytokenapp.Constants;
import mytoken.mytokenapp.R;
import mytoken.mytokenapp.data.local.PreferencesHelper;
import mytoken.mytokenapp.utils.Cryptography;
import mytoken.mytokenapp.utils.DialogFactory;
import mytoken.mytokenapp.utils.Token;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;

public class ConfirmPaymentDialog extends DialogFragment {

  public static final String TOADDRESS = "toaddress";
  public static final String AMOUNT = "amount";
  public static final String IS_ETH = "is_eth";
  public static final String TOKEN_SYMBOL = "token_symbol";
  public static final String TOKEN_ADDRESS = "token_address";
  Credentials credentials = null;
  BigInteger nonce = null;
  private ProgressDialog progressDialog;
  private Web3j web3j = null;

  public static ConfirmPaymentDialog newInstance(boolean isETH, String toAddress, String amount,
      String tokenSymbol, String tokenAddress) {
    ConfirmPaymentDialog frag = new ConfirmPaymentDialog();
    Bundle args = new Bundle();
    args.putString(TOADDRESS, toAddress);
    args.putString(AMOUNT, amount);
    args.putBoolean(IS_ETH, isETH);
    args.putString(TOKEN_SYMBOL, tokenSymbol);
    args.putString(TOKEN_ADDRESS, tokenAddress);
    frag.setArguments(args);
    return frag;
  }

  public static String encodeTransferData(String toAddress, BigInteger sum) {
    Function function = new Function(
        "transfer",  // function we're calling
        Arrays.asList(new Address(toAddress), new Uint256(sum)),
        // Parameters to pass as Solidity Types
        Arrays.asList(new org.web3j.abi.TypeReference<Bool>() {
        }));
    return FunctionEncoder.encode(function);
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
  }

  @Override
  public void onStart() {
    super.onStart();
    Dialog dialog = getDialog();
    if (dialog != null) {
      int width = ViewGroup.LayoutParams.MATCH_PARENT;
      int height = ViewGroup.LayoutParams.MATCH_PARENT;
      dialog.getWindow().setLayout(width, height);
    }
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    super.onCreateView(inflater, container, savedInstanceState);
    View view = inflater.inflate(R.layout.dialog_confirm_send, container, false);

    Toolbar toolbar = view.findViewById(R.id.toolbar);
    toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
    toolbar.setNavigationOnClickListener(view1 -> dismiss());
    toolbar.setTitle(getString(R.string.confirm_payment));

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    String toAddress = getArguments().getString(TOADDRESS, "");
    String amount = getArguments().getString(AMOUNT, "");
    boolean isEth = getArguments().getBoolean(IS_ETH, true);
    String tokenSymbol = getArguments().getString(TOKEN_SYMBOL, null);
    String tokenAddress = getArguments().getString(TOKEN_ADDRESS, null);

    TextView txt_dlg_confirm_to = view.findViewById(R.id.txt_dlg_confirm_to);
    TextView txt_dlg_confirm_from = view.findViewById(R.id.txt_dlg_confirm_from);
    TextView txt_dlg_confirm_amount = view.findViewById(R.id.txt_dlg_confirm_amount);
    TextView txt_dlg_confirm_fee = view.findViewById(R.id.txt_dlg_confirm_fee);
    TextView txt_dlg_confirm_total = view.findViewById(R.id.txt_dlg_confirm_total);

    Button btn_dlg_confirm_send = view.findViewById(R.id.btn_dlg_confirm_send);

    txt_dlg_confirm_to.setText(toAddress);

    txt_dlg_confirm_amount.setText(amount + " ETH");
    if (!isEth) {
      txt_dlg_confirm_amount.setText(amount + " " + tokenSymbol);
    }

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    String network_preference = prefs.getString("network_preference", "mainnet");
    if (!network_preference.toUpperCase().contains("mainnet")){
      txt_dlg_confirm_amount.setText(txt_dlg_confirm_amount.getText() + " (testnet)");
      txt_dlg_confirm_amount.setTextColor(getResources().getColor(R.color.appcolor_red_darker));
    }

    getDialog().setCancelable(true);
    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    getDialog().getWindow()
        .setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

    getDialog().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    PreferencesHelper preferencesHelper = new PreferencesHelper(getActivity());

    Cryptography cryptography = new Cryptography(getActivity());

    web3j = BaseApplication.getWeb3(getActivity());
    BigDecimal value = Convert.toWei(amount, Convert.Unit.ETHER);

    try {
      String decodedPassword = cryptography.decryptData(preferencesHelper.getPassword());
      String decodedSeed = cryptography.decryptData(preferencesHelper.getSeed());

      credentials = WalletUtils.loadBip39Credentials(decodedPassword, decodedSeed);

      nonce =
          web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST)
              .sendAsync()
              .get().getTransactionCount();
    } catch (NoSuchPaddingException | NoSuchAlgorithmException | UnrecoverableEntryException | CertificateException | KeyStoreException | InvalidKeyException | BadPaddingException | NoSuchProviderException | IllegalBlockSizeException | InterruptedException | ExecutionException | InvalidAlgorithmParameterException | IOException e) {
      e.printStackTrace();
    }

    // Get gas cost estimations
    try {
      txt_dlg_confirm_from.setText(credentials.getAddress());

      //String payload = encodeTransferData(toAddress, value.toBigInteger());
      //Transaction transaction =
      //    new Transaction(credentials.getAddress(), nonce,
      //        new BigInteger(Constants.DEFAULT_GAS_PRICE),
      //        new BigInteger(Constants.DEFAULT_GAS_LIMIT), toAddress, value.toBigInteger(),
      //        payload);
      //EthEstimateGas gasCost = web3j.ethEstimateGas(transaction).sendAsync().get();
      //BigInteger fee =
      //    gasCost.getAmountUsed().multiply(new BigInteger(Constants.DEFAULT_GAS_PRICE));
      //
      //txt_dlg_confirm_fee.setText(
      //    String.format("%s ETH",
      //        Convert.fromWei(fee.toString(), Convert.Unit.ETHER).toString()));

      if (isEth) {
        //calculate the fee
        BigInteger fee =
            new BigInteger("21000").multiply(Constants.getGasPrice(getActivity()));

        txt_dlg_confirm_fee.setText(
            String.format("%s ETH",
                Convert.fromWei(fee.toString(), Convert.Unit.ETHER).toString()));

        txt_dlg_confirm_total.setText(
            Convert.fromWei(String.valueOf(value.toBigInteger().add(fee)), Convert.Unit.ETHER)
                + " ETH");
      } else {
        txt_dlg_confirm_total.setText(amount + " " + tokenSymbol);

        //calculate the fee
        BigInteger fee =
            new BigInteger("36957").multiply(Constants.getGasPrice(getActivity()));

        txt_dlg_confirm_fee.setText(
            String.format("%s ETH",
                Convert.fromWei(fee.toString(), Convert.Unit.ETHER).toString()));
      }
    } catch (Exception ex) {
      KLog.e(ex);
    }

    btn_dlg_confirm_send.setOnClickListener(view1 -> {

      String dlgMessage = "Sending transaction of " + amount + " ETH";
      if (!isEth) {
        dlgMessage = "Sending transaction of " + amount + " " + tokenSymbol;
      }
      progressDialog =
          DialogFactory.createProgressDialog(getActivity(), dlgMessage);
      progressDialog.show();
      progressDialog.setCancelable(false);

      if (isEth) {
        sendTransactionForETH(credentials, toAddress, Double.valueOf(amount));
      } else {
        sendTransactionForToken(credentials, toAddress, amount);
      }
    });
  }

  private void sendTransactionForETH(Credentials credentials, String toAddress, double value) {

    Single.fromCallable(() -> Transfer.sendFunds(
        web3j, credentials, toAddress,
        BigDecimal.valueOf(value), Convert.Unit.ETHER)
        .send())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<TransactionReceipt>() {
          @Override
          public void onSubscribe(Disposable d) {
          }

          @Override
          public void onSuccess(TransactionReceipt transactionReceipt) {

            FragmentManager fm = getActivity().getSupportFragmentManager();
            TxHashDialog txHashDialog =
                TxHashDialog.newInstance(transactionReceipt.getTransactionHash());

            txHashDialog.show(fm, "tx_hash_dialog");

            if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
            }
            dismiss(); // close this fragment
          }

          @Override
          public void onError(Throwable e) {
            KLog.e("onError: ", e);

            DialogFactory.createGenericErrorDialog(getActivity(), e.getLocalizedMessage())
                .show();

            if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
            }
          }
        });
  }

  private void sendTransactionForToken(Credentials credentials, String toAddr, String strAmount) {

    Token mToken = Token.load(Constants.CONTRACT_ADDRESS, web3j, credentials,
        Constants.getGasPrice(getActivity()),
        Constants.DEFAULT_GAS_LIMIT);

    BigDecimal value = Convert.toWei(strAmount, Convert.Unit.ETHER);

    Single.fromCallable(() -> mToken.transfer(new Address(toAddr),
        new Uint256(value.toBigInteger()))
        .send())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<TransactionReceipt>() {
          @Override
          public void onSubscribe(Disposable d) {
          }

          @Override
          public void onSuccess(TransactionReceipt transactionReceipt) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            TxHashDialog txHashDialog =
                TxHashDialog.newInstance(transactionReceipt.getTransactionHash());

            txHashDialog.show(fm, "tx_hash_dialog");

            if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
            }
            dismiss(); // close this fragment
          }

          @Override
          public void onError(Throwable e) {
            KLog.e("onError: ", e);

            DialogFactory.createGenericErrorDialog(getActivity(), e.getLocalizedMessage())
                .show();

            if (progressDialog != null && progressDialog.isShowing()) {
              progressDialog.dismiss();
            }
          }
        });
  }
}
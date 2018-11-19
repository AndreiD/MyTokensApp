package mytoken.mytokenapp.utils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import mytoken.mytokenapp.data.local.EthToken;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;

public class QueryBlockchain {

  /**
   * Gets the balance for ETH
   */
  public static Single<BigInteger> getBalanceForETH(Web3j web3j, String address) {
    if (!WalletUtils.isValidAddress(address)) {
      return Single.error(new Exception("address is invalid"));
    }

    return Single.fromCallable(() -> web3j
        .ethGetBalance(address, DefaultBlockParameterName.LATEST)
        .send()
        .getBalance())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * Gets the balance for a EthToken
   */
  public static Single<Uint256> balanceOf(Token token, String address) {
    if (token == null) {
      return Single.error(new NullPointerException("where is the token?"));
    }

    if (!WalletUtils.isValidAddress(address)) {
      return Single.error(new Exception("address is invalid"));
    }

    return Single.fromCallable(() -> token.balanceOf(new Address(address))
        .send())
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  /**
   * Gets the info for a EthToken
   */
  public static Single<EthToken> detailsOf(Token token, String address) {

    if (!WalletUtils.isValidAddress(address)) {
      return Single.error(new Exception("address is invalid"));
    }
    String name;
    String symbol;
    int decimals;

    try {
      Utf8String nameUtf8 = token.name().sendAsync().get();
      if (nameUtf8 != null) {
        name = nameUtf8.toString();
      } else {
        return Single.error(new Exception("Ops! Seems like this token is not ERC20 compliant."));
      }

      Utf8String symbolUtf8 = token.symbol().sendAsync().get();
      if (symbolUtf8 != null) {
        symbol = symbolUtf8.toString();
      } else {
        return Single.error(new Exception("Ops! Seems like this token is not ERC20 compliant."));
      }

      Uint8 decimalsUint8 = token.decimals().sendAsync().get();
      if (decimalsUint8 != null) {
        decimals = decimalsUint8.getValue().intValue();
      } else {
        return Single.error(new Exception("Ops! Seems like this token is not ERC20 compliant."));
      }

      EthToken mToken = new EthToken(address, name, symbol, decimals);

      return Single.fromCallable(() -> mToken)
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    } catch (InterruptedException | ExecutionException e) {
      return Single.error(e);
    }
  }
}

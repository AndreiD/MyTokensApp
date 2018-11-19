//package mytoken.mytokenapp.utils;
//
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import in.co.massacre.massacrewallet.R;
//import in.co.massacre.massacrewallet.model.ERC20ContractModel;
//import in.co.massacre.massacrewallet.utils.Application;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.concurrent.ExecutionException;
//import org.web3j.abi.FunctionEncoder;
//import org.web3j.abi.TypeReference;
//import org.web3j.abi.datatypes.Address;
//import org.web3j.abi.datatypes.Function;
//import org.web3j.abi.datatypes.Type;
//import org.web3j.abi.datatypes.Utf8String;
//import org.web3j.abi.datatypes.generated.Uint256;
//import org.web3j.abi.datatypes.generated.Uint8;
//import org.web3j.crypto.Credentials;
//import org.web3j.crypto.ECKeyPair;
//import org.web3j.crypto.RawTransaction;
//import org.web3j.crypto.TransactionEncoder;
//import org.web3j.protocol.Web3j;
//import org.web3j.protocol.Web3jFactory;
//import org.web3j.protocol.core.DefaultBlockParameterName;
//import org.web3j.protocol.core.methods.request.Transaction;
//import org.web3j.protocol.core.methods.response.EthCall;
//import org.web3j.protocol.core.methods.response.EthGasPrice;
//import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
//import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
//import org.web3j.protocol.core.methods.response.EthSendTransaction;
//import org.web3j.protocol.core.methods.response.EthTransaction;
//import org.web3j.protocol.core.methods.response.TransactionReceipt;
//import org.web3j.protocol.core.methods.response.Web3ClientVersion;
//import org.web3j.protocol.http.HttpService;
//import org.web3j.utils.Numeric;
//
//public class CustomTransaction {
//  private static final String TAG = "CustomTransaction";
//  private static CustomTransaction instance;
//  Context context;
//  private String privateKeyRawFor = "104387595685105680362347703580680997717017125050293550282858177311185770550663";
//  private String[] serviceProvider = new String[]{"https://mainnet.infura.io/ygSC9EPQzpdB13FybRGG", "https://rinkeby.infura.io/ygSC9EPQzpdB13FybRGG", "https://ropsten.infura.io/ygSC9EPQzpdB13FybRGG", "https://kovan.infura.io/ygSC9EPQzpdB13FybRGG", "https://ipfs.infura.io", "https://ipfs.infura.io:5001"};
//  private Web3j web3;
//
//  private CustomTransaction(Context context) {
//    this.context = context;
//    initializeWeb3();
//  }
//
//  public synchronized void initializeWeb3() {
//    new AsyncTask<Void, Void, Void>() {
//      protected Void doInBackground(Void... voidArr) {
//        CustomTransaction.this.web3 = Web3jFactory.build(new HttpService(CustomTransaction.this.serviceProvider[Application.getEtherNetworkId(CustomTransaction.this.context)]));
//        try {
//          Log.d("Web3ClientVersion: ", ((Web3ClientVersion) CustomTransaction.this.web3.web3ClientVersion().send()).getWeb3ClientVersion());
//        } catch (Void[] voidArr2) {
//          StringBuilder stringBuilder = new StringBuilder();
//          stringBuilder.append("transaction.initializeWeb3: ");
//          stringBuilder.append(voidArr2.getMessage().toString());
//          Log.e("web", stringBuilder.toString());
//          voidArr2.printStackTrace();
//        }
//        return null;
//      }
//    }.execute(new Void[0]);
//  }
//
//  public void updateWeb3() {
//    initializeWeb3();
//  }
//
//  public static CustomTransaction getInstance(Context context) {
//    if (instance == null) {
//      instance = new CustomTransaction(context);
//      return instance;
//    }
//    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append("CustomTransaction.getInstance(): ");
//    stringBuilder.append(instance.web3);
//    Log.d("Web3:", stringBuilder.toString());
//    return instance;
//  }
//
//  public BigDecimal getERC20TokenBalance(String str, String str2) throws Exception {
//    if (!(str2 == null || str == null || str2.equals(""))) {
//      if (!str.equals("")) {
//        str = EthToken.load(str, this.web3, Credentials.create(ECKeyPair.create(new BigInteger(this.privateKeyRawFor))), new BigInteger("0"), new BigInteger("0"));
//        Uint256 uint256 = (Uint256) str.balanceOf(new Address(str2)).send();
//        Uint8 uint8 = (Uint8) str.decimals().send();
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("CustomTransaction.getERC20TokenBalance()=");
//        stringBuilder.append(uint256.getValue().toString());
//        Log.e("BalanceOf:", stringBuilder.toString());
//        if (uint256 == null || uint8 == null) {
//          return null;
//        }
//        double pow = Math.pow(10.0d, (double) uint8.getValue().longValue());
//        String str3 = TAG;
//        StringBuilder stringBuilder2 = new StringBuilder();
//        stringBuilder2.append("getERC20TokenBalance().divisor:");
//        stringBuilder2.append(pow);
//        stringBuilder2.append(" , decimal: ");
//        stringBuilder2.append(uint8);
//        Log.d(str3, stringBuilder2.toString());
//        str = new BigDecimal(uint256.getValue()).divide(new BigDecimal(pow));
//        String str4 = TAG;
//        StringBuilder stringBuilder3 = new StringBuilder();
//        stringBuilder3.append("getERC20TokenBalance().tokenBalance:");
//        stringBuilder3.append(uint256.getValue());
//        stringBuilder3.append(", inDecimal:");
//        stringBuilder3.append(str);
//        Log.d(str4, stringBuilder3.toString());
//        return str;
//      }
//    }
//    return null;
//  }
//
//  public BigInteger getBalanceERC20RPC(String str, String str2) throws ExecutionException, InterruptedException {
//    str = ((EthCall) this.web3.ethCall(Transaction.createEthCallTransaction(str, str2, FunctionEncoder.encode(new Function("balanceOf", Arrays.asList(new Type[]{new Address(str)}), Arrays.asList(new TypeReference[]{new TypeReference<Uint256>() {
//    }})))), DefaultBlockParameterName.LATEST).sendAsync().get()).getValue();
//    if (str == null) {
//      return null;
//    }
//    Log.d(TAG, str);
//    str = str.substring(2, str.length());
//    str2 = new BigInteger(str, 16);
//    String str3 = TAG;
//    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append(str);
//    stringBuilder.append(" : ");
//    stringBuilder.append(str2);
//    Log.d(str3, stringBuilder.toString());
//    return str2;
//  }
//
//  public ERC20ContractModel getDedicatedTokenDetailRPC(String str, String str2) throws ExecutionException, InterruptedException {
//    ERC20ContractModel eRC20ContractModel = new ERC20ContractModel();
//    String value = ((EthCall) this.web3.ethCall(Transaction.createEthCallTransaction(str, str2, FunctionEncoder.encode(new Function("balanceOf", Arrays.asList(new Type[]{new Address(str)}), Arrays.asList(new TypeReference[]{new TypeReference<Uint256>() {
//    }})))), DefaultBlockParameterName.LATEST).sendAsync().get()).getValue();
//    BigInteger bigInteger = new BigInteger(value.substring(2, value.length()), 16);
//    value = ((EthCall) this.web3.ethCall(Transaction.createEthCallTransaction(str, str2, FunctionEncoder.encode(new Function(JupiterCoin.FUNC_GETBUYPRICEINWEI, Arrays.asList(new Type[0]), Arrays.asList(new TypeReference[]{new TypeReference<Uint256>() {
//    }})))), DefaultBlockParameterName.LATEST).sendAsync().get()).getValue();
//    BigInteger bigInteger2 = new BigInteger(value.substring(2, value.length()), 16);
//    str = ((EthCall) this.web3.ethCall(Transaction.createEthCallTransaction(str, str2, FunctionEncoder.encode(new Function(JupiterCoin.FUNC_GETSELLPRICEINWEI, Arrays.asList(new Type[0]), Arrays.asList(new TypeReference[]{new TypeReference<Uint256>() {
//    }})))), DefaultBlockParameterName.LATEST).sendAsync().get()).getValue();
//    BigInteger bigInteger3 = new BigInteger(str.substring(2, str.length()), 16);
//    eRC20ContractModel.setBalance(bigInteger);
//    eRC20ContractModel.setBuyPriceInWei(bigInteger2);
//    eRC20ContractModel.setSellPriceInWei(bigInteger3);
//    eRC20ContractModel.setTokenName(this.context.getResources().getString(R.string.token_name));
//    eRC20ContractModel.setSymbol(this.context.getResources().getString(R.string.token_symbol));
//    eRC20ContractModel.setTokenAddress(str2);
//    return eRC20ContractModel;
//  }
//
//  public ERC20ContractModel getERC20TokenDetailRPC(String str, String str2) throws ExecutionException, InterruptedException {
//    str2 = new ERC20ContractModel();
//    str2.setTokenAddress(str);
//    return str2;
//  }
//
//  public ERC20ContractModel getDedicatedTokenDetail(String str, String str2) throws Exception {
//    if (str != null) {
//      if (!str.equals("")) {
//        ERC20ContractModel eRC20ContractModel = new ERC20ContractModel();
//        JupiterCoin load = JupiterCoin.load(str, this.web3, Credentials.create(ECKeyPair.create(new BigInteger(this.privateKeyRawFor))), new BigInteger("0"), new BigInteger("0"));
//        Utf8String utf8String = (Utf8String) load.name().sendAsync().get();
//        Utf8String utf8String2 = (Utf8String) load.symbol().sendAsync().get();
//        Uint256 uint256 = (Uint256) load.balanceOf(new Address(str2)).send();
//        Uint8 uint8 = (Uint8) load.decimals().sendAsync().get();
//        Uint256 uint2562 = (Uint256) load.getBuyPriceInWei().send();
//        Uint256 uint2563 = (Uint256) load.getSellPriceInWei().send();
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("CustomTransaction.getERC20TokenBalance()=");
//        stringBuilder.append(uint256.getValue().toString());
//        Log.e("BalanceOf:", stringBuilder.toString());
//        eRC20ContractModel.setTokenName(utf8String.getValue());
//        eRC20ContractModel.setSymbol(utf8String2.getValue());
//        eRC20ContractModel.setDecimal(uint8.getValue().longValue());
//        eRC20ContractModel.setBuyPriceInWei(uint2562.getValue());
//        eRC20ContractModel.setSellPriceInWei(uint2563.getValue());
//        Math.pow(10.0d, (double) uint8.getValue().longValue());
//        eRC20ContractModel.setBalance(uint256.getValue());
//        eRC20ContractModel.setTokenAddress(str);
//        eRC20ContractModel.setAccountAddress(str2);
//        eRC20ContractModel.setNetworkId(Application.getEtherNetworkId(this.context));
//        return eRC20ContractModel;
//      }
//    }
//    return null;
//  }
//
//  public ERC20ContractModel getERC20TokenDetail(String str, String str2) throws ExecutionException, InterruptedException {
//    if (str != null) {
//      if (!str.equals("")) {
//        ERC20ContractModel eRC20ContractModel = new ERC20ContractModel();
//        EthToken load = EthToken.load(str, this.web3, Credentials.create(ECKeyPair.create(new BigInteger(this.privateKeyRawFor))), new BigInteger("0"), new BigInteger("0"));
//        Utf8String utf8String = (Utf8String) load.name().sendAsync().get();
//        Utf8String utf8String2 = (Utf8String) load.symbol().sendAsync().get();
//        Uint256 uint256 = (Uint256) load.balanceOf(new Address(str2)).sendAsync().get();
//        Uint8 uint8 = (Uint8) load.decimals().sendAsync().get();
//        if (!(utf8String == null || utf8String2 == null || uint256 == null)) {
//          if (uint8 != null) {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("CustomTransaction.getERC20TokenBalance()=");
//            stringBuilder.append(uint256.getValue().toString());
//            Log.e("BalanceOf:", stringBuilder.toString());
//            eRC20ContractModel.setTokenName(utf8String.getValue());
//            eRC20ContractModel.setSymbol(utf8String2.getValue());
//            eRC20ContractModel.setDecimal(uint8.getValue().longValue());
//            Math.pow(10.0d, (double) uint8.getValue().longValue());
//            eRC20ContractModel.setBalance(uint256.getValue());
//            eRC20ContractModel.setTokenAddress(str);
//            eRC20ContractModel.setAccountAddress(str2);
//            eRC20ContractModel.setSellPriceInWei(new BigInteger(String.valueOf(0)));
//            eRC20ContractModel.setBuyPriceInWei(new BigInteger(String.valueOf(0)));
//            eRC20ContractModel.setPriceInDollar(null);
//            eRC20ContractModel.setNetworkId(Application.getEtherNetworkId(this.context));
//            return eRC20ContractModel;
//          }
//        }
//        return null;
//      }
//    }
//    return null;
//  }
//
//  public TransactionReceipt sendTokenTransaction(String str, String str2, BigInteger bigInteger, Credentials credentials) throws Exception {
//    if (!(str2 == null || credentials == null)) {
//      if (!str2.equals("")) {
//        EthToken load = EthToken.load(str, this.web3, credentials, new BigInteger(Application.DEFFAULT_GAS_PRICE), new BigInteger(Application.DEFAULT_GA_LIMIT));
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(str);
//        stringBuilder.append(" : ");
//        stringBuilder.append(str2);
//        stringBuilder.append(" : ");
//        stringBuilder.append(bigInteger.toString());
//        stringBuilder.append(" : ");
//        stringBuilder.append(credentials.getAddress());
//        Log.d("Send EthToken:", stringBuilder.toString());
//        TransactionReceipt transactionReceipt = (TransactionReceipt) load.transfer(new Address(str2), new Uint256(bigInteger)).send();
//        Log.e("TransactionReciept", transactionReceipt.getTransactionHash());
//        return transactionReceipt;
//      }
//    }
//    return null;
//  }
//
//  public String sendEtherTransaction(String str, BigInteger bigInteger, Credentials credentials) throws ExecutionException, InterruptedException {
//    if (!(str == null || credentials == null)) {
//      if (!str.equals("")) {
//        EthGetTransactionCount ethGetTransactionCount = (EthGetTransactionCount) this.web3.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("CustomTransaction.sendEtheTransaction()");
//        stringBuilder.append(str);
//        stringBuilder.append(" > ");
//        stringBuilder.append(bigInteger);
//        Log.d("Ether Send", stringBuilder.toString());
//        return ((EthSendTransaction) this.web3.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(RawTransaction.createEtherTransaction(ethGetTransactionCount.getTransactionCount(), new BigInteger(Application.DEFFAULT_GAS_PRICE), new BigInteger(Application.DEFAULT_GA_LIMIT), str, bigInteger), credentials))).sendAsync().get()).getTransactionHash();
//      }
//    }
//    return null;
//  }
//
//  public TransactionReceipt buyDedicatedToken(String str, BigInteger bigInteger, Credentials credentials) throws Exception {
//    if (bigInteger == null || credentials == null) {
//      return null;
//    }
//    JupiterCoin load = JupiterCoin.load(str, this.web3, credentials, new BigInteger(Application.DEFFAULT_GAS_PRICE), new BigInteger(Application.DEFAULT_GA_LIMIT));
//    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append(str);
//    stringBuilder.append(" : ");
//    stringBuilder.append(bigInteger.toString());
//    stringBuilder.append(" : ");
//    stringBuilder.append(credentials.getAddress());
//    Log.d("Buy EthToken:", stringBuilder.toString());
//    TransactionReceipt transactionReceipt = (TransactionReceipt) load.buy(bigInteger).send();
//    Log.e("TransactionReciept", transactionReceipt.getTransactionHash());
//    return transactionReceipt;
//  }
//
//  public TransactionReceipt sellDedicatedToken(String str, BigInteger bigInteger, Credentials credentials) throws Exception {
//    if (bigInteger == null || credentials == null) {
//      return null;
//    }
//    JupiterCoin load = JupiterCoin.load(str, this.web3, credentials, new BigInteger(Application.DEFFAULT_GAS_PRICE), new BigInteger(Application.DEFAULT_GA_LIMIT));
//    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append(str);
//    stringBuilder.append(" : ");
//    stringBuilder.append(bigInteger.toString());
//    stringBuilder.append(" : ");
//    stringBuilder.append(credentials.getAddress());
//    Log.d("Buy EthToken:", stringBuilder.toString());
//    TransactionReceipt transactionReceipt = (TransactionReceipt) load.sell(new Uint256(bigInteger)).send();
//    Log.e("TransactionReciept", transactionReceipt.getTransactionHash());
//    return transactionReceipt;
//  }
//
//  public TransactionReceipt getTransactionReceipt(String str) throws IOException {
//    return ((EthGetTransactionReceipt) this.web3.ethGetTransactionReceipt(str).send()).getTransactionReceipt();
//  }
//
//  public org.web3j.protocol.core.methods.response.Transaction getTransaction(String str) throws IOException {
//    return ((EthTransaction) this.web3.ethGetTransactionByHash(str).send()).getTransaction();
//  }
//
//  public String sentERC20RawTransaction(String str, String str2, BigInteger bigInteger, Credentials credentials) throws ExecutionException, InterruptedException {
//    Function function = new Function("transfer", Arrays.asList(new Type[]{new Address(str2), new Uint256(bigInteger)}), Collections.emptyList());
//    EthGetTransactionCount ethGetTransactionCount = (EthGetTransactionCount) this.web3.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();
//    StringBuilder stringBuilder = new StringBuilder();
//    stringBuilder.append("CustomTransaction.sendEtheTransaction()");
//    stringBuilder.append(str2);
//    stringBuilder.append(" > ");
//    stringBuilder.append(bigInteger);
//    Log.d("Ether Send", stringBuilder.toString());
//    return ((EthSendTransaction) this.web3.ethSendRawTransaction(Numeric.toHexString(TransactionEncoder.signMessage(RawTransaction.createTransaction(ethGetTransactionCount.getTransactionCount(), new BigInteger(Application.DEFFAULT_GAS_PRICE), new BigInteger(Application.DEFAULT_GA_LIMIT), str, FunctionEncoder.encode(function)), credentials))).sendAsync().get()).getTransactionHash();
//  }
//
//  public BigInteger getGasPrice() throws IOException {
//    return ((EthGasPrice) this.web3.ethGasPrice().send()).getGasPrice();
//  }
//}
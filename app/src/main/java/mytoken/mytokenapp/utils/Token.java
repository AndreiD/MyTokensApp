package mytoken.mytokenapp.utils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

public class Token extends Contract {
  private static final String BINARY = "0x";
  public static final String FUNC_ALLOWANCES = "allowances";
  public static final String FUNC_BALANCEOF = "balanceOf";
  public static final String FUNC_DECIMALS = "decimals";
  public static final String FUNC_NAME = "name";
  public static final String FUNC_SYMBOL = "symbol";
  public static final String FUNC_TOTALSUPPLY = "totalSupply";
  public static final String FUNC_TRANSFER = "transfer";
  public String name;
  public String symbol;
  public int decimals;
  public String address;
  public static final Event TRANSFER_EVENT = new Event("Transfer", Arrays.asList(new TypeReference[] {
      new TypeReference<Address>() {
      }, new TypeReference<Address>() {
  }
  }), Arrays.asList(new TypeReference[] {
      new TypeReference<Uint256>() {
      }
  }));
  protected static final HashMap<String, String> _addresses = new HashMap();

  public static class TransferEventResponse {
    public Address _from;
    public Address _to;
    public Uint256 _value;
    public Log log;
  }

  protected Token(String str, Web3j web3j, Credentials credentials, BigInteger bigInteger, BigInteger bigInteger2) {
    super(BINARY, str, web3j, credentials, bigInteger, bigInteger2);
  }

  protected Token(String str, Web3j web3j, TransactionManager transactionManager, BigInteger bigInteger, BigInteger bigInteger2) {
    super(BINARY, str, web3j, transactionManager, bigInteger, bigInteger2);
  }

  public RemoteCall<Utf8String> name() {
    return executeRemoteCallSingleValueReturn(new Function("name", Arrays.asList(new Type[0]), Arrays.asList(new TypeReference[] {
        new TypeReference<Utf8String>() {
        }
    })));
  }

  public RemoteCall<Uint256> totalSupply() {
    return executeRemoteCallSingleValueReturn(new Function("totalSupply", Arrays.asList(new Type[0]), Arrays.asList(new TypeReference[] {
        new TypeReference<Uint256>() {
        }
    })));
  }

  public RemoteCall<Uint8> decimals() {
    return executeRemoteCallSingleValueReturn(new Function("decimals", Arrays.asList(new Type[0]), Arrays.asList(new TypeReference[] {
        new TypeReference<Uint8>() {
        }
    })));
  }

  public RemoteCall<Uint256> allowances(Address address, Address address2) {
    return executeRemoteCallSingleValueReturn(
        new Function("allowances", Arrays.asList(new Type[] { address, address2 }), Arrays.asList(new TypeReference[] {
            new TypeReference<Uint256>() {
            }
        })));
  }

  public RemoteCall<Uint256> balanceOf(Address address) {
    return executeRemoteCallSingleValueReturn(new Function("balanceOf", Arrays.asList(new Type[] { address }), Arrays.asList(new TypeReference[] {
        new TypeReference<Uint256>() {
        }
    })));
  }

  public RemoteCall<Utf8String> symbol() {
    return executeRemoteCallSingleValueReturn(new Function("symbol", Arrays.asList(new Type[0]), Arrays.asList(new TypeReference[] {
        new TypeReference<Utf8String>() {
        }
    })));
  }

  public static RemoteCall<Token> deploy(Web3j web3j, Credentials credentials, BigInteger bigInteger, BigInteger bigInteger2, Utf8String utf8String,
      Utf8String utf8String2, Uint8 uint8, Uint256 uint256) {
    return deployRemoteCall(Token.class, web3j, credentials, bigInteger, bigInteger2, BINARY,
        FunctionEncoder.encodeConstructor(Arrays.asList(new Type[] { utf8String, utf8String2, uint8, uint256 })));
  }

  public static RemoteCall<Token> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger bigInteger, BigInteger bigInteger2,
      Utf8String utf8String, Utf8String utf8String2, Uint8 uint8, Uint256 uint256) {
    return deployRemoteCall(Token.class, web3j, transactionManager, bigInteger, bigInteger2, BINARY,
        FunctionEncoder.encodeConstructor(Arrays.asList(new Type[] { utf8String, utf8String2, uint8, uint256 })));
  }

  public Observable<TransferEventResponse> transferEventObservable(EthFilter ethFilter) {
    return this.web3j.ethLogObservable(ethFilter).map(new Func1<Log, TransferEventResponse>() {
      public TransferEventResponse call(Log log) {
        EventValuesWithLog access$000 = Token.this.extractEventParametersWithLog(Token.TRANSFER_EVENT, log);
        TransferEventResponse transferEventResponse = new TransferEventResponse();
        transferEventResponse.log = log;
        transferEventResponse._from = (Address) access$000.getIndexedValues().get(0);
        transferEventResponse._to = (Address) access$000.getIndexedValues().get(1);
        transferEventResponse._value = (Uint256) access$000.getNonIndexedValues().get(0);
        return transferEventResponse;
      }
    });
  }

  public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter defaultBlockParameter,
      DefaultBlockParameter defaultBlockParameter2) {
    EthFilter ethFilter = new EthFilter(defaultBlockParameter, defaultBlockParameter2, getContractAddress());
    ethFilter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
    return transferEventObservable(ethFilter);
  }

  public RemoteCall<TransactionReceipt> transfer(Address address, Uint256 uint256) {
    return executeRemoteCallTransaction(new Function("transfer", Arrays.asList(new Type[] { address, uint256 }), Collections.emptyList()));
  }

  public static Token load(String str, Web3j web3j, Credentials credentials, BigInteger bigInteger, BigInteger bigInteger2) {
    return new Token(str, web3j, credentials, bigInteger, bigInteger2);
  }

  public static Token load(String str, Web3j web3j, TransactionManager transactionManager, BigInteger bigInteger, BigInteger bigInteger2) {
    return new Token(str, web3j, transactionManager, bigInteger, bigInteger2);
  }

  protected String getStaticDeployedAddress(String str) {
    return _addresses.get(str);
  }

  public static String getPreviouslyDeployedAddress(String str) {
    return (String) _addresses.get(str);
  }
}
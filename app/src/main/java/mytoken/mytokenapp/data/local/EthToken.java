package mytoken.mytokenapp.data.local;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tokens_table", indices = {@Index(value = "address", unique = true)})
public class EthToken {

  @PrimaryKey(autoGenerate = true)
  private int uid;

  @ColumnInfo(name = "address")
  private String address;

  @ColumnInfo(name = "name")
  private String name;

  @ColumnInfo(name = "symbol")
  private String symbol;

  @ColumnInfo(name = "decimals")
  private int decimals;


  public EthToken(String address, String name, String symbol, int decimals) {
    this.address = address;
    this.name = name;
    this.symbol = symbol;
    this.decimals = decimals;
  }

  public int getUid() {
    return uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public int getDecimals() {
    return decimals;
  }

  public void setDecimals(int decimals) {
    this.decimals = decimals;
  }

  @Override public String toString() {
    return "EthToken{" +
        "uid=" + uid +
        ", address='" + address + '\'' +
        ", name='" + name + '\'' +
        ", symbol='" + symbol + '\'' +
        ", decimals=" + decimals +
        '}';
  }
}
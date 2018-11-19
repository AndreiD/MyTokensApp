package mytoken.mytokenapp.data.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import io.reactivex.Maybe;
import java.util.List;

@Dao
public interface TokenDAO {

  @Query("SELECT * FROM tokens_table")
  Maybe<List<EthToken>> getAll();

  @Query("SELECT * FROM tokens_table where address LIKE :address")
  Maybe<EthToken> findByAddress(String address);

  @Query("SELECT COUNT(*) from tokens_table")
  Maybe<Long> totalTokens();

  @Insert
  List<Long> insertAll(EthToken... ethTokens);

  @Delete
  void delete(EthToken ethToken);

  @Query("DELETE FROM tokens_table") void nukeContacts();
}
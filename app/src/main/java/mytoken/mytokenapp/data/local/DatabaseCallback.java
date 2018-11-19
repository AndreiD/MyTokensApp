package mytoken.mytokenapp.data.local;

import java.util.List;

public interface DatabaseCallback {

  void onTokensLoaded(List<EthToken> ethTokens);

  void onTokenLoaded(EthToken ethToken);

  void onTokenDeleted();

  void onTokenAdded();

  void onDataNotAvailable();
}

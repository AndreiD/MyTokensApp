package mytoken.mytokenapp.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import mytoken.mytokenapp.BaseFragment;
import mytoken.mytokenapp.R;
import mytoken.mytokenapp.activities.MainActivity;
import mytoken.mytokenapp.data.local.PreferencesHelper;
import mytoken.mytokenapp.dialogs.ConfirmSeedDialog;
import mytoken.mytokenapp.utils.Cryptography;
import mytoken.mytokenapp.utils.DialogFactory;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

public class NewAccountFragment extends BaseFragment {

  @BindView(R.id.button_new_account) Button button_new_account;
  @BindView(R.id.button_new_account_import) Button button_new_account_import;
  @BindView(R.id.editText_password) EditText editText_password;
  @BindView(R.id.editText_seed) EditText editText_seed;

  private ProgressDialog progressDialog;
  private PreferencesHelper preferencesHelper;
  private Cryptography cryptography;

  public NewAccountFragment() {
  }

  public static NewAccountFragment newInstance() {
    NewAccountFragment fragment = new NewAccountFragment();
    fragment.setRetainInstance(true);
    return fragment;
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_new_account, container, false);
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    preferencesHelper = new PreferencesHelper(getActivity());
    cryptography = new Cryptography(getActivity());
  }

  @OnClick(R.id.button_new_account) public void onClickNewAccount() {
    new EnterPasswordDialog(getActivity()).show();
  }

  @OnClick(R.id.button_new_account_import) public void onClickImportAccount() {
    String password = editText_password.getText().toString();
    String seed = editText_seed.getText().toString();

    if ((password.length() < 1) || (seed.length() < 10)) {
      DialogFactory.error_toast(getActivity(), "Invalid password or seed").show();
      return;
    }
    Credentials credentials = WalletUtils.loadBip39Credentials(password, seed);
    Log.d("Loaded account", credentials.getAddress());
    try {
      String encryptedPassword = cryptography.encryptData(password);
      String encryptedMnemonic = cryptography.encryptData(seed);
      String encryptedAddress = cryptography.encryptData(credentials.getAddress());
      preferencesHelper.setSeed(encryptedMnemonic);
      preferencesHelper.setPassword(encryptedPassword);
      preferencesHelper.setAddress(encryptedAddress);
      preferencesHelper.setWalletCreated(true);

      if ((progressDialog != null) && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }

      Intent intent = new Intent(getActivity(), MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      getActivity().finish();
    } catch (NoSuchPaddingException | NoSuchAlgorithmException |
        UnrecoverableEntryException | CertificateException | KeyStoreException |
        IOException | InvalidAlgorithmParameterException | InvalidKeyException |
        NoSuchProviderException | BadPaddingException | IllegalBlockSizeException e) {
      e.printStackTrace();
      DialogFactory.createGenericErrorDialog(getActivity(), e.getLocalizedMessage()).show();
    }
  }

  @Override public void onPause() {
    super.onPause();
    if ((progressDialog != null) && progressDialog.isShowing()) {
      progressDialog.dismiss();
    }
  }

  private class CreateAccountAsyncTask extends AsyncTask<String, String, String> {
    @Override protected String doInBackground(String... params) {
      String password = params[0];

      File folder = new File(getActivity().getFilesDir(), "MyTokenApp");
      if (!folder.exists()) {
        Log.d("NewAccount", "folder did not exist, creating the folder...");
        folder.mkdirs();
      }

      Cryptography cryptography = new Cryptography(getActivity());
      try {
        Bip39Wallet bip39Wallet = WalletUtils.generateBip39Wallet(password, folder);
        String mnemonic = bip39Wallet.getMnemonic();

        String encryptedPassword = cryptography.encryptData(password);
        String encryptedMnemonic = cryptography.encryptData(mnemonic);
        Credentials credentials = WalletUtils.loadBip39Credentials(password, mnemonic);
        String encryptedAddress = cryptography.encryptData(credentials.getAddress());

        preferencesHelper.setSeed(encryptedMnemonic);
        preferencesHelper.setPassword(encryptedPassword);
        preferencesHelper.setAddress(encryptedAddress);
        preferencesHelper.setWalletCreated(true);

        return mnemonic;
      } catch (CipherException | NoSuchPaddingException | NoSuchAlgorithmException |
          UnrecoverableEntryException | CertificateException | KeyStoreException |
          IOException | InvalidAlgorithmParameterException | InvalidKeyException |
          NoSuchProviderException | BadPaddingException | IllegalBlockSizeException e) {
        e.printStackTrace();
        DialogFactory.createGenericErrorDialog(getActivity(), e.getLocalizedMessage()).show();
      }
      return null;
    }

    @Override protected void onPostExecute(String mnemonic) {
      super.onPostExecute(mnemonic);
      if ((progressDialog != null) && progressDialog.isShowing()) {
        progressDialog.dismiss();
      }

      FragmentManager fm = getActivity().getSupportFragmentManager();
      ConfirmSeedDialog confirmPaymentDialog =
          ConfirmSeedDialog.newInstance(mnemonic);
      confirmPaymentDialog.show(fm, "seed_dialog_fragment");

      fm.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
          super.onFragmentViewDestroyed(fm, f);
          if (f instanceof ConfirmSeedDialog) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
          }
          fm.unregisterFragmentLifecycleCallbacks(this);
        }
      }, false);

      //AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
      //alertDialog.setTitle("Save the mnemonic somewhere safe");
      //alertDialog.setMessage(
      //    "The following mnemonic has been copied to your clipboard. Paste it somewhere safe (ex: an encrypted flash drive) \n\n"
      //        + mnemonic);
      //alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
      //    (dialog, which) -> {
      //      dialog.dismiss();
      //
      //      Intent intent = new Intent(getActivity(), MainActivity.class);
      //      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      //      startActivity(intent);
      //      getActivity().finish();
      //    });
      //alertDialog.show();
      //
      //android.content.ClipboardManager clipboard =
      //    (android.content.ClipboardManager) getActivity().getSystemService(
      //        Context.CLIPBOARD_SERVICE);
      //android.content.ClipData clip = android.content.ClipData.newPlainText("Save this", mnemonic);
      //if (clipboard != null) {
      //  clipboard.setPrimaryClip(clip);
      //}
    }
  }

  private class EnterPasswordDialog extends Dialog {

    public Activity activity;
    private EditText editText_wallet_password;
    private Button btn_dialog_password_cancel;
    private Button btn_dialog_password_continue;

    public EnterPasswordDialog(Activity activity) {
      super(activity);
      this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      requestWindowFeature(Window.FEATURE_NO_TITLE);
      setContentView(R.layout.dialog_wallet_password);
      editText_wallet_password = findViewById(R.id.editText_wallet_password);
      btn_dialog_password_cancel = findViewById(R.id.btn_dialog_password_cancel);
      btn_dialog_password_continue = findViewById(R.id.btn_dialog_password_continue);

      btn_dialog_password_continue.setOnClickListener(
          view -> {

            if (editText_wallet_password.getText().toString().length() < 4) {
              DialogFactory.error_toast(getActivity(),
                  "Please enter at least a 4 characters passphrase").show();
              return;
            }

            progressDialog = DialogFactory.createProgressDialog(getActivity(),
                "Creating a new ETH wallet...");
            progressDialog.show();

            new CreateAccountAsyncTask().execute(editText_wallet_password.getText().toString());
            dismiss();
          });

      btn_dialog_password_cancel.setOnClickListener(view -> dismiss());
    }
  }
}
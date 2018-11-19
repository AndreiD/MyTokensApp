package mytoken.mytokenapp.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import mytoken.mytokenapp.R;
import mytoken.mytokenapp.utils.DialogFactory;

public class ConfirmSeedDialog extends DialogFragment {

  public static final String SEED = "seed";

  public static ConfirmSeedDialog newInstance(String seed) {
    ConfirmSeedDialog frag = new ConfirmSeedDialog();
    Bundle args = new Bundle();
    args.putString(SEED, seed);
    frag.setArguments(args);
    return frag;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
  }

  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.dialog_show_seed, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    getDialog().getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    getDialog().setCancelable(false);

    String seed = getArguments().getString(SEED, "");

    TextView textView_the_seed = view.findViewById(R.id.textView_the_seed);
    CheckBox checkBox_seed = view.findViewById(R.id.checkBox_seed);
    Button btn_seed_clipboard = view.findViewById(R.id.btn_seed_clipboard);
    Button btn_seed_continue = view.findViewById(R.id.btn_seed_continue);

    textView_the_seed.setText(seed);

    btn_seed_continue.setOnClickListener(view1 -> {
      if (!checkBox_seed.isChecked()) {
        checkBox_seed.setTextColor(getResources().getColor(R.color.material_red));
        DialogFactory.error_toast(getActivity(), "Please check that you stored the seed safely!")
            .show();
        return;
      }
      dismiss();
    });

    btn_seed_clipboard.setOnClickListener(view12 -> {
      android.content.ClipboardManager clipboard =
          (android.content.ClipboardManager) getActivity().getSystemService(
              Context.CLIPBOARD_SERVICE);
      android.content.ClipData clip = android.content.ClipData.newPlainText("Save this", seed);
      if (clipboard != null) {
        clipboard.setPrimaryClip(clip);
        DialogFactory.simple_toast(getActivity(), "copied to clipboard").show();
      }
    });
  }
}
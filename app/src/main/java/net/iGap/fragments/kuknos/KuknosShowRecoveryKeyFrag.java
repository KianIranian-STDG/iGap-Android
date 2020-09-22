package net.iGap.fragments.kuknos;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentKuknosRecoveryKeyBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosShowRecoveryKeyVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KuknosShowRecoveryKeyFrag extends BaseFragment {
    private final int REQUEST_CODE_FOR_DIALOGFRAGMENT = 0;
    private final String DIALOG_FRAGMENT_TAG = "net.iGap.fragments.kuknos.dialogFragmentTag";
    private FragmentKuknosRecoveryKeyBinding binding;
    private KuknosShowRecoveryKeyVM kuknosShowRecoveryKeyVM;
    private String mnemonicWords;

    private WordsAdapter wordsAdapter;

    public static KuknosShowRecoveryKeyFrag newInstance() {
        return new KuknosShowRecoveryKeyFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosShowRecoveryKeyVM = ViewModelProviders.of(this).get(KuknosShowRecoveryKeyVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_recovery_key, container, false);
        binding.setViewmodel(kuknosShowRecoveryKeyVM);
        binding.setLifecycleOwner(this);
        isNeedResume = true;
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        binding.spinnerLanguage.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(getResources().getString(R.string.kuknos_recoveryKey_en), getResources().getString(R.string.kuknos_recoveryKey_fa))));
        binding.spinnerLength.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, Arrays.asList(CompatibleUnicode("12"), CompatibleUnicode("24"))));

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosRKSToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());
        onGetMnemonicLists();
        onErrorObserver();
        onNextObserver();
        progressState();
    }

    private void onGetMnemonicLists() {
        kuknosShowRecoveryKeyVM.getMnemonic().observe(getViewLifecycleOwner(), mnemonic -> {
            if (mnemonic != null) {
                mnemonicWords = mnemonic;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (kuknosShowRecoveryKeyVM.getSelectedLanguage().equals("EN")) {
                        binding.fragKuknosRKSkeysET.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                    } else {
                        binding.fragKuknosRKSkeysET.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                    }
                }

                wordsAdapter = new WordsAdapter(prepareQuestionsForDialogFragment());
                if (getResources().getBoolean(R.bool.isTablet)) {
                    binding.fragKuknosRKSkeysET.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                } else {
                    binding.fragKuknosRKSkeysET.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                }

                binding.fragKuknosRKSkeysET.setAdapter(wordsAdapter);
            }
        });
    }

    private String CompatibleUnicode(String entry) {
        return HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(entry)) : entry;
    }

    private void onErrorObserver() {
        kuknosShowRecoveryKeyVM.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                /*Snackbar snackbar = Snackbar.make(binding.fragKuknosRKSContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
                binding.fragKuknosIdSubmit.setEnabled(false);*/
                showDialog(errorM.getResID());
            }
        });
    }

    private void showDialog(int messageResource) {
        new MaterialDialog.Builder(getContext())
                .title(R.string.kuknos_viewRecoveryEP_failTitle)
                .positiveText(getResources().getString(R.string.kuknos_RecoverySK_Error_Snack))
                .content(getResources().getString(messageResource))
                .onPositive((dialog, which) -> ((ActivityMain) getActivity()).removeAllFragmentFromMain()).show();
    }

    private void onNextObserver() {
        kuknosShowRecoveryKeyVM.getNextPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                String[] questions = prepareQuestionsForDialogFragment();
                KuknosAskQuestionDialogFragment diaLogFragment = KuknosAskQuestionDialogFragment.newInstance(questions);
                diaLogFragment.setTargetFragment(KuknosShowRecoveryKeyFrag.this, REQUEST_CODE_FOR_DIALOGFRAGMENT);
                diaLogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
            }
        });
    }

    private String[] prepareQuestionsForDialogFragment() {
        int counter = 0;
        int innerCounter = 0;
        String[] questions = mnemonicWords.split("");
        String[] finalQuetions = new String[Integer.valueOf(kuknosShowRecoveryKeyVM.getSelectedLength())];
        Log.e("mcvcmvnmc", "prepareQuestionsForDialogFragment1: " + questions.length);
        for (int i = 0; i < questions.length; i++) {


            Log.e("mcvcmvnmc", "prepareQuestionsForDialogFragment: " + counter);
            if (counter < questions.length) {
                finalQuetions[innerCounter] = "";
                while (!questions[counter].equals(" ")) {
                    finalQuetions[innerCounter] += questions[counter];
                    counter++;
                    if (counter >= questions.length) {
                        break;
                    }
                }
            }
            counter++;
            innerCounter++;
            if (counter == questions.length) {
                break;
            }
        }
        return finalQuetions;
    }

    private void progressState() {
        kuknosShowRecoveryKeyVM.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_login_progress_str));
                binding.fragKuknosIdSubmit.setEnabled(false);
                binding.fragKuknosRKSProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosIdSubmit.setText(getString(R.string.kuknos_RecoverySK_Btn));
                binding.fragKuknosIdSubmit.setEnabled(true);
                binding.fragKuknosRKSProgressV.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // disable screenshot.
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
    }

    private class WordHolder extends RecyclerView.ViewHolder {
        String word;
        private TextView textView;

        public WordHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.word_items);
        }

        public void bind(String word, int position) {
            this.word = word;
            textView.setText(position + 1 + "  " + word);
        }
    }

    private class WordsAdapter extends RecyclerView.Adapter<WordHolder> {

        private List<String> words;

        public WordsAdapter(String[] words) {
            this.words = new ArrayList<>(Arrays.asList(words));
        }

        @NonNull
        @Override
        public WordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.mnemonic_words_item, parent, false);
            WordHolder doingHolder = new WordHolder(view);
            return doingHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull WordHolder holder, int position) {
            holder.bind(words.get(position), position);
        }

        @Override
        public int getItemCount() {
            return words.size();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null)
            return;
        boolean check = false;
        if (requestCode == REQUEST_CODE_FOR_DIALOGFRAGMENT) {
            check = data.getBooleanExtra(KuknosAskQuestionDialogFragment.newInstance().getCheckForOpenNextFragment(), false);
            Log.e("cmvnbjbvj", "onActivityResult: " + check);
            if (check) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment;
                fragment = fragmentManager.findFragmentByTag(KuknosSetPassFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosSetPassFrag.newInstance(0);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        }
    }
}

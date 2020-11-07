package net.iGap.kuknos.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import net.iGap.G;
import net.iGap.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class KuknosAskQuestionDialogFragment extends DialogFragment {
    public static final String QUESTIONS = "questions";
    private static final String CHECK_FOR_OPEN_NEXT_FRAGMENT = "checkFroOpenNextFragment";
    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;
    private Button fourthButton;
    private TextView questionTextView;
    private String buttonsTxtForChecking;
    private int counter = 0;
    private int randomNumber = 0;
    String trueAnswer;
    private List<String> questions;
    private HashMap<Integer, String> answersWithKey = new HashMap<>();
    private Random random = new Random();

    public static String getCheckForOpenNextFragment() {
        return CHECK_FOR_OPEN_NEXT_FRAGMENT;
    }

    public static KuknosAskQuestionDialogFragment newInstance(String[] questions) {
        KuknosAskQuestionDialogFragment fragment = new KuknosAskQuestionDialogFragment();
        Bundle args = new Bundle();
        args.putStringArray(QUESTIONS, questions);
        fragment.setArguments(args);
        return fragment;
    }

    public static KuknosAskQuestionDialogFragment newInstance() {
        KuknosAskQuestionDialogFragment fragment = new KuknosAskQuestionDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questions = new ArrayList(Arrays.asList(getArguments().getStringArray(QUESTIONS)));
        for (int i = 0; i < questions.size(); i++) {
            answersWithKey.put(i, questions.get(i));
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_kuknos_ask_question_dialog, null, false);
        initView(view);
        changeTheTextOfTextViewAndButtons();
        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsTxtForChecking = firstButton.getText().toString();
                if (checkTheClickedButton(buttonsTxtForChecking)) {
                    counter++;
                    if (counter >= 3) {
                        sendRsult(true);
                        dismiss();
                    } else {
                        changeTheTextOfTextViewAndButtons();
                    }
                } else {

                    Toast.makeText(getActivity(), getResources().getString(R.string.kuknos_toast), Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });
        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsTxtForChecking = secondButton.getText().toString();
                if (checkTheClickedButton(buttonsTxtForChecking)) {
                    counter++;
                    if (counter >= 3) {
                        sendRsult(true);
                        dismiss();
                    } else {
                        changeTheTextOfTextViewAndButtons();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.kuknos_toast), Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });
        thirdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsTxtForChecking = thirdButton.getText().toString();
                if (checkTheClickedButton(buttonsTxtForChecking)) {
                    counter++;
                    if (counter >= 3) {
                        sendRsult(true);
                        dismiss();
                    } else {
                        changeTheTextOfTextViewAndButtons();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.kuknos_toast), Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });
        fourthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonsTxtForChecking = fourthButton.getText().toString();
                if (checkTheClickedButton(buttonsTxtForChecking)) {
                    counter++;
                    if (counter >= 3) {
                        sendRsult(true);
                        dismiss();
                    } else {
                        changeTheTextOfTextViewAndButtons();
                    }
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.kuknos_toast), Toast.LENGTH_LONG).show();
                    dismiss();
                }
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    private void initView(View view) {
        firstButton = view.findViewById(R.id.kuknos_dialog_first_answer);
        secondButton = view.findViewById(R.id.kuknos_dialog_second_answer);
        thirdButton = view.findViewById(R.id.kuknos_dialog_third_answer);
        fourthButton = view.findViewById(R.id.kuknos_dialog_fourth_answer);
        questionTextView = view.findViewById(R.id.kuknos_dialog_question);
    }

    private boolean checkTheClickedButton(String txt) {
        if (txt.equals(trueAnswer)) {
            return true;
        }

        return false;
    }

    private void sendRsult(boolean isCompletedWithAnyMistake) {
        Fragment fragment = getTargetFragment();
        Intent intent = new Intent();
        intent.putExtra(CHECK_FOR_OPEN_NEXT_FRAGMENT, isCompletedWithAnyMistake);
        fragment.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

    private void changeTheTextOfTextViewAndButtons() {
        int randomNumberForTrueAnswer = random.nextInt(4);
        randomNumber = random.nextInt(questions.size());
        while (randomNumber == randomNumberForTrueAnswer || randomNumber == 0) {
            randomNumberForTrueAnswer = random.nextInt(4);
            randomNumber = random.nextInt(questions.size());
        }
        if (G.selectedLanguage.equals("fa")) {
            questionTextView.setText("کلمه" + " " + randomNumber + " " + "ام" + " " + "از کلمات بازیابی را انتخاب کنید:");
        } else {
            questionTextView.setText("Enter the " + randomNumber + " " + "Number From Recovery Keys:");
        }
        Collections.shuffle(questions);


        switch (randomNumberForTrueAnswer) {
            case 0:
                firstButton.setText(answersWithKey.get(randomNumber - 1));
                break;
            case 1:
                secondButton.setText(answersWithKey.get(randomNumber - 1));
                break;
            case 2:
                thirdButton.setText(answersWithKey.get(randomNumber - 1));
                break;
            case 3:
                fourthButton.setText(answersWithKey.get(randomNumber - 1));
                break;
        }
        trueAnswer = answersWithKey.get(randomNumber - 1);
        questions.remove(trueAnswer);

        if (randomNumberForTrueAnswer == 0) {
            do {
                secondButton.setText(questions.get(random.nextInt(questions.size())));
                thirdButton.setText(questions.get(random.nextInt(questions.size())));
                fourthButton.setText(questions.get(random.nextInt(questions.size())));
            } while (secondButton.getText().toString().equals(thirdButton.getText().toString()) ||
                    thirdButton.getText().toString().equals(fourthButton.getText().toString()) ||
                    secondButton.getText().toString().equals(fourthButton.getText().toString()));


        } else if (randomNumberForTrueAnswer == 1) {
            do {
                firstButton.setText(questions.get(random.nextInt(questions.size())));

                thirdButton.setText(questions.get(random.nextInt(questions.size())));

                fourthButton.setText(questions.get(random.nextInt(questions.size())));
            } while (firstButton.getText().toString().equals(thirdButton.getText().toString()) ||
                    thirdButton.getText().toString().equals(fourthButton.getText().toString()) ||
                    firstButton.getText().toString().equals(fourthButton.getText().toString()));


        } else if (randomNumberForTrueAnswer == 2) {
            do {
                firstButton.setText(questions.get(random.nextInt(questions.size())));

                secondButton.setText(questions.get(random.nextInt(questions.size())));

                fourthButton.setText(questions.get(random.nextInt(questions.size())));
            } while (firstButton.getText().toString().equals(secondButton.getText().toString()) ||
                    secondButton.getText().toString().equals(fourthButton.getText().toString()) ||
                    firstButton.getText().toString().equals(fourthButton.getText().toString()));


        } else {
            do {
                firstButton.setText(questions.get(random.nextInt(questions.size())));

                secondButton.setText(questions.get(random.nextInt(questions.size())));

                thirdButton.setText(questions.get(random.nextInt(questions.size())));
            } while (firstButton.getText().toString().equals(secondButton.getText().toString()) ||
                    secondButton.getText().toString().equals(thirdButton.getText().toString()) ||
                    firstButton.getText().toString().equals(thirdButton.getText().toString()));

        }

        refreshQuestionList();
    }

    private void refreshQuestionList() {
        questions = null;
        questions = new ArrayList(Arrays.asList(getArguments().getStringArray(QUESTIONS)));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kuknos_ask_question_dialog, container, false);
    }


}
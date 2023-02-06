package com.example.labgameprm392;

import static java.util.concurrent.TimeUnit.SECONDS;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //    TextView
    TextView balanceText;
    TextView firstPikachuText;
    TextView secondPikachuText;
    TextView thirdPikachuText;


    //    CheckBox
    CheckBox firstPikachuCheckBox;
    CheckBox secondPikachuCheckBox;
    CheckBox thirdPikachuCheckBox;

    //    EditText
    EditText firstPikachuAmountBet;
    EditText secondPikachuAmountBet;
    EditText thirdPikachuAmountBet;

    //    SeekBar
    SeekBar firstPikachuSeekBar;
    SeekBar secondPikachuSeekBar;
    SeekBar thirdPikachuSeekBar;

    //    Button
    Button startRaceBtn;


    private static final int maxCount = 100;
    private static final int minCount = 0;
    private static final int step = 10;
    private static final String REQUIRE = "Require";
    private static final String BALANCETEXT = "You balance: ";
    private final double balance = (int) (50 + (Math.random() * 150 + 1));
    private int firstRunnerStep = 0;
    private int secondRunnerStep = 0;
    private int thirdRunnerStep = 0;
    private ArrayList<String> rank = new ArrayList<>();


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TextView
        balanceText = findViewById(R.id.balanceText);
        firstPikachuText = findViewById(R.id.firstPikachuText);
        secondPikachuText = findViewById(R.id.secondPikachuText);
        thirdPikachuText = findViewById(R.id.thirdPikachuText);


//        CheckBox
        firstPikachuCheckBox = findViewById(R.id.firstPikachuCheckBox);
        secondPikachuCheckBox = findViewById(R.id.secondPikachuCheckBox);
        thirdPikachuCheckBox = findViewById(R.id.thirdPikachuCheckBox);


//        EditText
        firstPikachuAmountBet = findViewById(R.id.firstPikachuAmountBet);
        secondPikachuAmountBet = findViewById(R.id.secondPikachuAmountBet);
        thirdPikachuAmountBet = findViewById(R.id.thirdPikachuAmountBet);


//        SeekBar
        firstPikachuSeekBar = findViewById(R.id.firstPikachuSeekBar);
        secondPikachuSeekBar = findViewById(R.id.secondPikachuSeekBar);
        thirdPikachuSeekBar = findViewById(R.id.thirdPikachuSeekBar);


//        Button
        startRaceBtn = findViewById(R.id.startRaceBtn);


//        Set Enable SeekBar
        firstPikachuSeekBar.setEnabled(false);
        secondPikachuSeekBar.setEnabled(false);
        thirdPikachuSeekBar.setEnabled(false);


//        Set SeekBar max
        firstPikachuSeekBar.setMax(maxCount);
        secondPikachuSeekBar.setMax(maxCount);
        thirdPikachuSeekBar.setMax(maxCount);


//        Random your balance
        balanceText.setText(BALANCETEXT + balance + "$");


//        Set all EditText to disable
        firstPikachuAmountBet.setEnabled(false);
        firstPikachuAmountBet.setFocusable(false);
        secondPikachuAmountBet.setEnabled(false);
        secondPikachuAmountBet.setFocusable(false);
        thirdPikachuAmountBet.setEnabled(false);
        thirdPikachuAmountBet.setFocusable(false);


//        Check Box on checked

//        First
        firstPikachuCheckBox.setOnClickListener(
                v -> {
                    if (firstPikachuCheckBox.isChecked()) {
                        firstPikachuAmountBet.setFocusable(true);
                        firstPikachuAmountBet.setFocusableInTouchMode(true);
                        firstPikachuAmountBet.setEnabled(true);
                    } else {
                        firstPikachuAmountBet.setError(null);
                        firstPikachuAmountBet.setText("");
                        firstPikachuAmountBet.setEnabled(false);
                        firstPikachuAmountBet.setFocusable(false);
                    }
                }
        );

//        Second
        secondPikachuCheckBox.setOnClickListener(
                v -> {
                    if (secondPikachuCheckBox.isChecked()) {
                        secondPikachuAmountBet.setFocusable(true);
                        secondPikachuAmountBet.setFocusableInTouchMode(true);
                        secondPikachuAmountBet.setEnabled(true);
                    } else {
                        secondPikachuAmountBet.setError(null);
                        secondPikachuAmountBet.setText("");
                        secondPikachuAmountBet.setEnabled(false);
                        secondPikachuAmountBet.setFocusable(false);
                    }
                }
        );

//        Third
        thirdPikachuCheckBox.setOnClickListener(
                v -> {
                    if (thirdPikachuCheckBox.isChecked()) {
                        thirdPikachuAmountBet.setFocusable(true);
                        thirdPikachuAmountBet.setFocusableInTouchMode(true);
                        thirdPikachuAmountBet.setEnabled(true);
                    } else {
                        thirdPikachuAmountBet.setError(null);
                        thirdPikachuAmountBet.setText("");
                        thirdPikachuAmountBet.setEnabled(false);
                        thirdPikachuAmountBet.setFocusable(false);
                    }
                }
        );


//        Start Race
        startRaceBtn.setOnClickListener(
                v -> {

                    if (!checkInput() || !checkBalance()) {
                        return;
                    }

                    Thread.currentThread().interrupt();

//                    Set all disable
//                    startRaceBtn.setEnabled(false);
//                    firstPikachuCheckBox.setEnabled(false);
//                    secondPikachuCheckBox.setEnabled(false);
//                    thirdPikachuCheckBox.setEnabled(false);

                    rank.clear();

                    firstRunnerStep = 0;
                    secondRunnerStep = 0;
                    thirdRunnerStep = 0;

                    SeekBarRunner firstPikachu = new SeekBarRunner(firstPikachuText.getText().toString());
                    SeekBarRunner secondPikachu = new SeekBarRunner(secondPikachuText.getText().toString());
                    SeekBarRunner thirdPikachu = new SeekBarRunner(thirdPikachuText.getText().toString());

                    firstPikachuSeekBar.setProgress(minCount);
                    secondPikachuSeekBar.setProgress(minCount);
                    thirdPikachuSeekBar.setProgress(minCount);

                    Runnable firstRunner = () -> {
                        while (firstPikachu.isRunning()) {
                            int randomNum = (int) (0 + (Math.random() * step + 1));
                            if (firstRunnerStep < maxCount) {

                                firstRunnerStep += randomNum;

                                firstPikachu.setProgress(firstRunnerStep);

                                firstPikachuSeekBar.setProgress(firstRunnerStep);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                rank.add(firstPikachu.getName());
                                firstPikachu.setRunning(false);

                            }
                        }
                    };

                    Runnable secondRunner = () -> {
                        while (secondPikachu.isRunning()) {
                            int randomNum = (int) (0 + (Math.random() * step + 1));
                            if (secondRunnerStep < maxCount) {

                                secondRunnerStep += randomNum;

                                secondPikachu.setProgress(secondRunnerStep);

                                secondPikachuSeekBar.setProgress(secondRunnerStep);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                rank.add(secondPikachu.getName());
                                secondPikachu.setRunning(false);
                            }
                        }
                    };

                    Runnable thirdRunner = () -> {
                        while (thirdPikachu.isRunning()) {
                            int randomNum = (int) (0 + (Math.random() * step + 1));
                            if (thirdRunnerStep < maxCount) {

                                thirdRunnerStep += randomNum;

                                thirdPikachu.setProgress(thirdRunnerStep);

                                thirdPikachuSeekBar.setProgress(thirdRunnerStep);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                rank.add(thirdPikachu.getName());
                                thirdPikachu.setRunning(false);
                            }
                        }
                    };


//                    new Thread(firstRunner).start();
//                    new Thread(secondRunner).start();
//                    new Thread(thirdRunner).start();


                    Thread first = new Thread(firstRunner);
                    Thread second = new Thread(secondRunner);
                    Thread third = new Thread(thirdRunner);


                    first.start();
                    second.start();
                    third.start();



//                    startRaceBtn.setEnabled(true);
//                    startRaceBtn.setFocusable(true);
//                    firstPikachuCheckBox.setEnabled(true);
//                    firstPikachuCheckBox.setFocusable(true);
//                    secondPikachuCheckBox.setEnabled(true);
//                    secondPikachuCheckBox.setFocusable(true);
//                    thirdPikachuCheckBox.setEnabled(true);
//                    thirdPikachuCheckBox.setFocusable(true);
//
//                    Toast.makeText(this, rank.get(0) + " won the Race!!!"
//                            , Toast.LENGTH_LONG).show();
                }
        );
    }


    //    Check Input field
    private boolean checkInput() {
        if (firstPikachuCheckBox.isChecked() && TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {
            firstPikachuAmountBet.setError(REQUIRE);
            return false;
        }

        if (secondPikachuCheckBox.isChecked() && TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
            secondPikachuAmountBet.setError(REQUIRE);
            return false;
        }

        if (thirdPikachuCheckBox.isChecked() && TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
            thirdPikachuAmountBet.setError(REQUIRE);
            return false;
        }


        return true;
    }

    //    Check amount that bet
    private boolean checkBalance() {

        double firstPikachuBet = 0;
        double secondPikachuBet = 0;
        double thirdPikachuBet = 0;

        if (!TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {
            firstPikachuBet = Double.parseDouble(firstPikachuAmountBet.getText().toString());
        }

        if (!TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
            secondPikachuBet = Double.parseDouble(secondPikachuAmountBet.getText().toString());
        }

        if (!TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
            thirdPikachuBet = Double.parseDouble(thirdPikachuAmountBet.getText().toString());
        }

        double amountBet = firstPikachuBet + secondPikachuBet + thirdPikachuBet;

        if (balance - amountBet < 0) {
            Toast.makeText(this, "You can not bet more than what you have!"
                    , Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

//    Bet Check


}
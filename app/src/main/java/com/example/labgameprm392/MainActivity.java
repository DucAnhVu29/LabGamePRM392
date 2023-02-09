package com.example.labgameprm392;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


enum State {
    START,
    RESET,
}

public class MainActivity extends AppCompatActivity {

    //    TextView
    TextView balanceText;

    //    EditText
    EditText firstPikachuAmountBet;
    EditText secondPikachuAmountBet;
    EditText thirdPikachuAmountBet;

    //    SeekBar
    ArrayList<SeekBar> seekBarList = new ArrayList<>();

    //    Button
    Button startRaceBtn;

    //    State
    private State state = State.START;

    //    Bet List
    Double[] betAmountList = {0.0, 0.0, 0.0};

    //    Rank Text View list
    TextView[] rankTextViewList = {null, null, null};

    //    Pikachu List
    SeekBarRunner[] pikachuList = {null, null, null};

    //    Gif Image List
    GifImageView[] pikachuGifList = {null, null, null};

    private static final int TICK_TIME = 30;
    private static final int MAX_COUNT = 20000;
    private static final int MIN_COUNT = 0;
    private double balance = (int) (50 + (Math.random() * 150 + 1));
    private final ArrayList<String> rank = new ArrayList<>();
    private static final int INITIAL_SPEED = 10;

    private int nextRank = 1;

    private static final Logger logger = null;

    DecimalFormat decimalFormat = new DecimalFormat("###.###");

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        pikachuGifList[0] = findViewById(R.id.pikachu_gif_0);
        pikachuGifList[1] = findViewById(R.id.pikachu_gif_1);
        pikachuGifList[2] = findViewById(R.id.pikachu_gif_2);

        rankTextViewList[0] = findViewById(R.id.rank_0);
        rankTextViewList[1] = findViewById(R.id.rank_1);
        rankTextViewList[2] = findViewById(R.id.rank_2);


//        TextView
        balanceText = findViewById(R.id.balanceText);


//        EditText
        firstPikachuAmountBet = findViewById(R.id.firstPikachuAmountBet);
        secondPikachuAmountBet = findViewById(R.id.secondPikachuAmountBet);
        thirdPikachuAmountBet = findViewById(R.id.thirdPikachuAmountBet);


//        SeekBar
        seekBarList.add(findViewById(R.id.firstPikachuSeekBar));
        seekBarList.add(findViewById(R.id.secondPikachuSeekBar));
        seekBarList.add(findViewById(R.id.thirdPikachuSeekBar));


//        Button
        startRaceBtn = findViewById(R.id.startRaceBtn);


//        Set SeekBar disable and max
        for (SeekBar seekbar : seekBarList) {
            seekbar.setEnabled(false);
            seekbar.setMax(MAX_COUNT);
            seekbar.setOnSeekBarChangeListener(
                    onSeekBarChangeListener(seekBarList.indexOf(seekbar))
            );
        }


        // Pikachu List
        pikachuList = new SeekBarRunner[]{
                new SeekBarRunner("1", INITIAL_SPEED),
                new SeekBarRunner("2", INITIAL_SPEED),
                new SeekBarRunner("3", INITIAL_SPEED),
        };


//        Random your balance
        balanceText.setText(balance + "");


//        Start Race
        startRaceBtn.setOnClickListener(
                v -> {
                    if (state == State.START) {
                        resetRace();
                        Thread.currentThread().interrupt();

                        if (!checkInput() || !checkBalance()) {
                            return;
                        }

                        Runnable[] runnables = {null, null, null};

                        for (int i = 0; i < runnables.length; ++i) {
                            runnables[i] = createRunnable(i);
                        }

                        for (Runnable runnable : runnables) {
                            Thread t = new Thread(runnable);
                            t.start();
                        }

                        firstPikachuAmountBet.setVisibility(View.INVISIBLE);
                        secondPikachuAmountBet.setVisibility(View.INVISIBLE);
                        thirdPikachuAmountBet.setVisibility(View.INVISIBLE);

                        startRaceBtn.setText("Chơi lại");
                        startRaceBtn.setEnabled(false);
                        startRaceBtn.setBackgroundColor(0xFFe0e0e0);
                        state = State.RESET;
                    } else if (state == State.RESET) {
                        resetRace();

                        firstPikachuAmountBet.setVisibility(View.VISIBLE);
                        secondPikachuAmountBet.setVisibility(View.VISIBLE);
                        thirdPikachuAmountBet.setVisibility(View.VISIBLE);
                        startRaceBtn.setText("Bắt đầu");

                        state = State.START;
                    }
                }
        );
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener(int i) {
        return new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                // TODO document why this method is empty
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
                // TODO document why this method is empty
            }

            public void onProgressChanged(SeekBar bar,
                                          int paramInt, boolean paramBoolean) {
                ViewGroup.MarginLayoutParams params =
                        (ViewGroup.MarginLayoutParams) pikachuGifList[i].getLayoutParams();
                params.setMarginStart((int) ((double) paramInt / MAX_COUNT *
                        (seekBarList.get(i).getWidth() - pikachuGifList[i].getWidth())));
                pikachuGifList[i].requestLayout();
            }
        };
    }

    private void resetRace() {
        rank.clear();

        nextRank = 1;

        for (TextView rankTextView : rankTextViewList) {
            rankTextView.setText("");
        }

        for (GifImageView pikachuGif : pikachuGifList) {
            ((GifDrawable) pikachuGif.getDrawable()).start();
        }

        for (SeekBarRunner pikachu : pikachuList) {
            pikachu.setSpeed(INITIAL_SPEED);
            pikachu.setProgress(0);
            pikachu.setRunning(true);
        }

        for (SeekBar seekBar : seekBarList) {
            seekBar.setProgress(MIN_COUNT);
        }
    }

    @SuppressLint("SetTextI18n")
    private Runnable createRunnable(int i) {
        return () -> {
            while (pikachuList[i].isRunning()) {
                pikachuList[i].setSpeed(randomSpeed(pikachuList[i].getSpeed()));

                if (pikachuList[i].getProgress() < MAX_COUNT) {
                    pikachuList[i].setProgress(pikachuList[i].getProgress() + pikachuList[i].getSpeed());

                    seekBarList.get(i).setProgress(pikachuList[i].getProgress());
                    try {
                        Thread.sleep(TICK_TIME);
                    } catch (InterruptedException e) {
                        assert false;
                        logger.log(Level.WARNING, "Interrupted!", e);
                    }

                } else {
                    rank.add(pikachuList[i].getName());
                    pikachuList[i].setRank(nextRank);
                    nextRank++;
                    pikachuList[i].setRunning(false);
                    ((GifDrawable) pikachuGifList[i].getDrawable()).stop();

                    if (pikachuList[i].getRank() == 1) {
                        balance += betAmountList[i] * 2;
                        balance = Double.parseDouble(decimalFormat.format(balance));
                    }

                    runOnUiThread(() -> {
                        rankTextViewList[i].setText(Integer.toString(pikachuList[i].getRank()));

                        if (nextRank > 3) {
                            startRaceBtn.setEnabled(true);
                            startRaceBtn.setBackgroundColor(0xFFF6EB05);

                            balanceText.setText(Double.toString(balance));
                        }
                    });


                    Thread.currentThread().interrupt();

                }
            }
        };
    }

    //    Check Input field
    private boolean checkInput() {
        int count = 0;

        if (!TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {
            count++;
        }

        if (!TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
            count++;
        }

        if (!TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
            count++;
        }

        if (count >= 3) {
            Toast.makeText(this, "Cược tối đa 2 Pikachu thoyyy",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //    Check amount that bet
    @SuppressLint("SetTextI18n")
    private boolean checkBalance() {

        double firstPikachuBet = 0;
        double secondPikachuBet = 0;
        double thirdPikachuBet = 0;

        if (!TextUtils.isEmpty(firstPikachuAmountBet.getText().toString())) {
            firstPikachuBet = Double.parseDouble(firstPikachuAmountBet.getText().toString());
            betAmountList[0] = firstPikachuBet;
        }

        if (!TextUtils.isEmpty(secondPikachuAmountBet.getText().toString())) {
            secondPikachuBet = Double.parseDouble(secondPikachuAmountBet.getText().toString());
            betAmountList[1] = secondPikachuBet;
        }

        if (!TextUtils.isEmpty(thirdPikachuAmountBet.getText().toString())) {
            thirdPikachuBet = Double.parseDouble(thirdPikachuAmountBet.getText().toString());
            betAmountList[2] = thirdPikachuBet;
        }

        double amountBet = firstPikachuBet + secondPikachuBet + thirdPikachuBet;

        if (balance - amountBet < 0) {
            Toast.makeText(this, "Hong đủ tiền má oyy",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        balance -= amountBet;

        balance = Double.parseDouble(decimalFormat.format(balance));

        firstPikachuAmountBet.setText("");
        secondPikachuAmountBet.setText("");
        thirdPikachuAmountBet.setText("");

        balanceText.setText(Double.toString(balance));

        return true;
    }

    private int randomSpeed(int previousSpeed) {
        int randomNumber = (int) (Math.random() * 5 - 2);

        return Math.max(previousSpeed + randomNumber, 0);

    }
}
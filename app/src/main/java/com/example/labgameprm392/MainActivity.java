package com.example.labgameprm392;


import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
    EditText hitokageAmountBet;
    EditText zenigameAmountBet;
    EditText fushigidaneAmountBet;

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

    //    Pokemon List
    SeekBarRunner[] pokemonList = {null, null, null};

    //    Gif Image List
    GifImageView[] pokemonGifList = {null, null, null};

//    Audio
    MediaPlayer pokemonThemeSong;
    MediaPlayer runningThemeSong;
    MediaPlayer winningThemeSong;

    private static final int TICK_TIME = 50;
    private static final int MAX_COUNT = 20000;
    private static final int MIN_COUNT = 0;
    private double balance = (int) (50 + (Math.random() * 150 + 1));
    private final ArrayList<String> rank = new ArrayList<>();
    private static final int INITIAL_SPEED = 10;

    private int nextRank = 1;

    private static final Logger logger = null;

    private final DecimalFormat decimalFormat = new DecimalFormat("###.###");

    private SeekBarRunner seekBarWinner = null;

    boolean checkThread = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        pokemonGifList[0] = findViewById(R.id.hitokageGif);
        pokemonGifList[1] = findViewById(R.id.zenigameGif);
        pokemonGifList[2] = findViewById(R.id.fushigidaneGif);

        rankTextViewList[0] = findViewById(R.id.rank_0);
        rankTextViewList[1] = findViewById(R.id.rank_1);
        rankTextViewList[2] = findViewById(R.id.rank_2);


//        TextView
        balanceText = findViewById(R.id.balanceText);


//        EditText
        hitokageAmountBet = findViewById(R.id.hitokageAmountBet);
        zenigameAmountBet = findViewById(R.id.zenigameAmountBet);
        fushigidaneAmountBet = findViewById(R.id.fushigidaneAmountBet);


//        SeekBar
        seekBarList.add(findViewById(R.id.hitokageSeekBar));
        seekBarList.add(findViewById(R.id.zenigameSeekBar));
        seekBarList.add(findViewById(R.id.fushigidaneSeekBar));


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


        // Pokemon List
        pokemonList = new SeekBarRunner[]{
                new SeekBarRunner("Hitokage", INITIAL_SPEED, R.drawable.chamander),
                new SeekBarRunner("Zenigame", INITIAL_SPEED, R.drawable.squirtle_smile),
                new SeekBarRunner("Fushigidane", INITIAL_SPEED, R.drawable.bulbasaur_pokemon),
        };


//        Random your balance
        balanceText.setText(balance + "");


//      Audio
        pokemonThemeSong = MediaPlayer.create(this, R.raw.pokemon_theme_song);
        runningThemeSong = MediaPlayer.create(this, R.raw.background_theme);
        winningThemeSong = MediaPlayer.create(this, R.raw.vitory_theme);


//        Game default audio
        pokemonThemeSong.setLooping(true);
        pokemonThemeSong.start();


//        Start Race
        startRaceBtn.setOnClickListener(
                v -> {
                    if (state == State.START) {
                        resetRace();

                        pokemonThemeSong.pause();
                        pokemonThemeSong.seekTo(0);
                        runningThemeSong.start();

                        Thread.currentThread().interrupt();

                        if (!checkInput() || !checkBalance()) {
                            return;
                        }

                        Runnable[] runnableList = {null, null, null};

                        for (int i = 0; i < runnableList.length; ++i) {
                            runnableList[i] = createRunnable(i);
                        }

                        for (Runnable runnable : runnableList) {
                            Thread t = new Thread(runnable);
                            t.start();
                        }

                        hitokageAmountBet.setVisibility(View.INVISIBLE);
                        zenigameAmountBet.setVisibility(View.INVISIBLE);
                        fushigidaneAmountBet.setVisibility(View.INVISIBLE);

                        startRaceBtn.setText("Ch??i l???i");
                        startRaceBtn.setEnabled(false);
                        startRaceBtn.setBackgroundColor(0xFFe0e0e0);
                        state = State.RESET;
                    } else if (state == State.RESET) {

                        resetRace();

                        hitokageAmountBet.setVisibility(View.VISIBLE);
                        zenigameAmountBet.setVisibility(View.VISIBLE);
                        fushigidaneAmountBet.setVisibility(View.VISIBLE);
                        startRaceBtn.setText("B???t ?????u");

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
                        (ViewGroup.MarginLayoutParams) pokemonGifList[i].getLayoutParams();
                params.setMarginStart((int) ((double) paramInt / MAX_COUNT *
                        (seekBarList.get(i).getWidth() - pokemonGifList[i].getWidth())));
                pokemonGifList[i].requestLayout();
            }
        };
    }

    private void resetRace() {
        rank.clear();

        betAmountList[0] = 0.0;
        betAmountList[1] = 0.0;
        betAmountList[2] = 0.0;

        nextRank = 1;

        for (TextView rankTextView : rankTextViewList) {
            rankTextView.setText("");
        }

        for (GifImageView pokemonGif : pokemonGifList) {
            ((GifDrawable) pokemonGif.getDrawable()).start();
        }

        for (SeekBarRunner pokemon : pokemonList) {
            pokemon.setSpeed(INITIAL_SPEED);
            pokemon.setProgress(0);
            pokemon.setRunning(true);
        }

        for (SeekBar seekBar : seekBarList) {
            seekBar.setProgress(MIN_COUNT);
        }
    }

    @SuppressLint("SetTextI18n")
    private Runnable createRunnable(int i) {
        return () -> {
            checkThread = false;

            while (pokemonList[i].isRunning()) {
                pokemonList[i].setSpeed(randomSpeed(pokemonList[i].getSpeed()));

                if (pokemonList[i].getProgress() < MAX_COUNT) {
                    pokemonList[i].setProgress(pokemonList[i].getProgress() + pokemonList[i].getSpeed());

                    seekBarList.get(i).setProgress(pokemonList[i].getProgress());
                    try {
                        Thread.sleep(TICK_TIME);
                    } catch (InterruptedException e) {
                        assert false;
                        logger.log(Level.WARNING, "Interrupted!", e);
                    }

                } else {
                    rank.add(pokemonList[i].getName());
                    pokemonList[i].setRank(nextRank);
                    nextRank++;
                    pokemonList[i].setRunning(false);
                    ((GifDrawable) pokemonGifList[i].getDrawable()).stop();

                    if (pokemonList[i].getRank() == 1) {
                        seekBarWinner = pokemonList[i];
                        balance += betAmountList[i] * 2;
                        balance = Double.parseDouble(decimalFormat.format(balance));
                    }

                    runOnUiThread(() -> {
                        rankTextViewList[i].setText(Integer.toString(pokemonList[i].getRank()));

                        if (nextRank > 3) {
                            startRaceBtn.setEnabled(true);
                            startRaceBtn.setBackgroundColor(0xFFF6EB05);
                            checkThread = true;
                            checkThreadOpenPopup();

                            balanceText.setText(Double.toString(balance));
                        }
                    });

                    Thread.currentThread().interrupt();

                }
            }
        };
    }

    private void checkThreadOpenPopup(){
        if (checkThread) openPopupResult(Gravity.CENTER, seekBarWinner);
    }

    @SuppressLint("SetTextI18n")
    private void openPopupResult(int gravity, SeekBarRunner seekBarRunner){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_result);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }

        runningThemeSong.pause();
        runningThemeSong.seekTo(0);
        winningThemeSong.start();

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(Gravity.BOTTOM == gravity);

        String win = " won!";

        TextView winnerName = dialog.findViewById(R.id.winnerName);
        winnerName.setText(seekBarRunner.getName() + win);

        GifImageView winnerGif = dialog.findViewById(R.id.winnerGif);
        winnerGif.setBackgroundResource(seekBarRunner.getImage());

        Button closePopup = dialog.findViewById(R.id.closePopup);

        closePopup.setOnClickListener(v -> {
            pokemonThemeSong.start();
            winningThemeSong.pause();
            winningThemeSong.seekTo(0);
            dialog.dismiss();
        });

        dialog.show();
    }

    //    Check Input field
    private boolean checkInput() {
        int count = 0;

        if (!TextUtils.isEmpty(hitokageAmountBet.getText().toString())) {
            count++;
        }

        if (!TextUtils.isEmpty(zenigameAmountBet.getText().toString())) {
            count++;
        }

        if (!TextUtils.isEmpty(fushigidaneAmountBet.getText().toString())) {
            count++;
        }

        if (count >= 3) {
            Toast.makeText(this, "C?????c t???i ??a 2 Pokemon thoyyy",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    //    Check amount that bet
    @SuppressLint("SetTextI18n")
    private boolean checkBalance() {

        double hitokageBet = 0;
        double zenigameBet = 0;
        double fushigidaneBet = 0;

        if (!TextUtils.isEmpty(hitokageAmountBet.getText().toString())) {
            hitokageBet = Double.parseDouble(hitokageAmountBet.getText().toString());
            betAmountList[0] = hitokageBet;
        }

        if (!TextUtils.isEmpty(zenigameAmountBet.getText().toString())) {
            zenigameBet = Double.parseDouble(zenigameAmountBet.getText().toString());
            betAmountList[1] = zenigameBet;
        }

        if (!TextUtils.isEmpty(fushigidaneAmountBet.getText().toString())) {
            fushigidaneBet = Double.parseDouble(fushigidaneAmountBet.getText().toString());
            betAmountList[2] = fushigidaneBet;
        }

        double amountBet = hitokageBet + zenigameBet + fushigidaneBet;

        if (balance - amountBet < 0) {
            Toast.makeText(this, "Hong ????? ti???n m?? oyy",
                    Toast.LENGTH_LONG).show();
            return false;
        }

        balance -= amountBet;

        balance = Double.parseDouble(decimalFormat.format(balance));

        hitokageAmountBet.setText("");
        zenigameAmountBet.setText("");
        fushigidaneAmountBet.setText("");

        balanceText.setText(Double.toString(balance));

        return true;
    }

    private int randomSpeed(int previousSpeed) {
        int randomNumber = (int) (Math.random() * 5 - 2);

        return Math.max(previousSpeed + randomNumber, 0);

    }
}
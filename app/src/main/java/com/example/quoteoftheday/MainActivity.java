package com.example.quoteoftheday;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quoteoftheday.database.QuoteDao;
import com.example.quoteoftheday.database.QuoteDatabase;
import com.example.quoteoftheday.models.Quote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView quoteTextView, authorTextView, dateTextView;
    private Button refreshButton, shareButton, favoriteButton, viewFavoritesButton;
    private Quote currentQuote;
    private QuoteDatabase database;
    private List<Quote> quoteList;
    private Random random;
    private static final String TAG = "MainActivity";
    private int lastQuoteIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        initializeViews();
        setupDatabase();
        loadQuotes();
        displayRandomQuote();
        setupListeners();
        updateDate();
    }

    private void initializeViews() {
        quoteTextView = findViewById(R.id.quoteTextView);
        authorTextView = findViewById(R.id.authorTextView);
        dateTextView = findViewById(R.id.dateTextView);
        refreshButton = findViewById(R.id.refreshButton);
        shareButton = findViewById(R.id.shareButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        viewFavoritesButton = findViewById(R.id.viewFavoritesButton);
        random = new Random();

        Log.d(TAG, "Views initialized");
    }

    private void setupDatabase() {
        database = QuoteDatabase.getInstance(this);
        Log.d(TAG, "Database setup complete");
    }

    private void loadQuotes() {
        quoteList = new ArrayList<>();
        quoteList.add(new Quote("Some secrets are better kept secrets", "Sarah Abbas"));
        quoteList.add(new Quote("Tough World, Rough Opinions", "Sarah Abbas"));
        quoteList.add(new Quote("Love is all about thorns and roses", "Sarah Abbas"));
        quoteList.add(new Quote("Humans tend to blame themselves when misfortune strikes their loved ones.", "Sarah Abbas"));
        quoteList.add(new Quote("Love is what you give without any expectations", "Sarah Abbas"));
        quoteList.add(new Quote("It's better to be silent than being violent", "Sarah Abbas"));
        quoteList.add(new Quote("Life is what happens to you while you're busy making other plans.", "John Lennon"));
        quoteList.add(new Quote("I love you. Thorns and all.", "Sarah J. Mass"));
        quoteList.add(new Quote("A person who never learns never earns.", "Sarah Abbas"));
        quoteList.add(new Quote("The secret of getting ahead is getting started.", "Mark Twain"));
        quoteList.add(new Quote("The only way to do great work is to love what you do.", "Steve Jobs"));
        quoteList.add(new Quote("In the middle of difficulty lies opportunity.", "Albert Einstein"));
        quoteList.add(new Quote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill"));
        quoteList.add(new Quote("The best time to plant a tree was 20 years ago. The second best time is now.", "Chinese Proverb"));
        quoteList.add(new Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"));

        Log.d(TAG, "Loaded " + quoteList.size() + " quotes");
    }

    private void displayRandomQuote() {
        if (quoteList != null && !quoteList.isEmpty()) {
            int index;

            do {
                index = random.nextInt(quoteList.size());
            } while (index == lastQuoteIndex && quoteList.size() > 1);

            lastQuoteIndex = index;
            currentQuote = quoteList.get(index);

            String quoteText = "\"" + currentQuote.getText() + "\"";
            String authorText = "- " + currentQuote.getAuthor();

            quoteTextView.setText(quoteText);
            authorTextView.setText(authorText);

            Log.d(TAG, "Displaying quote #" + (index + 1) + ": " + quoteText);
            Log.d(TAG, "Author: " + authorText);
        } else {
            Log.e(TAG, "Quote list is empty!");
            quoteTextView.setText("No quotes available");
            authorTextView.setText("");
        }
    }

    private void setupListeners() {
        refreshButton.setOnClickListener(v -> {
            Log.d(TAG, "Refresh button clicked");
            displayRandomQuote();
        });

        shareButton.setOnClickListener(v -> {
            Log.d(TAG, "Share button clicked");
            shareQuote();
        });

        favoriteButton.setOnClickListener(v -> {
            Log.d(TAG, "Favorite button clicked");
            saveFavorite();
        });

        viewFavoritesButton.setOnClickListener(v -> {
            Log.d(TAG, "View Favorites button clicked");
            Intent intent = new Intent(MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        });
    }

    private void shareQuote() {
        if (currentQuote != null) {
            String shareText = "\"" + currentQuote.getText() + "\"\n- " + currentQuote.getAuthor();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(shareIntent, "Share Quote via"));
            Log.d(TAG, "Sharing quote");
        } else {
            Toast.makeText(this, "No quote to share!", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveFavorite() {
        if (currentQuote != null) {
            favoriteButton.setEnabled(false);
            favoriteButton.setText("Saving...");

            new Thread(() -> {
                try {
                    QuoteDao dao = database.quoteDao();

                    // Check if already in favorites
                    List<Quote> favorites = dao.getAllFavorites();
                    boolean exists = false;
                    for (Quote q : favorites) {
                        if (q.getText().equals(currentQuote.getText()) &&
                                q.getAuthor().equals(currentQuote.getAuthor())) {
                            exists = true;
                            break;
                        }
                    }

                    final boolean finalExists = exists;

                    if (!exists) {
                        Quote favoriteQuote = new Quote(currentQuote.getText(), currentQuote.getAuthor());
                        dao.insert(favoriteQuote);
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "⭐ Quote saved to favorites!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Quote saved to favorites");
                            favoriteButton.setEnabled(true);
                            favoriteButton.setText(R.string.favorite);
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this,
                                    "⚠️ Quote already in favorites!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Quote already in favorites");
                            favoriteButton.setEnabled(true);
                            favoriteButton.setText(R.string.favorite);
                        });
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error saving favorite: " + e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this,
                                "Error saving quote!", Toast.LENGTH_SHORT).show();
                        favoriteButton.setEnabled(true);
                        favoriteButton.setText(R.string.favorite);
                    });
                }
            }).start();
        }
    }

    private void updateDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText("Today: " + currentDate);
        Log.d(TAG, "Date updated: " + currentDate);
    }
}
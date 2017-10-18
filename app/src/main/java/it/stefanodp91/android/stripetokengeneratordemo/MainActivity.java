package it.stefanodp91.android.stripetokengeneratordemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

/**
 * Created by stefanodp91 on 07/10/17.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();

    // the stripe publishable key
    // TODO: 18/10/17 replace with a public production key
    private static final String PUBLISHABLE_KEY = "pk_test_Kw0OhxztY1RfT5DKRvWlm5pP"; // ch public test key

    // Note that if the data in the widget is either incomplete or
    // fails client-side validity checks, the Card object will be null.
    private CardInputWidget mCardInputWidget;
    private Button mSaveBtn;
    private ProgressBar mProgress;
    private TextView mResponseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // card input stripe widget
        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);

        mProgress = findViewById(R.id.progress_bar);

        mResponseView = (TextView) findViewById(R.id.response);

        // save button
        mSaveBtn = (Button) findViewById(R.id.generate);
        mSaveBtn.setOnClickListener(this);
    }

    // create the card
    private Card createCard() {
        Card card = mCardInputWidget.getCard();
        if (card == null) {

            Log.e(TAG, "Invalid Card Data");
        }

//        // according to stripe documentation it is possible to set optionals params
//        cardToSave.setName("Customer Name");
//        cardToSave.setAddressZip("12345");

        return card;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.generate) {
            onSaveClickAction();
        }
    }

    // perfom
    private void onSaveClickAction() {

        mProgress.setVisibility(View.VISIBLE); // show a progress

        // if the response is already used, clean it
        mResponseView.setText(""); // clean the response textview

        // create card
        Card card = createCard();

        // Remember to validate the card object before you use it to save time.
        if (card.validateCard()) {

            // set the publishable key
            Stripe stripe = new Stripe(MainActivity.this, PUBLISHABLE_KEY);

            // create the stripe token
            stripe.createToken(card, onTokenRetrievedCallback);
        }
    }

    // callback called when the stripe token has been retrieved
    private TokenCallback onTokenRetrievedCallback = new TokenCallback() {
        @Override
        public void onError(Exception error) {
            if (mResponseView != null) {
                mResponseView.setTextColor(Color.parseColor("#F44336")); // text color red
                mResponseView.setText(error.toString()); // error message
            }

            if (mProgress != null)
                mProgress.setVisibility(View.GONE); // dismiss the progress
        }

        @Override
        public void onSuccess(Token token) {
            // retrieve the token id
            String tokenId = token.getId();
            Log.d(TAG, "stripeToken: " + tokenId);

            if (mResponseView != null) {
                mResponseView.setTextColor(Color.parseColor("#9E9E9E")); // text color gray
                mResponseView.setText(tokenId); // response
            }

            if (mProgress != null)
                mProgress.setVisibility(View.GONE); // dismiss the progress
        }
    };
}
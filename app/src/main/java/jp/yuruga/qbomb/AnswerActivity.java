package jp.yuruga.qbomb;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.content.res.Resources;
import android.widget.Toast;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;

import static jp.yuruga.qbomb.common.Share.*;
import static jp.yuruga.qbomb.common.Constants.*;


public class AnswerActivity extends Activity {

    private CardContainer mCardContainer;

    @Override
    protected void onNewIntent(Intent intent) {
        log("AnswerActivity:onNewIntent with bomb_id: "+intent.getStringExtra("bombId"));
        Toast.makeText(this, "AnswerActivity:onNewIntent with bomb_id: "+intent.getStringExtra("bombId"),
                Toast.LENGTH_LONG).show();
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        mCardContainer = (CardContainer) findViewById(R.id.cardContainer);

        Resources r = getResources();

        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(this);

        adapter.add(new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title2", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title3", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title4", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title5", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title6", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title2", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title3", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title4", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title5", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title6", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title2", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title3", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title4", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title5", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title6", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title2", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title3", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title4", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title5", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title6", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title2", "Description goes here", r.getDrawable(R.drawable.picture2)));
        adapter.add(new CardModel("Title3", "Description goes here", r.getDrawable(R.drawable.picture3)));
        adapter.add(new CardModel("Title4", "Description goes here", r.getDrawable(R.drawable.picture1)));
        adapter.add(new CardModel("Title5", "Description goes here", r.getDrawable(R.drawable.picture2)));

        CardModel cardModel = new CardModel("Title1", "Description goes here", r.getDrawable(R.drawable.picture1));
        cardModel.setOnClickListener(new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                Log.i("Swipeable Cards", "I am pressing the card");
            }
        });

        cardModel.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
            @Override
            public void onLike() {
                Log.i("Swipeable Cards","I like the card");
            }

            @Override
            public void onDislike() {
                Log.i("Swipeable Cards","I dislike the card");
            }
        });

        adapter.add(cardModel);

        mCardContainer.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.answer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_answer, container, false);
            return rootView;
        }
    }
}

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
import android.widget.TextView;
import android.widget.Toast;

import com.andtinder.model.CardModel;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import org.json.JSONException;
import org.json.JSONObject;



import java.util.HashMap;

import jp.yuruga.qbomb.model.QCardModel;

import static jp.yuruga.qbomb.common.Share.*;
import static jp.yuruga.qbomb.common.Constants.*;


public class AnswerActivity extends Activity {

    private CardContainer mCardContainer;

    @Override
    protected void onNewIntent(Intent intent) {
        log("AnswerActivity:onNewIntent");
        super.onNewIntent(intent);
        log("AnswerActivity:onNewIntent with bomb_id: "+intent.getStringExtra("bombId"));
        Toast.makeText(this, "AnswerActivity:onNewIntent with bomb_id: "+intent.getStringExtra("bombId"),
                Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        log("AnswerActivity:onResume with bomb_id: "+intent.getStringExtra("bomb_id"));
        Toast.makeText(this, "AnswerActivity:onResume with bomb_id: "+intent.getStringExtra("bomb_id"),
                Toast.LENGTH_LONG).show();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id", intent.getStringExtra("bomb_id"));
        ParseCloud.callFunctionInBackground("getBomb", params, new FunctionCallback<HashMap<String,String>>() {
            public void done(HashMap<String,String> result, ParseException e) {
                if (e == null) {
                    log("result is "+result);
                    // result is "Hello world!"
                    onQuestionRetrieved(result);
                }else
                {
                    log("error is "+e.getMessage());
                    onQuestionRetrieved(null);
                }
            }
        });
    }

    private void onQuestionRetrieved(HashMap<String,String> result)//String result)
    {
        //make dummy

        if(result == null)
        {
            result = new HashMap<String, String>();
            result.put("id", "IDdummy");
            result.put("question", "くえすちょん？くえすちょん？");
            result.put("answer_0", "答え０答え０");
            result.put("answer_1", "答え1答え1");
            //translate();
        }


        mCardContainer = (CardContainer) findViewById(R.id.cardContainer);
        Resources r = getResources();
        QuestionCardStackAdapter adapter = new QuestionCardStackAdapter(this);
        //adapter.add(new CardModel(resultJSON.getString("question"), "Description goes here", r.getDrawable(R.drawable.picture1)));

        final String bombId = result.get("id");


        TextView answer0Field = (TextView) findViewById(R.id.answer_0);
        TextView answer1Field = (TextView) findViewById(R.id.answer_1);
        answer0Field.setText(result.get("answer_0"));
        answer1Field.setText(result.get("answer_1"));

        //CardModel cardModel = new CardModel(resultJSON.getString("question"), resultJSON.getString("answer_0"), r.getDrawable(R.drawable.picture1));
        QCardModel cardModel = new QCardModel(result.get("question"));//, result.get("answer_0"), result.get("answer_1"));
        cardModel.setOnClickListener(new CardModel.OnClickListener() {
            @Override
            public void OnClickListener() {
                log("I am pressing the card");
            }
        });

        cardModel.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
            @Override
            public void onLike() {
                log("I like the card");
                sendResult(bombId,0);
            }

            @Override
            public void onDislike()
            {
                log("I dislike the card");
                sendResult(bombId,1);
            }
        });

        adapter.add(cardModel);

        mCardContainer.setAdapter(adapter);



    }

    private void sendResult(String id, int answer)
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("answer", answer);
        ParseCloud.callFunctionInBackground("answer", params,null);
        finish();
        /*new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {
                if (e == null) {
                    log("result is "+result);
                    finish();
                }else
                {
                    log("error is "+e.getMessage());
                    finish();
                }
            }
        });*/
    }

    private void translate()
    {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://leonardoscorza-translate-text-v1.p.mashape.com/translate?languageConverted=en&languageToConvert=pt&text=Hi+mister")
                    .header("X-Mashape-Key", "heeETp88GymshAKR6H4bZoEBRKDrp164J8Yjsnk6q2D38JLGHn")
                    .asJson();
            log("ttttttttttttttttt"+response.getBody().getObject().get("answer"));
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("AnswerActivity:onCreate");
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_answer);
       /* if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/


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

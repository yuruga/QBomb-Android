package jp.yuruga.qbomb;

/**
 * Created by maeda on 2014/10/05.
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.andtinder.model.CardModel;
import com.andtinder.view.CardStackAdapter;

import jp.yuruga.qbomb.model.QCardModel;

public final class QuestionCardStackAdapter extends CardStackAdapter {

    public QuestionCardStackAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getCardView(int position, CardModel model, View convertView, ViewGroup parent) {
        QCardModel qCardModel = (QCardModel) model;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.q_card_inner, parent, false);
            assert convertView != null;
        }

        //((ImageView) convertView.findViewById(R.id.image)).setImageDrawable(model.getCardImageDrawable());
        ((TextView) convertView.findViewById(R.id.question)).setText(qCardModel.getQustion());
        /*((TextView) convertView.findViewById(R.id.answer_0)).setText(qCardModel.getAnswer0());
        ((TextView) convertView.findViewById(R.id.answer_1)).setText(qCardModel.getAnswer1());*/

        return convertView;
    }
}

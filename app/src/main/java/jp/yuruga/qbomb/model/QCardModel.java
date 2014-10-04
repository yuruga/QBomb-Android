/**
 * AndTinder v0.1 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * TAndTinder is a native library for Android that provide a
 * Tinder card like effect. A card can be constructed using an
 * image and displayed with animation effects, dismiss-to-like
 * and dismiss-to-unlike, and use different sorting mechanisms.
 *
 * AndTinder is compatible with API Level 13 and upwards
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */

package jp.yuruga.qbomb.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.andtinder.model.CardModel;

public class QCardModel extends CardModel {


    private String question;
    private String answer0;
    private String answer1;


	public QCardModel(String question) {
		super();
        this.question = question;
        this.answer0 = answer0;
        this.answer1 = answer1;

	}




    public String getQustion() {
        return question;
    }

    /*public String getAnswer0() {
        return  answer0;
    }

    public String getAnswer1() {
        return answer1;
    }*/
}
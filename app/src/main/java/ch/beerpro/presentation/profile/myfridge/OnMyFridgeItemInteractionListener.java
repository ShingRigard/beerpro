package ch.beerpro.presentation.profile.myfridge;

import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import ch.beerpro.domain.models.Beer;

public interface OnMyFridgeItemInteractionListener {

    void onMoreClickedListener(ImageView photo, Beer beer);

    void onFridgeBeerDeleteClickedListener(Beer beer);

    void onIncrementClickedListener(Beer beer, View view);

    void onDecrementClickedListener(Beer beer, View view);

    void afterTextChanged(Beer beer, Editable s);
}

package ch.beerpro.presentation.profile.myfridge;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import ch.beerpro.domain.models.Beer;

public interface OnMyFridgeItemInteractionListener extends TextWatcher {

    void onMoreClickedListener(ImageView photo, Beer beer);

    void onFridgeBeerDeleteClickedListener(Beer beer);

    void onIncrementClickedListener(Beer beer);

    void onDecrementClickedListener(Beer beer);

    void afterTextChanged(Beer beer, Editable s);
}

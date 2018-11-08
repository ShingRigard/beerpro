package ch.beerpro.presentation.profile.myfridge;

import android.app.ActivityOptions;
import android.content.Intent;
import android.text.Editable;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.R;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.MyFridgeBeer;
import ch.beerpro.presentation.details.DetailsActivity;
import lombok.val;

import java.util.List;

public class MyFridgeActivity extends AppCompatActivity implements OnMyFridgeItemInteractionListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.emptyView)
    View emptyView;

    private MyFridgeViewModel model;
    private MyFridgeRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wishlist);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_activity_myFridge));


        model = ViewModelProviders.of(this).get(MyFridgeViewModel.class);
        model.getMyFridgeWithBeers().observe(this, this::updateMyFridgeList);

        val layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyFridgeRecyclerViewAdapter(this);

        recyclerView.setAdapter(adapter);

    }

    private void updateMyFridgeList(List<Pair<MyFridgeBeer, Beer>> entries) {
        adapter.submitList(entries);
        if (entries.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMoreClickedListener(ImageView animationSource, Beer beer) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.ITEM_ID, beer.getId());
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, animationSource, "image");
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onFridgeBeerDeleteClickedListener(Beer beer) {
        model.toggleItemInFridgeList(beer.getId());
    }

    @Override
    public void onIncrementClickedListener(Beer beer, View view) {
        model.addAmountToFridgeBeer(beer.getId());
        EditText editText = view.findViewById(R.id.amountInFridge);
        editText.setText(Integer.toString(Integer.parseInt(editText.getText().toString()) + 1));
    }

    @Override
    public void onDecrementClickedListener(Beer beer, View view) {
        EditText editText = view.findViewById(R.id.amountInFridge);
        int newAmount = Integer.parseInt(editText.getText().toString()) - 1;
        if(newAmount <= 0) {
            model.toggleItemInFridgeList(beer.getId());
        }
        model.removeAmountToFridgeBeer(beer.getId());
        editText.setText(Integer.toString(newAmount));
    }

    @Override
    public void afterTextChanged(Beer beer, Editable s) {
        if (!s.toString().equals("")) {
            model.changeAmountOfFridgeBeer(beer.getId(), Integer.parseInt(s.toString()));
        }
    }
}

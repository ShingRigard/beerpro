package ch.beerpro.presentation.ratings;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import ch.beerpro.GlideApp;
import ch.beerpro.R;
import ch.beerpro.presentation.utils.EntityPairDiffItemCallback;
import ch.beerpro.domain.models.Rating;
import ch.beerpro.domain.models.Wish;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;

import static ch.beerpro.presentation.utils.DrawableHelpers.setDrawableTint;


public class RatingsRecyclerViewAdapter extends ListAdapter<Pair<Rating, Wish>, RatingsRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RatingsRecyclerViewAdap";

    private static final DiffUtil.ItemCallback<Pair<Rating, Wish>> DIFF_CALLBACK = new EntityPairDiffItemCallback<>();

    private final OnRatingsItemInteractionListener listener;
    private Fragment fragment;
    private final FirebaseUser user;

    public RatingsRecyclerViewAdapter(OnRatingsItemInteractionListener listener, Fragment fragment, FirebaseUser user) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.fragment = fragment;
        this.user = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.fragment_ratings_listentry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Pair<Rating, Wish> item = getItem(position);
        holder.bind(item.first, item.second, listener);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment)
        TextView comment;

        @BindView(R.id.beerName)
        TextView beerName;

        @BindView(R.id.avatar)
        ImageView avatar;

        @BindView(R.id.ratingBar)
        RatingBar ratingBar;

        @BindView(R.id.authorName)
        TextView authorName;

        @BindView(R.id.date)
        TextView date;

        @BindView(R.id.numLikes)
        TextView numLikes;

        @BindView(R.id.details)
        Button details;

        @BindView(R.id.wishlist)
        Button wishlist;

        @BindView(R.id.like)
        Button like;

        @BindView(R.id.photo)
        ImageView photo;

        @BindView(R.id.locationTextView)
        TextView location;

        @BindView(R.id.gridLayoutAdditionalRatings)
        GridLayout gLAdditionalRatings;

        @BindView(R.id.ratingBarBitterness)
        RatingBar ratingBarBitterness;

        @BindView(R.id.textViewFlavoursValue)
        TextView beerFlavours;

        @BindView(R.id.textViewSmellValue)
        TextView beerSmell;

        @BindView(R.id.textViewLookValue)
        TextView beerLook;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        void bind(Rating item, Wish wish, OnRatingsItemInteractionListener listener) {
            // TODO This code is almost the same in MyBeersRecyclerViewAdapter.. could be simplified
            // with databinding!
            beerName.setText(item.getBeerName());
            comment.setText(item.getComment());

            ratingBar.setNumStars(5);
            ratingBar.setRating(item.getRating());

            if(item.getRatingBitterness() >= 0) {
                gLAdditionalRatings.setVisibility(View.VISIBLE);
                ratingBarBitterness.setNumStars(5);
                ratingBarBitterness.setRating(item.getRatingBitterness());
                beerFlavours.setText(item.getBeerFlavour());
                beerSmell.setText(item.getBeerSmell());
                beerLook.setText(item.getBeerLook());

            } else {
                gLAdditionalRatings.setVisibility(View.GONE);
            }

            if(item.getLocation() != null) {
                location.setText("@" + item.getLocation());
                location.setVisibility(View.VISIBLE);
            } else {
                location.setVisibility(View.GONE);
            }

            String formattedDate =
                    DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(item.getCreationDate());
            date.setText(formattedDate);

            if (item.getPhoto() != null) {
                // Take a look at https://bumptech.github.io/glide/int/recyclerview.html
                GlideApp.with(fragment).load(item.getPhoto()).into(photo);
                photo.setVisibility(View.VISIBLE);
            } else {
                GlideApp.with(fragment).clear(photo);
                photo.setVisibility(View.GONE);
            }

            authorName.setText(item.getUserName());
            GlideApp.with(fragment).load(item.getUserPhoto()).apply(new RequestOptions().circleCrop()).into(avatar);

            numLikes.setText(itemView.getResources().getString(R.string.fmt_num_ratings, item.getLikes().size()));

            if (item.getLikes().containsKey(user.getUid())) {
                int color = fragment.getResources().getColor(R.color.colorPrimary);
                setDrawableTint(like, color);
            } else {
                int color = fragment.getResources().getColor(android.R.color.darker_gray);
                setDrawableTint(like, color);
            }

            if (wish != null) {
                int color = fragment.getResources().getColor(R.color.colorPrimary);
                setDrawableTint(wishlist, color);
            } else {
                int color = fragment.getResources().getColor(android.R.color.darker_gray);
                setDrawableTint(wishlist, color);
            }

            if (listener != null) {
                like.setOnClickListener(v -> listener.onRatingLikedListener(item));
                details.setOnClickListener(v -> listener.onMoreClickedListener(item));
                wishlist.setOnClickListener(v -> listener.onWishClickedListener(item));
            }
        }
    }
}

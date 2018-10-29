package ch.beerpro.data.repositories;

import androidx.lifecycle.LiveData;
import ch.beerpro.data.utils.Quadruple;
import ch.beerpro.domain.models.*;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;

import static androidx.lifecycle.Transformations.map;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class MyBeersRepository {

    private static List<MyBeer> getMyBeers(Quadruple<List<Wish>, List<MyFridgeBeer>, List<Rating>, HashMap<String, Beer>> input) {
        List<Wish> wishlist = input.getFirst();
        List<MyFridgeBeer> fridgeBeers = input.getSecond();
        List<Rating> ratings = input.getThird();
        HashMap<String, Beer> beers = input.getFourth();

        ArrayList<MyBeer> result = new ArrayList<>();
        Set<String> beersAlreadyOnTheList = new HashSet<>();

        if (wishlist != null) {
            for (Wish wish : wishlist) {
                String beerId = wish.getBeerId();
                result.add(new MyBeerFromWishlist(wish, beers.get(beerId)));
                beersAlreadyOnTheList.add(beerId);
            }
        }

        if (fridgeBeers != null) {
            for (MyFridgeBeer fridgeBeer : fridgeBeers) {
                String beerId = fridgeBeer.getBeerId();
                if (beersAlreadyOnTheList.contains(beerId)) {
                    // if the beer is already on the wish list, don't add it again
                } else {
                    result.add(new MyBeerFromFridge(fridgeBeer, beers.get(beerId)));
                    // we also don't want to see a rated beer twice
                    beersAlreadyOnTheList.add(beerId);
                }
            }
        }

        if (ratings != null) {
            for (Rating rating : ratings) {
                String beerId = rating.getBeerId();
                if (beersAlreadyOnTheList.contains(beerId)) {
                    // if the beer is already on the wish list, don't add it again
                } else {
                    result.add(new MyBeerFromRating(rating, beers.get(beerId)));
                    // we also don't want to see a rated beer twice
                    beersAlreadyOnTheList.add(beerId);
                }
            }
        }
        Collections.sort(result, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        return result;
    }


    public LiveData<List<MyBeer>> getMyBeers(LiveData<List<Beer>> allBeers, LiveData<List<Wish>> myWishlist, LiveData<List<MyFridgeBeer>> myFridge, LiveData<List<Rating>> myRatings) {
        return map(combineLatest(myWishlist, myFridge, myRatings, map(allBeers, Entity::entitiesById)), MyBeersRepository::getMyBeers);
    }

}

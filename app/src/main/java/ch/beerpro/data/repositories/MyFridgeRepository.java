package ch.beerpro.data.repositories;

import android.util.Pair;
import androidx.lifecycle.LiveData;
import ch.beerpro.domain.models.Beer;
import ch.beerpro.domain.models.Entity;
import ch.beerpro.domain.models.MyFridgeBeer;
import ch.beerpro.domain.utils.FirestoreQueryLiveData;
import ch.beerpro.domain.utils.FirestoreQueryLiveDataArray;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static ch.beerpro.domain.utils.LiveDataExtensions.combineLatest;

public class MyFridgeRepository {
    private static LiveData<List<MyFridgeBeer>> getFridgeBeersByUser(String userId) {
        return new FirestoreQueryLiveDataArray<>(FirebaseFirestore.getInstance().collection(MyFridgeBeer.COLLECTION)
                .orderBy(MyFridgeBeer.FIELD_AMOUNT_STORED, Query.Direction.DESCENDING).whereEqualTo(MyFridgeBeer.FIELD_USER_ID, userId),
                MyFridgeBeer.class);
    }

    private static LiveData<MyFridgeBeer> getUserFridgeListFor(Pair<String, Beer> input) {
        String userId = input.first;
        Beer beer = input.second;
        DocumentReference document = FirebaseFirestore.getInstance().collection(MyFridgeBeer.COLLECTION)
                .document(MyFridgeBeer.generateId(userId, beer.getId()));
        return new FirestoreQueryLiveData<>(document, MyFridgeBeer.class);
    }

    public Task<Void> toggleUserFridgeItem(String userId, String itemId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String fridgeBeerId = MyFridgeBeer.generateId(userId, itemId);

        DocumentReference fridgeEntryQuery = db.collection(MyFridgeBeer.COLLECTION).document(fridgeBeerId);

        return fridgeEntryQuery.get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                return fridgeEntryQuery.delete();
            } else if (task.isSuccessful()) {
                return fridgeEntryQuery.set(new MyFridgeBeer(fridgeBeerId, userId, itemId, 1, new Date()));
            } else {
                throw task.getException();
            }
        });
    }

    public LiveData<List<Pair<MyFridgeBeer, Beer>>> getMyFridgeWithBeers(LiveData<String> currentUserId,
                                                                         LiveData<List<Beer>> allBeers) {
        return map(combineLatest(getMyFridge(currentUserId), map(allBeers, Entity::entitiesById)), input -> {
            List<MyFridgeBeer> fridgeBeers = input.first;
            HashMap<String, Beer> beersById = input.second;

            ArrayList<Pair<MyFridgeBeer, Beer>> result = new ArrayList<>();
            for (MyFridgeBeer fridgeBeer : fridgeBeers) {
                Beer beer = beersById.get(fridgeBeer.getBeerId());
                result.add(Pair.create(fridgeBeer, beer));
            }
            return result;
        });
    }

    public LiveData<List<MyFridgeBeer>> getMyFridge(LiveData<String> currentUserId) {
        return switchMap(currentUserId, MyFridgeRepository::getFridgeBeersByUser);
    }


    public LiveData<MyFridgeBeer> getMyFridgeForBeer(LiveData<String> currentUserId, LiveData<Beer> beer) {


        return switchMap(combineLatest(currentUserId, beer), MyFridgeRepository::getUserFridgeListFor);
    }
}

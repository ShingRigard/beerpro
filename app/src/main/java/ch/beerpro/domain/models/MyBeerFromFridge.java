package ch.beerpro.domain.models;

import lombok.Data;

import java.util.Date;

@Data
public class MyBeerFromFridge implements MyBeer {
    private MyFridgeBeer fridgeBeer;
    private Beer beer;

    public MyBeerFromFridge(MyFridgeBeer fridgeBeer, Beer beer) {
        this.fridgeBeer = fridgeBeer;
        this.beer = beer;
    }

    @Override
    public String getBeerId() {
        return fridgeBeer.getBeerId();
    }

    @Override
    public Date getDate() {
        return fridgeBeer.getAddedAt();
    }
}

package org.example.labx.utils;

import java.util.ArrayList;

public class Page<E>{
    private final ArrayList<E> entitiesOnPage;
    private final Long totalNumberOfElements;

    public Page(ArrayList<E> entitiesOnPage, Long totalNumberOfElements) {
        this.entitiesOnPage = entitiesOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public ArrayList<E> getEntitiesOnPage() {
        return entitiesOnPage;
    }

    public Long getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}

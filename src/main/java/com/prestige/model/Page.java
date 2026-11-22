package com.prestige.model;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    private final List<T> items;
    private final int page;
    private final int size;
    private final long total;

    public Page(List<T> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public List<T> getItems() {
        return items;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotal() {
        return total;
    }

    public int getTotalPages() {
        return (int) Math.max(1L, (total + size - 1) / size);
    }

    public List<Integer> getPages() {
        int last = getTotalPages();
        int from = Math.max(1, page - 2);
        int to = Math.min(last, page + 2);
        List<Integer> numbers = new ArrayList<>();
        for (int n = from; n <= to; n++) {
            numbers.add(n);
        }
        return numbers;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}

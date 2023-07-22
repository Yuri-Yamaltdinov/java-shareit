package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class Pagination extends PageRequest {

    protected Pagination(int page, int size) {
        super(page, size, Sort.unsorted());
    }
}

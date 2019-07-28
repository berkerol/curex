package com.popleads.interview.repositories;

import com.popleads.interview.entities.History;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<History, Long> {
}

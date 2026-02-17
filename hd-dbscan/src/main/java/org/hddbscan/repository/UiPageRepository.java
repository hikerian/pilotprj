package org.hddbscan.repository;

import org.hddbscan.entity.UiPage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UiPageRepository extends CrudRepository<UiPage, Long> {

}

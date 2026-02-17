package org.hddbscan.repository;

import org.hddbscan.entity.UiElements;
import org.hddbscan.entity.UiElementsId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UiElementsRepository extends CrudRepository<UiElements, UiElementsId> {

}

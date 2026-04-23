package org.hddbscan.repository;

import org.hddbscan.entity.ClusterModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ClusterModelRepository extends CrudRepository<ClusterModel, Long> {

}

package com.spatial.index.domain.map.repository;

import com.spatial.index.domain.map.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository extends JpaRepository<Long, Location> {

}

package com.splitterorg.splitter.persistence;


import java.util.Set;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface GruppenRepository extends CrudRepository<GruppeTabelle, UUID> {

  Set<GruppeTabelle> findAll();
}


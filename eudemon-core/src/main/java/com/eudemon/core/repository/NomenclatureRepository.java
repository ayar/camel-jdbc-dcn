package com.eudemon.core.repository;

import com.eudemon.core.model.base.Nomenclature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface NomenclatureRepository extends JpaRepository<Nomenclature,Long> {
    Stream<Nomenclature> findBySchemeIgnoreCase(String scheme);
    Optional<Nomenclature> findOneBySchemeAndCode(String scheme,String code);
    Optional<Nomenclature> findById(Long id);
    Page<Nomenclature> findBySchemeIgnoreCase(String scheme, Pageable page);
}

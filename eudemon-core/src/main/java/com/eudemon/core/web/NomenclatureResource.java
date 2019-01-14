package com.eudemon.core.web;


import com.codahale.metrics.annotation.Timed;
import com.eudemon.core.config.Constants;
import com.eudemon.core.model.base.Nomenclature;
import com.eudemon.core.repository.NomenclatureRepository;
import com.eudemon.core.web.util.HeaderUtil;
import com.eudemon.core.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/org/flowable/form/api")
public class NomenclatureResource {

    private final Logger log = LoggerFactory.getLogger(NomenclatureResource.class);
    private static final String ENTITY_NAME = "nomenclatures";

    @Autowired
    NomenclatureRepository repository;


    @PostMapping("/nomencaltures")
    @Timed
    //@PreAuthorize("hasRole('ROLE_ADMIN_USER') ")
    public ResponseEntity createNomenclature(@Valid @RequestBody Nomenclature nomenclature) throws URISyntaxException {
        if (nomenclature.getId() != null) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new code cannot already have an ID"))
                .body(null);
        } else if (repository.findOneBySchemeAndCode (nomenclature.getScheme() ,nomenclature.getCode()).isPresent()) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "codeexists", "code already in use"))
                .body(null);
        } else {
            Nomenclature newNomenclature = repository.save(nomenclature);
            return ResponseEntity.created(new URI(String.format("/org/flowable/form/api/nomenclatures/%s/%s", newNomenclature.getScheme(), newNomenclature.getCode())))
                .headers(HeaderUtil.createAlert("nomenclatures.created", newNomenclature.getCode()))
                .body(newNomenclature);
        }
    }

    @PutMapping("/nomencaltures")
    @Timed
   // @PreAuthorize("hasRole('ROLE_ADMIN_USER') ")
    public ResponseEntity<Nomenclature> updateNomenclature(@Valid @RequestBody Nomenclature managedNomenclature) throws URISyntaxException {
        log.debug("REST request to update User : {}", managedNomenclature);

        Optional<Nomenclature> existingNomenclature = repository.findOneBySchemeAndCode(managedNomenclature.getScheme(),managedNomenclature.getCode());
        if (existingNomenclature.isPresent() && (!existingNomenclature.get().getId().equals(managedNomenclature.getId()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "codexists", "Code already in use")).body(null);
        }
        Nomenclature updatedNomenclature = repository.save(managedNomenclature);

       return  ResponseEntity.created(new URI(String.format("/org/flowable/form/api/nomenclatures/%s/%s", updatedNomenclature.getScheme(), updatedNomenclature.getCode())))
            .headers(HeaderUtil.createAlert("nomenclatures.updated", updatedNomenclature.getCode()))
            .body(updatedNomenclature);
       }


    @GetMapping("/nomencaltures/{scheme:" + Constants.ALPHANUM_REGEX +"}/{code:" +  Constants.ALPHANUM_REGEX + "}")
    @Timed
    public ResponseEntity<Nomenclature> getNomenclature(@PathVariable String scheme,@PathVariable String code) {
        log.debug("REST request to get code : {}:{}", scheme,code);
        return ResponseUtil.wrapOrNotFound(
            repository.findOneBySchemeAndCode (scheme,code));
    }



    @GetMapping("/nomencaltures/{scheme:" + Constants.ALPHANUM_REGEX +"}")
    @Timed
    public ResponseEntity<List<Nomenclature>> getNomenclatureForScheme(@PathVariable String scheme,@ApiParam Pageable pageable){
        final Page<Nomenclature> page = repository.findBySchemeIgnoreCase(scheme,pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/org/flowable/form/api/nomencaltures/" +scheme );
        return new ResponseEntity(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/nomencaltures")
    @Timed
    public ResponseEntity<List<Nomenclature>> getNomenclatures(@ApiParam Pageable pageable){
        final Page<Nomenclature> page = repository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/org/flowable/form/api/nomencaltures");
        return new ResponseEntity(page.getContent(), headers, HttpStatus.OK);
    }



    @DeleteMapping ("/nomencaltures/{scheme:" + Constants.ALPHANUM_REGEX +"}/{code:" +  Constants.ALPHANUM_REGEX + "}")
    @Timed
    // @PreAuthorize("hasRole('ROLE_ADMIN_USER') ")
    public ResponseEntity<Void> deleteNomenclature(@PathVariable String scheme,@PathVariable String code) {
        log.debug("REST request to delete scheme: {}:{}", scheme,code);
        repository.findOneBySchemeAndCode(scheme,code);
        return ResponseEntity.ok().headers(HeaderUtil.createAlert( "nomenclatures.deleted", String.format("%s:%s",scheme,code))).build();
    }


}

package com.eudemon.core.model.agreement;

import com.eudemon.core.model.base.Contract;
import com.eudemon.core.model.organisation.Organisaton;

import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "CORE_AGREEMENTHEADER")
public class Agreement extends Contract<Organisaton,Organisaton,Product> {
}

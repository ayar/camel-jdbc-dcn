package com.eudemon.core.model.deal;

import com.eudemon.core.model.agreement.Agreement;
import com.eudemon.core.model.agreement.Product;
import com.eudemon.core.model.base.Contract;
import com.eudemon.core.model.timesheet.Task;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CORE_DEALS")
public class Deal extends Contract<Agreement,Agreement,Product> {

    Date dealdate;
    double quantity;


}

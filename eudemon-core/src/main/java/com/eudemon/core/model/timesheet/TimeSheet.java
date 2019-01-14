package com.eudemon.core.model.timesheet;

import com.eudemon.core.model.base.Contract;
import com.eudemon.core.model.organisation.Organisaton;
import com.eudemon.core.model.resource.Resource;

import java.util.List;

public class TimeSheet extends Contract<Organisaton,Resource,Task> {

    List<Event> events;

}

package com.eudemon.core.model.organisation;


import com.eudemon.core.model.adresse.AdressScheme;
import com.eudemon.core.model.adresse.BaseAdress;
import com.eudemon.core.model.base.Group;
import com.eudemon.core.model.base.Status;
import com.eudemon.core.model.contact.Contact;
import com.eudemon.core.model.holidays.HolidayCalendar;
import com.eudemon.core.model.holidays.HolidayCalendarType;
import com.eudemon.core.model.mesure.Rating;
import com.eudemon.core.model.contact.ContactRole;
import com.eudemon.core.model.organisation.refdata.OrgRelation;
import com.eudemon.core.model.organisation.refdata.OrgRole;

import java.util.List;
import java.util.Map;

public class Organisaton {
    Long id;
    String code;
    String name;
    String description;
    Status status;

    Map<ContactRole,Group<Contact>> contacts;
    Map<AdressScheme,Group<BaseAdress>> adresses;
    Map<OrgRelation,Group<Organisaton>> relationShips;
    Map<HolidayCalendarType,Group<HolidayCalendar>> holidays;
    List<OrgRole> roles;
    List<Rating> ratings;
    List<OrgUdfData> udfs;
}

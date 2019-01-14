package com.eudemon.flow.util;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class UuidIdGenerator implements IdentifierGenerator {


    protected static volatile TimeBasedGenerator timeBasedGenerator;

    {
        ensureGeneratorInitialized();
    }
    protected void ensureGeneratorInitialized() {
        if (timeBasedGenerator == null) {
            synchronized (UuidIdGenerator.class) {
                if (timeBasedGenerator == null) {
                    timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
                }
            }
        }
    }


    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {
         return timeBasedGenerator.generate().toString();
    }
}

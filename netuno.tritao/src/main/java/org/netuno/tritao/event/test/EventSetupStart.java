package org.netuno.tritao.event.test;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.event.EventBase;
import org.netuno.tritao.event.EventId;
import org.netuno.tritao.hili.Hili;

public class EventSetupStart extends EventBase {

    public EventSetupStart(Proteu proteu, Hili hili) {
        super(proteu, hili, EventId.SETUP_START, 100);
    }

    @Override
    public void run(Values data) {
        System.out.println("EVENT START "+ data.toJSON());
    }

}

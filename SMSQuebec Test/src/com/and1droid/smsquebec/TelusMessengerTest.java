package com.and1droid.smsquebec;

import android.test.AndroidTestCase;

public class TelusMessengerTest extends AndroidTestCase {

    private Texto textoToTelus;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        textoToTelus = new Texto("5146221233", "Test victoire de Bibi");
    }

    public void testSendTelusMessage() {
        //TelusMessenger.send(textoToTelus);
    }

}

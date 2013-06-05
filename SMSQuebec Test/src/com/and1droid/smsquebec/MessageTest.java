package com.and1droid.smsquebec;

import android.test.AndroidTestCase;

public class MessageTest extends AndroidTestCase {

	private Texto texto;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		texto = new Texto("5146221233", "Test victoire de Bibi");
	}

	public void testGetCode() {
		assertEquals("514", texto.getCode());
	}

	public void testGetNum() {
		assertEquals("6221233", texto.getNum());
	}
	
	public void testGetMessageSansSignature() {
        assertEquals("Test victoire de Bibi", texto.getMessage());
    }
	
	public void testGetMessageAvecSignature() {
	    texto.setFrom("Guilhem");
        assertEquals("Test victoire de Bibi\r\nGuilhem", texto.getMessage());
    }

	public void testCleanNumber() {
        assertEquals("5141234567", texto.formatNumber("+1 (514) 123 4567"));
        assertEquals("5141234567", texto.formatNumber("(514) 123 4567"));
        assertEquals("5141234567", texto.formatNumber("+1 514 123 4567"));
        assertEquals("5141234567", texto.formatNumber("1 514 123 4567"));
        assertEquals("5141234567", texto.formatNumber("15141234567"));
        assertEquals("5141234567", texto.formatNumber("5141234567"));
    }
}


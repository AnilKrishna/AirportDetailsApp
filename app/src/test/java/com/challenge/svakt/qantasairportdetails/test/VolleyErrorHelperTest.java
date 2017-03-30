package com.challenge.svakt.qantasairportdetails.test;

import android.content.Context;

import com.android.volley.VolleyError;
import com.challenge.svakt.qantasairportdetails.AirportListActivity;
import com.challenge.svakt.qantasairportdetails.utils.VolleyErrorHelper;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created on 30-03-2017.
 */
public class VolleyErrorHelperTest {

    private VolleyErrorHelper mHelper;
    private VolleyError er;


    @Before
    public void setUp() throws Exception {
        mHelper = new VolleyErrorHelper();
        er = new VolleyError();
    }

    @Test
    public void volleyErrorObjectTest() throws Exception {
        assertNotNull(er);
    }

    @Test
    public void volleyClassTest() throws Exception {
        assertNotNull(mHelper);
    }

}
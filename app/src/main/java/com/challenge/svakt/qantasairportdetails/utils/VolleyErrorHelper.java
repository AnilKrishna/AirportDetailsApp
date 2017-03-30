package com.challenge.svakt.qantasairportdetails.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.challenge.svakt.qantasairportdetails.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunny on 30-03-2017.
 */

public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user
     * against the specified error object.
     *
     * @param error
     * @param context
     * @return
     */

    public static String getMessage (Object error , Context context){
        if(error instanceof TimeoutError){
            return context.getResources().getString(R.string.timeout);
        }else if (isServerProblem(error)){
            return handleServerError(error ,context);

        }else if(isNetworkProblem(error)){
            return context.getResources().getString(R.string.no_internet);
        }
        return context.getResources().getString(R.string.generic_error);

    }

    private static String handleServerError(Object error, Context context) {

        VolleyError er = (VolleyError)error;
        NetworkResponse response = er.networkResponse;
        if(response != null){
            //Log.v("QAD","Volley Helper Err :" + response.data);
            switch (response.statusCode){

                case 404:
                case 422:
                case 401:
                    try {
                        String result = new String(response.data);
                        //Log.v("QAD","Volley Helper Err result:" + result);
                        if (result != null) {
                                //Log.v("QAD","Volley Helper Err in if:" + response.data);
                                return context.getResources().getString(R.string.http_error);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // invalid request
                    return ((VolleyError) error).getMessage();

                default:
                    return context.getResources().getString(R.string.timeout);
            }
        }

        return context.getResources().getString(R.string.generic_error);
    }

    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private static boolean isNetworkProblem (Object error){
        return (error instanceof NetworkError || error instanceof NoConnectionError);
    }

}

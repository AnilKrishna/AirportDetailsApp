package com.challenge.svakt.qantasairportdetails;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.challenge.svakt.qantasairportdetails.model.DummyContent;
import com.challenge.svakt.qantasairportdetails.utils.ConnectivityStatus;
import com.challenge.svakt.qantasairportdetails.utils.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static android.R.id.progress;


/**
 * An activity representing a list of Airports. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link AirportDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class AirportListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    final String url = "https://www.qantas.com.au/api/airports";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        spinner = (ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);


        AirportListActivity.this.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        final View recyclerView = findViewById(R.id.airport_list);
        assert recyclerView != null;
        //Log.v("Inside If","Inside");
        setupRecyclerView((RecyclerView) recyclerView);


        if (findViewById(R.id.airport_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }



        // GET API request call

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,(String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("QAD","RES : " + response.toString());

                PreferenceManager.getDefaultSharedPreferences( AirportListActivity.this).edit()
                        .putString("airportData",response.toString()).apply();
                prepareJSONData(response);
                myAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("QAD","Err :" + error);
                String ErrorMessage = VolleyErrorHelper.getMessage(error,AirportListActivity.this);
                Toast.makeText(AirportListActivity.this, ErrorMessage,
                        Toast.LENGTH_LONG).show();
            }
        });

        Volley.newRequestQueue(this).add(jsonRequest);
    }


    private void prepareJSONData(JSONObject response) {
        try{
            JSONArray airports = null;
            airports = response.getJSONArray("airports");
            JSONObject airportObj = null;
            int totalAirports = airports.length();
            //Log.d("QAD","totalAirports : " + totalAirports);
            for (int i = 0; i < totalAirports; i++) {
                //Main Root element
                airportObj = airports.getJSONObject(i);
                //Log.d("QAD","airportObj : " + airportObj.toString());
                //Airport Name, Currency, timezone . Element of same level
                String airportName = airportObj.getString("display_name");
                String currency = airportObj.getString("currency_code");
                String timeZone = airportObj.getString("timezone");

                //Location
                JSONObject location = airportObj.getJSONObject("location");
                String latitude = location.getString("latitude");
                String longitude = location.getString("longitude");

                // Country
                JSONObject country = airportObj.getJSONObject("country");
                String countryName = country.getString("display_name");

                //String airportDetails = currency+", "+timeZone+", "+latitude+", "+longitude;
                String airport = airportName + " Airport";
                String airportDetails = "Timezone: " + timeZone + "\n" + "Currency: " + currency + "\n" + "Location:" + latitude + "," + longitude;

                // Storing JSON for offline use

                DummyContent.DummyItem airportData = new DummyContent.DummyItem(airport, countryName, airportDetails);
                DummyContent.addItem(airportData);
            }
        }catch (JSONException e){
            Log.v("QAD","Err :" + e.getLocalizedMessage());
        }
    }

    // Check for the network
    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!ConnectivityStatus.isConnected(AirportListActivity.this)){
                // no connection
                Log.v("QAD","Connection :" + "NOT CONNECTED");
                String jsonObjectString = PreferenceManager.
                        getDefaultSharedPreferences(AirportListActivity.this).getString("airportData","");
                try{
                    JSONObject airportJSONObject = new JSONObject(jsonObjectString);
                    prepareJSONData(airportJSONObject);
                }catch (JSONException e){
                    Log.v("QAD","Err :" + e.getLocalizedMessage());
                }
                myAdapter.notifyDataSetChanged();
                spinner.setVisibility(View.GONE);
            }else {
                // connected
                Log.v("QAD","Connection :" + "CONNECTED");
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        AirportListActivity.this.registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();
        AirportListActivity.this.unregisterReceiver(receiver);

    }

    private SimpleItemRecyclerViewAdapter myAdapter;
    private ProgressBar spinner;

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new DividerItemDecoration(AirportListActivity.this, DividerItemDecoration.VERTICAL));
        myAdapter = new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS);
        recyclerView.setAdapter(myAdapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.airport_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            Log.v("QAD","mValues" + mValues);
            Log.v("QAD","mItem" + holder.mItem);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(AirportDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        AirportDetailFragment fragment = new AirportDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.airport_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, AirportDetailActivity.class);
                        intent.putExtra(AirportDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}

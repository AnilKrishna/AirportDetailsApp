package com.challenge.svakt.qantasairportdetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.challenge.svakt.qantasairportdetails.model.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


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

    //private ArrayList<QantasAirportData> airportDataList = new ArrayList<>();
    //private List<DummyItem> airportData = new ArrayList<DummyItem>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_airport_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

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

        // ***************JSON  REQUEST OBJECT ********************

        final JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,(String) null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Log.d("QAD","RES : " + response.toString());
                JSONArray airports = null;

                try {
                    airports = response.getJSONArray("airports");
                    JSONObject airportObj = null;
                    int totalAirports = airports.length();
                    //Log.d("QAD","totalAirports : " + totalAirports);
                    for(int i=0; i < totalAirports;i++){
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
                        String airport = airportName+" Airport";
                        String airportDetails = "Timezone: " + timeZone+ "\n" + "Currency: " + currency + "\n" + "Location:" + latitude+","+longitude;

                        //  Log.v("JSON : " , "AirportName :" + airportName + "--" + "CountryName ; " + countryName);

                        //QantasAirportData airportData = new QantasAirportData(airportName,countryName,currency,timeZone,latitude,longitude);
                        //ITEMS.add(airportData);
                        //Log.d("QAD","RES in TRY: " + airports.toString());

                        DummyContent.DummyItem airportData = new DummyContent.DummyItem(airport,countryName,airportDetails);
                        DummyContent.addItem(airportData);
                    }
                    myAdapter.notifyDataSetChanged();
                    progress.dismiss();
                } catch (JSONException e) {
                    Log.v("QAD","Err :" + e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("QAD","Err :" + error.getLocalizedMessage());
            }
        });


// **********************JSON REQUEST OBJECT END ****************************************

        Volley.newRequestQueue(this).add(jsonRequest);
    }

    private SimpleItemRecyclerViewAdapter myAdapter;

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        //recyclerView.invalidate();
        //recyclerView.getAdapter().notifyDataSetChanged();
        //recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEMS));
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

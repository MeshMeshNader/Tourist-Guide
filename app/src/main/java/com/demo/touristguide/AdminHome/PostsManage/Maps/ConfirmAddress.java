package com.demo.touristguide.AdminHome.PostsManage.Maps;


import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.demo.touristguide.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class ConfirmAddress extends DialogFragment implements
        android.view.View.OnClickListener, OnMapReadyCallback {

    float DEFALT_ZOOM = 15f;
    private getDataDialogListener listener;
    private GoogleMap mMap;
    Double Lat;
    Double Long;
    String Address;
    TextView myAddress;
    Button SelectBtn;
    Button ChangeBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Lat = getArguments().getDouble("lat");
        Long = getArguments().getDouble("long");
        Address = getArguments().getString("address");

    }

    MapFragment mapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.custom_confirm_address, container, false);
        myAddress = v.findViewById(R.id.myAddress);
        SelectBtn = v.findViewById(R.id.Select);
        ChangeBtn = v.findViewById(R.id.Change);


        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapp);
        mapFragment.getMapAsync(this);

        SelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(),myAddress.getText().toString(),Toast.LENGTH_LONG).show();
                getFragmentManager().beginTransaction().remove(mapFragment).commit();
                listener.onFinishDialog(Lat, Long, Address);
                dismiss();
            }
        });
        ChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().remove(mapFragment).commit();
                dismiss();
            }
        });
        getDialog().setCanceledOnTouchOutside(true);
        return v;

    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        getFragmentManager().beginTransaction().remove(mapFragment).commit();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        dismiss();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        myAddress.setText(Address);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        MarkerData mMarkerData = new MarkerData(Lat
                , Long
                , "Me"
                , Address
                , R.drawable.pin32);

        mMap.clear();
        createMarker(mMarkerData.getLatitude(), mMarkerData.getLongitude()
                , mMarkerData.getTitle(), mMarkerData.getSnippet()
                , mMarkerData.getIconResId());

        Log.e("Success", "This is the location of the res");
    }

    private void moveCamera(LatLng latLng, float zoom) {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    protected GoogleMap createMarker(double latitude, double longitude,
                                     String title, String snippet, int iconID) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconID)));
        moveCamera(new LatLng(latitude, longitude),
                DEFALT_ZOOM);

        return mMap;
    }

    public interface getDataDialogListener {
        void onFinishDialog(double lat, double lon, String Address);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the getDataDialogListener so we can send events to the host
            listener = (getDataDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement getDataDialogListener");
        }
    }


}
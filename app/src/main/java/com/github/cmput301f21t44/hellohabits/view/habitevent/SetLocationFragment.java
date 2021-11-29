package com.github.cmput301f21t44.hellohabits.view.habitevent;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.github.cmput301f21t44.hellohabits.databinding.FragmentSetLocationBinding;
import com.github.cmput301f21t44.hellohabits.R;
import com.github.cmput301f21t44.hellohabits.viewmodel.LocationViewModel;
import com.github.cmput301f21t44.hellohabits.viewmodel.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SetLocationFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap map;
    private MapView mapView;
    private Button setLocationButton;
    private double latitude;
    private double longitude;
    private String eventID;
    private FragmentSetLocationBinding binding;
    private NavController mNavController;
    private LocationViewModel mlocationviewmodel;

    public SetLocationFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSetLocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ViewModelProvider provider = ViewModelFactory.getProvider(requireActivity());
        mlocationviewmodel = provider.get(LocationViewModel.class);
        mNavController = NavHostFragment.findNavController(this);
        //get the ids of all xml elements
        mapView = view.findViewById(R.id.mapView);
        setLocationButton = view.findViewById(R.id.setLocationButton);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync((OnMapReadyCallback) this);

        //on click listener for set location button
        setLocationButton.setOnClickListener(new View.OnClickListener() {
          @Override
        public void onClick(View v) {
              mlocationviewmodel.setMlatitude(latitude);
              mlocationviewmodel.setMlongitude(longitude);
              mlocationviewmodel.setIsLocationChanged(true);
              mNavController.navigate(R.id.EventCreateEditFragment);
        }
        });

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setScrollGesturesEnabled(true);
        map.getUiSettings().setZoomGesturesEnabled(true);
        //map.getUiSettings().setMyLocationButtonEnabled(true);
        // Add an initial marker in Edmonton and move the camera to it
        LatLng edmonton = new LatLng(53.5461, -113.4938);
        map.addMarker(new MarkerOptions().position(edmonton).title("Exchange location"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 10f));
        Toast.makeText(getContext(), "Tap anywhere to move the marker to your preferred drop off location!", Toast.LENGTH_SHORT).show();

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                // add a marker at the clicked location and move the camera to it
                map.addMarker(new MarkerOptions().position(latLng).title("Exchange location"));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //save the coordinates of the clicked location
                latitude = latLng.latitude;
                longitude = latLng.longitude;
            }
        });

    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}

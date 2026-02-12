package com.healthai.app.ui.nearby

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest

@Composable
fun NearbyHospitalBloodBankScreen(context: Context) {

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val placesClient = Places.createClient(context)

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var placesList by remember { mutableStateOf<List<Place>>(emptyList()) }

    // Get Current Location
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    // Fetch Nearby Hospitals & Blood Banks
    LaunchedEffect(currentLocation) {
        currentLocation?.let { loc ->

            val placeFields = listOf(
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS,
                Place.Field.TYPES
            )

            val request = FindCurrentPlaceRequest.newInstance(placeFields)

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                placesClient.findCurrentPlace(request)
                    .addOnSuccessListener { response ->
                        placesList = response.placeLikelihoods
                            .map { it.place }
                            .filter {
                                val placeTypes = it.types?.map { it.toString().lowercase() } ?: emptyList()
                                placeTypes.contains("hospital") || placeTypes.contains("blood_bank")
                            }
                    }
            }
        }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1C2D))
            .padding(16.dp)
    ) {

        Text(
            text = "Nearby Hospitals & Blood Banks",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn {
            items(placesList) { place ->
                PlaceCard(place)
            }
        }
    }
}

@Composable
fun PlaceCard(place: Place) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF132F4C)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = place.name ?: "Unknown",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = place.address ?: "",
                color = Color.LightGray,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (place.types?.map { it.toString().lowercase() }?.contains("blood_bank") == true)
                    "Blood Bank"
                else
                    "Hospital",
                color = Color(0xFF00E5FF),
                fontSize = 12.sp
            )
        }
    }
}
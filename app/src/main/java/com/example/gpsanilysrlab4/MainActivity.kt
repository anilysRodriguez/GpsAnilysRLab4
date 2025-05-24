package com.example.gpsanilysrlab4

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF5F5F5)
            ) {
                SendMessageWithLocation()
            }
        }
    }
}

@Composable
fun SendMessageWithLocation() {
    val context = LocalContext.current
    val message = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("+507 ") }

    val locationLink = remember { mutableStateOf("Ubicación no disponible") }

    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    LaunchedEffect(Unit) {
        if (
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    locationLink.value = "https://www.google.com/maps?q=${it.latitude},${it.longitude}"
                }
            }
        }
    }

    // se activa solo si hay ubicación válida
    val isLocationReady = locationLink.value != "Ubicación no disponible"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.map),
            contentDescription = "Logo",
            modifier = Modifier
                .size(160.dp)
                .padding(top = 15.dp, bottom = 24.dp)
        )
        Text(
            text = "Mensaje y ubicación por WhatsApp",
            fontSize = 18.sp,
            color = Color(0xFF8E24AA),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = phoneNumber.value,
            onValueChange = { phoneNumber.value = it.filter { c -> c.isDigit() || c == '+' } },
            label = { Text("Número de WhatsApp (ej: +50760001111)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = message.value,
            onValueChange = { message.value = it },
            label = { Text("Escribe tu mensaje \uD83E\uDDED") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val finalMessage = "${message.value.trim()}\n \uD83D\uDCCDMi ubicación:\uD83D\uDC49\uD83D\uDCCD ${locationLink.value}"
                val cleanNumber = phoneNumber.value.replace("+", "").trim()
                val uri = Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber&text=${Uri.encode(finalMessage)}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = isLocationReady
        ) {
            Text("\uD83D\uDE03 Enviar por WhatsApp")
        }
    }
}



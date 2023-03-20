# MapsSDK_Country_Highlighter


A Kotlin project that aims to create an interactive map of the world using the Maps SDK and Google Maps API.

It uses a list of country GeoJSON layers which act as the overlay for each country, as to provide visual and informational feedback to touching the corresponding country.

It also pulls data from a CSV file for each country.

This file contains the GeoJSON and the country code, both of which are necessary for the system to work, as well as supplementary data on any given country.

Notes : Please add local.properties file containing the following

```
sdk.dir=C\:\\Users\\PDS12\\AppData\\Local\\Android\\Sdk

MAPS_API_KEY=YOUR MAPS_API_KEY
```

You can obtain your own api key here : https://developers.google.com/maps/documentation/javascript/get-api-key#console

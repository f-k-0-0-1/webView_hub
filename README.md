# WebView Hub

WebView Hub is a lightweight Android application that brings your favorite websites together in one place.

Instead of opening multiple websites separately, users can create website shortcuts and open them directly inside the app using Android WebView.

## Features

- Add custom website shortcuts using a URL
- Automatically generate website names from URLs
- Automatically load website favicons
- Open websites directly inside the app using Android WebView
- Built-in shortcuts for popular websites
- Long-press website cards to access deletion controls
- Temporary delete button with automatic 3-second timeout
- Persistent local storage for custom website shortcuts
- Deleted shortcuts remain deleted after app restarts
- Website Locker for managing saved shortcuts
- Clean and responsive grid-based interface
- Light-themed WebView experience
- Centralized theme management
- Customized Android status-bar and navigation-bar appearance
- Fully local shortcut management with no account or server required
- Modular architecture for storage, theme, website, and system UI management

## Built-in Websites

WebView Hub currently includes:

- Amazon
- Facebook
- Instagram
- Reddit

Users can add additional websites using the `+` button.

## How It Works

### Adding a Website

1. Tap the `+` button.
2. Enter a website URL.
3. If the URL does not contain `https://`, WebView Hub adds it automatically.
4. The website name is generated from the domain.
5. The website favicon is loaded automatically.
6. The shortcut is added to the home screen.
7. The shortcut is saved locally on the device.

### Opening a Website

Tap any website card to open it inside the application's WebView.

### Deleting a Website

Long press any website card.

A red delete button appears in the corner of the card.

Tap the delete button to remove the shortcut.

The delete button automatically disappears after 3 seconds if it is not used.

Deleted shortcuts are also removed from local storage, so they do not return when the application is restarted.

## Technology Stack

- Kotlin
- Android Studio
- Android SDK
- AndroidX
- Material Components
- RecyclerView
- WebView
- Jetpack DataStore
- Coil

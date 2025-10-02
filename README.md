# 🎵 MusicPlayer

A modern, lightweight music player app for Android built with Jetpack Compose, powered by Media3 API for audio playback and SQLDelight for playlist management.

Designed with Clean Architecture (data/domain/presentation) and Material3 UI, the app lets you easily browse your local music folders, create and manage playlists, and enjoy smooth playback with persistent notifications and lockscreen integration.

➡️ [Download MusicPlayer APK](https://github.com/andreiflo94/musicplayer/raw/master/builds/app-debug.apk)

## ✨ Features

📂 Browse by Folders – Automatically scans your storage and lists available music folders.

🎶 Playback Controls – Play/Pause, Next/Previous, Seek bar with smooth progress updates.

🖼 Artwork & Metadata – Displays album art, artist, and track name.

📑 Playlist Management – Create and delete playlists using SQLDelight.

🔔 Persistent Notification – Full playback control directly from the notification bar.

🔒 Lockscreen & Headset Integration – Control playback from your lockscreen and headset buttons.

🧭 Navigation – Simple bottom navigation between Music Folders and Playlists.

🌙 Dark mode

🎨 Modern UI – Built entirely with Jetpack Compose and Material3.

## 🏗 Architecture

The project follows a Clean Architecture approach with three main layers:

### Data Layer
Responsible for accessing the MediaStore (scanning music files and folders).
Provides repositories to manage playlists using SQLDelight.
Handles playback logic through MediaControllerManagerImpl.

### Domain Layer
Defines core interfaces and models (Track, MediaControllerManager).

### Presentation Layer
Built with Jetpack Compose (Material3).
Includes bottom navigation, folder browsing, playlist screens, and now-playing UI.

## ⚙️ Tech Stack

Kotlin + Coroutines

Jetpack Compose (Material3)

Media3 (ExoPlayer, MediaSession, UI)

SQLDelight for playlists

Hilt (Dependency Injection)

Glide Compose for album art

Accompanist Permissions

## 🧪 Testing

Includes unit tests with Mockito for MediaControllerManager.

Basic tests cover playback state, track info, and interaction with the controller.

UI tests can be extended using Compose UI Test.

## 📸 Screenshots
<img src="https://github.com/user-attachments/assets/a6765775-ebd1-4c10-98ed-9ce8f9c2df5c" width="250"/>
<img src="https://github.com/user-attachments/assets/aa420b72-fabb-4039-a7b3-f3390d413a6a" width="250"/>
<img src="https://github.com/user-attachments/assets/a729826b-d5aa-48b5-99bf-282c1af48346" width="250"/>
<img src="https://github.com/user-attachments/assets/80a392a5-b27c-4f8e-b64e-74a801a8b92a" width="250"/>
<img src="https://github.com/user-attachments/assets/4000f098-75d2-4aff-a4de-413b979bd1f3" width="250"/>
<img src="https://github.com/user-attachments/assets/61bf1792-8c9a-4a39-8305-8debdfcfac6f" width="250"/>

<h1 align="center">Notes</h1>

<p align="center">  
Notes is a small demo application based on modern Android application tech-stacks and MVVM architecture.<br>This project is for focusing especially on the new persistence library Room.</p>
</br>

## Download
Go to the [Releases](https://github.com/asayushg/Notes/releases) to download the lastest APK.


## Tech stack & Open-source libraries
- Minimum SDK level 24
- JetPack
  - LiveData - notify domain layer data to views.
  - Lifecycle - dispose of observing data when lifecycle state changes.
  - ViewModel - UI related data holder, lifecycle aware.
  - [Room Persistence](https://developer.android.com/jetpack/androidx/releases/room) - construct a database using the abstract layer.
- Architecture
  - MVVM Architecture (View - DataBinding - ViewModel - Model)
- [Firebase](https://firebase.google.com/) - for saving user's notes with Firebase Database & Firebase Auth.


## Architecture
Notes is based on MVVM architecture and a repository pattern.

![architecture](https://user-images.githubusercontent.com/24237865/77502018-f7d36000-6e9c-11ea-92b0-1097240c8689.png)


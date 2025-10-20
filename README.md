# Mishu Music Player — Starter (v2)
This project is a starter **Mishu Music Player** inspired by SimpleMobileTools' Simple Music Player.
It includes:
- MediaStore scan of audio files
- RecyclerView listing songs
- Playback using ExoPlayer via a foreground MediaService with notification & media session controls
- Simple playlist manager (save named playlists to SharedPreferences)
- Sleep timer (stop playback after X minutes)
- Basic AppWidget (play/pause)
- Blue Material theme and placeholder adaptive icon — replace `app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.png` with your logo image for final branding.

## Replace the logo
You provided a logo image — replace the placeholder adaptive icon files in `app/src/main/res/mipmap-*` and `mipmap-anydpi-v26` with your provided logo PNGs (512x512 recommended). Also set the adaptive icon xml in `res/mipmap-anydpi-v26/ic_launcher.xml` to point to your foreground/background layers.

## Build & release
1. Open in Android Studio and let Gradle sync.
2. Test on device; grant media permissions.
3. Generate signed APK via Build > Generate Signed Bundle / APK and upload to Play Console (remember to sign with your key).

## Notes
- This is a minimal starter; expand features as needed (equalizer, album art caching, playlists UI polishing, widgets with more states).
- I integrated a MediaService and notification so playback continues in background and supports lockscreen controls.

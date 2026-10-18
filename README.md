# JARVIS AI — Android starter project

This is a fresh Android WebView starter for the JARVIS tablet app. It is prepared for a cloud APK build using GitHub Actions.

## Included now
- Android app shell that opens a local JARVIS chat UI
- Text chat interface with optional speech-to-text (Android speech recognizer) and text-to-speech
- Quick buttons to open WhatsApp, Instagram, Snapchat, Phone, and Settings
- Responsive layout for Android tablets

## Important limitations
- This starter is **not a complete AI assistant yet**. The chat currently returns a local demo response.
- Reading or sending Instagram/Snapchat private messages is not included; those services do not provide unrestricted access to DMs for third-party apps.
- Full device control and calling require Android permissions, user-granted access, and OS restrictions. The app only offers safe app-launch shortcuts.
- No APK is included in this source ZIP. GitHub Actions builds it after you upload the project to a GitHub repository.

## Build on your tablet
1. Sign in at https://github.com and create a new **public or private** repository named `jarvis-android`.
2. Upload the *contents* of this folder (not the parent folder) to the repository. GitHub mobile browser can upload files, but uploading folders may be awkward; use GitHub's web upload UI or a repository-upload app if needed.
3. Open the repository's **Actions** tab and enable Actions if prompted.
4. Select **Build JARVIS Android APK** and press **Run workflow** (if manual workflow is available), or push a commit to trigger it.
5. Wait for the workflow to finish, open the successful run, then download the `jarvis-debug-apk` artifact.
6. Extract the artifact ZIP and install `app-debug.apk`. Android may ask you to allow installs from that browser/file manager.

## AI backend
To make responses genuinely AI-powered, a backend endpoint and API key are required. Do not put secret API keys inside the Android app. A backend can be added later.

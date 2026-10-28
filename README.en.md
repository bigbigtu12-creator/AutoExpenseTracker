# AutoExpenseTracker

[简体中文](README.md) | English

AutoExpenseTracker is an Android app for recording WeChat Pay and Alipay transactions with as little manual work as possible. It reads relevant payment notifications on your phone, presents an entry for you to review, and sends the confirmed record to your own Feishu (Lark) group bot or Bitable table. The core payment-to-Feishu flow has been used on the author's Android phone.

## Preview

| Home | Confirm entry | Skin picker |
| --- | --- | --- |
| ![Real app home screen](screenshots/app-home-real.jpg) | ![Real app confirmation screen](screenshots/app-confirm-real.jpg) | ![Real app skin picker](screenshots/skin-picker-real.jpg) |

These are unaltered device screenshots supplied by the author. The confirmation amount is the `0.00` placeholder; no real transaction or Feishu credential is shown. The device status bar and floating shortcut strip remain visible.

| Feishu group notification example | Feishu Bitable example |
| --- | --- |
| ![Fictional Feishu group notification](screenshots/feishu-chat-example.png) | ![Fictional Bitable entries](screenshots/feishu-bitable-example.png) |

These two illustrations were recreated from Feishu screenshots with **entirely fictional messages and rows**. They do not display real financial records; small interface details may differ from Feishu itself.

## Toffee and skins

The little pig with a magnifying glass is **Toffee**, a character IP designed by the author. Toffee appears in the default Caramel skin. You can choose one of three other built-in styles in the app: Sunny, Folk, or Mono. The app does not currently include a skin editor. Developers can modify the open source theme, UI, and image resources to build their own version; see the [skin customization guide (Chinese)](SKIN_CUSTOMIZATION.md). The code and character artwork have different usage terms; see [ASSETS.md](ASSETS.md).

## How it works

1. The app detects a relevant WeChat or Alipay notification and attempts to extract the amount and transaction type on device.
2. You review the amount, choose a category, and confirm the entry. You can also add an entry manually or open the manual entry screen from a home-screen widget.
3. Choose one of three delivery modes: group bot only, Bitable only, or Bitable followed by a group notification after the table write succeeds.

Feishu credentials and the target table belong to each user. This repository does not contain an account, secret, webhook, or personal transaction data.

## Build and set up

- To install without building, download `AutoExpenseTracker-v1.0-debug.apk` from GitHub Releases. It is an installable debug-signed build of this public source, not a production-signed APK. Android cannot install it over an existing copy with the same package name but a different signing key.
- Android 8.0 (API 26) or newer; Android Studio, Android SDK 34, and JDK 17 for building.
- Open this repository in Android Studio and run the `app` configuration. On Windows, `gradlew.bat :app:assembleDebug` builds an installable debug APK under `app/build/outputs/apk/debug/`.
- Grant notification access, accessibility, and overlay permissions as requested by the app.
- Create your own Feishu app and Bitable table or group bot, then enter their details in the app. The detailed [setup guide](CONFIGURATION.md) is currently in Chinese.
- Use the settings test before recording real transactions.

The app interface and detailed setup guide are currently Chinese. A debug-signed APK is attached to the GitHub Release; a production-signed APK is not provided.

## Privacy and limitations

Android treats notification access and accessibility as sensitive permissions. This app uses accessibility notification events and a notification-listener backup path to recognize WeChat and Alipay payment alerts. The accessibility service is configured without window-content retrieval. Android grants notification access broadly; the app then filters for those two package names. Notification permission lets it show a confirmation alert; foreground operation and battery settings help keep detection available in the background. Network access is used to send confirmed entries to your own Feishu configuration.

The current onboarding also asks for overlay permission, although the current code opens the confirmation screen through a system notification and does not appear to draw a system overlay directly. This requirement should be reviewed in a future version.

The app connects directly to your Feishu configuration; the project author does not operate a relay server. Credentials are stored in the app's local settings, and Android automatic backup is disabled. If your Bitable table includes an `原始消息` (raw notification) field, the app writes the recognized notification text to that field; omit it if you do not want to store that text.

Notification formats and background behavior vary by device and app version, so always review the suggested entry. The cleaned public source has passed unit tests and an independent debug build; it has not yet completed a fresh real-payment test on a phone after the privacy changes. See the [privacy details (Chinese)](PRIVACY.md). The project code uses the [MIT license](LICENSE); Toffee, other original artwork, and screenshots have separate [asset usage terms](ASSETS.md). The bundled font has its own [OFL license](licenses/OFL-zcool-kuaile.txt).

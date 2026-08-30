# Remote Android APK (on‑device companion app)

The on‑device half of
[`JavaRemoteAndroidSDKController`](https://github.com/FruNoman/JavaRemoteAndroidSDKController) /
[`python-remote-android-sdk-controller`](https://github.com/FruNoman/python-remote-android-sdk-controller).

An external test process on the PC sends `am broadcast` intents; this app receives
them and calls the real Android framework managers (Bluetooth, Wi‑Fi / Wi‑Fi P2P,
audio, media session, telephony / SMS, location, USB, storage, device‑policy), then
returns the result. It exists so a test can manipulate a device's radios and
subsystems from the outside without an instrumentation build per case.

Targets `android.hardware.type.automotive` — the intended host is an infotainment
head unit on a test bench.

## Components

- `MainActivity` — wires up the receivers, hosts a small media/visualizer surface
- `receivers/` — one `BroadcastReceiver` per subsystem: `BluetoothReceiver`,
  `WifiReceiver`, `WifiP2PReceiver`, `AudioReceiver`, `MediaSessionReceiver`,
  `TelephonyReceiver`, `LocationReceiver`, `EnvironmentReceiver`, `PlayerReceiver`,
  `DeviceAdminSample`
- `utils/` — helpers (`WifiConfigUtil`, `PlayerUtils`, …)

## Build

```bash
./gradlew assembleDebug        # app/build/outputs/apk/debug/app-debug.apk
```

The Java/Python controllers bundle a prebuilt `app-debug.apk` and install it on
first use, so you normally don't build this by hand.

## Permissions

The manifest requests a large set of protected/`signature|privileged` permissions
(`LOCATION_HARDWARE`, `MODIFY_PHONE_STATE`, `BLUETOOTH_PRIVILEGED`,
`MANAGE_USB`, `NETWORK_SETUP_WIZARD`, `CREATE_USERS`, `CAPTURE_AUDIO_OUTPUT`, …).
It therefore only works on a **userdebug/eng build, a rooted device, or when
installed as a privileged system app** — the normal automotive bench setup.
It is a test tool, not a Play‑Store app.

## License

MIT — see [LICENSE](LICENSE).

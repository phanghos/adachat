## Gradle
Paste this line in your project build.gradle below `jcenter()`
```
maven { url 'https://www.jitpack.io' }
```
Add this line to your dependencies in your build.gradle (module)
```
compile 'com.github.phanghos:adachat:1.0.3'
```
Use a `NoActionBar` theme, either `Theme.AppCompat.NoActionBar` or `Theme.AppCompat.Light.NoActionBar` as your app theme in `styles.xml`
##### You're good to go!
## Basic Usage
The easiest way to integrate the chat module into your project is using the default configuration. You only need to call the `ChatUtils.startChat()` method from your `Activity` or `Fragment` and the default `ChatActivity` will be launched. `ChatUtils.startChat()` receives the `Context`, ID of the current user, ID of the receiver o the message, and the window title (which will be used as `Toolbar` title).
```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ChatUtils.startChat(this, 1, 2, "Chat Window");
}
```

# DefNotification
App internal banner notifications

## Features
- Support swiping up to close
- Support set animation interpolator
- Support for custom layouts
- Support for use in specified ViewGroup

![image](https://github.com/cyynf/DefNotification/blob/master/image.gif)

## Usage

Add it in your root build.gradle at the end of repositories:
``` groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Add the dependency
``` groovy
	implementation 'com.github.cyynf:DefNotification:1.0'
```
Add notification layout
``` xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="80dp"
    android:layout_margin="5dp"
    android:background="@drawable/bg_white_corner"
    android:elevation="3dp"
    android:orientation="vertical"
    android:padding="15dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:gravity="center_vertical">

        <ImageView
            android:id="@+id/icon"
            android:layout_width="20dp"
            android:layout_height="20dp"
            tools:src="@mipmap/ic_launcher" />

        <TextView
            android:id="@+id/title"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="10dp"
            android:textColor="#333"
            android:textSize="16sp" />

    </LinearLayout>

    <TextView
        android:id="@+id/content"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:textColor="#666"
        android:textSize="14sp" />

</LinearLayout>
```
Use Java
``` java
    DefNotification defNotification = new DefNotification(this)
            .setContentView(R.layout.notification)
            .setDuration(3000)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(), "click notification", Toast.LENGTH_SHORT).show();
                }
            });

    ImageView icon = defNotification.findViewById(R.id.icon);
    icon.setImageDrawable(getDrawable(this));
    TextView title = defNotification.findViewById(R.id.title);
    title.setText(getString(R.string.app_name));
    TextView content = defNotification.findViewById(R.id.content);
    content.setText("Your order has been cancelled, click for details...");

    defNotification.show();
```

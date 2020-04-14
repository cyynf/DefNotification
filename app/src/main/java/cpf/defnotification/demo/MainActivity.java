package cpf.defnotification.demo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cpf.defnotification.DefNotification;

public class MainActivity extends AppCompatActivity {

    private DefNotification defNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defNotification = new DefNotification(this)
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
        title.setText("订单已取消");
        TextView content = defNotification.findViewById(R.id.content);
        content.setText("您的订单已被取消，点击查看详情...");
    }

    public void show(View v) {
        defNotification.show();
    }

    public void hide(View v) {
        defNotification.hide();
    }

    public Drawable getDrawable(Context context) {
        try {
            PackageManager packageManager = context.getApplicationContext()
                    .getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    context.getPackageName(), 0);
            return packageManager.getApplicationIcon(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

}

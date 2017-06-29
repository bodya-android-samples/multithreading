package ru.popov.bodya.executorcompletionsample;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;


import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements Worker.LoaderCallback {

    @BindView(R.id.linear_layout)
    LinearLayout linearLayout;

    @BindView(R.id.load_button)
    Button button;

    private Worker worker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        worker = ((CompletionApplication) getApplication()).getWorker();
        worker.setListener(this);

        button.setOnClickListener(v -> worker.queueTask(() -> drawableFromUrl("https://avatars2.githubusercontent.com/u/9767952?v=3&s=460")));
    }

    public static Drawable drawableFromUrl(String url) throws IOException {
        InputStream is = null;
        try {
            URLConnection urlConn = new URL(url).openConnection();
            is = urlConn.getInputStream();
        } catch (Exception ignored) {
        }
        return Drawable.createFromStream(is, "src");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        worker.setListener(null);
    }


    @Override
    public void onLoadFinished(Drawable image) {
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(image);
        linearLayout.addView(imageView);
    }
}

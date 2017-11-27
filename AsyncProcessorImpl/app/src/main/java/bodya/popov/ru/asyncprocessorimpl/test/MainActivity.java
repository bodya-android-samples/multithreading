package bodya.popov.ru.asyncprocessorimpl.test;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Callable;

import bodya.popov.ru.asyncprocessorimpl.R;
import bodya.popov.ru.asyncprocessorimpl.async.SingleAsyncExecution;

public class MainActivity extends AppCompatActivity {

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultTextView = findViewById(R.id.result_text_view);
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SingleAsyncExecution.fromCallable(mCallable)
                        .addCallback(mCallback)
                        .execute();
            }
        });

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResultTextView.setText(R.string.app_name);
            }
        });


    }

    private SingleAsyncExecution.Callback mCallback = new SingleAsyncExecution.Callback() {
        @Override
        public <V> void onSuccess(@NonNull V result) {
            mResultTextView.setText(String.valueOf(result));
        }

        @Override
        public void onException(Exception e) {
            mResultTextView.setText(R.string.exception_message);
        }
    };

    private Callable<Integer> mCallable = new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            return execute();
        }
    };

    private int execute() {
        return 5;
    }


}

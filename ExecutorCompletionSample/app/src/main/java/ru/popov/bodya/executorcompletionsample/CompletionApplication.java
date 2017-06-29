package ru.popov.bodya.executorcompletionsample;


import android.app.Application;

public class CompletionApplication extends Application {

    private Worker worker;


    @Override
    public void onCreate() {
        super.onCreate();
        worker = new Worker();
    }

    public Worker getWorker() {
        return worker;
    }
}

package com.blanke.downloadprogress;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    private DownloadProgressButton downloadButton;
    private Subscription sub;
    private Button resetButton;
    private Observable<Long> obser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadButton = (DownloadProgressButton) findViewById(R.id.download);
        resetButton = (Button) findViewById(R.id.reset);
        downloadButton.setEnablePause(false);

        obser = Observable.interval(700, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread());

        downloadButton.setOnDownLoadClickListener(new DownloadProgressButton.OnDownLoadClickListener() {
            @Override
            public void clickDownload() {
                sub = obser.subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (downloadButton.getState() == DownloadProgressButton.FINISH) {
                            sub.unsubscribe();
                            return;
                        }
                        int p = new Random().nextInt(20);
                        downloadButton.setProgress(downloadButton.getProgress() + p);
                    }
                });
            }

            @Override
            public void clickPause() {
                sub.unsubscribe();
            }

            @Override
            public void clickResume() {
                clickDownload();
            }

            @Override
            public void clickFinish() {
                sub.unsubscribe();
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sub != null) {
                    sub.unsubscribe();
                }
                downloadButton.reset();
            }
        });
    }
}

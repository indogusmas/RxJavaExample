package com.example.indo.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    EditText textCount;
    EditText textTotal;
    private String TAG = MainActivity.class.getSimpleName();
    List<String> listAnimal;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textTotal = (EditText) findViewById(R.id.text_total);
        textCount = (EditText) findViewById(R.id.text_count);

        final Observable<String> ediTextObservable = Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                        assert  textCount != null;
                        textCount.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (s.length()  == 5){
                                    emitter.onComplete();
                                }
                                emitter.onNext(s.toString());
                            }
                        });
                    }
                }
        );

        final Observer<String>editTextObserver = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {


            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: "+ s );
                textTotal.setText(s.toUpperCase());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: "+ textCount.getText());
            }
        };

        ediTextObservable
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        return s.toUpperCase();
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(editTextObserver);

        Observable<String> animalObServable = getAnimalObservable();
        listAnimal = new ArrayList<>();

        Observer<String>animalObserver = getAnimalObserver();

        animalObServable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s.toLowerCase().startsWith("b");
                    }
                })
                .subscribe(animalObserver);

        Log.e(TAG, "onCreate: "+ listAnimal);

    }
    private  Observer<String> getAnimalObserver(){
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "onSubscribe: " );
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: "+ s );
                listAnimal.add(s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete: "+ listAnimal );
            }
        };

    }

    private Observable<String>getAnimalObservable(){
        return  Observable.just("Ant","Bee","Cat","Dog","Fox","Buffalo","Bear");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * don't send event once the Activity is destroyed
         */
        disposable.dispose();
    }
}
























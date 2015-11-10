package edu.mit.ll.apsm.thermalestimatorlib.util;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.util.Log;


public class DataSource implements Runnable {

    class MyObservable extends Observable {
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
        }
    }

    private MyObservable notifier;

    {
        notifier = new MyObservable();
    }


    public void run() {
        Log.i("DataSource", "DataSource:run()");
        try {
            while (true) {
                Thread.sleep(50); // refresh rate
                // Collect some data
                notifier.notifyObservers();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void addObserver(Observer observer) {
        notifier.addObserver(observer);
    }


    public void removeObserver(Observer observer) {
        notifier.deleteObserver(observer);
    }


    public void addData(ArrayList<Integer> primary, ArrayList<Integer> secondary) {
        Log.i("DataSource", "addData() called");
    }
    // Needs GetX and GetY functions still
}

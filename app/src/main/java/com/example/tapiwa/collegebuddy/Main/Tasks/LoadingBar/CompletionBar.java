package com.example.tapiwa.collegebuddy.Main.Tasks.LoadingBar;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tapiwa on 10/23/17.
 */

public class CompletionBar {

    int total;
    int completed;
    int screenWidth;
    View horizontalBar;
    TextView completionTxtV;

    public CompletionBar() {

    }

    public CompletionBar(int total, int completed, View view, TextView completionTxtV) {
        this.total = total;
        this.completed = completed;
        this.horizontalBar = view;
        this.completionTxtV = completionTxtV;
        this.screenWidth = view.getWidth();
    }

    public void setCompletionBar() {
        changeLengthCompletionBar();
        setColor();
        percentageCompleted();
    }

    public void updateCompletionBar(int completed, int uncompleted, int initialLength, View view, TextView pcntage) {
        this.total = completed + uncompleted;
        this.completed = completed;
        this.completionTxtV = pcntage;
        this.horizontalBar = view;
        this.screenWidth = initialLength;
        setCompletionBar();
    }

    public void percentageCompleted() {
        int pcnt = (int) Math.ceil(((double) completed / total) * 100);
        String percentage = pcnt + "%";
        completionTxtV.setText(percentage);
    }



    public int calculateCompletedScreenWidth() {
        double x = ((double) completed / total) * screenWidth;
        int newLoadingBarWdith = (int) Math.floor(x);
        return newLoadingBarWdith;
    }

    public void changeLengthCompletionBar() {
        horizontalBar.setLayoutParams(new LinearLayout
                .LayoutParams(calculateCompletedScreenWidth(), 100));
    }

    public void reset() {
        completed = 0;
        total = 0;
        changeLengthCompletionBar();
        percentageCompleted();
        setColor();
    }

    public void updateTotal() {
        ++total;
        setCompletionBar();
    }


    public void setColor() {

       if(completed <= (0.10 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(208,35,35));
           completionTxtV.setTextColor(Color.rgb(208,35,35));
           return;
       }

       if(completed < (0.20 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(191,32,69));
           completionTxtV.setTextColor(Color.rgb(191,32,69));
           return;
       }

        if(completed < (0.30 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(170,28,103));
            completionTxtV.setTextColor(Color.rgb(170,28,103));
            return;
        }

        if(completed < (0.40 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(147,25,148));
            completionTxtV.setTextColor(Color.rgb(147,25,148));
            return;
        }

        if(completed < (0.50 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(97,30,139));
            completionTxtV.setTextColor(Color.rgb(97,30,139));
            return;
        }

        if(completed < (0.60 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(78,34,142));
            completionTxtV.setTextColor(Color.rgb(78,34,142));
            return;
        }

        if(completed < (0.70 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(54,40,144));
            completionTxtV.setTextColor(Color.rgb(54,40,144));
            return;
        }

       if(completed < (0.80 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(36,64,140));
           completionTxtV.setTextColor(Color.rgb(36,64,140));
           return;
       }


       if(completed < (0.90 * total)) {
           horizontalBar.setBackgroundColor(Color.rgb(31,84,135));
           completionTxtV.setTextColor(Color.rgb(31,84,135));
           return;
       }

        if(completed < (0.95 * total)) {
            horizontalBar.setBackgroundColor(Color.rgb(42,143,92));
            completionTxtV.setTextColor(Color.rgb(42,143,92));
            return;
        }

       horizontalBar.setBackgroundColor(Color.rgb(35,169,28));
        completionTxtV.setTextColor(Color.rgb(35,169,28));
    }
}

package org.mti.hip;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import org.hamcrest.Matchers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


/**
 * Created by r624513 on 12/10/15.
 */
public class SuperTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public SuperTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    private SuperActivity activity;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        activity = getActivity();
    }

    public boolean isAppSetup() {
        if(activity instanceof MainActivity) {
           return ((MainActivity) activity).checkForAppReadiness();
        }
        return false;
    }

    public void runShellCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String aux;

        try {
            while ((aux = bufferedReader.readLine()) != null) {
                builder.append(aux);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("Shell", builder.toString());
    }

    public void clickOnText(String text) {
        Espresso.onView(withText(text)).perform(click());
    }

    public void isTextVisible(String text) {
        // NOTE: Following comment requires exact match
//        Espresso.onView(withText(text)).check(matches(isDisplayed()));

        // NOTE: matches based on "contains"
        Espresso.onView(withText(Matchers.containsString(text))).check(matches(isDisplayed()));
    }

    public void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void enterText(int id, String text) {
        Espresso.onView(withId(id)).perform(typeText(text));
    }

    public void clickOnView(int id) {
        pause(600);
        Espresso.onView(withId(id)).perform(click());
    }
}

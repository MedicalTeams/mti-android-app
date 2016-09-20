package org.mti.hip.manual;

import org.mti.hip.MainActivity;
import org.mti.hip.R;
import org.mti.hip.SuperTest;

/**
 * Created by r624513 on 12/15/15.
 */
public class DemoTests extends SuperTest {

    public DemoTests() {
        super(MainActivity.class);
    }

    public void testDemoPartOne() {
        // Assumes somewhere/someone other than Ayao Jackline has been selected
        // Dashboard
        pause(2000);
        clickOnView(R.id.new_visit);

        // Consultation
        clickOnView(R.id.rb_visit);
        clickOnView(R.id.rb_refugee);
        enterText(R.id.patient_years, "25");
        clickOnView(R.id.patient_years);
        clickOnView(R.id.rb_female);
        enterText(R.id.opd_number, "1234");
        clickOnView(R.id.action_next);

        // Diagnosis entry
        pause(400);
        clickOnText(getActivity().getString(R.string.header_diagnosis));
        pause(400);
        clickOnText("Meningitis");

        clickOnView(R.id.action_next);

        pause(700);
        // Summary Screen
        clickOnView(R.id.submit);

        // Dashboard
        pause(1500);
    }

    public void testDemoPartTwo() {
        // TODO refactor to allow test to inject clinicians
        // Assumes dummy code to add clinicians is active in the app
        // Important, toggle airplane mode on before running
        pause(500);
        clickOnView(R.id.bt_sign_out);
        pause(1000);
        clickOnText("Nakivale");
        pause(1000);
        clickOnText("Kibengo");
        pause(1000);
        clickOnText("Ayao Jackline");

        // Dashboard
        pause(2000);
        clickOnView(R.id.new_visit);

        // Consultation
        clickOnView(R.id.rb_visit);
        clickOnView(R.id.rb_refugee);
        clickOnView(R.id.patient_years);
        enterText(R.id.patient_years, "34");
        clickOnView(R.id.rb_male);
        enterText(R.id.opd_number, "1235");
        clickOnView(R.id.action_next);

        // Diagnosis entry
        pause(400);
        clickOnText("Injuries");
        pause(400);
        clickOnText("Accident");

        clickOnView(R.id.action_next);

        // displays alert
        pause(2000);
        clickOnText("Okay");

        clickOnText(getActivity().getString(R.string.injury_location));

        clickOnText("Camp");

        clickOnView(R.id.action_next);

        pause(700);
        // Summary Screen
        clickOnView(R.id.submit);

        // Dashboard
        pause(500);

        clickOnView(R.id.bt_manual_sync);
        pause(500);
        clickOnText("Okay");
        pause(1500);
    }

    public void testDemoPartThree() {
        // Assumes there are offline visits to sync and the device is online
        pause(1000);
        clickOnView(R.id.bt_manual_sync);
        pause(1500);
        clickOnText("Okay");
        pause(1500);
    }


}

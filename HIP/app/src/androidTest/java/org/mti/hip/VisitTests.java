package org.mti.hip;

import java.util.concurrent.TimeUnit;

public class VisitTests extends SuperTest {

    public VisitTests() {
        super(MainActivity.class);
    }

    public void testHappyPath() {
        long oneDayMillis = TimeUnit.DAYS.toMillis(1);
//        ArrayList<String> commands = new ArrayList<>();
//        commands.add("date");
//        commands.add("date -u 14000000"); // on my emulator this works for a second and then resets
//        commands.add("date -u " + System.currentTimeMillis() + oneDayMillis);
//        commands.add("date");
//        for(String command : commands) {
//            runShellCommand(command);
//        }
        if(isAppSetup()) {
            enterBasicHappyPathVisit();
        } else {
            setupApp();
        }
    }

    private void setupApp() {
        clickOnView(R.id.register);
        clickOnView(R.id.register);
        clickOnText("Nakivale");
        clickOnText("Kibengo");
        clickOnView(R.id.bt_add_new_staff_member);
        enterText(R.id.et_dialog, "Mick");
        clickOnText(getActivity().getString(R.string.okay));
        clickOnText("Mick");
        enterBasicHappyPathVisit();


    }

    private void enterBasicHappyPathVisit() {
        // Dashboard
        clickOnView(R.id.new_visit);

        // Consultation
        clickOnView(R.id.rb_visit);
        clickOnView(R.id.rb_refugee);
        clickOnView(R.id.patient_years);
        enterText(R.id.patient_years, "25");
        clickOnView(R.id.rb_female);
        enterText(R.id.opd_number, "1234");
        clickOnView(R.id.action_next);

        // Diagnosis entry
        // TODO brittle (should use onData matching such as list ID, pos 0), refactor
        clickOnText(getActivity().getString(R.string.header_diagnosis));
        clickOnText("URTI");

        clickOnView(R.id.action_next);

        // TODO method to retrieve list obj for getName() text matching

        // Summary Screen (with simple assertion)
        isTextVisible("URTI");
        clickOnView(R.id.submit);

        // Dashboard
        isTextVisible("You have sent");
    }


}

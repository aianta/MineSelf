package ca.mineself;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)

public class MiningActivityTest {

    @Rule
    public ActivityTestRule<MiningActivity> activityRule =
            new ActivityTestRule<>(MiningActivity.class);

    @Test
    public void activityAppears(){
        onView(withText("MineSelf")).check(matches(isDisplayed()));
    }

}

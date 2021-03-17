package luongvo.com.todolistminimal.UITests;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.AdapterView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import luongvo.com.todolistminimal.MainActivity;
import luongvo.com.todolistminimal.R;
import luongvo.com.todolistminimal.ToDoItem;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(JUnit4.class)
public class MainActivityTest {
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);

    @Test
    public void descriptImageCheck() {
        onView(withId(R.id.descriptImage))
                .check(matches(isDisplayed()));
    }

    @Test
    public void tabsCheck() {
        onView(withId(R.id.tabs))
                .check(matches(isDisplayed()));
    }

    @Test
    public void view_pagerCheck() {
        onView(withId(R.id.view_pager))
                .check(matches(isDisplayed()));
    }

    @Test
    public void actionButtonCheck() {
        onView(withId(R.id.actionButton))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void actionButtonClick() {
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());
    }

    /*@Test
    public void selectTab() {


    }*/

}

package luongvo.com.todolistminimal.UITests;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import luongvo.com.todolistminimal.AboutActivity;
import luongvo.com.todolistminimal.AddTodoItem;
import luongvo.com.todolistminimal.MainActivity;
import luongvo.com.todolistminimal.R;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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
                        isClickable()
                )));
    }

    @Test
    public void actionButtonClick() {
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());
        Intents.intended(hasComponent(AddTodoItem.class.getName()));
    }


    @Test
    public void actionsBarCheck() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(ViewMatchers.withText(R.string.about))
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.clean_all_done))
                .check(matches(isDisplayed()));
    }

    @Test
    public void aboutMenuItemClick() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(ViewMatchers.withText(R.string.about))
                .perform(ViewActions.click());
        Intents.intended(hasComponent(AboutActivity.class.getName()));
    }
}

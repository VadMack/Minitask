package luongvo.com.todolistminimal.UITests;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import luongvo.com.todolistminimal.MainActivity;
import luongvo.com.todolistminimal.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class CreatedTaskWithoutReminderTest {

    private static final String PACKAGE = InstrumentationRegistry.getTargetContext().getPackageName();
    private UiDevice device;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void createdTaskCheck() {
        String title = "Help a friend with something";
        createTask(title);
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .check(matches(isDisplayed()));
        clear(title);
    }

    @Test
    public void createdTaskCheckOnTodayPageNotFound() {
        String title = "Wash a car";
        createTask(title);
        onView(withText("TODAY"))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());

        UiObject2 todoItem = device.findObject(By.text(title));
        assertNull(todoItem);
        clear(title);
    }

    @Test
    public void createdTaskCheckOn7DaysPageNotFound() {
        String title = "Do my homework";
        createTask(title);
        onView(withText("NEXT 7 DAYS"))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
        UiObject2 todoItem = device.findObject(By.text(title));
        assertNull(todoItem);
        clear(title);
    }


    private void createTask(String title) {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());

        onView(withId(R.id.todoEditText))
                .perform(typeText(title));

        onView(withId(R.id.addTodoBtn))
                .perform(ViewActions.click());
    }

    private void clear(String title) {
        onView(withText("INBOX"))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());

        UiObject2 todoItem = device.findObject(By.text(title));

        if (todoItem != null) {
            todoItem.click();
            UiObject2 delBtn = device.findObject(By.res(PACKAGE, "deleteTodoBtn"));
            if (delBtn != null) {
                delBtn.click();
            }
        }
    }

}

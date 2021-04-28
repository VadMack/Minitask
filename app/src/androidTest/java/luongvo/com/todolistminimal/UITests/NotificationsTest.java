package luongvo.com.todolistminimal.UITests;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.Direction;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.Until;

import com.wdullaer.materialdatetimepicker.date.DayPickerView;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;

import luongvo.com.todolistminimal.MainActivity;
import luongvo.com.todolistminimal.R;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class NotificationsTest {

    UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

    String NOTIFICATION_TITLE = "Task to be done";

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    public void createTaskWithReminder(String title) {
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);

        onView(withId(R.id.reminderSwitch))
                .perform(ViewActions.click());

        onView(withId(R.id.buttonSetDate))
                .perform(ViewActions.click());
        onView(isAssignableFrom(DayPickerView.class))
                .check(matches(isDisplayed()))
                .perform(AddToDoItemTest.setDate(calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)));
        onView(withText("OK")).perform(ViewActions.click());

        onView(withId(R.id.buttonSetTime))
                .perform(ViewActions.click());
        onView(isAssignableFrom(RadialPickerLayout.class))
                .check(matches(isDisplayed()))
                .perform(AddToDoItemTest.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        onView(withText("OK")).perform(ViewActions.click());

        onView(withId(R.id.todoEditText))
                .perform(typeText(title));

        onView(withId(R.id.addTodoBtn))
                .perform(ViewActions.click());
    }

    @After
    public void clear() {
        try {
            onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                    .perform(ViewActions.click());
            onView(withId(R.id.deleteTodoBtn))
                    .perform(ViewActions.click());
        } catch (PerformException ignored) {

        }
    }

    @Test
    public void receiveNotification() throws InterruptedException {
        String NOTIFICATION_TEXT = "Create a website";
        createTaskWithReminder(NOTIFICATION_TEXT);
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 600000);

        UiObject2 title = device.findObject(By.text(NOTIFICATION_TITLE));
        UiObject2 text = device.findObject(By.text(NOTIFICATION_TEXT));
        assertNotNull(title);
        assertNotNull(text);
        assertEquals(NOTIFICATION_TITLE, title.getText());
        assertEquals(NOTIFICATION_TEXT, text.getText());
        UiObject2 clearButton = device.findObject(By.text("Clear all"));
        clearButton.click();
        device.pressBack();
    }

    @Test
    public void notificationNotFoundBeforeSelectedTime() {
        createTaskWithReminder("Do washing");
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 5);
        UiObject2 elem = device.findObject(By.text(NOTIFICATION_TITLE));
        assertNull(elem);
        device.pressBack();
    }

    @Test
    public void notificationNotFoundTaskWithoutReminder() {
        createTaskWithoutReminder("Write an essay");
        device.openNotification();
        device.wait(Until.hasObject(By.text(NOTIFICATION_TITLE)), 60000);
        UiObject2 elem = device.findObject(By.text(NOTIFICATION_TITLE));
        assertNull(elem);
        device.pressBack();
    }

    private void createTaskWithoutReminder(String title) {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());

        onView(withId(R.id.todoEditText))
                .perform(typeText(title));

        onView(withId(R.id.addTodoBtn))
                .perform(ViewActions.click());
    }


}

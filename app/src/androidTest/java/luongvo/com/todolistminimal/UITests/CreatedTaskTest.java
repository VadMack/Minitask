package luongvo.com.todolistminimal.UITests;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v4.view.ViewPager;

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

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class CreatedTaskTest {
    private static final String TITLE = "Buy one apple";
    private static final String PACKAGE = InstrumentationRegistry.getTargetContext().getPackageName();
    private UiDevice device;

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        onView(withId(R.id.actionButton))
                .perform(ViewActions.click());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);

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
                .perform(AddToDoItemTest.setTime(23, 59));
        onView(withText("OK")).perform(ViewActions.click());

        onView(withId(R.id.todoEditText))
                .perform(typeText(TITLE));

        onView(withId(R.id.addTodoBtn))
                .perform(ViewActions.click());
    }

    @After
    public void clear() {
        /*onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.deleteTodoBtn))
                .perform(ViewActions.click());*/
        UiObject2 todoItem = device.findObject(By.text(TITLE));

        if (todoItem != null) {
            todoItem.click();
            UiObject2 delBtn = device.findObject(By.res(PACKAGE, "deleteTodoBtn"));
            if (delBtn != null) {
                delBtn.click();
            }
        }
    }

    @Test
    public void createdTaskCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .check(matches(isDisplayed()));
    }

    @Test
    public void todoInfoCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.todoInfo))
                .check(matches(isDisplayed()));
    }

    @Test
    public void deleteTodoBtnCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.deleteTodoBtn))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void editTodoBtnCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.editTodoBtn))
                .check(matches(allOf(
                        isDisplayed(),
                        isClickable())));
    }

    @Test
    public void deleteToDoItem() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.deleteTodoBtn))
                .perform(ViewActions.click());

        UiObject2 todoItem = device.findObject(By.text(TITLE));
        assertNull(todoItem);

        onView(withText(R.string.item_deleted)).inRoot(
                withDecorView(not(is(activityTestRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
    }

    @Test
    public void changeToDoItem() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .perform(ViewActions.click());
        onView(withId(R.id.editTodoBtn))
                .perform(ViewActions.click());
        onView(withId(R.id.todoEditText))
                .perform(
                        clearText(),
                        typeText("Buy two apples"));
        onView(withId(R.id.addTodoBtn))
                .perform(ViewActions.click());

        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed()))
                .atPosition(0)
                .onChildView(withText("Buy two apples"))
                .check(matches(isDisplayed()));

        UiObject2 todoItem = device.findObject(By.text("Buy two apples"));
        if (todoItem != null) {
            todoItem.click();
            UiObject2 delBtn = device.findObject(By.res(PACKAGE, "deleteTodoBtn"));
            if (delBtn != null) {
                delBtn.click();
            }
        }
    }

    @Test
    public void checkDoneCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .onChildView(withId(R.id.checkDone))
                .check(matches(allOf(isDisplayed(),
                        isClickable(),
                        not(isChecked())
                )));
    }

    @Test
    public void todoContentCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .onChildView(withId(R.id.todoContent))
                .check(matches(isDisplayed()));
    }

    @Test
    public void clockReminderCheck() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .onChildView(withId(R.id.clockReminder))
                .check(matches(isDisplayed()));
    }

    @Test
    public void checkDoneMakeChecked() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .onChildView(withId(R.id.checkDone))
                .perform(ViewActions.click())
                .check(matches(allOf(isDisplayed(),
                        isClickable(),
                        isChecked()
                )));
    }

    @Test
    public void deleteToDoItemByCleanAllDone() {
        onData(anything()).inAdapterView(allOf(withId(R.id.todoList), isDisplayed())).atPosition(0)
                .onChildView(withId(R.id.checkDone))
                .perform(ViewActions.click());
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(ViewMatchers.withText(R.string.clean_all_done))
                .perform(ViewActions.click());

        UiObject2 todoItem = device.findObject(By.text(TITLE));
        assertNull(todoItem);
    }

    @Test
    public void createdTaskCheckOn7DaysPage() {
        onView(withText("NEXT 7 DAYS"))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
        UiDevice device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        UiObject2 todoItem = device.findObject(By.text(TITLE));
        assertNotNull(todoItem);
        assertEquals(todoItem.getText(), TITLE);
    }

    @Test
    public void createdTaskCheckOnTodayPageNotFound() {
        onView(withText("TODAY"))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());

        UiObject2 todoItem = device.findObject(By.text(TITLE));
        assertNull(todoItem);

        onView(withText("INBOX"))
                .check(matches(isDisplayed()))
                .perform(ViewActions.click());
    }
}

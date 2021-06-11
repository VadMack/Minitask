package luongvo.com.todolistminimal.UITests;

import android.support.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import luongvo.com.todolistminimal.AboutActivity;
import luongvo.com.todolistminimal.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(JUnit4.class)
public class AboutActivityTest {
    @Rule
    public IntentsTestRule<AboutActivity> mActivityRule = new IntentsTestRule<>(
            AboutActivity.class);

    @Test
    public void appVersionCheck(){
        onView(withText(R.string.app_version)).check(matches(isDisplayed()));
    }

    @Test
    public void madeByCheck(){
        onView(withText(R.string.made_by)).check(matches(isDisplayed()));
    }

    @Test
    public void contactMeCheck(){
        onView(withText(R.string.contact_me)).check(matches(isDisplayed()));
    }

    @Test
    public void myEmailVersionCheck(){
        onView(withText(R.string.my_email)).check(matches(isDisplayed()));
    }

    @Test
    public void twitterAccCheck(){
        onView(withText(R.string.twitter_acc)).check(matches(isDisplayed()));
    }

    @Test
    public void facebookAccCheck(){
        onView(withText(R.string.facebook_acc)).check(matches(isDisplayed()));
    }

    @Test
    public void supportMeAccCheck(){
        onView(withText(R.string.support_me)).check(matches(isDisplayed()));
    }

    @Test
    public void patreonAccCheck(){
        onView(withText(R.string.patreon_acc)).check(matches(isDisplayed()));
    }

    @Test
    public void paypalAccCheck(){
        onView(withText(R.string.paypal_acc)).check(matches(isDisplayed()));
    }
}

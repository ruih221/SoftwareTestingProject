import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        autoCompleteSearchTest.class,
        FrontPageTest.class,
        ImgTest.class,
        ManagePageTest.class,
        newStreamTest.class,
        socialAndMapTest.class,
        subscribeTest.class,
        trendingTest.class
})

public class AllTest {
}
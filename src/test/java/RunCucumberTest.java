
import com.template.lib.cucumber.NewCucumber;
import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(NewCucumber.class)
@CucumberOptions(
		monochrome = true,
		format = {"pretty"},
		glue = {"com.template.stepDefinitions"},
		features = {"src/test/resources/features/"},
		tags = {"ymtest"}
		)
public class RunCucumberTest {
}
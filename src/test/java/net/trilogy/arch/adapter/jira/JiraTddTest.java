package net.trilogy.arch.adapter.jira;

import net.trilogy.arch.domain.architectureUpdate.Tdd;
import net.trilogy.arch.domain.architectureUpdate.TddContent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.util.List;

import static net.trilogy.arch.adapter.jira.JiraStory.JiraTdd;
import static org.hamcrest.Matchers.equalTo;

public class JiraTddTest {
    @Rule
    public final ErrorCollector collector = new ErrorCollector();


    @Test
    public void shouldReturnInlinedTddContent() {
        JiraTdd tdd = new JiraTdd(
                new Tdd.Id("TDD 1.1"),
                new Tdd("text", null),
                "10",
                null
        );

        collector.checkThat(tdd.getText(), equalTo("text"));
    }

    @Test
    public void shouldReturnFileTddContent() {
        JiraTdd tdd = new JiraTdd(
                new Tdd.Id("TDD 1.1"),
                new Tdd(null, "TDD 1.1 : Component-10.md"),
                "10",
                new TddContent("#Content\nTdd Content", "TDD 1.1 : Component-10.md")
        );

        collector.checkThat(tdd.getText(), equalTo("#Content\nTdd Content"));
    }

    @Test
    public void shouldReturnFileTddContentIfTddOmitsFile() {
        JiraTdd tdd = new JiraTdd(
                new Tdd.Id("TDD 1.1"),
                new Tdd("ignored text", null),
                "10",
                new TddContent("#Content\nTdd Content", "TDD 1.1 : Component-10.md")
        );

        collector.checkThat(tdd.getText(), equalTo("#Content\nTdd Content"));
    }

    @Test
    public void shouldReturnFileTddContentIfBothPresent() {
        JiraTdd tdd = new JiraTdd(
                new Tdd.Id("TDD 1.1"),
                new Tdd("ignored text", "TDD 1.1 : Component-10.md"),
                "10",
                new TddContent("#Content\nTdd Content", "TDD 1.1 : Component-10.md")
        );

        collector.checkThat(tdd.getText(), equalTo("#Content\nTdd Content"));
    }

    @Test
    public void shouldConstructJiraTddFromAuTddContents() {
        TddContent correctContent = new TddContent("correct content", "TDD 2.0 : Component-10.md");
        JiraTdd tdd = JiraTdd.make(
                new Tdd.Id("TDD 2.0"),
                new Tdd("ignored content", "TDD 2.0 : Component-10.md"),
                "10",
                List.of(
                        new TddContent("wrong content", "TDD 1.0 : Component-10.md"),
                        new TddContent("wrong content", "TDD 1.0 : Component-11.md"),
                        correctContent,
                        new TddContent("wrong content", "TDD 3.0 : Component-10.md"),
                        new TddContent("wrong content", "TDD 3.0 : Component-11.md")
                )
        );

        collector.checkThat(tdd.getId(), equalTo("TDD 2.0"));
        collector.checkThat(tdd.getComponent(), equalTo("10"));
        collector.checkThat(tdd.getTddContent(), equalTo(correctContent));
        collector.checkThat(tdd.getText(), equalTo("correct content"));
    }

    @Test
    public void shouldConstructJiraTddFromAuTddContentsEvenIfFileOmitted() {
        TddContent correctContent = new TddContent("correct content", "TDD 2.0 : Component-10.md");
        JiraTdd tdd = JiraTdd.make(
                new Tdd.Id("TDD 2.0"),
                new Tdd("ignored content", null),
                "10",
                List.of(
                        new TddContent("wrong content", "TDD 1.0 : Component-10.md"),
                        new TddContent("wrong content", "TDD 1.0 : Component-11.md"),
                        correctContent,
                        new TddContent("wrong content", "TDD 3.0 : Component-10.md"),
                        new TddContent("wrong content", "TDD 3.0 : Component-11.md")
                )
        );

        collector.checkThat(tdd.getId(), equalTo("TDD 2.0"));
        collector.checkThat(tdd.getComponent(), equalTo("10"));
        collector.checkThat(tdd.getTddContent(), equalTo(correctContent));
        collector.checkThat(tdd.getText(), equalTo("correct content"));
    }

    @Test
    public void shouldConstructJiraTddFromEmptyAuTddContents() {
        JiraTdd tdd = JiraTdd.make(
                new Tdd.Id("TDD 2.0"),
                new Tdd("text", null),
                "10",
                List.of()
        );

        collector.checkThat(tdd.getId(), equalTo("TDD 2.0"));
        collector.checkThat(tdd.getComponent(), equalTo("10"));
        collector.checkThat(tdd.getTddContent(), equalTo(null));
        collector.checkThat(tdd.getText(), equalTo("text"));
    }
}

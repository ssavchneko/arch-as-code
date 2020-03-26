package net.trilogy.arch.commands;

import net.trilogy.arch.adapter.ArchitectureUpdateObjectMapper;
import net.trilogy.arch.domain.ArchitectureUpdate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

@Command(
        name = "architecture-update",
        aliases = "au",
        description = "Architecture Update Commands",
        subcommands = {
                ArchitectureUpdateCommand.Initialize.class,
                ArchitectureUpdateCommand.New.class
        }
)
public class ArchitectureUpdateCommand implements Callable<Integer> {
    private static final Log logger = LogFactory.getLog(ArchitectureUpdateCommand.class);
    public static final String ARCHITECTURE_UPDATES_ROOT_FOLDER = "architecture-updates";
    private static final ArchitectureUpdateObjectMapper objectMapper = new ArchitectureUpdateObjectMapper();

    @Spec
    private CommandSpec spec;

    @Override
    public Integer call() {
        logger.info(spec.commandLine().getUsageMessage());
        return 0;
    }

    private static File getAuFolder(File productDocumentationRoot) {
        return productDocumentationRoot.toPath().resolve(ARCHITECTURE_UPDATES_ROOT_FOLDER).toFile();
    }

    @Command(name = "initialize", aliases = "init", description = "Initialize the architecture updates work space.")
    public static class Initialize implements Callable<Integer> {

        @Parameters(index = "0", description = "Product documentation root directory")
        private File productDocumentationRoot;

        @Override
        public Integer call() {
            File auFolder = getAuFolder(productDocumentationRoot);
            boolean succeeded = auFolder.mkdir();
            if (!succeeded) {
                logger.error(String.format("Unable to create %s", auFolder.getAbsolutePath()));
                return 1;
            }
            logger.info(String.format("Architecture updates initialized under - %s", auFolder.getAbsolutePath()));
            return 0;
        }
    }

    @Command(name = "new", description = "Initialize a new architecture update.")
    public static class New implements Callable<Integer> {

        @Parameters(index = "0", description = "Name for new architecture update")
        private String name;

        @Parameters(index = "1", description = "Product documentation root directory")
        private File productDocumentationRoot;

        @Override
        public Integer call() throws IOException {
            File auFolder = getAuFolder(productDocumentationRoot);

            if (!auFolder.isDirectory()) {
                logger.error(String.format("Root path - %s - seems incorrect. Run init first.", auFolder.getAbsolutePath()));
                return 1;
            }

            File auFile = auFolder.toPath().resolve(name).toFile();
            if (auFile.isFile()) {
                logger.error(String.format("AU %s already exists. Try a different name.", name));
                return 1;
            }

            ArchitectureUpdate au = ArchitectureUpdate.blank();
            Files.writeString(auFile.toPath(), objectMapper.writeValueAsString(au));
            return 0;
        }
    }
}

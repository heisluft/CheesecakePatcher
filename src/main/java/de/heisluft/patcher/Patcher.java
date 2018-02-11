package de.heisluft.patcher;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Patcher {

	private static File gameWorkingDir = new File("work/CheesecakeAdventures");
	private static Logger setup = LogManager.getLogger("Setup");

	public static void main(String[] args) {
		System.setProperty("log4j.configuration", "log4j.xml");
		setup.info("Setting up working dir");
		try {
			prepareSrc();
		} catch(Exception e) {
			setup.catching(e);
			try {
				FileUtils.deleteDirectory(gameWorkingDir);
			}catch(IOException e1) {
				setup.fatal("COULD NOT CLEANUP DIR, THIS IS VERY BAD!!!");
			}
			System.exit(1);
		}
		setup.info("Done");

	}
	private static void prepareSrc() throws IOException, URISyntaxException, GitAPIException {
		FileUtils.copyDirectory(new File("patches"), new File("work/patches"));
		FileUtils.deleteDirectory(gameWorkingDir);

		setup.info("Cloning the game");
		new CloneCommand().setURI(new URI("https://github.com/TinyLittleStudio/java-2d-cheesecake-adventures.git").toString()).setDirectory(
				gameWorkingDir).call().close();

		setup.info("Deleting unnecessary Files");
		FileUtils.deleteDirectory(new File(gameWorkingDir, ".git"));
		FileUtils.deleteDirectory(new File(gameWorkingDir, "META-INF"));
		new File(gameWorkingDir, ".gitignore").delete();
		new File(gameWorkingDir,  "README.md").delete();
		new File(gameWorkingDir,  "LICENSE").delete();

		setup.info("Relocating src");
		File protoType = new File(gameWorkingDir, "Cheesecake Adventures - Prototype");
		File engine = new File(gameWorkingDir, "Cheesecake Adventures - Engine");
		File newSrc = new File(gameWorkingDir, "src/main/java");
		FileUtils.copyDirectoryToDirectory(new File(protoType, "src"), newSrc);
		FileUtils.copyDirectoryToDirectory(new File(engine, "src"), newSrc);
		FileUtils.copyDirectoryToDirectory(new File(protoType, "res"), new File(gameWorkingDir, "src/main/resources"));

		setup.info("Deleting old src");
		FileUtils.deleteDirectory(protoType);
		FileUtils.deleteDirectory(engine);
	}
}
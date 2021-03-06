package de.heisluft.patcher;

import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.PatchFailedException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Patcher {

	private static final File workingDir = new File("work");
	private static final File gameWorkingDir = new File(workingDir,"CheesecakeAdventures");
	private static final File changeWorkingDir = new File(workingDir,"changes");
	private static final File patchWorkingDir = new File(changeWorkingDir,"patches");
	private static final File injsrcWorkingDir = new File(changeWorkingDir, "src");
	private static final Logger setup = LogManager.getLogger("Setup");
	private static final List<Path> paths = new ArrayList<>();
	private static boolean justSource;

	public static void main(String[] args) {
		long l = System.nanoTime();

		OptionParser parser = new OptionParser();
		parser.accepts("js");
		justSource = parser.parse(args).has("js");

		setup.info("Setting up working dir\n");
		try {
			prepareSrc();
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}
		if(justSource) {
			setup.info("DONE!!! (" + (System.nanoTime() - l) + " nanos)");
			return;
		}

		setup.info("Adding gradle stuff\n");
		try {
			patchGradle();
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}

		setup.info("Injecting Source\n");
		try {
			injectSource(injsrcWorkingDir);
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}
		setup.info("Done\n");

		setup.info("Registering Patches\n");
		try {
			registerPatches(patchWorkingDir);
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}
		setup.info("Done\n");

		setup.info("Applying Patches\n");
		try {
			patchSrc();
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}

		setup.info("Deleting Unused Files\n");
		try {
			deleteUnusedFiles();
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}

		setup.info("Building jar\n");
		try {
			buildJar();
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}

		setup.info("Cleaning up\n");
		try {
			copyAndCleanup();
		} catch(Exception e) {
			setup.catching(e);
			System.exit(1);
		}

		setup.info("DONE!!! (" + (System.nanoTime() - l) + " nanos)");
	}

	private static void buildJar() throws IOException, InterruptedException {
		String classpath = new File(gameWorkingDir, "gradle/wrapper/gradle-wrapper.jar").getAbsolutePath();
		String path = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		ProcessBuilder processBuilder =
				new ProcessBuilder(path, "-cp", classpath, "org.gradle.wrapper.GradleWrapperMain", "shadowJar")
				.directory(gameWorkingDir).inheritIO();
		Process process = processBuilder.start();
		process.waitFor();
		setup.info("Done\n");
	}

	private static void prepareSrc() throws IOException, GitAPIException {
		FileUtils.deleteDirectory(workingDir);
		workingDir.mkdir();

		setup.info("Cloning the game");
		new CloneCommand().setURI("https://github.com/TinyLittleStudio/java-2d-cheesecake-adventures.git").setDirectory(
				gameWorkingDir).call().close();
		if(!justSource) {
			setup.info("Cloning changes");
			new CloneCommand().setURI("https://github.com/heisluft/CheesecakePatcher.git").setDirectory(changeWorkingDir).setBranch("patches").call().close();
		}

		setup.info("Deleting unnecessary Files");
		FileUtils.deleteDirectory(new File(gameWorkingDir, ".git"));
		FileUtils.deleteDirectory(new File(gameWorkingDir, "META-INF"));
		new File(gameWorkingDir, ".gitignore").delete();
		new File(gameWorkingDir,  "README.md").delete();
		new File(gameWorkingDir,  "LICENSE").delete();

		if(!justSource) {
			FileUtils.deleteDirectory(new File(changeWorkingDir, ".git"));
			new File(changeWorkingDir, "README.md").delete();
			new File(changeWorkingDir, "LICENSE").delete();
		}

		setup.info("Relocating src");
		File protoType = new File(gameWorkingDir, "Cheesecake Adventures - Prototype");
		File engine = new File(gameWorkingDir, "Cheesecake Adventures - Engine");
		File unifiedSrc = new File(gameWorkingDir, "unified");
		File newSrc = new File(gameWorkingDir, "src/main/java");
		FileUtils.copyDirectoryToDirectory(new File(protoType, "src"), unifiedSrc);
		FileUtils.copyDirectoryToDirectory(new File(engine, "src"), unifiedSrc);
		moveAllSubDirsTo(new File(unifiedSrc, "src"), newSrc);
		File finalAssets =new File(gameWorkingDir, "src/main/resources/assets/cheesecake");
		FileUtils.moveDirectory(new File(protoType, "res"), finalAssets);
		new File(finalAssets, "images").renameTo(new File(finalAssets, "textures"));
		new File(finalAssets, "audio").renameTo(new File(finalAssets, "sound"));
		new File(finalAssets, "utils/32-original.png").renameTo(new File(finalAssets,"textures/32-original.png"));
		File dataDir =new File(finalAssets,"data");
		new File(finalAssets, "utils").renameTo(dataDir);
		new File(dataDir, "data").renameTo(new File(dataDir, "levels"));


		setup.info("Deleting old src");
		FileUtils.deleteDirectory(protoType);
		FileUtils.deleteDirectory(engine);
		FileUtils.deleteDirectory(unifiedSrc);
		setup.info("Done\n");
	}

	private static void patchGradle() throws IOException {
		FileUtils.copyDirectory(new File(changeWorkingDir, "injected-gradle-files"), gameWorkingDir,null, true);
		setup.info("Done\n");
	}

	private static void patchSrc() throws IOException, PatchFailedException {
		Path src = new File(gameWorkingDir, "src/main/java/eu/hackathon").toPath();
		for(Path p : paths) {
			setup.info("Patching with " + p.toString().replace('\\','/') + "...");
			Path old = src.resolve(p.resolveSibling(p.getFileName().toString().replaceAll(".patch$", ".java")));
			Files.write(old, UnifiedDiffUtils.parseUnifiedDiff(Files.readAllLines(patchWorkingDir.toPath().resolve(p))).applyTo(Files.readAllLines(old)),
					StandardOpenOption.TRUNCATE_EXISTING);
		}
		setup.info("Done\n");
	}

	private static void injectSource(File f) throws IOException {
		for (File file : f.listFiles())
			if (file.isDirectory()) injectSource(file);
			else
				FileUtils.copyFile(file, gameWorkingDir.toPath().resolve(changeWorkingDir.toPath().relativize(file.toPath())).toFile());
	}

	private static void deleteUnusedFiles() throws IOException {
		List<String> deletionList = Files.readAllLines(new File(changeWorkingDir, "deletions").toPath());
		for(String deletion : deletionList)
			if(!deletion.isEmpty()) {
				setup.info("Deleting " + deletion + "...\n");
				File f = new File(gameWorkingDir, deletion);
				if(f.exists()) FileUtils.forceDelete(f);
			}
		setup.info("Done\n");
	}

	private static void registerPatches(File f) {
		for (File file : f.listFiles())
			if (file.isDirectory()) registerPatches(file);
			else {
				Path p = patchWorkingDir.toPath().relativize(file.toPath());
				paths.add(p);
				setup.info("Found Patch " + p.toString().replace('\\','/'));
		}
	}

	private static void moveAllSubDirsTo(File in, File to) throws IOException {
		for(File f : in.listFiles())
			if(f.isDirectory()) FileUtils.moveDirectory(f, new File(to, f.getName()));
	}

	private static void copyAndCleanup() throws IOException {
		setup.info("Copying jars");
		for(File f : new File(gameWorkingDir, "build/libs").listFiles()) {
			File nf =  new File(f.getName());
			if(nf.exists()) nf.delete();
			FileUtils.moveFile(f, nf);
		}

		setup.info("Deleting working dir");
		FileUtils.deleteDirectory(workingDir);
		setup.info("Done\n");
	}

}
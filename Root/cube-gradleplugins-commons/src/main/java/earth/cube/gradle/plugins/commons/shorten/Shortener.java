package earth.cube.gradle.plugins.commons.shorten;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.gradle.api.Action;
import org.gradle.api.JavaVersion;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.FileTree;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.StopExecutionException;

import earth.cube.gradle.plugins.commons.gitignoretpl.GitIgnoreTpl;
import earth.cube.gradle.plugins.commons.utils.GitRepoFinder;

public class Shortener {
	
	private Project _project;

	public Shortener(Project project) {
		_project = project;
	}
	

	/** Create src/main/java, src/main/resources, src/test/java, src/test/resources
	 * 
	 * @param rootProject
	 */
	protected void extendEclipseClasspathTask(Project rootProject) {
		rootProject.allprojects(new Action<Project>() {

			@Override
			public void execute(Project project) {
				for(Task task : project.getTasksByName("eclipseClasspath", false)) {
					task.doFirst(new Action<Task>() {

						@Override
						public void execute(Task task) {
							Project project = task.getProject();
							JavaPluginConvention javaPlugin = project.getConvention().getPlugin(JavaPluginConvention.class);
							for(SourceSet sourceSet : javaPlugin.getSourceSets()) {
								for(File dir : sourceSet.getAllJava().getSrcDirs()) {
									dir.mkdirs();
								}
								for(File dir : sourceSet.getResources().getSrcDirs()) {
									dir.mkdirs();
								}
								try {
									GitIgnoreTpl ignore = new GitIgnoreTpl(GitRepoFinder.findGitRepo(project.getRootDir(), false));
									ignore.addIgnore(project.getBuildDir().toPath(), true);
									ignore.addIgnore(new File(project.getProjectDir(), "bin").toPath(), true);
									ignore.addIgnore(new File(project.getProjectDir(), "target").toPath(), true);
									ignore.save();
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}
						}
						
					});
				}
			}
			
		});
		
	}
	
	protected void addDependencies(Project rootProject) {
		rootProject.allprojects(new Action<Project>() {

			@SuppressWarnings("deprecation")
			@Override
			public void execute(Project project) {
				Map<String, String> args = new HashMap<>();
				args.put("dir", "lib");
				args.put("include", "**/*.jar");	
				FileTree files = project.fileTree(args);
				Dependency dep = project.getDependencies().create(files);
				
				project.getConfigurations().findByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).getDependencies().add(dep);
				
				JavaPluginConvention javaPlugin = project.getConvention().getPlugin(JavaPluginConvention.class);
				JavaVersion ver = javaPlugin.getTargetCompatibility();
				if(ver.isJava7() || ver.isJava7Compatible()) {
					dep = project.getDependencies().create("junit:junit:4.12");
					project.getConfigurations().findByName(JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME).getDependencies().add(dep);
				}
			}
			
		});
	}
	
	protected void addMavenRepositories(Project rootProject) {
		rootProject.allprojects(new Action<Project>() {

			@Override
			public void execute(Project project) {

				project.getRepositories().mavenLocal();

				project.getRepositories().maven(new Action<MavenArtifactRepository>() {
            
					@Override
					public void execute(MavenArtifactRepository repo) {
						repo.setUrl("http://repo.maven.apache.org/maven2");
					}
				});

			}
		});
	}

	private void addMavenPublications(Project rootProject) {		
		rootProject.allprojects(new Action<Project>() {

			@Override
			public void execute(Project project) {
				
				project.configure(Arrays.asList(project), new Action<Project>() {

					@Override
					public void execute(Project project) {
						if(project.hasProperty("maven_aware")) {
							project.getPluginManager().apply("maven-publish");
						}
					}
					
				});
				
				project.afterEvaluate(new Action<Project>() {

					@Override
					public void execute(final Project project) {
						if(project.hasProperty("maven_aware")) {
							PublishingExtension publishing = (PublishingExtension) project.getExtensions().getByName(PublishingExtension.NAME);
							publishing.getPublications().create("MavenPublication", MavenPublication.class, new Action<MavenPublication>() {

								@Override
								public void execute(MavenPublication pub) {
									pub.from(project.getComponents().findByName("java"));
								}
								
							});
							
						}
					}
					
				});
			}
		});
	}

	protected void loadProperties(Project rootProject) {
		if(rootProject.hasProperty("environment")) {
			File file = rootProject.file("profiles/${environment}.properties");
			if(file.exists()) {
				Properties props = new Properties();
				try {
					props.load(new FileInputStream(file));
				} catch (IOException e) {
					throw new StopExecutionException(e.getMessage());
				}
				for(Entry<Object, Object> e : props.entrySet()) {
					rootProject.setProperty(e.getKey().toString(), e.getValue());
				}
			}
		}
	}

	
	public void prepare() {
		extendEclipseClasspathTask(_project);
		addDependencies(_project);
		addMavenRepositories(_project);
		addMavenPublications(_project);
		loadProperties(_project);
	}





}

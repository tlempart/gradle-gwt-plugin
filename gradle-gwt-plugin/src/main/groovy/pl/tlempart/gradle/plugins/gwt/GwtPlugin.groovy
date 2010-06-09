package pl.tlempart.gradle.plugins.gwt

import java.io.File;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class GwtPlugin implements Plugin<Project> {

	void apply(Project project) {
		GwtPluginConvention convention = new GwtPluginConvention()
		project.convention.plugins.gwt = convention
		
		project.task('compileGwt') << {
			def classpath = project.configurations.gwt.collect { it.toURI().toURL() }.join(':')
			def sourceDirectory = (project.sourceSets.main.java.srcDirs as List)[0]
			classpath = "$classpath:$sourceDirectory"
			
			new File("./$project.convention.plugins.gwt.buildDir").mkdirs()
			
			def command = """java -cp $classpath com.google.gwt.dev.Compiler
			-war $project.convention.plugins.gwt.buildDir
			-logLevel $project.convention.plugins.gwt.logLevel
			-style $project.convention.plugins.gwt.style
			${project.convention.plugins.gwt.treeLogger? '-treeLogger':''}
			$project.convention.plugins.gwt.moduleClass"""
			
			println "Execute command: ${command}"
					
			def process = command.execute()
			process.waitFor()
			println "return code: ${process.exitValue()}"
			println "stderr: ${process.err.text}"
			println "stdout: ${process.in.text}"

		}
	}
}

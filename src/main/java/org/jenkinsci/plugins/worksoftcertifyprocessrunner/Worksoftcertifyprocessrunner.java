package org.jenkinsci.plugins.worksoftcertifyprocessrunner;

/**
 * 
 * @author susmitha
 */

import hudson.Launcher;
import hudson.Extension;
import hudson.FilePath;
import hudson.Proc;
import hudson.util.FormValidation;
import hudson.model.*;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import java.io.BufferedReader;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.InputStreamReader;

public class Worksoftcertifyprocessrunner extends Builder {

	/**
	 *  This field retains the method name  
	 */
	
    private final String name;
    private final String password;
    private final String project;
    private final String process;
    private final String recordset;
    private final String layout;
    private final String mode;
    private final String target;
    private final boolean useLayout;
    
    public String getName() {
        return name;
    }

    public String getProject() {
        return project;
    }

    public String getPassword() {
        return password;
    }

    public String getProcess() {
        return process;
    }

    public String getRecordset() {
        return recordset;
    }

    public String getLayout() {
        return layout;
    }

    public String getMode() {
        return mode;
    }
    public String getTarget() {
    	return target;
    }


    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public Worksoftcertifyprocessrunner(String name, String password, String project, String process, String recordset, String layout, String mode, String target, boolean useLayout) {
       
    	
    	/**
    	 * @param password Password
    	 * @param name User Name
    	 * @param project Project
    	 * @param target Results
    	 */
    	this.name = name;
        this.password = password;
        this.project = project;
        this.process = process;
        this.layout = layout;
        this.recordset = recordset;
        this.mode = mode;
        this.target = target;
        this.useLayout = useLayout;
    }

    /**
     * @return True if process is executed else error will be displayed
     */
        @Override
    public boolean perform(final AbstractBuild<?, ?> build, final Launcher launcher,final BuildListener listener) {
        // This is where you 'build' the project.
        // This also shows how you can consult the global configuration of the builder
        String command;
        System.out.println(useLayout);
       /**
        * If user did not give layout and recordset if will execute otherwise else will execute.
        */
        
        if (!useLayout) {
            command = "Certify.exe  /usecertifyconfig+ /useprocessdata+ /stepdelay=0  /Process=\"" + process + "\"   /Project=\"" + project + "\"  /VerifyObjects=Disabled  /outputlocation=\"" + target + "\" /createoutputlocation+ /user=\"" + name + "\" /password=\"" + password + "\"";
           
        }else {

            command = "Certify.exe  /usecertifyconfig+ /useprocessdata+ /stepdelay=0  /Process=\"" + process + "\"   /Project=\"" + project + "\"  /Recordset=\"" + recordset + "\" /RecordsetsMode=\"" + mode + "\" /Layout=\"" + layout + "\" /VerifyObjects=Disabled  /outputlocation=\"" + target + "\" /createoutputlocation+  /user=\"" + name + "\" /password=\"" + password + "\"";
        }
        System.out.println(command);
        try {
              Proc proc = launcher.launch(command, build.getEnvVars(),listener.getLogger(),build.getProject().getWorkspace());
              int exitCode = proc.join();
              if(exitCode==0){
                  listener.getLogger().println("Execution Success");
              }

        } catch (IOException | InterruptedException ex) {
            listener.getLogger().println("Failed to build due to following error:\n" + ex.getMessage());
            build.setResult(Result.FAILURE);
        }
        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        public DescriptorImpl() {
            load();
        }

      
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        
        public String getDisplayName() {
            return "Worksoft Certify Process Runner";
        }
        
        /**
         * @return the name of the process in post build
         */

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
           
            save();
            return super.configure(req, formData);
        }

    }
}

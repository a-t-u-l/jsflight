package com.focusit.jsflight.player.cli;

import com.focusit.jmeter.JMeterRecorder;
import com.focusit.jsflight.player.controller.DuplicateHandlerController;
import com.focusit.jsflight.player.controller.OptionsController;
import com.focusit.jsflight.player.controller.ScriptEventExectutionController;
import com.focusit.jsflight.player.controller.WebLookupController;
import com.focusit.jsflight.player.scenario.ScenarioProcessor;
import com.focusit.jsflight.player.scenario.UserScenario;
import com.focusit.jsflight.player.webdriver.SeleniumDriver;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CliPlayer
{
    private static final Logger LOG = LoggerFactory.getLogger(CliPlayer.class);

    private CliConfig config;

    private JMeterRecorder jmeter = new JMeterRecorder();

    private SeleniumDriver seleniumDriver;

    public CliPlayer(CliConfig config) throws Exception
    {
        this.config = config;
        String templatePath = config.getJmxTemplatePath();
        if (templatePath.trim().isEmpty())
        {
            LOG.info("Initializing Jmeter with default jmx template: template.jmx");
            jmeter.init();
        }
        else
        {
            LOG.info("Initializing Jmeter with jmx template: {}", templatePath);
            jmeter.init(templatePath);
        }

        updateControllers();
    }

    public void play() throws IOException
    {
        UserScenario scenario = new UserScenario();
        LOG.info("Loading {}", config.getPathToRecording());
        scenario.parse(config.getPathToRecording());
        scenario.postProcessScenario();
        seleniumDriver = new SeleniumDriver(scenario);
        scenario.getContext().setJMeterBridge(jmeter.getBridge());
        if (config.isEnableRecording())
        {
            jmeter.setProxyPort(Integer.parseInt(config.getProxyPort()));
            jmeter.startRecording();
            try
            {
                ScenarioProcessor.play(scenario, seleniumDriver, Integer.parseInt(config.getStartStep()), Integer.parseInt(config.getFinishStep()));
            }
            finally
            {
                jmeter.stopRecording();
                LOG.info("Saving recorded scenario to {}", config.getJmeterRecordingName());
                jmeter.saveScenario(config.getJmeterRecordingName());
            }
        }
        else
        {
            ScenarioProcessor.play(scenario, seleniumDriver, Integer.parseInt(config.getStartStep()), Integer.parseInt(config.getFinishStep()));
        }
    }

    private void updateControllers() throws IOException
    {
        if (this.config.getJmeterStepPreprocess() != null && !this.config.getJmeterStepPreprocess().trim().isEmpty())
        {
            jmeter.getScriptProcessor().setRecordingScript(
                    new String(Files.readAllBytes(Paths.get(config.getJmeterStepPreprocess().trim())), "UTF-8"));
        }

        if (this.config.getJmeterScenarioPreprocess() != null
                && !this.config.getJmeterScenarioPreprocess().trim().isEmpty())
        {
            jmeter.getScriptProcessor().setProcessScript(
                    new String(Files.readAllBytes(Paths.get(config.getJmeterScenarioPreprocess().trim())), "UTF-8"));
        }

        //Init options, based on config
        OptionsController options = OptionsController.getInstance();

        options.setFfPath(config.getFfPath());
        options.setMakeShots(config.getMakeShots());
        options.setPageReadyTimeout(config.getPageReadyTimeout());
        options.setProxyHost(config.getProxyHost());
        options.setProxyPort(config.getProxyPort());
        options.setScreenDir(config.getScreenDir());
        options.setUseFirefox(config.isUseFirefox());
        options.setUsePhantomJs(config.isUsePhantomJs());
        options.setPjsPath(config.getPjsPath());
        options.setUseRandomChars(config.isUseRandomChars());

        //Init weblookup script
        WebLookupController.getInstance()
                .setScript(FileUtils.readFileToString(new File(config.getWebLookupScriptPath())));

        //Init duplicate handler script
        DuplicateHandlerController.getInstance()
                .setScriptBody(FileUtils.readFileToString(new File(config.getDuplicateHandlerScriptPath())));

        //Init script events handler
        ScriptEventExectutionController.getInstance()
                .setScript(FileUtils.readFileToString(new File(config.getScriptEventHandlerScriptPath())));
    }

    public SeleniumDriver getSeleniumDriver() {
        return seleniumDriver;
    }
}

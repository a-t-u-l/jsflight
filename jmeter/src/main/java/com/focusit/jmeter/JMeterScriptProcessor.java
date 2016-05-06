package com.focusit.jmeter;

import com.focusit.script.ScriptEngine;
import groovy.lang.Binding;
import groovy.lang.Script;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerBase;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to run groovy scripts against recorded samples
 * Created by doki on 25.03.16.
 */
public class JMeterScriptProcessor {
    private static final Logger log = LoggerFactory.getLogger(JMeterScriptProcessor.class);
    // script called at recording phase. Can skip sample
    private String recordingScript;
    // script callled at storing phase. Can skip sample
    private String processScript;
    private JMeterRecorder recorder;

    public JMeterScriptProcessor(JMeterRecorder recorder){

        this.recorder = recorder;
    }

    public String getRecordingScript() {
        return recordingScript;
    }

    public void setRecordingScript(String recordingScript) {
        this.recordingScript = recordingScript;
    }

    public String getProcessScript() {
        return processScript;
    }

    public void setProcessScript(String processScript) {
        this.processScript = processScript;
    }

    /**
     * Post process sample with groovy script.
     *
     * @param sampler
     * @param result
     * @return is sample ok
     */
    public boolean processSampleDuringRecord(HTTPSamplerBase sampler, SampleResult result) {
        Binding binding = new Binding();
        binding.setVariable("logger", log);
        binding.setVariable("request", sampler);
        binding.setVariable("response", result);
        binding.setVariable("ctx", recorder.getContext());
        binding.setVariable("jsflight", recorder.getBridge());
        binding.setVariable("classloader", ScriptEngine.getInstance().getClassLoader());

        boolean isOk = true;

        Script s = ScriptEngine.getInstance().getThreadBindedScript(recordingScript);
        if(s==null){
            log.error(Thread.currentThread().getName()+":"+"Sample "+sampler.getName()+"No script found. default result "+isOk);
            return isOk;
        }
        s.setBinding(binding);
        log.info(Thread.currentThread().getName()+":"+"running "+sampler.getName()+" compiled script");
        Object scriptResult = s.run();

        if(scriptResult!=null && scriptResult instanceof Boolean){
            isOk = (boolean) scriptResult;
        } else {
            log.error(Thread.currentThread().getName()+":"+"Sample "+sampler.getName()+" script result UNDEFINED shifted to"+isOk);
        }

        log.error(Thread.currentThread().getName()+":"+"Sample "+sampler.getName()+" script result "+isOk);
        return isOk;
    }

    /**
     * Post process every stored request just before it get saved to disk
     * @param sample  recorded http-request (sample)
     * @param tree HashTree (XML like data structure) that represents exact recorded sample
     */
    public void processScenario(HTTPSamplerBase sample, HashTree tree, Arguments userVariables) {
        Binding binding = new Binding();
        binding.setVariable("logger", log);
        binding.setVariable("sample", sample);
        binding.setVariable("tree", tree);
        binding.setVariable("ctx", recorder.getContext());
        binding.setVariable("jsflight", recorder.getBridge());
        binding.setVariable("vars", userVariables);
        binding.setVariable("classloader", ScriptEngine.getInstance().getClassLoader());

        Script compiledProcessScript = ScriptEngine.getInstance().getScript(processScript);
        if(compiledProcessScript==null){
            return;
        }
        compiledProcessScript.setBinding(binding);
        compiledProcessScript.run();
    }

    public JMeterRecorder getRecorder() {
        return recorder;
    }
}

package com.focusit.script.jmeter;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Class holds references to user scenario step and associated http samples.
 * Can be used during post processing of every http sample to find an appropriate cookie or other auth mechanism
 */
public class JMeterJSFlightBridge
{
    private static final JMeterJSFlightBridge instance = new JMeterJSFlightBridge();
    public static JSONObject NO_SCENARIO_STEP;
    public static String TAG_FIELD = "uuid";
    private final ConcurrentHashMap<Object, JSONObject> samplersEvents = new ConcurrentHashMap<>();
    private JSONObject currentScenarioStep = NO_SCENARIO_STEP;

    {
        NO_SCENARIO_STEP = new JSONObject();
        NO_SCENARIO_STEP.put("DO NOT USE IT", true);
    }

    private JMeterJSFlightBridge()
    {

    }

    public static JMeterJSFlightBridge getInstace()
    {
        return instance;
    }

    public void addSampler(Object sampler)
    {
        samplersEvents.put(sampler, getCurrentScenarioStep());
    }

    public boolean isCurrentStepEmpty(){
        return currentScenarioStep.equals(NO_SCENARIO_STEP);
    }

    public JSONObject getCurrentScenarioStep()
    {
        return currentScenarioStep;
    }

    public void setCurrentScenarioStep(JSONObject currentScenarioStep)
    {
        this.currentScenarioStep = currentScenarioStep;
    }

    public JSONObject getSourceEvent(Object sampler)
    {
    	JSONObject result = samplersEvents.get(sampler);

    	return result;
    }
}

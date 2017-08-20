/**
 * this class represents a parsed JSON data
 */
package bgu.spl.a2.sim.conf;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParsedData {

    @SerializedName("threads")
    @Expose
    private Integer threads;
    @SerializedName("tools")
    @Expose
    private List<ParsedTool> tools = null;
    @SerializedName("plans")
    @Expose
    private List<Plan> plans = null;
    @SerializedName("waves")
    @Expose
    private List<List<Order>> waves = null;

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public List<ParsedTool> getTools() {
        return tools;
    }

    public void setTools(List<ParsedTool> tools) {
        this.tools = tools;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public List<List<Order>> getWaves() {
        return waves;
    }

    public void setWaves(List<List<Order>> waves) {
        this.waves = waves;
    }
  
  

}

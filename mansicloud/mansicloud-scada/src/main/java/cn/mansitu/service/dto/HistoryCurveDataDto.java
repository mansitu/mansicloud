package cn.mansitu.service.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
public class HistoryCurveDataDto implements Serializable {

    @ApiModelProperty(value = "采集时间戳")
    private Timestamp ts;

    private Map<String, Float> dynamicFields = new HashMap<>();

    @JsonAnyGetter
    public Map<String, Float> getDynamicFields() {
        return dynamicFields;
    }

    @JsonAnySetter
    public void setDynamicField(String key, Float value) {
        this.dynamicFields.put(key, value);
    }
}

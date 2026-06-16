package cn.mansitu.service.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class LatestDeviceDataResponseDto implements Serializable {

    @ApiModelProperty(value = "按设备类型分组的数据")
    private Map<String, List<Map<String, Object>>> groupedData;

    @ApiModelProperty(value = "设备总数")
    private Integer totalCount;
}

package com.bonanza.daip.market.upbit.request;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.bonanza.daip.market.upbit.enums.OrderByType;
import com.bonanza.daip.market.upbit.enums.OrderStateType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderListRequest {
    private String market; // 마켓 아이디
    private List<String> uuids; // 주문 UUID의 목록
    private List<String> identifiers; // 주문 identifier의 목록
    private OrderStateType state; // 주문 상태
    private List<OrderStateType> states; // //미체결 주문(wait, watch)과 완료 주문(done, cancel)을 혼합하여 조회하실 수 없습니다.
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer limit = 100;
    @Builder.Default
    @JsonProperty("order_by")
    private OrderByType orderBy = OrderByType.ASC;

    public String toQueryString() {
        List<String> queryElements = new ArrayList<>();
        if (Objects.nonNull(state)) {
            queryElements.add("state="+state.getType());
        }

        if (StringUtils.isNotBlank(market)) {
            queryElements.add("market="+market);
        }

        if (!CollectionUtils.isEmpty(uuids)) {
            for(String uuid : uuids) {
                queryElements.add("uuids[]=" + uuid);
            }
        }

        if (!CollectionUtils.isEmpty(identifiers)) {
            for(String uuid : identifiers) {
                queryElements.add("identifiers[]=" + uuid);
            }
        }

        queryElements.add("page=" + page);
        queryElements.add("limit=" + limit);
        queryElements.add("order_by=" + orderBy.getType());

        return String.join("&", queryElements.toArray(new String[0]));
    }
}

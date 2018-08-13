package com.lunera.response;

import java.util.ArrayList;
import java.util.List;

import com.lunera.dto.SummarizeData;

import lombok.Data;

/**
 * 
 * @author gautam.vijay added on 8 Aug 2018
 */
@Data
public class SummarizeDataResponse {
	private List<SummarizeData> summaryData = new ArrayList<SummarizeData>();
}

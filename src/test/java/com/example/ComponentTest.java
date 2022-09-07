package com.example;

import com.example.messaging.CsvMessageHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ComponentTest {

	@Autowired
	private CsvMessageHandler csvMessageHandler;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void requestPrice() throws Exception {
		var csv = """
				106, EUR/USD, 1.1000,1.2000,01-06-2020 12:01:01:001
				107, EUR/JPY, 119.60,119.90,01-06-2020 12:01:02:002
				108, GBP/USD, 1.2500,1.2560,01-06-2020 12:01:02:002
				109, GBP/USD, 1.2499,1.2561,01-06-2020 12:01:02:100
				110, EUR/JPY, 119.61,119.91,01-06-2020 12:01:02:110
				""";

		csvMessageHandler.processCsv(csv);

		this.mockMvc.perform(MockMvcRequestBuilders.get("/price?base=GBP&quote=USD"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.currencyPair.base", equalTo("GBP")))
				.andExpect(jsonPath("$.currencyPair.quote", equalTo("USD")))
				.andExpect(jsonPath("$.bid", equalTo("TODO")))
				.andExpect(jsonPath("$.ask", equalTo("TODO")))
				.andExpect(jsonPath("$.timestamp", equalTo("2020-06-01T12:01:02:100")));
	}

}

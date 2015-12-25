package com.davengeo.etag;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EtagDemoApplication.class)
@WebAppConfiguration
public class EtagDemoApplicationTests {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ShallowEtagHeaderFilter shallowEtagHeaderFilter;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).
                addFilter(shallowEtagHeaderFilter, "/*").alwaysDo(print()).build();
    }

	@Test
	public void should_be_200_or_304_with_IfNoneMatch_Header() throws Exception {
        final String etag = mockMvc.perform(get("/check").
                accept("application/hal+json")).
                andExpect(header().string("ETag", instanceOf(String.class))).
                andReturn().
                getResponse().
                getHeader("Etag");

        mockMvc.perform(get("/check").
                accept("application/hal+json").
                header("If-None-Match", etag)).
                andExpect(status().isNotModified());
	}

}

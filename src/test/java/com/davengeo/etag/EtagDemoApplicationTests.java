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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = EtagDemoApplication.class)
@WebAppConfiguration
public class EtagDemoApplicationTests {

    public static final String ETAG = "ETag";
    public static final String CONTENT = "content";
    public static final String NEW_CONTENT = "new content";
    public static final String APPLICATION_HAL_JSON = "application/hal+json";

    @Autowired
    WebApplicationContext context;
    @Autowired
    ShallowEtagHeaderFilter shallowEtagHeaderFilter;

    private MockMvc mockMvc;


    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).
                addFilter(shallowEtagHeaderFilter, "/*").
                alwaysDo(print()).
                build();

        mockMvc.perform(delete("/channel/")).
                andExpect(status().isNoContent());
    }

    @Test
    public void should_be_200_or_304_with_IfNoneMatch_Header() throws Exception {
        final String etag = mockMvc.perform(get("/check").
                accept(APPLICATION_HAL_JSON)).
                andExpect(header().string(ETAG, instanceOf(String.class))).
                andReturn().
                getResponse().
                getHeader(ETAG);

        mockMvc.perform(get("/check").
                accept(APPLICATION_HAL_JSON).
                header("If-None-Match", etag)).
                andExpect(status().isNotModified());
    }

    @Test
    public void should_behave_as_etag() throws Exception {

        mockMvc.perform(get("/channel/1").
                accept(APPLICATION_HAL_JSON)).
                andExpect(status().isNotFound());

        mockMvc.perform(post("/channel/1").
                accept(APPLICATION_HAL_JSON).
                content(CONTENT)).
                andExpect(status().isOk());

        final String etag = mockMvc.perform(get("/channel/1").
                accept(APPLICATION_HAL_JSON)).
                andExpect(status().isOk()).
                andExpect(header().string(ETAG, instanceOf(String.class))).
                andExpect(content().string(equalTo(CONTENT))).
                andReturn().
                    getResponse().getHeader(ETAG);

        mockMvc.perform(get("/channel/1").
                accept(APPLICATION_HAL_JSON).
                header("If-None-Match", etag)).
                andExpect(status().isNotModified());

        mockMvc.perform(post("/channel/1").
                accept(APPLICATION_HAL_JSON).
                content(NEW_CONTENT)).
                andExpect(status().isOk());

        final String etag2 = mockMvc.perform(get("/channel/1").
                accept(APPLICATION_HAL_JSON).
                header("If-None-Match", etag)).
                andExpect(status().isOk()).
                andExpect(content().string(equalTo(NEW_CONTENT))).
                andReturn().
                getResponse().getHeader(ETAG);

        mockMvc.perform(get("/channel/1").
                accept(APPLICATION_HAL_JSON).
                header("If-None-Match", etag2)).
                andExpect(status().isNotModified());

    }

    @Test
    public void should_etag_with_additive_endpoint() throws Exception {

        mockMvc.perform(post("/channel/1").
                accept(APPLICATION_HAL_JSON).
                content("AaB")).
                andExpect(status().isOk());

        mockMvc.perform(post("/channel/2").
                accept(APPLICATION_HAL_JSON).
                content("BbC")).
                andExpect(status().isOk());

        final String etag = mockMvc.perform(get("/channel/").
                accept(APPLICATION_HAL_JSON)).
                andExpect(status().isOk()).
                andExpect(content().string(equalTo("AaBBbC"))).
                andReturn().
                getResponse().getHeader(ETAG);

        mockMvc.perform(get("/channel/").
                accept(APPLICATION_HAL_JSON).
                header("If-None-Match", etag)).
                andExpect(status().isNotModified());
    }

}

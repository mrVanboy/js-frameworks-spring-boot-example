package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class JavaScriptFrameworkControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JavaScriptFrameworkRepository repository;

    private static final Random r = new Random();


    private static final Iterable<JavaScriptFramework> defaultState = ImmutableSet.of(
            new JavaScriptFramework("Angular", "6.0.0", 1572735600L, (byte) 8),
            new JavaScriptFramework("Angular", "1.4.1", 1497571200L, (byte) 3),
            new JavaScriptFramework("Ember", "2.4.0", 1492732800L, (byte) 6),
            new JavaScriptFramework("ForeverYoungFramework", "1.2.3", Instant.now().getEpochSecond() + 3600L, (byte) 10)
    );

    private Iterable<JavaScriptFramework> defaultRepositoryItems;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        repository.deleteAll();
        this.defaultRepositoryItems = repository.saveAll(defaultState);
    }

    @Test
    public void ShouldEqualDefaultFrameworks() throws Exception {
        final ResultActions result = mockMvc.perform(get("/frameworks"));
        AtomicInteger i = new AtomicInteger(0);
        AtomicReference<Exception> err = new AtomicReference<>();
        defaultState.forEach(framework -> {
            if (err.get() != null) return;
            try {
                result.andExpect(jsonPath("$[" + i + "].id", notNullValue()))
                        .andExpect(jsonPath("$[" + i + "].name", is(framework.getName())))
                        .andExpect(jsonPath("$[" + i + "].version", is(framework.getVersion())))
                        .andExpect(jsonPath("$[" + i + "].deprecationDate", is(framework.getDeprecationDate().intValue())))
                        .andExpect(jsonPath("$[" + i + "].hypeLevel", is(framework.getHypeLevel().intValue())));
            } catch (Exception e) {
                err.set(e);
            }
            i.getAndIncrement();
        });
        if (err.get() != null) throw err.get();
    }

    @Test
    public void ShouldReturnEmptyArray() throws Exception {
        repository.deleteAll();
        mockMvc.perform(get("/frameworks"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    public void ShouldSizeEqual() throws Exception {
        mockMvc.perform(get("/frameworks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(Iterables.size(defaultState))));
    }

    @Test
    public void ShouldNotFound() throws Exception {
        mockMvc.perform(get("/frameworks/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void ShouldPost() throws Exception {
        final String fixture = String.join("\n",
                "{",
                "  \"name\": \"fixture\",",
                "  \"version\": \"3.2.1\",",
                "  \"deprecationDate\": 1572735600,",
                "  \"hypeLevel\": 1",
                "}"
        );

        mockMvc.perform(post("/frameworks")
                .content(fixture)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("fixture")))
                .andExpect(jsonPath("$.version", is("3.2.1")))
                .andExpect(jsonPath("$.deprecationDate", is(1572735600)))
                .andExpect(jsonPath("$.hypeLevel", is(1)))
                .andExpect(jsonPath("$.*", hasSize(5)));

        mockMvc.perform(get("/frameworks"))
                .andExpect(jsonPath("$", hasSize(Iterables.size(defaultState) + 1)));
    }

    @Test
    public void ShouldPatch() throws Exception {
        final JavaScriptFramework frameworkToPatch = defaultRepositoryItems.iterator().next();

        final List<Map<String, Object>> fixtures = generatePatchFixtures();

        for (Map<String,Object> fixture : fixtures){
            JSONObject jsonBody = new JSONObject();
            jsonBody.putAll( fixture );
            final ResultActions result = mockMvc.perform(patch("/frameworks/" + frameworkToPatch.getId())
                    .content(jsonBody.toString())
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk());

            // check that all fields from fixture was changed
            for (Map.Entry<String, Object> field : fixture.entrySet()) {
                result.andExpect(jsonPath("$."+field.getKey(), is(field.getValue())));
            }
            repository.save(frameworkToPatch);
        }
    }

    /**
     * Generate the all possible fields combinations for patching the framework
     *
     * @return List of all possible fields combinations for patch
     */
    private static List<Map<String, Object>> generatePatchFixtures() {
        Map<String, Object> fixtures = new HashMap<>();
        fixtures.put("name", "test-name");
        fixtures.put("version", r.nextInt(10) + "." + r.nextInt(10) + "." + r.nextInt(10));
        fixtures.put("deprecationDate", r.nextInt((int) Instant.now().getEpochSecond()));
        fixtures.put("hypeLevel", r.nextInt(9)+1);

        List<Map<String, Object>> patchFixtures = new ArrayList<>();

        final HashMap<String, Object> fixturesLeft = new HashMap<>(fixtures);
        fixtures.forEach(((k, v) -> {
            Map<String, Object> fixture = new HashMap<>();
            fixturesLeft.forEach((k2, v2) -> {
                fixture.put(k2, v2);
                patchFixtures.add(new HashMap<>(fixture));
            });
            fixturesLeft.remove(k);
        }));
        return patchFixtures;
    }

    @Test
    public void ShouldPutFramework() throws Exception {
        final JavaScriptFramework framework = Iterables.getLast(defaultRepositoryItems);

        Map<String, Object> initState = new HashMap<>();
        initState.put("id", framework.getId());
        initState.put("name", framework.getName() );
        initState.put("deprecationDate", framework.getDeprecationDate() );
        initState.put("version", framework.getVersion());
        initState.put("hypeLevel", framework.getHypeLevel() );

        final List<Map<String, Object>> patchFixtures = generatePatchFixtures();

        for (Map<String, Object> fixture : patchFixtures) {
            Map<String, Object> mapBody = new HashMap<>(initState);
            mapBody.putAll(fixture);

            final JSONObject jsonBody = new JSONObject();
            jsonBody.putAll(mapBody);

            final ResultActions result = mockMvc.perform(put("/frameworks/" + framework.getId())
                    .content(jsonBody.toString())
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(status().isOk());

            // check that all fields from fixture was changed
            for (Map.Entry<String, Object> field : fixture.entrySet()) {
                result.andExpect(jsonPath("$."+field.getKey(), is(field.getValue())));
            }
        }
    }

    @Test
    public void ShouldNotPutWithBadId() throws Exception {
        final JavaScriptFramework framework = Iterables.getLast(defaultRepositoryItems);
        final String fixture = String.join("\n",
                "{",
                "  \"id\": \""+ (framework.getId() - 1) +"\",",
                "  \"name\": \"fixture\",",
                "  \"version\": \"3.2.1\",",
                "  \"deprecationDate\": 1572735600,",
                "  \"hypeLevel\": 1",
                "}"
        );

        mockMvc.perform(put("/frameworks/" + framework.getId())
                        .content(fixture)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ShouldDeleteFramework() throws Exception {
        final JavaScriptFramework frameworkToDelete = defaultRepositoryItems.iterator().next();
        final int initSize = Iterables.size(defaultRepositoryItems);

        mockMvc.perform(delete("/frameworks/" + frameworkToDelete.getId()))
                .andExpect(status().isNoContent());

        // Ensure that deleted framework is not present in all frameworks list
        mockMvc.perform(get("/frameworks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(initSize - 1) ))
                .andExpect(jsonPath("$", not(contains(frameworkToDelete)) ));
    }

    @Test
    public void ShouldFilterByNameIgnoreCase() throws Exception {
        mockMvc.perform(get("/frameworks?name=AnGuLaR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2) ))
                .andExpect(jsonPath("$[0].name", equalToIgnoringCase("angular")))
                .andExpect(jsonPath("$[1].name", equalToIgnoringCase("angular")));
    }

    @Test
    public void ShouldFilterByNameUnexisted() throws Exception {
        mockMvc.perform(get("/frameworks?name=unexisted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0) ));
    }

    @Test
    public void ShouldFilterDeprecated() throws Exception {
        List<Boolean> statuses =  Arrays.asList(true, false);

        for (Boolean deprecated : statuses) {
            AtomicInteger expectedSize = new AtomicInteger();

            defaultRepositoryItems.forEach(framework -> {
                if ((framework.getDeprecationDate() < Instant.now().getEpochSecond()) == deprecated) {
                    expectedSize.getAndIncrement();
                }
            });

            final ResultActions result = mockMvc.perform(get("/frameworks?deprecated=" + deprecated))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(expectedSize.get())));

            if (!deprecated) {
                result.andExpect(jsonPath("$[*].name", hasItem("ForeverYoungFramework")));
            } else {
                result.andExpect(jsonPath("$[*].name", not(hasItem("ForeverYoungFramework"))));
            }

        }
    }

    @Test
    public void ShouldFilterByMultipleHypeLevels() throws Exception{
        final JavaScriptFramework framework = Iterables.getLast(defaultRepositoryItems);

        mockMvc.perform(get("/frameworks?hypeLevel="+
                                        (framework.getHypeLevel() - 1) +
                                        "&hypeLevel="+
                                        (framework.getHypeLevel() + 1)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].id", is(framework.getId().intValue()) ));
    }

}
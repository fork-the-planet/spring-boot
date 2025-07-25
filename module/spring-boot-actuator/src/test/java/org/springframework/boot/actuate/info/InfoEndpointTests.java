/*
 * Copyright 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.info;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InfoEndpoint}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @author Meang Akira Tanaka
 * @author Andy Wilkinson
 */
class InfoEndpointTests {

	@Test
	void info() {
		InfoEndpoint endpoint = new InfoEndpoint(Arrays.asList((builder) -> builder.withDetail("key1", "value1"),
				(builder) -> builder.withDetail("key2", "value2")));
		Map<String, Object> info = endpoint.info();
		assertThat(info).hasSize(2);
		assertThat(info).containsEntry("key1", "value1");
		assertThat(info).containsEntry("key2", "value2");
	}

	@Test
	void infoWithNoContributorsProducesEmptyMap() {
		InfoEndpoint endpoint = new InfoEndpoint(Collections.emptyList());
		Map<String, Object> info = endpoint.info();
		assertThat(info).isEmpty();
	}

}

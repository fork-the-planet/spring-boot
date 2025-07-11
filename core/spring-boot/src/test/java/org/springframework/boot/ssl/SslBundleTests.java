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

package org.springframework.boot.ssl;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link SslBundle}.
 *
 * @author Phillip Webb
 * @author Moritz Halbritter
 */
class SslBundleTests {

	@Test
	void createSslContextDelegatesToManagers() {
		SslManagerBundle managers = mock(SslManagerBundle.class);
		SslBundle bundle = SslBundle.of(null, null, null, "testprotocol", managers);
		bundle.createSslContext();
		then(managers).should().createSslContext("testprotocol");
	}

	@Test
	void ofCreatesSslBundle() {
		SslStoreBundle stores = mock(SslStoreBundle.class);
		SslBundleKey key = mock(SslBundleKey.class);
		SslOptions options = mock(SslOptions.class);
		String protocol = "test";
		SslManagerBundle managers = mock(SslManagerBundle.class);
		SslBundle bundle = SslBundle.of(stores, key, options, protocol, managers);
		assertThat(bundle.getStores()).isSameAs(stores);
		assertThat(bundle.getKey()).isSameAs(key);
		assertThat(bundle.getOptions()).isSameAs(options);
		assertThat(bundle.getProtocol()).isSameAs(protocol);
		assertThat(bundle.getManagers()).isSameAs(managers);
	}

	@Test
	void shouldCreateSystemDefaultBundle() {
		SslBundle sslBundle = SslBundle.systemDefault();
		SSLContext sslContext = sslBundle.createSslContext();
		assertThat(sslContext).isNotNull();
		TrustManager[] trustManagers = sslBundle.getManagers().getTrustManagers();
		assertThat(trustManagers).isNotEmpty();
		TrustManager trustManager = trustManagers[0];
		assertThat(trustManager).isInstanceOf(X509TrustManager.class);
		X509TrustManager x509TrustManager = (X509TrustManager) trustManager;
		assertThat(x509TrustManager.getAcceptedIssuers()).isNotEmpty();
	}

}

/**
 * Copyright (C) 2013 Microsoft Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.microsoft.reef.runtime.common.utils;

import com.microsoft.wake.EventHandler;
import com.microsoft.wake.remote.RemoteIdentifierFactory;
import com.microsoft.wake.remote.RemoteMessage;

import javax.inject.Inject;

public class RemoteManager {

	private final com.microsoft.wake.remote.RemoteManager raw;
	
	private final RemoteIdentifierFactory factory;
	
	@Inject
	public RemoteManager(final com.microsoft.wake.remote.RemoteManager raw, final RemoteIdentifierFactory factory) {
		this.raw = raw;
		this.factory = factory;
	}

	public final com.microsoft.wake.remote.RemoteManager raw() {
		return this.raw;
	}
	
	public void close() throws Exception {
		this.raw.close();
	}

	public <T> EventHandler<T> getHandler(
			String destinationIdentifier,
			Class<? extends T> messageType) {
		return this.raw.getHandler(factory.getNewInstance(destinationIdentifier), messageType);
	}

	public <T, U extends T> AutoCloseable registerHandler(
			String sourceIdentifier, Class<U> messageType,
			EventHandler<T> theHandler) {
		return this.raw.registerHandler(factory.getNewInstance(sourceIdentifier), messageType, theHandler);
	}

	public <T, U extends T> AutoCloseable registerHandler(Class<U> messageType, EventHandler<RemoteMessage<T>> theHandler) {
		return this.raw.registerHandler(messageType, theHandler);
	}

	public AutoCloseable registerErrorHandler(EventHandler<Exception> theHandler) {
		return this.raw.registerErrorHandler(theHandler);
	}

	public String getMyIdentifier() {
		return this.raw.getMyIdentifier().toString();
	}

}

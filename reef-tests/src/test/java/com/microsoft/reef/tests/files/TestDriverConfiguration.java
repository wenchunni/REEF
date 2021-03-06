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
package com.microsoft.reef.tests.files;

import com.microsoft.tang.annotations.Name;
import com.microsoft.tang.annotations.NamedParameter;
import com.microsoft.tang.formats.ConfigurationModule;
import com.microsoft.tang.formats.ConfigurationModuleBuilder;
import com.microsoft.tang.formats.RequiredParameter;

import java.util.Set;

public final class TestDriverConfiguration extends ConfigurationModuleBuilder {

  @NamedParameter(doc = "The names of the files to expect in the local filesystem.")
  public static final class FileNamesToExpect implements Name<Set<String>> {
  }

  public static final RequiredParameter<String> EXPECTED_FILE_NAME = new RequiredParameter<>();

  public static final ConfigurationModule CONF = new TestDriverConfiguration()
      .bindSetEntry(FileNamesToExpect.class, EXPECTED_FILE_NAME)
      .build();

}

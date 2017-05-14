/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *           http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.blindpirate.gogradle.ide

import com.github.blindpirate.gogradle.GogradleRunner
import com.github.blindpirate.gogradle.crossplatform.GoBinaryManager
import com.github.blindpirate.gogradle.support.WithResource
import com.github.blindpirate.gogradle.util.IOUtils
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

import static org.mockito.Mockito.when

@RunWith(GogradleRunner)
@WithResource('')
class IntellijIdeIntegrationTest {
    @Mock
    GoBinaryManager manager
    @Mock
    Project project
    File resource

    IntellijIdeIntegration intellijIdeIntegration

    @Before
    void setUp() {
        intellijIdeIntegration = new IntellijIdeIntegration(manager, project)
        when(project.getRootDir()).thenReturn(resource)
        when(manager.getBinaryPath()).thenReturn(new File(resource, 'go/bin/go').toPath())
        when(manager.getGoroot()).thenReturn(new File(resource, 'go').toPath())
        when(manager.getGoVersion()).thenReturn('1.7.1')

        when(project.getName()).thenReturn('MyAwesomeProject')
    }

    @Test
    void 'xmls should be generated correctly'() {
        intellijIdeIntegration.generateXmls()

        assert new File(resource, '.idea/goLibraries.xml').exists()

        String moduleIml = IOUtils.toString(new File(resource, '.idea/MyAwesomeProject.iml'))
        assert moduleIml.contains('WEB_MODULE')
        assert moduleIml.contains('Go SDK')

        String goSdkXml = IOUtils.toString(new File(resource, '.idea/libraries/Go_SDK.xml'))
        assert goSdkXml.contains("file://${new File(resource, 'go/src').getAbsolutePath()}")

        String modulesXml = IOUtils.toString(new File(resource, '.idea/modules.xml'))
        assert modulesXml.contains('.idea/MyAwesomeProject.iml')
    }
}

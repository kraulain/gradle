/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.artifacts.transform

import com.google.common.collect.ImmutableList
import org.gradle.test.fixtures.file.CleanupTestDirectory
import org.gradle.test.fixtures.file.TestFile
import org.gradle.test.fixtures.file.TestNameTestDirectoryProvider
import org.junit.Rule
import spock.lang.Specification

@CleanupTestDirectory
class TransformationResultWriterTest extends Specification {
    @Rule
    final TestNameTestDirectoryProvider temporaryFolder = TestNameTestDirectoryProvider.newInstance(getClass())

    def inputArtifact = file("inputArtifact").createDir()
    def outputDir = file("outputDir")
    def resultFile = file("results.txt")

    def "reads and writes transformation results"() {
        expect:
        assertCanWriteAndReadResult(
            inputArtifact.file("inside"),
            inputArtifact,
            outputDir.file("first"),
            outputDir.file("second"),
            outputDir
        )
    }

    def "reads and writes output only transformation results"() {
        expect:
        assertCanWriteAndReadResult(
            outputDir.file("first"),
            outputDir.file("second"),
            outputDir
        )
    }

    def "reads and writes input only transformation results"() {
        expect:
        assertCanWriteAndReadResult(
            inputArtifact.file("inside"),
            inputArtifact,
            inputArtifact
        )
    }

    def "resolves files in input artifact relative to input artifact"() {
        def writer = new TransformationResultWriter(inputArtifact, outputDir)
        def newInputArtifact = file("newInputArtifact").createDir()

        ImmutableList<File> result = ImmutableList.of(
            inputArtifact.file("inside"),
            inputArtifact,
            outputDir,
            inputArtifact
        )
        ImmutableList<File> resultResolvedForNewInputArtifact = ImmutableList.of(
            newInputArtifact.file("inside"),
            newInputArtifact,
            outputDir,
            newInputArtifact
        )

        when:
        def initialResults = writer.writeToFile(resultFile, result)
        then:
        resultFile.exists()
        initialResults.resolveOutputsForInputArtifact(inputArtifact) == result
        initialResults.resolveOutputsForInputArtifact(newInputArtifact) == resultResolvedForNewInputArtifact

        when:
        def loadedResults = writer.readResultsFile(resultFile)
        then:
        loadedResults.resolveOutputsForInputArtifact(inputArtifact) == result
        loadedResults.resolveOutputsForInputArtifact(newInputArtifact) == resultResolvedForNewInputArtifact
    }

    def "loads files in output directory relative to output directory"() {
        def writer = new TransformationResultWriter(inputArtifact, outputDir)
        def newOutputDir = file("newOutputDir").createDir()

        ImmutableList<File> result = ImmutableList.of(
            inputArtifact,
            outputDir,
            outputDir.file("output.txt")
        )
        ImmutableList<File> resultInNewOutputDir = ImmutableList.of(
            inputArtifact,
            newOutputDir,
            newOutputDir.file("output.txt")
        )

        when:
        def initialResults = writer.writeToFile(resultFile, result)
        then:
        resultFile.exists()
        initialResults.resolveOutputsForInputArtifact(inputArtifact) == result

        when:
        def writerWithNewOutputDir = new TransformationResultWriter(inputArtifact, newOutputDir)
        def loadedResults = writerWithNewOutputDir.readResultsFile(resultFile)
        then:
        loadedResults.resolveOutputsForInputArtifact(inputArtifact) == resultInNewOutputDir
    }

    private void assertCanWriteAndReadResult(File... files) {
        def writer = new TransformationResultWriter(inputArtifact, outputDir)
        ImmutableList<File> result = ImmutableList.<File>builder().add(files).build()
        def initialResults = writer.writeToFile(resultFile, result)

        assert resultFile.exists()
        assert initialResults.resolveOutputsForInputArtifact(inputArtifact) == result

        assert writer.readResultsFile(resultFile).resolveOutputsForInputArtifact(inputArtifact) == result
    }

    TestFile file(String path) {
        temporaryFolder.file(path)
    }
}
